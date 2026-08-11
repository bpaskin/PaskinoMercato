package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * JPA entity for mercato.riga_ordine.
 * subtotale is a generated (stored) column in PostgreSQL;
 * it is mapped insertable=false, updatable=false so JPA never writes it.
 */
@Entity
@Table(name = "riga_ordine", schema = "mercato")
public class RigaOrdine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ordine_id", nullable = false)
    private int ordineId;

    @Column(name = "prodotto_id", nullable = false)
    private int prodottoId;

    @Column(name = "quantita", nullable = false)
    private int quantita;

    @Column(name = "prezzo_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal prezzoUnitario;

    /** Generated stored column — never written by JPA. */
    @Column(name = "subtotale", precision = 10, scale = 2,
            insertable = false, updatable = false)
    private BigDecimal subtotale;

    /** Denormalised display field — not persisted, populated by service queries. */
    @Transient
    private String nomeProdotto;

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
