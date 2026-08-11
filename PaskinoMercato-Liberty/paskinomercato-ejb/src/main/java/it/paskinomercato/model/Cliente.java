package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * JPA entity for mercato.cliente.
 */
@Entity
@Table(name = "cliente", schema = "mercato")
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "email", nullable = false, unique = true, length = 200)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nome", nullable = false, length = 100)
    private String nome;

    @Column(name = "cognome", nullable = false, length = 100)
    private String cognome;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "lingua", nullable = false, length = 2)
    private String lingua;

    @Column(name = "attivo", nullable = false)
    private boolean attivo;

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

    public String getPasswordHash()        { return passwordHash; }
    public void setPasswordHash(String p)  { this.passwordHash = p; }

    public String getNome()                { return nome; }
    public void setNome(String nome)       { this.nome = nome; }

    public String getCognome()             { return cognome; }
    public void setCognome(String c)       { this.cognome = c; }

    public String getTelefono()            { return telefono; }
    public void setTelefono(String t)      { this.telefono = t; }

    public String getLingua()              { return lingua; }
    public void setLingua(String lingua)   { this.lingua = lingua; }

    public boolean isAttivo()              { return attivo; }
    public void setAttivo(boolean attivo)  { this.attivo = attivo; }

    public String getNomeCompleto()        { return nome + " " + cognome; }
}
