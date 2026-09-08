package it.paskinomercato.ejb.cliente;

import it.paskinomercato.ejb.entity.cliente.ClienteEntityData;
import it.paskinomercato.ejb.entity.cliente.ClienteEntityService;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;

import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * EJB 2.0 Stateless Session Bean — Cliente.
 * Customer lookup/registration delegates to ClienteEntityBean (BMP Entity EJB).
 * Address operations (indirizzo table) remain as direct JDBC — no entity bean exists for that table.
 */
public class ClienteBean implements SessionBean {

    private SessionContext ctx;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    // ----------------------------------------------------------------
    // Entity home + DataSource lookups
    // ----------------------------------------------------------------
    private ClienteEntityService getClienteHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (ClienteEntityService) ic.lookup(
            "java:comp/env/ejb/ClienteEntityBean");
    }

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    // ----------------------------------------------------------------
    // Business methods — customer
    // ----------------------------------------------------------------

    public Cliente registra(String email, String passwordHash, String nome,
                             String cognome, String telefono, String lingua) {
        try {
            ClienteEntityService home = getClienteHome();
            ClienteEntityData entity = home.create(email, passwordHash, nome, cognome, telefono, lingua);
            return toValueObject(entity);
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("registra failed: " + e.getMessage(), e);
        }
    }

    public Cliente login(String email, String passwordHash) {
        // Authentication requires checking password_hash — use direct JDBC query
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
        try {
            ClienteEntityService home = getClienteHome();
            ClienteEntityData entity = home.findByPrimaryKey(id);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getClienteById failed", e);
        }
    }

    public Cliente getClienteByEmail(String email) {
        try {
            ClienteEntityService home = getClienteHome();
            ClienteEntityData entity = home.findByEmail(email);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getClienteByEmail failed", e);
        }
    }

    public void aggiornaLingua(int clienteId, String lingua) {
        try {
            ClienteEntityService home = getClienteHome();
            ClienteEntityData entity = home.findByPrimaryKey(clienteId);
            entity.setLingua(lingua);
            // ejbStore() called by container at transaction commit
        } catch (Exception e) {
            throw new EJBException("aggiornaLingua failed", e);
        }
    }

    // ----------------------------------------------------------------
    // Address methods — JDBC (no entity bean for indirizzo)
    // ----------------------------------------------------------------

    public void aggiungiIndirizzo(int clienteId, String via, String civico,
                                   String citta, String cap, String provincia) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
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
    // Mappers
    // ----------------------------------------------------------------
    private Cliente toValueObject(ClienteEntityData e) {
        Cliente c = new Cliente();
        c.setId(e.getId());
        c.setEmail(e.getEmail());
        c.setNome(e.getNome());
        c.setCognome(e.getCognome());
        c.setTelefono(e.getTelefono());
        c.setLingua(e.getLingua());
        c.setAttivo(e.isAttivo());
        return c;
    }

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
        try { if (rs  != null) rs.close();  } catch (Exception _) {}
        try { if (st  != null) st.close();  } catch (Exception _) {}
        try { if (con != null) con.close(); } catch (Exception _) {}
    }
}
