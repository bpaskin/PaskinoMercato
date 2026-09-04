package it.paskinomercato.ejb.entity.cliente;

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
 * EJB 2.x BMP Entity Bean — Cliente.
 *
 * Primary key: Integer (mercato.cliente.id)
 */
public class ClienteEntityBean implements EntityBean {

    private Integer id;
    private String  email;
    private String  nome;
    private String  cognome;
    private String  telefono;
    private String  lingua;
    private boolean attivo;

    private EntityContext ctx;

    public void setEntityContext(EntityContext ctx) { this.ctx = ctx; }
    public void unsetEntityContext()               { this.ctx = null; }
    public void ejbActivate()  {}
    public void ejbPassivate() {}

    public Integer ejbCreate(String email, String passwordHash, String nome,
                              String cognome, String telefono, String lingua)
            throws CreateException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.cliente (email, password_hash, nome, cognome, telefono, lingua) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, nome);
            ps.setString(4, cognome);
            ps.setString(5, telefono);
            ps.setString(6, lingua != null ? lingua : "it");
            rs = ps.executeQuery();
            if (!rs.next()) throw new CreateException("INSERT cliente returned no key");
            this.id       = rs.getInt(1);
            this.email    = email;
            this.nome     = nome;
            this.cognome  = cognome;
            this.telefono = telefono;
            this.lingua   = lingua != null ? lingua : "it";
            this.attivo   = true;
            return this.id;
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("ejbCreate cliente failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public void ejbPostCreate(String email, String passwordHash, String nome,
                               String cognome, String telefono, String lingua) {}

    public void ejbLoad() {
        Integer pk = (Integer) ctx.getPrimaryKey();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
                "FROM mercato.cliente WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new EJBException("Cliente not found: " + pk);
            this.id       = rs.getInt(1);
            this.email    = rs.getString(2);
            this.nome     = rs.getString(3);
            this.cognome  = rs.getString(4);
            this.telefono = rs.getString(5);
            this.lingua   = rs.getString(6);
            this.attivo   = rs.getBoolean(7);
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("ejbLoad cliente failed", e);
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
                "UPDATE mercato.cliente SET " +
                "email=?, nome=?, cognome=?, telefono=?, lingua=?, attivo=? WHERE id=?");
            ps.setString(1, email);
            ps.setString(2, nome);
            ps.setString(3, cognome);
            ps.setString(4, telefono);
            ps.setString(5, lingua);
            ps.setBoolean(6, attivo);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbStore cliente failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    public void ejbRemove() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("DELETE FROM mercato.cliente WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbRemove cliente failed", e);
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
            ps = con.prepareStatement("SELECT id FROM mercato.cliente WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Cliente not found: " + pk);
            return pk;
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Integer ejbFindByEmail(String email) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.cliente WHERE email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Cliente not found by email: " + email);
            return rs.getInt(1);
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByEmail failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Collection<Integer> ejbFindByAttivo(boolean attivo) throws FinderException {
        Collection<Integer> pks = new ArrayList<Integer>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.cliente WHERE attivo = ?");
            ps.setBoolean(1, attivo);
            rs = ps.executeQuery();
            while (rs.next()) pks.add(rs.getInt(1));
            return pks;
        } catch (Exception e) {
            throw new EJBException("ejbFindByAttivo failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Accessors
    // ----------------------------------------------------------------
    public Integer getId()                     { return id; }

    public String getEmail()                   { return email; }
    public void setEmail(String email)         { this.email = email; }

    public String getNome()                    { return nome; }
    public void setNome(String nome)           { this.nome = nome; }

    public String getCognome()                 { return cognome; }
    public void setCognome(String cognome)     { this.cognome = cognome; }

    public String getTelefono()                { return telefono; }
    public void setTelefono(String telefono)   { this.telefono = telefono; }

    public String getLingua()                  { return lingua; }
    public void setLingua(String lingua)       { this.lingua = lingua; }

    public boolean isAttivo()                  { return attivo; }
    public void setAttivo(boolean attivo)      { this.attivo = attivo; }

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
