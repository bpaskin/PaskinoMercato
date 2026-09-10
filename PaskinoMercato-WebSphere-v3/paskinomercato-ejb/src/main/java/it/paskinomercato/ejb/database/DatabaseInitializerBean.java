package it.paskinomercato.ejb.database;

import it.paskinomercato.persistence.H2Database;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

/** EJB 2.1 facade that initializes the WebSphere-managed H2 database. */
public class DatabaseInitializerBean implements SessionBean {

    private SessionContext context;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext context) { this.context = context; }

    public int populateDatabase() {
        try {
            return H2Database.populateDatabase();
        } catch (Exception e) {
            throw new EJBException("Unable to initialize the H2 database", e);
        }
    }
}
