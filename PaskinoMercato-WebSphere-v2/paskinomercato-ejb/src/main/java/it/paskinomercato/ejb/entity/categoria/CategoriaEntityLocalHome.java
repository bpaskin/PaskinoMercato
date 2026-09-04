package it.paskinomercato.ejb.entity.categoria;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import java.util.Collection;

/**
 * EJB 2.x Local Home interface for the Categoria BMP Entity Bean.
 */
public interface CategoriaEntityLocalHome extends EJBLocalHome {

    CategoriaEntityLocal create(String codice, String nomeIt, String nomeEn,
                                String descrizioneIt, String descrizioneEn,
                                String immagine)
            throws CreateException;

    CategoriaEntityLocal findByPrimaryKey(Integer pk) throws FinderException;

    Collection<CategoriaEntityLocal> findAll() throws FinderException;

    CategoriaEntityLocal findByCodice(String codice) throws FinderException;
}
