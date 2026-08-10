package it.paskinomercato.model;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Cart item - used both for session cart and persisted carrello rows.
 */
public class CarrelloItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private int        prodottoId;
    private String     nomeProdotto;
    private int        quantita;
    private BigDecimal prezzoUnitario;
    private String     immagine;

    public CarrelloItem() {}

    public CarrelloItem(int prodottoId, String nomeProdotto, int quantita, BigDecimal prezzoUnitario, String immagine) {
        this.prodottoId     = prodottoId;
        this.nomeProdotto   = nomeProdotto;
        this.quantita       = quantita;
        this.prezzoUnitario = prezzoUnitario;
        this.immagine       = immagine;
    }

    public int getProdottoId()                   { return prodottoId; }
    public void setProdottoId(int p)             { this.prodottoId = p; }

    public String getNomeProdotto()              { return nomeProdotto; }
    public void setNomeProdotto(String n)        { this.nomeProdotto = n; }

    public int getQuantita()                     { return quantita; }
    public void setQuantita(int q)               { this.quantita = q; }

    public BigDecimal getPrezzoUnitario()        { return prezzoUnitario; }
    public void setPrezzoUnitario(BigDecimal p)  { this.prezzoUnitario = p; }

    public String getImmagine()                  { return immagine; }
    public void setImmagine(String i)            { this.immagine = i; }

    public BigDecimal getSubtotale() {
        if (prezzoUnitario == null) return BigDecimal.ZERO;
        return prezzoUnitario.multiply(new BigDecimal(quantita));
    }
}
