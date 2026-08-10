package it.paskinomercato.controller;

import it.paskinomercato.cart.CarrelloSessionBean;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Makes the session-scoped cart available as "carrello" in every view,
 * preserving the ${sessionScope.carrello.numeroArticoli} expressions used in
 * the JSP header without any change.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final CarrelloSessionBean carrello;

    public GlobalModelAdvice(CarrelloSessionBean carrello) {
        this.carrello = carrello;
    }

    @ModelAttribute("carrello")
    public CarrelloSessionBean carrello() {
        return carrello;
    }
}
