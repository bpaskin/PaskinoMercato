package it.paskinomercato.ejb.mail;

import jakarta.ejb.EJBLocalHome;
import jakarta.ejb.CreateException;

public interface MailLocalHome extends EJBLocalHome {
    MailLocal create() throws CreateException;
}
