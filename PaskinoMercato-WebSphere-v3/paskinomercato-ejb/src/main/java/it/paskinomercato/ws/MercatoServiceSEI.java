package it.paskinomercato.ws;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * JAX-RPC Service Endpoint Interface for PaskinoMercato.
 * Exposes catalog and order lookup as SOAP web services.
 */
public interface MercatoServiceSEI extends Remote {

    /**
     * Returns product details as XML string for a given product code.
     * Used by external partners/integrations.
     */
    String getProdottoXml(String codice) throws RemoteException;

    /**
     * Returns order status for a given order number.
     */
    String getStatoOrdine(String numeroOrdine) throws RemoteException;

    /**
     * Returns the list of active categories as XML.
     */
    String getCategorieXml() throws RemoteException;
}
