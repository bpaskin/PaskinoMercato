package it.paskinomercato.ejb.entity.ordine;

import jakarta.ejb.Stateless;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import jakarta.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

@Stateless(name = "OrdineEntityBean")
public class OrdineEntityHomeBean implements OrdineEntityService {

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
    public OrdineEntityData create(String numeroOrdine, int clienteId, int indirizzoId,
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
            Integer id = rs.getInt(1);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            return new OrdineEntityDataImpl(id, numeroOrdine, clienteId, indirizzoId, "IN_ATTESA", totale, note, false, now);
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("create ordine failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public OrdineEntityData findByPrimaryKey(Integer pk) throws FinderException {
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
            if (!rs.next()) throw new ObjectNotFoundException("Ordine not found: " + pk);
            return new OrdineEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getInt(3),
                rs.getInt(4),
                rs.getString(5),
                rs.getBigDecimal(6),
                rs.getString(7),
                rs.getBoolean(8),
                rs.getTimestamp(9)
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
    public OrdineEntityData findByNumeroOrdine(String numeroOrdine) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, numero_ordine, cliente_id, indirizzo_id, stato, totale, note, email_inviata, created_at " +
                "FROM mercato.ordine WHERE numero_ordine = ?");
            ps.setString(1, numeroOrdine);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Ordine not found: " + numeroOrdine);
            return new OrdineEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getInt(3),
                rs.getInt(4),
                rs.getString(5),
                rs.getBigDecimal(6),
                rs.getString(7),
                rs.getBoolean(8),
                rs.getTimestamp(9)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByNumeroOrdine failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Collection<OrdineEntityData> findByClienteId(int clienteId) throws FinderException {
        Collection<OrdineEntityData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, numero_ordine, cliente_id, indirizzo_id, stato, totale, note, email_inviata, created_at " +
                "FROM mercato.ordine WHERE cliente_id = ? ORDER BY created_at DESC");
            ps.setInt(1, clienteId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new OrdineEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getInt(3),
                    rs.getInt(4),
                    rs.getString(5),
                    rs.getBigDecimal(6),
                    rs.getString(7),
                    rs.getBoolean(8),
                    rs.getTimestamp(9)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findByClienteId failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }
}
