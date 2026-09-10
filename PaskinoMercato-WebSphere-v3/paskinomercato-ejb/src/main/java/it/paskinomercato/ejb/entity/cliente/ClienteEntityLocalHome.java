package it.paskinomercato.ejb.entity.cliente;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import java.util.Collection;

/**
 * EJB 2.x Local Home interface for the Cliente BMP Entity Bean.
 */
public interface ClienteEntityLocalHome extends EJBLocalHome {

    ClienteEntityLocal create(String email, String passwordHash, String nome,
                              String cognome, String telefono, String lingua)
            throws CreateException;

    ClienteEntityLocal findByPrimaryKey(Integer pk) throws FinderException;

    ClienteEntityLocal findByEmail(String email) throws FinderException;

    Collection<ClienteEntityLocal> findByAttivo(boolean attivo) throws FinderException;
}
