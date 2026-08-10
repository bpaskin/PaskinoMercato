package it.paskinomercato.model;

import java.io.Serializable;

public class Indirizzo implements Serializable {

    private int     id;
    private int     clienteId;
    private String  via;
    private String  civico;
    private String  citta;
    private String  cap;
    private String  provincia;
    private String  paese;
    private boolean predefinito;

    public int getId()                       { return id; }
    public void setId(int id)               { this.id = id; }

    public int getClienteId()               { return clienteId; }
    public void setClienteId(int c)         { this.clienteId = c; }

    public String getVia()                  { return via; }
    public void setVia(String via)          { this.via = via; }

    public String getCivico()               { return civico; }
    public void setCivico(String civico)    { this.civico = civico; }

    public String getCitta()               { return citta; }
    public void setCitta(String citta)     { this.citta = citta; }

    public String getCap()                 { return cap; }
    public void setCap(String cap)         { this.cap = cap; }

    public String getProvincia()           { return provincia; }
    public void setProvincia(String p)     { this.provincia = p; }

    public String getPaese()               { return paese; }
    public void setPaese(String paese)     { this.paese = paese; }

    public boolean isPredefinito()         { return predefinito; }
    public void setPredefinito(boolean p)  { this.predefinito = p; }

    public String getIndirizzoCompleto() {
        return via + ", " + civico + " - " + cap + " " + citta + " (" + provincia + ")";
    }
}
