package it.paskinomercato.ejb.entity.prodotto;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * EJB 2.x Local Home interface for the Prodotto BMP Entity Bean.
 */
public interface ProdottoEntityLocalHome extends EJBLocalHome {

    ProdottoEntityLocal create(String codice, String nomeIt, String nomeEn,
                               String descrizioneIt, String descrizioneEn,
                               BigDecimal prezzo, String unitaMisura,
                               int quantitaStock, int categoriaId,
                               String immagine, double pesoKg)
            throws CreateException;

    ProdottoEntityLocal findByPrimaryKey(Integer pk) throws FinderException;

    Collection<ProdottoEntityLocal> findAll() throws FinderException;

    Collection<ProdottoEntityLocal> findByCategoriaId(int categoriaId) throws FinderException;

    Collection<ProdottoEntityLocal> findByAttivo(boolean attivo) throws FinderException;

    ProdottoEntityLocal findByCodice(String codice) throws FinderException;

    Collection<ProdottoEntityLocal> findByNomeContaining(String pattern) throws FinderException;
}
