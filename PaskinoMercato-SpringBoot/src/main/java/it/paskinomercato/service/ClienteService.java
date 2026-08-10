package it.paskinomercato.service;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class ClienteService {

    private final JdbcTemplate jdbc;

    public ClienteService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public Cliente registra(String email, String passwordHash, String nome,
                             String cognome, String telefono, String lingua) {
        String lang = lingua != null ? lingua : "it";
        // Use RETURNING id for PostgreSQL
        Integer id = jdbc.queryForObject(
            "INSERT INTO mercato.cliente (email, password_hash, nome, cognome, telefono, lingua) " +
            "VALUES (?, ?, ?, ?, ?, ?) RETURNING id",
            Integer.class,
            email, passwordHash, nome, cognome, telefono, lang);
        if (id == null) throw new RuntimeException("Registrazione fallita");
        Cliente c = new Cliente();
        c.setId(id);
        c.setEmail(email);
        c.setNome(nome);
        c.setCognome(cognome);
        c.setTelefono(telefono);
        c.setLingua(lang);
        c.setAttivo(true);
        return c;
    }

    public Cliente login(String email, String passwordHash) {
        List<Cliente> list = jdbc.query(
            "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
            "FROM mercato.cliente WHERE email = ? AND password_hash = ? AND attivo = true",
            (rs, i) -> mapCliente(rs),
            email, passwordHash);
        return list.isEmpty() ? null : list.get(0);
    }

    public Cliente getClienteById(int id) {
        List<Cliente> list = jdbc.query(
            "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
            "FROM mercato.cliente WHERE id = ?",
            (rs, i) -> mapCliente(rs),
            id);
        return list.isEmpty() ? null : list.get(0);
    }

    public Cliente getClienteByEmail(String email) {
        List<Cliente> list = jdbc.query(
            "SELECT id, email, nome, cognome, telefono, lingua, attivo " +
            "FROM mercato.cliente WHERE email = ?",
            (rs, i) -> mapCliente(rs),
            email);
        return list.isEmpty() ? null : list.get(0);
    }

    public void aggiornaLingua(int clienteId, String lingua) {
        jdbc.update("UPDATE mercato.cliente SET lingua = ? WHERE id = ?", lingua, clienteId);
    }

    public void aggiungiIndirizzo(int clienteId, String via, String civico,
                                   String citta, String cap, String provincia) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM mercato.indirizzo WHERE cliente_id = ?",
            Integer.class, clienteId);
        boolean isFirst = count != null && count == 0;
        jdbc.update(
            "INSERT INTO mercato.indirizzo (cliente_id, via, civico, citta, cap, provincia, paese, predefinito) " +
            "VALUES (?, ?, ?, ?, ?, ?, 'IT', ?)",
            clienteId, via, civico, citta, cap, provincia.toUpperCase(), isFirst);
    }

    public List<Indirizzo> getIndirizzi(int clienteId) {
        return jdbc.query(
            "SELECT id, cliente_id, via, civico, citta, cap, provincia, paese, predefinito " +
            "FROM mercato.indirizzo WHERE cliente_id = ? ORDER BY predefinito DESC, id ASC",
            (rs, i) -> mapIndirizzo(rs),
            clienteId);
    }

    public Indirizzo getIndirizzo(int indirizzoId) {
        List<Indirizzo> list = jdbc.query(
            "SELECT id, cliente_id, via, civico, citta, cap, provincia, paese, predefinito " +
            "FROM mercato.indirizzo WHERE id = ?",
            (rs, i) -> mapIndirizzo(rs),
            indirizzoId);
        return list.isEmpty() ? null : list.get(0);
    }

    // ── Mappers ──────────────────────────────────────────────────────────────

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
}
