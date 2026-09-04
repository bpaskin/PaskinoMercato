package it.paskinomercato.ejb.entity.ordine;

import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * EJB 2.x Local Home interface for the Ordine BMP Entity Bean.
 */
public interface OrdineEntityLocalHome extends EJBLocalHome {

    OrdineEntityLocal create(String numeroOrdine, int clienteId, int indirizzoId,
                             BigDecimal totale, String note)
            throws CreateException;

    OrdineEntityLocal findByPrimaryKey(Integer pk) throws FinderException;

    OrdineEntityLocal findByNumeroOrdine(String numeroOrdine) throws FinderException;

    Collection<OrdineEntityLocal> findByClienteId(int clienteId) throws FinderException;
}
