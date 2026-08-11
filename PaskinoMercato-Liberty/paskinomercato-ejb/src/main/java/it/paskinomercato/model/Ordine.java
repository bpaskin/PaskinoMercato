package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * JPA entity for mercato.ordine.
 */
@Entity
@Table(name = "ordine", schema = "mercato")
public class Ordine implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "numero_ordine", nullable = false, unique = true, length = 30)
    private String numeroOrdine;

    @Column(name = "cliente_id", nullable = false)
    private int clienteId;

    @Column(name = "indirizzo_id", nullable = false)
    private int indirizzoId;

    @Column(name = "stato", nullable = false, length = 30)
    private String stato;

    @Column(name = "totale", nullable = false, precision = 10, scale = 2)
    private BigDecimal totale;

    @Column(name = "note")
    private String note;

    @Column(name = "email_inviata", nullable = false)
    private boolean emailInviata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Timestamp createdAt;

    public int getId()                         { return id; }
    public void setId(int id)                  { this.id = id; }

    public String getNumeroOrdine()            { return numeroOrdine; }
    public void setNumeroOrdine(String n)      { this.numeroOrdine = n; }

    public int getClienteId()                  { return clienteId; }
    public void setClienteId(int c)            { this.clienteId = c; }

    public int getIndirizzoId()                { return indirizzoId; }
    public void setIndirizzoId(int i)          { this.indirizzoId = i; }

    public String getStato()                   { return stato; }
    public void setStato(String stato)         { this.stato = stato; }

    public BigDecimal getTotale()              { return totale; }
    public void setTotale(BigDecimal totale)   { this.totale = totale; }

    public String getNote()                    { return note; }
    public void setNote(String note)           { this.note = note; }

    public boolean isEmailInviata()            { return emailInviata; }
    public void setEmailInviata(boolean e)     { this.emailInviata = e; }

    public Timestamp getCreatedAt()            { return createdAt; }
    public void setCreatedAt(Timestamp t)      { this.createdAt = t; }
}
