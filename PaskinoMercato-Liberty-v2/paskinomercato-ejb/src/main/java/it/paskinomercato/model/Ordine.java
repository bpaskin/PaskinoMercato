package it.paskinomercato.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
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
    private int id;

    @Column(name = "numero_ordine")
    private String numeroOrdine;

    @Column(name = "cliente_id")
    private int clienteId;

    @Column(name = "indirizzo_id")
    private int indirizzoId;

    @Column(name = "stato")
    private String stato;

    @Column(name = "totale")
    private BigDecimal totale;

    @Column(name = "note")
    private String note;

    @Column(name = "email_inviata")
    private boolean emailInviata;

    @Column(name = "created_at", insertable = false, updatable = false)
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
