package it.paskinomercato.cart;

import it.paskinomercato.model.CarrelloItem;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Session-scoped shopping cart bean.
 * Replaces the EJB 2.x Stateful CarrelloBean and the plain
 * CarrelloSessionBean stored directly in HttpSession.
 */
@Component
@SessionScope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CarrelloSessionBean implements Serializable {

    private final List<CarrelloItem> items = new ArrayList<>();

    public void aggiungi(int prodottoId, String nomeProdotto,
                         BigDecimal prezzo, String immagine) {
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
        return items.stream()
            .map(CarrelloItem::getSubtotale)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getNumeroArticoli() {
        return items.stream().mapToInt(CarrelloItem::getQuantita).sum();
    }
}
