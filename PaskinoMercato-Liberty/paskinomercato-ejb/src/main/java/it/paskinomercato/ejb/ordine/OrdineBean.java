package it.paskinomercato.ejb.ordine;

import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.model.CarrelloItem;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EJB 2.0 Stateless Session Bean — Ordine.
 * Handles order creation and retrieval via direct JDBC.
 */
public class OrdineBean implements SessionBean {

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

    // ----------------------------------------------------------------
    // creaOrdine — transactional: inserts ordine + righe_ordine + decrements stock
    // ----------------------------------------------------------------
    public String creaOrdine(int clienteId, int indirizzoId, List<CarrelloItem> carrelloItems, String note) {
        if (carrelloItems == null || carrelloItems.isEmpty()) {
            throw new EJBException("Carrello vuoto");
        }
        Connection con = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psRiga   = null;
        PreparedStatement psStock  = null;
        try {
            con = getConnection();

            // Calculate total
            BigDecimal totale = BigDecimal.ZERO;
            for (int i = 0; i < carrelloItems.size(); i++) {
                CarrelloItem item = carrelloItems.get(i);
                totale = totale.add(item.getSubtotale());
            }

            // Generate order number: ORD-YYYYMMDD-HHMMSS-clienteId
            String numeroOrdine = "ORD-" +
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +
                "-" + clienteId;

            // Insert order
            psOrdine = con.prepareStatement(
                "INSERT INTO mercato.ordine (numero_ordine, cliente_id, indirizzo_id, stato, totale, note) " +
                "VALUES (?, ?, ?, 'IN_ATTESA', ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            psOrdine.setString(1, numeroOrdine);
            psOrdine.setInt(2, clienteId);
            psOrdine.setInt(3, indirizzoId);
            psOrdine.setBigDecimal(4, totale);
            psOrdine.setString(5, note);
            psOrdine.executeUpdate();

            ResultSet generatedKeys = psOrdine.getGeneratedKeys();
            if (!generatedKeys.next()) {
                throw new EJBException("Failed to retrieve generated order id");
            }
            int ordineId = generatedKeys.getInt(1);

            // Insert order lines and decrement stock
            psRiga = con.prepareStatement(
                "INSERT INTO mercato.riga_ordine (ordine_id, prodotto_id, quantita, prezzo_unitario) " +
                "VALUES (?, ?, ?, ?)");
            psStock = con.prepareStatement(
                "UPDATE mercato.prodotto SET quantita_stock = quantita_stock - ? WHERE id = ?");

            for (int i = 0; i < carrelloItems.size(); i++) {
                CarrelloItem item = carrelloItems.get(i);
                psRiga.setInt(1, ordineId);
                psRiga.setInt(2, item.getProdottoId());
                psRiga.setInt(3, item.getQuantita());
                psRiga.setBigDecimal(4, item.getPrezzoUnitario());
                psRiga.executeUpdate();

                psStock.setInt(1, item.getQuantita());
                psStock.setInt(2, item.getProdottoId());
                psStock.executeUpdate();
            }

            // Clear persisted cart
            PreparedStatement psCarrello = con.prepareStatement(
                "DELETE FROM mercato.carrello WHERE cliente_id = ?");
            psCarrello.setInt(1, clienteId);
            psCarrello.executeUpdate();
            psCarrello.close();

            return numeroOrdine;

        } catch (Exception e) {
            ctx.setRollbackOnly();
            throw new EJBException("creaOrdine failed: " + e.getMessage(), e);
        } finally {
            closeQuietly(null, psStock, null);
            closeQuietly(null, psRiga, null);
            closeQuietly(null, psOrdine, con);
        }
    }

    public Ordine getOrdineByNumero(String numeroOrdine) {
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
            if (rs.next()) return mapOrdine(rs);
        } catch (Exception e) {
            throw new EJBException("getOrdineByNumero failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public List<Ordine> getOrdiniCliente(int clienteId) {
        List<Ordine> list = new ArrayList<Ordine>();
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
                list.add(mapOrdine(rs));
            }
        } catch (Exception e) {
            throw new EJBException("getOrdiniCliente failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public List<RigaOrdine> getRigheOrdine(int ordineId) {
        List<RigaOrdine> list = new ArrayList<RigaOrdine>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT r.id, r.ordine_id, r.prodotto_id, r.quantita, r.prezzo_unitario, r.subtotale, " +
                "p.nome_it " +
                "FROM mercato.riga_ordine r JOIN mercato.prodotto p ON r.prodotto_id = p.id " +
                "WHERE r.ordine_id = ?");
            ps.setInt(1, ordineId);
            rs = ps.executeQuery();
            while (rs.next()) {
                RigaOrdine riga = new RigaOrdine();
                riga.setId(rs.getInt(1));
                riga.setOrdineId(rs.getInt(2));
                riga.setProdottoId(rs.getInt(3));
                riga.setQuantita(rs.getInt(4));
                riga.setPrezzoUnitario(rs.getBigDecimal(5));
                riga.setSubtotale(rs.getBigDecimal(6));
                riga.setNomeProdotto(rs.getString(7));
                list.add(riga);
            }
        } catch (Exception e) {
            throw new EJBException("getRigheOrdine failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public void aggiornaStato(int ordineId, String nuovoStato) {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "UPDATE mercato.ordine SET stato = ?, updated_at = NOW() WHERE id = ?");
            ps.setString(1, nuovoStato);
            ps.setInt(2, ordineId);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("aggiornaStato failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    // ----------------------------------------------------------------
    private Ordine mapOrdine(ResultSet rs) throws SQLException {
        Ordine o = new Ordine();
        o.setId(rs.getInt(1));
        o.setNumeroOrdine(rs.getString(2));
        o.setClienteId(rs.getInt(3));
        o.setIndirizzoId(rs.getInt(4));
        o.setStato(rs.getString(5));
        o.setTotale(rs.getBigDecimal(6));
        o.setNote(rs.getString(7));
        o.setEmailInviata(rs.getBoolean(8));
        o.setCreatedAt(rs.getTimestamp(9));
        return o;
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
