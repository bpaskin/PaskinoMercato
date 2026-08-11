package it.paskinomercato.model;

import javax.persistence.*;
import java.io.Serializable;

/**
 * JPA entity for mercato.indirizzo.
 */
@Entity
@Table(name = "indirizzo", schema = "mercato")
public class Indirizzo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "cliente_id", nullable = false)
    private int clienteId;

    @Column(name = "via", nullable = false, length = 255)
    private String via;

    @Column(name = "civico", nullable = false, length = 20)
    private String civico;

    @Column(name = "citta", nullable = false, length = 100)
    private String citta;

    @Column(name = "cap", nullable = false, length = 5)
    private String cap;

    @Column(name = "provincia", nullable = false, length = 2)
    private String provincia;

    @Column(name = "paese", nullable = false, length = 2)
    private String paese;

    @Column(name = "predefinito", nullable = false)
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

    /** Full formatted address string */
    public String getIndirizzoCompleto() {
        return via + ", " + civico + " - " + cap + " " + citta + " (" + provincia + ")";
    }
}
