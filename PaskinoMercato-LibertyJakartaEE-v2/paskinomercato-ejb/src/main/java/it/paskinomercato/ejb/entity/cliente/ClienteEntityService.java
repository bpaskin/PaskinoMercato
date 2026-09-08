package it.paskinomercato.ejb.entity.cliente;

import jakarta.ejb.CreateException;
import jakarta.ejb.FinderException;
import java.util.Collection;

/**
 * Local business interface for the ClienteEntityBean Stateless Session Bean.
 */
public interface ClienteEntityService {

    ClienteEntityData create(String email, String passwordHash, String nome,
                              String cognome, String telefono, String lingua)
            throws CreateException;

    ClienteEntityData findByPrimaryKey(Integer pk) throws FinderException;

    ClienteEntityData findByEmail(String email) throws FinderException;

    Collection<ClienteEntityData> findByAttivo(boolean attivo) throws FinderException;
}
