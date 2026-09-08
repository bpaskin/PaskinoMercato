package it.paskinomercato.ejb.carrello;

import it.paskinomercato.model.CarrelloItem;

import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * EJB 2.0 Stateful Session Bean — Carrello.
 * Holds the shopping cart in server-side state per session.
 */
public class CarrelloBean implements SessionBean {

    private SessionContext ctx;
    private List<CarrelloItem> items = new ArrayList<CarrelloItem>(); // List<CarrelloItem>

    public void ejbCreate() {
        items = new ArrayList<CarrelloItem>();
    }
    public void ejbRemove()   {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    public void aggiungi(int prodottoId, String nomeProdotto, BigDecimal prezzo, String immagine) {
        // If already in cart, increment quantity
        for (int i = 0; i < items.size(); i++) {
            CarrelloItem item = items.get(i);
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
            CarrelloItem item = it.next();
            if (item.getProdottoId() == prodottoId) {
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
        for (int i = 0; i < items.size(); i++) {
            CarrelloItem item = items.get(i);
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
        return new ArrayList<CarrelloItem>(items);
    }

    public BigDecimal getTotale() {
        BigDecimal totale = BigDecimal.ZERO;
        for (int i = 0; i < items.size(); i++) {
            CarrelloItem item = items.get(i);
            totale = totale.add(item.getSubtotale());
        }
        return totale;
    }

    public int getNumeroArticoli() {
        int count = 0;
        for (int i = 0; i < items.size(); i++) {
            CarrelloItem item = items.get(i);
            count += item.getQuantita();
        }
        return count;
    }
}
