package it.paskinomercato.ejb.database;

import javax.ejb.EJBLocalObject;

/** Local interface used by the web module during application startup. */
public interface DatabaseInitializerLocal extends EJBLocalObject {
    int populateDatabase();
}
