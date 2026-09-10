package it.paskinomercato.ejb.mail;

import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface MailLocalHome extends EJBLocalHome {
    MailLocal create() throws CreateException;
}
