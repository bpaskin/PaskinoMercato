package it.paskinomercato.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

public class Ordine implements Serializable {

    private int       id;
    private String    numeroOrdine;
    private int       clienteId;
    private int       indirizzoId;
    private String    stato;
    private BigDecimal totale;
    private String    note;
    private boolean   emailInviata;
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
