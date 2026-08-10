package it.paskinomercato.service;

import it.paskinomercato.model.Categoria;
import it.paskinomercato.model.Prodotto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CatalogoService {

    private static final int PAGINA_DIM = 24;

    private final JdbcTemplate jdbc;

    public CatalogoService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        int offset = (pagina - 1) * dimensionePagina;
        return jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
            "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
            "FROM mercato.prodotto WHERE attivo = true ORDER BY nome_it LIMIT ? OFFSET ?",
            (rs, i) -> mapProdotto(rs),
            dimensionePagina, offset);
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        int offset = (pagina - 1) * dimensionePagina;
        return jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
            "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
            "FROM mercato.prodotto WHERE attivo = true AND categoria_id = ? ORDER BY nome_it LIMIT ? OFFSET ?",
            (rs, i) -> mapProdotto(rs),
            categoriaId, dimensionePagina, offset);
    }

    public Prodotto getProdottoById(int id) {
        List<Prodotto> list = jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
            "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
            "FROM mercato.prodotto WHERE id = ?",
            (rs, i) -> mapProdotto(rs),
            id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Prodotto getProdottoByCodice(String codice) {
        List<Prodotto> list = jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
            "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
            "FROM mercato.prodotto WHERE codice = ?",
            (rs, i) -> mapProdotto(rs),
            codice);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Prodotto> cercaProdotti(String testo) {
        String pattern = "%" + testo.toLowerCase() + "%";
        return jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
            "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
            "FROM mercato.prodotto WHERE attivo = true " +
            "AND (LOWER(nome_it) LIKE ? OR LOWER(nome_en) LIKE ? OR LOWER(codice) LIKE ?) " +
            "ORDER BY nome_it LIMIT 100",
            (rs, i) -> mapProdotto(rs),
            pattern, pattern, pattern);
    }

    public List<Categoria> getCategorie() {
        return jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
            "FROM mercato.categoria ORDER BY nome_it",
            (rs, i) -> mapCategoria(rs));
    }

    public Categoria getCategoriaById(int id) {
        List<Categoria> list = jdbc.query(
            "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
            "FROM mercato.categoria WHERE id = ?",
            (rs, i) -> mapCategoria(rs),
            id);
        return list.isEmpty() ? null : list.get(0);
    }

    public int contaProdotti() {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM mercato.prodotto WHERE attivo = true", Integer.class);
        return count != null ? count : 0;
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM mercato.prodotto WHERE attivo = true AND categoria_id = ?",
            Integer.class, categoriaId);
        return count != null ? count : 0;
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        Integer stock = jdbc.queryForObject(
            "SELECT quantita_stock FROM mercato.prodotto WHERE id = ? AND attivo = true",
            Integer.class, prodottoId);
        return stock != null && stock >= quantita;
    }

    public int getPaginaDim() {
        return PAGINA_DIM;
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

    private Prodotto mapProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getInt(1));
        p.setCodice(rs.getString(2));
        p.setNomeIt(rs.getString(3));
        p.setNomeEn(rs.getString(4));
        p.setDescrizioneIt(rs.getString(5));
        p.setDescrizioneEn(rs.getString(6));
        p.setPrezzo(rs.getBigDecimal(7));
        p.setUnitaMisura(rs.getString(8));
        p.setQuantitaStock(rs.getInt(9));
        p.setCategoriaId(rs.getInt(10));
        p.setImmagine(rs.getString(11));
        p.setAttivo(rs.getBoolean(12));
        p.setPesoKg(rs.getDouble(13));
        return p;
    }

    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt(1));
        c.setCodice(rs.getString(2));
        c.setNomeIt(rs.getString(3));
        c.setNomeEn(rs.getString(4));
        c.setDescrizioneIt(rs.getString(5));
        c.setDescrizioneEn(rs.getString(6));
        c.setImmagine(rs.getString(7));
        return c;
    }
}
