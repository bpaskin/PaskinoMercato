package it.paskinomercato.ejb.ordine;

import it.paskinomercato.ejb.entity.ordine.OrdineEntityData;
import it.paskinomercato.ejb.entity.ordine.OrdineEntityService;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.model.CarrelloItem;

import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * EJB 2.0 Stateless Session Bean — Ordine.
 * Single-ordine lookups and status updates delegate to OrdineEntityBean (BMP Entity EJB).
 * creaOrdine remains here because it spans multiple tables in one transaction.
 */
public class OrdineBean implements SessionBean {

    private SessionContext ctx;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    // ----------------------------------------------------------------
    // Lookups
    // ----------------------------------------------------------------
    private OrdineEntityService getOrdineHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (OrdineEntityService) ic.lookup(
            "java:comp/env/ejb/OrdineEntityBean");
    }

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    // ----------------------------------------------------------------
    // creaOrdine — transactional: inserts ordine + righe_ordine + decrements stock
    // The entity bean is used only to validate that the new ordine was created.
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

            BigDecimal totale = BigDecimal.ZERO;
            for (int i = 0; i < carrelloItems.size(); i++) {
                totale = totale.add(carrelloItems.get(i).getSubtotale());
            }

            String numeroOrdine = "ORD-" +
                new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +
                "-" + clienteId;

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

    // ----------------------------------------------------------------
    // Lookup via Entity Bean
    // ----------------------------------------------------------------

    public Ordine getOrdineByNumero(String numeroOrdine) {
        try {
            OrdineEntityService home = getOrdineHome();
            OrdineEntityData entity = home.findByNumeroOrdine(numeroOrdine);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getOrdineByNumero failed", e);
        }
    }

    public List<Ordine> getOrdiniCliente(int clienteId) {
        try {
            OrdineEntityService home = getOrdineHome();
            Collection<OrdineEntityData> col =
                (Collection<OrdineEntityData>) home.findByClienteId(clienteId);
            List<Ordine> list = new ArrayList<Ordine>();
            for (Iterator<OrdineEntityData> it = col.iterator(); it.hasNext();) {
                list.add(toValueObject(it.next()));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("getOrdiniCliente failed", e);
        }
    }

    public List<RigaOrdine> getRigheOrdine(int ordineId) {
        // No entity bean for riga_ordine — JDBC remains
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
        try {
            OrdineEntityService home = getOrdineHome();
            OrdineEntityData entity = home.findByPrimaryKey(ordineId);
            entity.setStato(nuovoStato);
            // ejbStore() called by container at transaction commit
        } catch (FinderException fe) {
            throw new EJBException("aggiornaStato: ordine not found " + ordineId, fe);
        } catch (Exception e) {
            throw new EJBException("aggiornaStato failed", e);
        }
    }

    // ----------------------------------------------------------------
    private Ordine toValueObject(OrdineEntityData e) {
        Ordine o = new Ordine();
        o.setId(e.getId());
        o.setNumeroOrdine(e.getNumeroOrdine());
        o.setClienteId(e.getClienteId());
        o.setIndirizzoId(e.getIndirizzoId());
        o.setStato(e.getStato());
        o.setTotale(e.getTotale());
        o.setNote(e.getNote());
        o.setEmailInviata(e.isEmailInviata());
        o.setCreatedAt(e.getCreatedAt());
        return o;
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception _) {}
        try { if (st  != null) st.close();  } catch (Exception _) {}
        try { if (con != null) con.close(); } catch (Exception _) {}
    }
}
