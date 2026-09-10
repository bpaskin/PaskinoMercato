package it.paskinomercato.servlet;

import it.paskinomercato.ejb.database.DatabaseInitializerLocal;
import it.paskinomercato.ejb.database.DatabaseInitializerLocalHome;

import javax.naming.InitialContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/** Populates the embedded database before the web application accepts requests. */
public class DatabaseStartupListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
        DatabaseInitializerLocal initializer = null;
        try {
            InitialContext context = new InitialContext();
            DatabaseInitializerLocalHome home = (DatabaseInitializerLocalHome) context.lookup(
                "java:comp/env/ejb/DatabaseInitializerBean");
            initializer = home.create();
            int productCount = initializer.populateDatabase();
            event.getServletContext().log(
                "PaskinoMercato H2 database initialized with " + productCount + " products");
        } catch (Exception e) {
            event.getServletContext().log("PaskinoMercato database initialization failed", e);
            throw new IllegalStateException("Unable to initialize the application database", e);
        } finally {
            if (initializer != null) {
                try { initializer.remove(); } catch (Exception ignored) {}
            }
        }
    }

    public void contextDestroyed(ServletContextEvent event) {}
}
