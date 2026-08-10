package it.paskinomercato.cart;

import it.paskinomercato.model.CarrelloItem;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Plain serializable cart stored directly in the HTTP session.
 * Replaces the EJB 2.x Stateful CarrelloBean stub which cannot be
 * safely stored in HttpSession on Liberty.
 */
public class CarrelloSessionBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private final List<CarrelloItem> items = new ArrayList<CarrelloItem>();

    public void aggiungi(int prodottoId, String nomeProdotto, BigDecimal prezzo, String immagine) {
        for (CarrelloItem item : items) {
            if (item.getProdottoId() == prodottoId) {
                item.setQuantita(item.getQuantita() + 1);
                return;
            }
        }
        items.add(new CarrelloItem(prodottoId, nomeProdotto, 1, prezzo, immagine));
    }

    public void rimuovi(int prodottoId) {
        Iterator<CarrelloItem> it = items.iterator();
        while (it.hasNext()) {
            if (it.next().getProdottoId() == prodottoId) {
                it.remove();
                return;
            }
        }
    }

    public void aggiornaQuantita(int prodottoId, int nuovaQuantita) {
        if (nuovaQuantita <= 0) {
            rimuovi(prodottoId);
            return;
        }
        for (CarrelloItem item : items) {
            if (item.getProdottoId() == prodottoId) {
                item.setQuantita(nuovaQuantita);
                return;
            }
        }
    }

    public void svuota() {
        items.clear();
    }

    public List<CarrelloItem> getItems() {
        return items;
    }

    public BigDecimal getTotale() {
        BigDecimal totale = BigDecimal.ZERO;
        for (CarrelloItem item : items) {
            totale = totale.add(item.getSubtotale());
        }
        return totale;
    }

    public int getNumeroArticoli() {
        int count = 0;
        for (CarrelloItem item : items) {
            count += item.getQuantita();
        }
        return count;
    }
}
