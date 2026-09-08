package it.paskinomercato.ws;

import java.io.Serial;
import java.io.Serializable;

/**
 * JAX-RPC document/literal wrapped response bean for getCategorieXml.
 * Maps to the WSDL schema element tns:getCategorieXmlResponse.
 */
public class GetCategorieXmlResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String _return;

    public GetCategorieXmlResponse() {}

    public GetCategorieXmlResponse(String _return) {
        this._return = _return;
    }

    public String get_return()               { return _return; }
    public void set_return(String _return)   { this._return = _return; }
}
