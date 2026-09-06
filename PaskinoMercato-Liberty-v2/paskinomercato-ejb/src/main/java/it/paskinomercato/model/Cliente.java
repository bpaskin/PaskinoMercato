package it.paskinomercato.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    private int id;

    @Column(name = "email")
    private String email;

    @Column(name = "nome")
    private String nome;

    @Column(name = "cognome")
    private String cognome;

    @Column(name = "telefono")
    private String telefono;

    @Column(name = "lingua")
    private String lingua;

    @Column(name = "attivo")
    private boolean attivo;

    public int getId()                      { return id; }
    public void setId(int id)              { this.id = id; }

    public String getEmail()               { return email; }
    public void setEmail(String email)     { this.email = email; }

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
