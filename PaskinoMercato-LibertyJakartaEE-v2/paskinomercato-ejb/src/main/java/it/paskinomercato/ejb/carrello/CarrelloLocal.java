package it.paskinomercato.ejb.carrello;

import jakarta.ejb.EJBLocalObject;
import it.paskinomercato.model.CarrelloItem;
import java.util.List;
import java.math.BigDecimal;

/**
 * EJB 2.0 Local Object for the Stateful CarrelloBean.
 * Manages the in-memory shopping cart per session.
 */
public interface CarrelloLocal extends EJBLocalObject {

    void aggiungi(int prodottoId, String nomeProdotto, BigDecimal prezzo, String immagine);

    void rimuovi(int prodottoId);

    void aggiornaQuantita(int prodottoId, int nuovaQuantita);

    void svuota();

    List<CarrelloItem> getItems();

    BigDecimal getTotale();

    int getNumeroArticoli();
}
