package it.paskinomercato.controller;

import it.paskinomercato.cart.CarrelloSessionBean;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.service.CatalogoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/carrello")
public class CarrelloController {

    private final CatalogoService     catalogo;
    private final CarrelloSessionBean carrello;

    public CarrelloController(CatalogoService catalogo, CarrelloSessionBean carrello) {
        this.catalogo = catalogo;
        this.carrello = carrello;
    }

    @GetMapping
    public String view() {
        return "carrello";
    }

    @PostMapping
    public String action(
            @RequestParam String azione,
            @RequestParam(required = false) Integer prodottoId,
            @RequestParam(required = false) Integer quantita,
            HttpSession session) {

        try {
            switch (azione) {
                case "aggiungi" -> {
                    if (prodottoId != null) {
                        Prodotto p = catalogo.getProdottoById(prodottoId);
                        if (p != null && p.isAttivo() && p.getQuantitaStock() > 0) {
                            String lang = (String) session.getAttribute("lang");
                            String nome = p.getNome(lang != null ? lang : "it");
                            carrello.aggiungi(p.getId(), nome, p.getPrezzo(), p.getImmagine());
                        }
                    }
                }
                case "rimuovi" -> {
                    if (prodottoId != null) carrello.rimuovi(prodottoId);
                }
                case "aggiorna" -> {
                    if (prodottoId != null && quantita != null)
                        carrello.aggiornaQuantita(prodottoId, quantita);
                }
                case "svuota" -> carrello.svuota();
            }
        } catch (Exception e) {
            // non-fatal: cart operation failures redirect to cart page
        }

        return "redirect:/carrello";
    }
}
