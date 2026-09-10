package it.paskinomercato.model;

import java.io.Serializable;

/**
 * Value object for mercato.cliente.
 */
public class Cliente implements Serializable {

    private static final long serialVersionUID = 1L;

    private int    id;
    private String email;
    private String nome;
    private String cognome;
    private String telefono;
    private String lingua;
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
