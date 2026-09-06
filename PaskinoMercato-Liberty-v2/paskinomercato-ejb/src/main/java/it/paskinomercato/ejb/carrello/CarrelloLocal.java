package it.paskinomercato.ejb.carrello;

import javax.ejb.EJBLocalObject;
import it.paskinomercato.model.CarrelloItem;
import java.util.List;
import java.math.BigDecimal;

/**
 * EJB 2.x Local Object interface for CarrelloBean.
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
