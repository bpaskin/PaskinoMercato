package it.paskinomercato.model;

import java.io.Serializable;

public class Categoria implements Serializable {

    private int    id;
    private String codice;
    private String nomeIt;
    private String nomeEn;
    private String descrizioneIt;
    private String descrizioneEn;
    private String immagine;

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getCodice()                 { return codice; }
    public void setCodice(String codice)      { this.codice = codice; }

    public String getNomeIt()                 { return nomeIt; }
    public void setNomeIt(String n)           { this.nomeIt = n; }

    public String getNomeEn()                 { return nomeEn; }
    public void setNomeEn(String n)           { this.nomeEn = n; }

    public String getDescrizioneIt()          { return descrizioneIt; }
    public void setDescrizioneIt(String d)    { this.descrizioneIt = d; }

    public String getDescrizioneEn()          { return descrizioneEn; }
    public void setDescrizioneEn(String d)    { this.descrizioneEn = d; }

    public String getImmagine()               { return immagine; }
    public void setImmagine(String immagine)  { this.immagine = immagine; }

    public String getNome(String lang) {
        return "en".equalsIgnoreCase(lang) ? nomeEn : nomeIt;
    }
}
