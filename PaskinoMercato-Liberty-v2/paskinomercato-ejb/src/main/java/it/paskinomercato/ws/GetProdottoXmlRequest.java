package it.paskinomercato.ws;

import java.io.Serializable;

/**
 * JAX-RPC document/literal wrapped request bean for getProdottoXml.
 * Maps to the WSDL schema element tns:getProdottoXmlRequest.
 */
public class GetProdottoXmlRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String codice;

    public GetProdottoXmlRequest() {}

    public GetProdottoXmlRequest(String codice) {
        this.codice = codice;
    }

    public String getCodice()              { return codice; }
    public void setCodice(String codice)   { this.codice = codice; }
}
