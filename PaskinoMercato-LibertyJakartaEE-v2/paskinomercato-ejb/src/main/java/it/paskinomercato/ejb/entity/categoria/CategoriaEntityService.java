package it.paskinomercato.ejb.entity.categoria;

import jakarta.ejb.CreateException;
import jakarta.ejb.FinderException;
import java.util.Collection;

/**
 * Local business interface for the CategoriaEntityBean Stateless Session Bean.
 */
public interface CategoriaEntityService {

    CategoriaEntityData create(String codice, String nomeIt, String nomeEn,
                                String descrizioneIt, String descrizioneEn,
                                String immagine)
            throws CreateException;

    CategoriaEntityData findByPrimaryKey(Integer pk) throws FinderException;

    Collection<CategoriaEntityData> findAll() throws FinderException;

    CategoriaEntityData findByCodice(String codice) throws FinderException;
}
