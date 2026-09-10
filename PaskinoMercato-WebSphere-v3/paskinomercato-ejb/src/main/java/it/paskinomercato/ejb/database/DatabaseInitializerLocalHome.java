package it.paskinomercato.ejb.database;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

/** Local home for the Java EE 5 compatible database initializer bean. */
public interface DatabaseInitializerLocalHome extends EJBLocalHome {
    DatabaseInitializerLocal create() throws CreateException;
}
