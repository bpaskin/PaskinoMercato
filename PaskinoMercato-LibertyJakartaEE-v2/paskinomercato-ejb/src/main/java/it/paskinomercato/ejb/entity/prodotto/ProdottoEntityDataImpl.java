package it.paskinomercato.ejb.entity.prodotto;

import java.math.BigDecimal;

/**
 * Modernized POJO implementation of ProdottoEntityLocal.
 */
public class ProdottoEntityDataImpl implements ProdottoEntityData {

    private Integer    id;
    private String     codice;
    private String     nomeIt;
    private String     nomeEn;
    private String     descrizioneIt;
    private String     descrizioneEn;
    private BigDecimal prezzo;
    private String     unitaMisura;
    private int        quantitaStock;
    private int        categoriaId;
    private String     immagine;
    private boolean    attivo;
    private double     pesoKg;

    public ProdottoEntityDataImpl(Integer id, String codice, String nomeIt, String nomeEn,
                                   String descrizioneIt, String descrizioneEn, BigDecimal prezzo,
                                   String unitaMisura, int quantitaStock, int categoriaId,
                                   String immagine, boolean attivo, double pesoKg) {
        this.id = id;
        this.codice = codice;
        this.nomeIt = nomeIt;
        this.nomeEn = nomeEn;
        this.descrizioneIt = descrizioneIt;
        this.descrizioneEn = descrizioneEn;
        this.prezzo = prezzo;
        this.unitaMisura = unitaMisura;
        this.quantitaStock = quantitaStock;
        this.categoriaId = categoriaId;
        this.immagine = immagine;
        this.attivo = attivo;
        this.pesoKg = pesoKg;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getCodice() { return codice; }

    @Override
    public void setCodice(String codice) { this.codice = codice; }

    @Override
    public String getNomeIt() { return nomeIt; }

    @Override
    public void setNomeIt(String nomeIt) { this.nomeIt = nomeIt; }

    @Override
    public String getNomeEn() { return nomeEn; }

    @Override
    public void setNomeEn(String nomeEn) { this.nomeEn = nomeEn; }

    @Override
    public String getDescrizioneIt() { return descrizioneIt; }

    @Override
    public void setDescrizioneIt(String descrizioneIt) { this.descrizioneIt = descrizioneIt; }

    @Override
    public String getDescrizioneEn() { return descrizioneEn; }

    @Override
    public void setDescrizioneEn(String descrizioneEn) { this.descrizioneEn = descrizioneEn; }

    @Override
    public BigDecimal getPrezzo() { return prezzo; }

    @Override
    public void setPrezzo(BigDecimal prezzo) { this.prezzo = prezzo; }

    @Override
    public String getUnitaMisura() { return unitaMisura; }

    @Override
    public void setUnitaMisura(String unitaMisura) { this.unitaMisura = unitaMisura; }

    @Override
    public int getQuantitaStock() { return quantitaStock; }

    @Override
    public void setQuantitaStock(int quantitaStock) { this.quantitaStock = quantitaStock; }

    @Override
    public int getCategoriaId() { return categoriaId; }

    @Override
    public void setCategoriaId(int categoriaId) { this.categoriaId = categoriaId; }

    @Override
    public String getImmagine() { return immagine; }

    @Override
    public void setImmagine(String immagine) { this.immagine = immagine; }

    @Override
    public boolean isAttivo() { return attivo; }

    @Override
    public void setAttivo(boolean attivo) { this.attivo = attivo; }

    @Override
    public double getPesoKg() { return pesoKg; }

    @Override
    public void setPesoKg(double pesoKg) { this.pesoKg = pesoKg; }
}
