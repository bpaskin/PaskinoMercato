package it.paskinomercato.ejb.entity.prodotto;

import jakarta.ejb.CreateException;
import jakarta.ejb.FinderException;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Local business interface for the ProdottoEntityBean Stateless Session Bean.
 */
public interface ProdottoEntityService {

    ProdottoEntityData create(String codice, String nomeIt, String nomeEn,
                               String descrizioneIt, String descrizioneEn,
                               BigDecimal prezzo, String unitaMisura,
                               int quantitaStock, int categoriaId,
                               String immagine, double pesoKg)
            throws CreateException;

    ProdottoEntityData findByPrimaryKey(Integer pk) throws FinderException;

    Collection<ProdottoEntityData> findAll() throws FinderException;

    Collection<ProdottoEntityData> findByCategoriaId(int categoriaId) throws FinderException;

    Collection<ProdottoEntityData> findByAttivo(boolean attivo) throws FinderException;

    ProdottoEntityData findByCodice(String codice) throws FinderException;

    Collection<ProdottoEntityData> findByNomeContaining(String pattern) throws FinderException;
}
