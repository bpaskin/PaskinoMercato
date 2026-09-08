package it.paskinomercato.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/**
 * JAX-WS Service Endpoint Interface for PaskinoMercato.
 * Exposes catalog and order lookup as SOAP web services.
 */
@WebService(name = "MercatoServiceSEI", targetNamespace = "http://ws.paskinomercato.it/")
public interface MercatoServiceSEI {

    /**
     * Returns product details as XML string for a given product code.
     * Used by external partners/integrations.
     */
    @WebMethod
    String getProdottoXml(@WebParam(name = "codice") String codice);

    /**
     * Returns order status for a given order number.
     */
    @WebMethod
    String getStatoOrdine(@WebParam(name = "numeroOrdine") String numeroOrdine);

    /**
     * Returns the list of active categories as XML.
     */
    @WebMethod
    String getCategorieXml();
}
