package it.paskinomercato.ejb.catalogo;

import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.CreateException;

/**
 * EJB 2.0 Local Home for CatalogoBean.
 */
public interface CatalogoLocalHome extends EJBLocalHome {
    CatalogoLocal create() throws CreateException;
}
