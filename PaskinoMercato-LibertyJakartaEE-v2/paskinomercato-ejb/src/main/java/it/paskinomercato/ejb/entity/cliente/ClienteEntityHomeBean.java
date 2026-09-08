package it.paskinomercato.ejb.entity.cliente;

import jakarta.ejb.Stateless;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import jakarta.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

@Stateless(name = "ClienteEntityBean")
public class ClienteEntityHomeBean implements ClienteEntityService {

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/MercatoDB");
        return ds.getConnection();
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }

    @Override
    public ClienteEntityData create(String email, String passwordHash, String nome,
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
            Integer id = rs.getInt(1);
            return new ClienteEntityDataImpl(id, email, nome, cognome, telefono, lingua != null ? lingua : "it", true);
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("create cliente failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public ClienteEntityData findByPrimaryKey(Integer pk) throws FinderException {
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
            if (!rs.next()) throw new ObjectNotFoundException("Cliente not found: " + pk);
            return new ClienteEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getBoolean(7)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public ClienteEntityData findByEmail(String email) throws FinderException {
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
            if (!rs.next()) throw new ObjectNotFoundException("Cliente not found by email: " + email);
            return new ClienteEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getBoolean(7)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByEmail failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Collection<ClienteEntityData> findByAttivo(boolean attivo) throws FinderException {
        Collection<ClienteEntityData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
                "FROM mercato.cliente WHERE attivo = ?");
            ps.setBoolean(1, attivo);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ClienteEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getBoolean(7)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findByAttivo failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }
}
