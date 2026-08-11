package it.paskinomercato.ejb.entity.ordine;

import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * EJB 2.x BMP Entity Bean — Ordine.
 *
 * Primary key: Integer (mercato.ordine.id)
 *
 * Note: order creation (with order lines and stock decrement) remains in
 * OrdineBean (Session Bean) because it spans multiple tables and requires
 * a single database transaction.  This entity handles single-ordine
 * load/store/find operations.
 */
public class OrdineEntityBean implements EntityBean {

    private Integer    id;
    private String     numeroOrdine;
    private int        clienteId;
    private int        indirizzoId;
    private String     stato;
    private BigDecimal totale;
    private String     note;
    private boolean    emailInviata;
    private Timestamp  createdAt;

    private EntityContext ctx;

    public void setEntityContext(EntityContext ctx) { this.ctx = ctx; }
    public void unsetEntityContext()               { this.ctx = null; }
    public void ejbActivate()  {}
    public void ejbPassivate() {}

    public Integer ejbCreate(String numeroOrdine, int clienteId, int indirizzoId,
                              BigDecimal totale, String note) throws CreateException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.ordine (numero_ordine, cliente_id, indirizzo_id, stato, totale, note) " +
                "VALUES (?, ?, ?, 'IN_ATTESA', ?, ?) RETURNING id");
            ps.setString(1, numeroOrdine);
            ps.setInt(2, clienteId);
            ps.setInt(3, indirizzoId);
            ps.setBigDecimal(4, totale);
            ps.setString(5, note);
            rs = ps.executeQuery();
            if (!rs.next()) throw new CreateException("INSERT ordine returned no key");
            this.id           = rs.getInt(1);
            this.numeroOrdine = numeroOrdine;
            this.clienteId    = clienteId;
            this.indirizzoId  = indirizzoId;
            this.stato        = "IN_ATTESA";
            this.totale       = totale;
            this.note         = note;
            this.emailInviata = false;
            return this.id;
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("ejbCreate ordine failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public void ejbPostCreate(String numeroOrdine, int clienteId, int indirizzoId,
                               BigDecimal totale, String note) {}

    public void ejbLoad() {
        Integer pk = (Integer) ctx.getPrimaryKey();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, numero_ordine, cliente_id, indirizzo_id, stato, totale, note, email_inviata, created_at " +
                "FROM mercato.ordine WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new EJBException("Ordine not found: " + pk);
            this.id           = rs.getInt(1);
            this.numeroOrdine = rs.getString(2);
            this.clienteId    = rs.getInt(3);
            this.indirizzoId  = rs.getInt(4);
            this.stato        = rs.getString(5);
            this.totale       = rs.getBigDecimal(6);
            this.note         = rs.getString(7);
            this.emailInviata = rs.getBoolean(8);
            this.createdAt    = rs.getTimestamp(9);
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("ejbLoad ordine failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public void ejbStore() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "UPDATE mercato.ordine SET stato=?, totale=?, note=?, email_inviata=?, updated_at=NOW() " +
                "WHERE id=?");
            ps.setString(1, stato);
            ps.setBigDecimal(2, totale);
            ps.setString(3, note);
            ps.setBoolean(4, emailInviata);
            ps.setInt(5, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbStore ordine failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    public void ejbRemove() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("DELETE FROM mercato.ordine WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbRemove ordine failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Finders
    // ----------------------------------------------------------------
    public Integer ejbFindByPrimaryKey(Integer pk) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.ordine WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Ordine not found: " + pk);
            return pk;
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Integer ejbFindByNumeroOrdine(String numeroOrdine) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.ordine WHERE numero_ordine = ?");
            ps.setString(1, numeroOrdine);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Ordine not found: " + numeroOrdine);
            return rs.getInt(1);
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByNumeroOrdine failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Collection<Integer> ejbFindByClienteId(int clienteId) throws FinderException {
        Collection<Integer> pks = new ArrayList<Integer>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id FROM mercato.ordine WHERE cliente_id = ? ORDER BY created_at DESC");
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();
            while (rs.next()) pks.add(rs.getInt(1));
            return pks;
        } catch (Exception e) {
            throw new EJBException("ejbFindByClienteId failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------
    public Integer getId()                           { return id; }
    public String getNumeroOrdine()                  { return numeroOrdine; }
    public int getClienteId()                        { return clienteId; }
    public int getIndirizzoId()                      { return indirizzoId; }

    public String getStato()                         { return stato; }
    public void setStato(String stato)               { this.stato = stato; }

    public BigDecimal getTotale()                    { return totale; }
    public void setTotale(BigDecimal totale)         { this.totale = totale; }

    public String getNote()                          { return note; }
    public void setNote(String note)                 { this.note = note; }

    public boolean isEmailInviata()                  { return emailInviata; }
    public void setEmailInviata(boolean e)           { this.emailInviata = e; }

    public Timestamp getCreatedAt()                  { return createdAt; }

    // ----------------------------------------------------------------
    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
