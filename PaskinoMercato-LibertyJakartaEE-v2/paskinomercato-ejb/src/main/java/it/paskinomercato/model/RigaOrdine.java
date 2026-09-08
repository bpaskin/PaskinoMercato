package it.paskinomercato.model;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Value object for mercato.riga_ordine.
 */
public class RigaOrdine implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int        id;
    private int        ordineId;
    private int        prodottoId;
    private int        quantita;
    private BigDecimal prezzoUnitario;
    private BigDecimal subtotale;
    private String     nomeProdotto;  // denormalised for display

    public int getId()                               { return id; }
    public void setId(int id)                        { this.id = id; }

    public int getOrdineId()                         { return ordineId; }
    public void setOrdineId(int o)                   { this.ordineId = o; }

    public int getProdottoId()                       { return prodottoId; }
    public void setProdottoId(int p)                 { this.prodottoId = p; }

    public int getQuantita()                         { return quantita; }
    public void setQuantita(int q)                   { this.quantita = q; }

    public BigDecimal getPrezzoUnitario()            { return prezzoUnitario; }
    public void setPrezzoUnitario(BigDecimal p)      { this.prezzoUnitario = p; }

    public BigDecimal getSubtotale()                 { return subtotale; }
    public void setSubtotale(BigDecimal s)           { this.subtotale = s; }

    public String getNomeProdotto()                  { return nomeProdotto; }
    public void setNomeProdotto(String n)            { this.nomeProdotto = n; }
}
