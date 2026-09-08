package it.paskinomercato.ejb.entity.ordine;

import jakarta.ejb.CreateException;
import jakarta.ejb.FinderException;
import java.math.BigDecimal;
import java.util.Collection;

/**
 * Local business interface for the OrdineEntityBean Stateless Session Bean.
 */
public interface OrdineEntityService {

    OrdineEntityData create(String numeroOrdine, int clienteId, int indirizzoId,
                             BigDecimal totale, String note)
            throws CreateException;

    OrdineEntityData findByPrimaryKey(Integer pk) throws FinderException;

    OrdineEntityData findByNumeroOrdine(String numeroOrdine) throws FinderException;

    Collection<OrdineEntityData> findByClienteId(int clienteId) throws FinderException;
}
