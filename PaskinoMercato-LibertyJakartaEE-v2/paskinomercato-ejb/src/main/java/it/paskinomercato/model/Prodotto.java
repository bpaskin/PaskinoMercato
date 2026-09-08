package it.paskinomercato.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Value object representing a product row from mercato.prodotto.
 * No JPA - populated manually from JDBC ResultSet.
 */
public class Prodotto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int    id;
    private String codice;
    private String nomeIt;
    private String nomeEn;
    private String descrizioneIt;
    private String descrizioneEn;
    private BigDecimal prezzo;
    private String unitaMisura;
    private int    quantitaStock;
    private int    categoriaId;
    private String immagine;
    private boolean attivo;
    private double  pesoKg;

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
