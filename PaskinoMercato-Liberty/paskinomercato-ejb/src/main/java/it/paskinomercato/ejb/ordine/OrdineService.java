package it.paskinomercato.ejb.ordine;

import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.model.CarrelloItem;
import java.util.List;

/**
 * Service interface for the Ordine CDI bean.
 * Replaces the EJB 2.x OrdineLocal + OrdineLocalHome pair.
 */
public interface OrdineService {

    String creaOrdine(int clienteId, int indirizzoId, List<CarrelloItem> carrelloItems, String note);

    Ordine getOrdineByNumero(String numeroOrdine);

    List<Ordine> getOrdiniCliente(int clienteId);

    List<RigaOrdine> getRigheOrdine(int ordineId);

    void aggiornaStato(int ordineId, String nuovoStato);
}
