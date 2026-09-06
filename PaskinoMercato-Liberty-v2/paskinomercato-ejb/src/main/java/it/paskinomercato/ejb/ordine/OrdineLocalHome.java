package it.paskinomercato.ejb.ordine;

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

/**
 * EJB 2.0 Local Home for OrdineBean.
 */
public interface OrdineLocalHome extends EJBLocalHome {
    OrdineLocal create() throws CreateException;
}
