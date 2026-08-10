package it.paskinomercato.ejb.cliente;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EJB 2.0 Stateless Session Bean — Cliente.
 * Manages customer registration, authentication and addresses.
 */
public class ClienteBean implements SessionBean {

    private SessionContext ctx;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    public Cliente registra(String email, String passwordHash, String nome,
                             String cognome, String telefono, String lingua) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.cliente (email, password_hash, nome, cognome, telefono, lingua) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
                Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            ps.setString(3, nome);
            ps.setString(4, cognome);
            ps.setString(5, telefono);
            ps.setString(6, lingua != null ? lingua : "it");
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                Cliente c = new Cliente();
                c.setId(rs.getInt(1));
                c.setEmail(email);
                c.setNome(nome);
                c.setCognome(cognome);
                c.setTelefono(telefono);
                c.setLingua(lingua != null ? lingua : "it");
                c.setAttivo(true);
                return c;
            }
            throw new EJBException("Registrazione fallita");
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("registra failed: " + e.getMessage(), e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Cliente login(String email, String passwordHash) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
                "FROM mercato.cliente WHERE email = ? AND password_hash = ? AND attivo = true");
            ps.setString(1, email);
            ps.setString(2, passwordHash);
            rs = ps.executeQuery();
            if (rs.next()) return mapCliente(rs);
        } catch (Exception e) {
            throw new EJBException("login failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public Cliente getClienteById(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
                "FROM mercato.cliente WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapCliente(rs);
        } catch (Exception e) {
            throw new EJBException("getClienteById failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public Cliente getClienteByEmail(String email) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
                "FROM mercato.cliente WHERE email = ?");
            ps.setString(1, email);
            rs = ps.executeQuery();
            if (rs.next()) return mapCliente(rs);
        } catch (Exception e) {
            throw new EJBException("getClienteByEmail failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public void aggiornaLingua(int clienteId, String lingua) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "UPDATE mercato.cliente SET lingua = ? WHERE id = ?");
            ps.setString(1, lingua);
            ps.setInt(2, clienteId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("aggiornaLingua failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    public void aggiungiIndirizzo(int clienteId, String via, String civico,
                                   String citta, String cap, String provincia) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            // Check if first address — make it default
            PreparedStatement count = con.prepareStatement(
                "SELECT COUNT(*) FROM mercato.indirizzo WHERE cliente_id = ?");
            count.setInt(1, clienteId);
            ResultSet rs = count.executeQuery();
            boolean isFirst = rs.next() && rs.getInt(1) == 0;
            rs.close(); count.close();

            ps = con.prepareStatement(
                "INSERT INTO mercato.indirizzo (cliente_id, via, civico, citta, cap, provincia, paese, predefinito) " +
                "VALUES (?, ?, ?, ?, ?, ?, 'IT', ?)");
            ps.setInt(1, clienteId);
            ps.setString(2, via);
            ps.setString(3, civico);
            ps.setString(4, citta);
            ps.setString(5, cap);
            ps.setString(6, provincia.toUpperCase());
            ps.setBoolean(7, isFirst);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("aggiungiIndirizzo failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    public List<Indirizzo> getIndirizzi(int clienteId) {
        List<Indirizzo> list = new ArrayList<Indirizzo>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, cliente_id, via, civico, citta, cap, provincia, paese, predefinito " +
                "FROM mercato.indirizzo WHERE cliente_id = ? ORDER BY predefinito DESC, id ASC");
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapIndirizzo(rs));
            }
        } catch (Exception e) {
            throw new EJBException("getIndirizzi failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public Indirizzo getIndirizzo(int indirizzoId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, cliente_id, via, civico, citta, cap, provincia, paese, predefinito " +
                "FROM mercato.indirizzo WHERE id = ?");
            ps.setInt(1, indirizzoId);
            rs = ps.executeQuery();
            if (rs.next()) return mapIndirizzo(rs);
        } catch (Exception e) {
            throw new EJBException("getIndirizzo failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    // ----------------------------------------------------------------
    private Cliente mapCliente(ResultSet rs) throws SQLException {
        Cliente c = new Cliente();
        c.setId(rs.getInt(1));
        c.setEmail(rs.getString(2));
        c.setNome(rs.getString(3));
        c.setCognome(rs.getString(4));
        c.setTelefono(rs.getString(5));
        c.setLingua(rs.getString(6));
        c.setAttivo(rs.getBoolean(7));
        return c;
    }

    private Indirizzo mapIndirizzo(ResultSet rs) throws SQLException {
        Indirizzo i = new Indirizzo();
        i.setId(rs.getInt(1));
        i.setClienteId(rs.getInt(2));
        i.setVia(rs.getString(3));
        i.setCivico(rs.getString(4));
        i.setCitta(rs.getString(5));
        i.setCap(rs.getString(6));
        i.setProvincia(rs.getString(7));
        i.setPaese(rs.getString(8));
        i.setPredefinito(rs.getBoolean(9));
        return i;
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
