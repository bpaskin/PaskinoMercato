package it.paskinomercato.ejb.ordine;

import jakarta.ejb.EJBLocalObject;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.model.CarrelloItem;
import java.util.List;

/**
 * EJB 2.0 Local Object for OrdineBean.
 */
public interface OrdineLocal extends EJBLocalObject {

    /**
     * Creates a new order from the cart items and persists it.
     * Returns the new order number.
     */
    String creaOrdine(int clienteId, int indirizzoId, List<CarrelloItem> carrelloItems, String note);

    Ordine getOrdineByNumero(String numeroOrdine);

    List<Ordine> getOrdiniCliente(int clienteId);

    List<RigaOrdine> getRigheOrdine(int ordineId);

    void aggiornaStato(int ordineId, String nuovoStato);
}
