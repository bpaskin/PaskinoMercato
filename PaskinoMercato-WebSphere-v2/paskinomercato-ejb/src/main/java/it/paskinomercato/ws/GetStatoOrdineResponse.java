package it.paskinomercato.ws;

import java.io.Serializable;

/**
 * JAX-RPC document/literal wrapped response bean for getStatoOrdine.
 * Maps to the WSDL schema element tns:getStatoOrdineResponse.
 */
public class GetStatoOrdineResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private String _return;

    public GetStatoOrdineResponse() {}

    public GetStatoOrdineResponse(String _return) {
        this._return = _return;
    }

    public String get_return()               { return _return; }
    public void set_return(String _return)   { this._return = _return; }
}
