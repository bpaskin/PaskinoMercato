package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * JPA entity for mercato.categoria.
 */
@Entity
@Table(name = "categoria", schema = "mercato")
public class Categoria implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codice", nullable = false, unique = true, length = 50)
    private String codice;

    @Column(name = "nome_it", nullable = false, length = 100)
    private String nomeIt;

    @Column(name = "nome_en", nullable = false, length = 100)
    private String nomeEn;

    @Column(name = "descrizione_it")
    private String descrizioneIt;

    @Column(name = "descrizione_en")
    private String descrizioneEn;

    @Column(name = "immagine", length = 255)
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
