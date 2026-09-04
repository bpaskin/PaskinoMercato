package it.paskinomercato.ejb.carrello;

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface CarrelloLocalHome extends EJBLocalHome {
    CarrelloLocal create() throws CreateException;
}
