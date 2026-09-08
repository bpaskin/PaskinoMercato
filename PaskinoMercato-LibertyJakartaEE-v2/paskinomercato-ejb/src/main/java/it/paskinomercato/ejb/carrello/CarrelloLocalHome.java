package it.paskinomercato.ejb.carrello;

import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.CreateException;

public interface CarrelloLocalHome extends EJBLocalHome {
    CarrelloLocal create() throws CreateException;
}
