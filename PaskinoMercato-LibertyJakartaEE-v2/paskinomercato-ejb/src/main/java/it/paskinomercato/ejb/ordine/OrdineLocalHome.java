package it.paskinomercato.ejb.ordine;

import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.CreateException;

/**
 * EJB 2.0 Local Home for OrdineBean.
 */
public interface OrdineLocalHome extends EJBLocalHome {
    OrdineLocal create() throws CreateException;
}
