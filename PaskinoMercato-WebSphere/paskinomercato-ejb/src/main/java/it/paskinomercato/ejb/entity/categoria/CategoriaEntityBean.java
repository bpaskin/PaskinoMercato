package it.paskinomercato.ejb.entity.categoria;

import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * EJB 2.x BMP Entity Bean — Categoria.
 *
 * Primary key: Integer (mercato.categoria.id)
 */
public class CategoriaEntityBean implements EntityBean {

    private Integer id;
    private String  codice;
    private String  nomeIt;
    private String  nomeEn;
    private String  descrizioneIt;
    private String  descrizioneEn;
    private String  immagine;

    private EntityContext ctx;

    public void setEntityContext(EntityContext ctx) { this.ctx = ctx; }
    public void unsetEntityContext()               { this.ctx = null; }
    public void ejbActivate()  {}
    public void ejbPassivate() {}

    public Integer ejbCreate(String codice, String nomeIt, String nomeEn,
                              String descrizioneIt, String descrizioneEn,
                              String immagine) throws CreateException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.categoria " +
                "(codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, codice);
            ps.setString(2, nomeIt);
            ps.setString(3, nomeEn);
            ps.setString(4, descrizioneIt);
            ps.setString(5, descrizioneEn);
            ps.setString(6, immagine);
            rs = ps.executeQuery();
            if (!rs.next()) throw new CreateException("INSERT categoria returned no key");
            this.id           = rs.getInt(1);
            this.codice       = codice;
            this.nomeIt       = nomeIt;
            this.nomeEn       = nomeEn;
            this.descrizioneIt = descrizioneIt;
            this.descrizioneEn = descrizioneEn;
            this.immagine     = immagine;
            return this.id;
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("ejbCreate categoria failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public void ejbPostCreate(String codice, String nomeIt, String nomeEn,
                               String descrizioneIt, String descrizioneEn,
                               String immagine) {}

    public void ejbLoad() {
        Integer pk = (Integer) ctx.getPrimaryKey();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
                "FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new EJBException("Categoria not found: " + pk);
            this.id           = rs.getInt(1);
            this.codice       = rs.getString(2);
            this.nomeIt       = rs.getString(3);
            this.nomeEn       = rs.getString(4);
            this.descrizioneIt = rs.getString(5);
            this.descrizioneEn = rs.getString(6);
            this.immagine     = rs.getString(7);
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("ejbLoad categoria failed", e);
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
                "UPDATE mercato.categoria SET " +
                "codice=?, nome_it=?, nome_en=?, descrizione_it=?, descrizione_en=?, immagine=? " +
                "WHERE id=?");
            ps.setString(1, codice);
            ps.setString(2, nomeIt);
            ps.setString(3, nomeEn);
            ps.setString(4, descrizioneIt);
            ps.setString(5, descrizioneEn);
            ps.setString(6, immagine);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbStore categoria failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    public void ejbRemove() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("DELETE FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbRemove categoria failed", e);
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
            ps = con.prepareStatement("SELECT id FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Categoria not found: " + pk);
            return pk;
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Collection<Integer> ejbFindAll() throws FinderException {
        Collection<Integer> pks = new ArrayList<Integer>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT id FROM mercato.categoria ORDER BY nome_it");
            while (rs.next()) pks.add(rs.getInt(1));
            return pks;
        } catch (Exception e) {
            throw new EJBException("ejbFindAll categoria failed", e);
        } finally {
            closeQuietly(rs, st, con);
        }
    }

    public Integer ejbFindByCodice(String codice) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.categoria WHERE codice = ?");
            ps.setString(1, codice);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Categoria not found by codice: " + codice);
            return rs.getInt(1);
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByCodice failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------
    public Integer getId()                        { return id; }

    public String getCodice()                     { return codice; }
    public void setCodice(String codice)          { this.codice = codice; }

    public String getNomeIt()                     { return nomeIt; }
    public void setNomeIt(String n)               { this.nomeIt = n; }

    public String getNomeEn()                     { return nomeEn; }
    public void setNomeEn(String n)               { this.nomeEn = n; }

    public String getDescrizioneIt()              { return descrizioneIt; }
    public void setDescrizioneIt(String d)        { this.descrizioneIt = d; }

    public String getDescrizioneEn()              { return descrizioneEn; }
    public void setDescrizioneEn(String d)        { this.descrizioneEn = d; }

    public String getImmagine()                   { return immagine; }
    public void setImmagine(String immagine)      { this.immagine = immagine; }

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
