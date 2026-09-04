package it.paskinomercato.ejb.cliente;

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface ClienteLocalHome extends EJBLocalHome {
    ClienteLocal create() throws CreateException;
}
