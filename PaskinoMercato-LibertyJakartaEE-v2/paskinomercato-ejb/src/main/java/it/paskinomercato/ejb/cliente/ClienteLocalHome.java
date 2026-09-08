package it.paskinomercato.ejb.cliente;

import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.CreateException;

public interface ClienteLocalHome extends EJBLocalHome {
    ClienteLocal create() throws CreateException;
}
