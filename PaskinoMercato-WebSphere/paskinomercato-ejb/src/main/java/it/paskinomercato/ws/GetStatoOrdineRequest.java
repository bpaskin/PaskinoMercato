package it.paskinomercato.ws;

import java.io.Serializable;

/**
 * JAX-RPC document/literal wrapped request bean for getStatoOrdine.
 * Maps to the WSDL schema element tns:getStatoOrdineRequest.
 */
public class GetStatoOrdineRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String numeroOrdine;

    public GetStatoOrdineRequest() {}

    public GetStatoOrdineRequest(String numeroOrdine) {
        this.numeroOrdine = numeroOrdine;
    }

    public String getNumeroOrdine()                    { return numeroOrdine; }
    public void setNumeroOrdine(String numeroOrdine)   { this.numeroOrdine = numeroOrdine; }
}
