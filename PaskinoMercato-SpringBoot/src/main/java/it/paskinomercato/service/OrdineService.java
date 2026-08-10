package it.paskinomercato.service;

import it.paskinomercato.model.CarrelloItem;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class OrdineService {

    private final JdbcTemplate jdbc;

    public OrdineService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Transactional
    public String creaOrdine(int clienteId, int indirizzoId,
                              List<CarrelloItem> carrelloItems, String note) {
        if (carrelloItems == null || carrelloItems.isEmpty()) {
            throw new IllegalArgumentException("Carrello vuoto");
        }

        BigDecimal totale = carrelloItems.stream()
            .map(CarrelloItem::getSubtotale)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        String numeroOrdine = "ORD-" +
            new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date()) +
            "-" + clienteId;

        Integer ordineId = jdbc.queryForObject(
            "INSERT INTO mercato.ordine (numero_ordine, cliente_id, indirizzo_id, stato, totale, note) " +
            "VALUES (?, ?, ?, 'IN_ATTESA', ?, ?) RETURNING id",
            Integer.class,
            numeroOrdine, clienteId, indirizzoId, totale, note);

        if (ordineId == null) throw new RuntimeException("Failed to retrieve generated order id");

        for (CarrelloItem item : carrelloItems) {
            jdbc.update(
                "INSERT INTO mercato.riga_ordine (ordine_id, prodotto_id, quantita, prezzo_unitario) " +
                "VALUES (?, ?, ?, ?)",
                ordineId, item.getProdottoId(), item.getQuantita(), item.getPrezzoUnitario());
            jdbc.update(
                "UPDATE mercato.prodotto SET quantita_stock = quantita_stock - ? WHERE id = ?",
                item.getQuantita(), item.getProdottoId());
        }

        jdbc.update("DELETE FROM mercato.carrello WHERE cliente_id = ?", clienteId);

        return numeroOrdine;
    }

    public Ordine getOrdineByNumero(String numeroOrdine) {
        List<Ordine> list = jdbc.query(
            "SELECT id, numero_ordine, cliente_id, indirizzo_id, stato, totale, note, email_inviata, created_at " +
            "FROM mercato.ordine WHERE numero_ordine = ?",
            (rs, i) -> mapOrdine(rs),
            numeroOrdine);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Ordine> getOrdiniCliente(int clienteId) {
        return jdbc.query(
            "SELECT id, numero_ordine, cliente_id, indirizzo_id, stato, totale, note, email_inviata, created_at " +
            "FROM mercato.ordine WHERE cliente_id = ? ORDER BY created_at DESC",
            (rs, i) -> mapOrdine(rs),
            clienteId);
    }

    public List<RigaOrdine> getRigheOrdine(int ordineId) {
        return jdbc.query(
            "SELECT r.id, r.ordine_id, r.prodotto_id, r.quantita, r.prezzo_unitario, r.subtotale, p.nome_it " +
            "FROM mercato.riga_ordine r JOIN mercato.prodotto p ON r.prodotto_id = p.id " +
            "WHERE r.ordine_id = ?",
            (rs, i) -> {
                RigaOrdine riga = new RigaOrdine();
                riga.setId(rs.getInt(1));
                riga.setOrdineId(rs.getInt(2));
                riga.setProdottoId(rs.getInt(3));
                riga.setQuantita(rs.getInt(4));
                riga.setPrezzoUnitario(rs.getBigDecimal(5));
                riga.setSubtotale(rs.getBigDecimal(6));
                riga.setNomeProdotto(rs.getString(7));
                return riga;
            },
            ordineId);
    }

    public void aggiornaStato(int ordineId, String nuovoStato) {
        jdbc.update("UPDATE mercato.ordine SET stato = ?, updated_at = NOW() WHERE id = ?",
            nuovoStato, ordineId);
    }

    public void segnaEmailInviata(int ordineId) {
        jdbc.update("UPDATE mercato.ordine SET email_inviata = true WHERE id = ?", ordineId);
    }

    // ── Mapper ───────────────────────────────────────────────────────────────

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
}
