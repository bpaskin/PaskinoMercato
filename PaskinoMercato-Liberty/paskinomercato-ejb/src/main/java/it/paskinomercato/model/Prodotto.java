package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * JPA entity for mercato.prodotto.
 */
@Entity
@Table(name = "prodotto", schema = "mercato")
public class Prodotto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "codice", nullable = false, unique = true, length = 50)
    private String codice;

    @Column(name = "nome_it", nullable = false, length = 200)
    private String nomeIt;

    @Column(name = "nome_en", nullable = false, length = 200)
    private String nomeEn;

    @Column(name = "descrizione_it")
    private String descrizioneIt;

    @Column(name = "descrizione_en")
    private String descrizioneEn;

    @Column(name = "prezzo", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzo;

    @Column(name = "unita_misura", nullable = false, length = 20)
    private String unitaMisura;

    @Column(name = "quantita_stock", nullable = false)
    private int quantitaStock;

    @Column(name = "categoria_id", nullable = false)
    private int categoriaId;

    @Column(name = "immagine", length = 255)
    private String immagine;

    @Column(name = "attivo", nullable = false)
    private boolean attivo;

    @Column(name = "peso_kg", precision = 6, scale = 3)
    private double pesoKg;

    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getCodice()                 { return codice; }
    public void setCodice(String codice)      { this.codice = codice; }

    public String getNomeIt()                 { return nomeIt; }
    public void setNomeIt(String nomeIt)      { this.nomeIt = nomeIt; }

    public String getNomeEn()                 { return nomeEn; }
    public void setNomeEn(String nomeEn)      { this.nomeEn = nomeEn; }

    public String getDescrizioneIt()          { return descrizioneIt; }
    public void setDescrizioneIt(String d)    { this.descrizioneIt = d; }

    public String getDescrizioneEn()          { return descrizioneEn; }
    public void setDescrizioneEn(String d)    { this.descrizioneEn = d; }

    public BigDecimal getPrezzo()             { return prezzo; }
    public void setPrezzo(BigDecimal prezzo)  { this.prezzo = prezzo; }

    public String getUnitaMisura()            { return unitaMisura; }
    public void setUnitaMisura(String u)      { this.unitaMisura = u; }

    public int getQuantitaStock()             { return quantitaStock; }
    public void setQuantitaStock(int q)       { this.quantitaStock = q; }

    public int getCategoriaId()               { return categoriaId; }
    public void setCategoriaId(int c)         { this.categoriaId = c; }

    public String getImmagine()               { return immagine; }
    public void setImmagine(String immagine)  { this.immagine = immagine; }

    public boolean isAttivo()                 { return attivo; }
    public void setAttivo(boolean attivo)     { this.attivo = attivo; }

    public double getPesoKg()                 { return pesoKg; }
    public void setPesoKg(double pesoKg)      { this.pesoKg = pesoKg; }

    /** Convenience: returns localised name based on lang code */
    public String getNome(String lang) {
        return "en".equalsIgnoreCase(lang) ? nomeEn : nomeIt;
    }

    /** Convenience: returns localised description based on lang code */
    public String getDescrizione(String lang) {
        return "en".equalsIgnoreCase(lang) ? descrizioneEn : descrizioneIt;
    }
}
