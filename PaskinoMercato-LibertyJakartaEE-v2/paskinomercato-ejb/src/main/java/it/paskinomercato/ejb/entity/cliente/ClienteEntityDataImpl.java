package it.paskinomercato.ejb.entity.cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Modernized POJO implementation of ClienteEntityLocal.
 */
public class ClienteEntityDataImpl implements ClienteEntityData {

    private Integer id;
    private String  email;
    private String  nome;
    private String  cognome;
    private String  telefono;
    private String  lingua;
    private boolean attivo;

    public ClienteEntityDataImpl(Integer id, String email, String nome, String cognome,
                                  String telefono, String lingua, boolean attivo) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.telefono = telefono;
        this.lingua = lingua;
        this.attivo = attivo;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getEmail() { return email; }

    @Override
    public void setEmail(String email) { this.email = email; }

    @Override
    public String getNome() { return nome; }

    @Override
    public void setNome(String nome) { this.nome = nome; }

    @Override
    public String getCognome() { return cognome; }

    @Override
    public void setCognome(String cognome) { this.cognome = cognome; }

    @Override
    public String getTelefono() { return telefono; }

    @Override
    public void setTelefono(String telefono) { this.telefono = telefono; }

    @Override
    public String getLingua() { return lingua; }

    @Override
    public void setLingua(String lingua) {
        this.lingua = lingua;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("jdbc/MercatoDB");
            con = ds.getConnection();
            ps = con.prepareStatement("UPDATE mercato.cliente SET lingua = ? WHERE id = ?");
            ps.setString(1, lingua);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update lingua in database", e);
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public boolean isAttivo() { return attivo; }

    @Override
    public void setAttivo(boolean attivo) { this.attivo = attivo; }
}
