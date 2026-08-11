package it.paskinomercato.ejb.ordine;

import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.model.CarrelloItem;
import it.paskinomercato.model.Prodotto;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * CDI ApplicationScoped service — Ordine.
 * Handles order creation and retrieval via JPA EntityManager.
 */
@ApplicationScoped
public class OrdineBean implements OrdineService {

    @PersistenceContext(unitName = "MercatoPU")
    private EntityManager em;

    @Transactional
    public String creaOrdine(int clienteId, int indirizzoId, List<CarrelloItem> carrelloItems, String note) {
        if (carrelloItems == null || carrelloItems.isEmpty()) {
            throw new RuntimeException("Carrello vuoto");
        }

        BigDecimal totale = BigDecimal.ZERO;
        for (int i = 0; i < carrelloItems.size(); i++) {
            totale = totale.add(carrelloItems.get(i).getSubtotale());
        }

        String numeroOrdine = "ORD-" +
            new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +
            "-" + clienteId;

        Ordine ordine = new Ordine();
        ordine.setNumeroOrdine(numeroOrdine);
        ordine.setClienteId(clienteId);
        ordine.setIndirizzoId(indirizzoId);
        ordine.setStato("IN_ATTESA");
        ordine.setTotale(totale);
        ordine.setNote(note);
        ordine.setEmailInviata(false);
        ordine.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        em.persist(ordine);
        em.flush(); // populate generated id

        for (int i = 0; i < carrelloItems.size(); i++) {
            CarrelloItem item = carrelloItems.get(i);

            RigaOrdine riga = new RigaOrdine();
            riga.setOrdineId(ordine.getId());
            riga.setProdottoId(item.getProdottoId());
            riga.setQuantita(item.getQuantita());
            riga.setPrezzoUnitario(item.getPrezzoUnitario());
            em.persist(riga);

            // decrement stock
            Prodotto p = em.find(Prodotto.class, item.getProdottoId());
            if (p != null) {
                p.setQuantitaStock(p.getQuantitaStock() - item.getQuantita());
            }
        }

        // clear legacy persisted cart table if present (no-op if table absent)
        em.createNativeQuery(
            "DELETE FROM mercato.carrello WHERE cliente_id = ?1")
            .setParameter(1, clienteId)
            .executeUpdate();

        return numeroOrdine;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Ordine getOrdineByNumero(String numeroOrdine) {
        List<Ordine> results = em.createQuery(
            "SELECT o FROM Ordine o WHERE o.numeroOrdine = :num",
            Ordine.class)
            .setParameter("num", numeroOrdine)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Ordine> getOrdiniCliente(int clienteId) {
        return em.createQuery(
            "SELECT o FROM Ordine o WHERE o.clienteId = :cid ORDER BY o.createdAt DESC",
            Ordine.class)
            .setParameter("cid", clienteId)
            .getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<RigaOrdine> getRigheOrdine(int ordineId) {
        // Use a native query to join prodotto and populate the @Transient nomeProdotto field
        @SuppressWarnings("unchecked")
        List<Object[]> rows = em.createNativeQuery(
            "SELECT r.id, r.ordine_id, r.prodotto_id, r.quantita, r.prezzo_unitario, r.subtotale, p.nome_it " +
            "FROM mercato.riga_ordine r JOIN mercato.prodotto p ON r.prodotto_id = p.id " +
            "WHERE r.ordine_id = ?1")
            .setParameter(1, ordineId)
            .getResultList();

        List<RigaOrdine> list = new java.util.ArrayList<RigaOrdine>();
        for (Object[] row : rows) {
            RigaOrdine riga = new RigaOrdine();
            riga.setId(((Number) row[0]).intValue());
            riga.setOrdineId(((Number) row[1]).intValue());
            riga.setProdottoId(((Number) row[2]).intValue());
            riga.setQuantita(((Number) row[3]).intValue());
            riga.setPrezzoUnitario((BigDecimal) row[4]);
            riga.setSubtotale((BigDecimal) row[5]);
            riga.setNomeProdotto((String) row[6]);
            list.add(riga);
        }
        return list;
    }

    @Transactional
    public void aggiornaStato(int ordineId, String nuovoStato) {
        Ordine o = em.find(Ordine.class, ordineId);
        if (o != null) {
            o.setStato(nuovoStato);
        }
    }
}
