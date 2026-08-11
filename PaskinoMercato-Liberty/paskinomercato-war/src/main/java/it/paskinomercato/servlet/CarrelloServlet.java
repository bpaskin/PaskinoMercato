package it.paskinomercato.servlet;

import it.paskinomercato.cart.CarrelloSessionBean;
import it.paskinomercato.ejb.catalogo.CatalogoService;
import it.paskinomercato.model.Prodotto;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles shopping cart operations: view, add, update, remove, clear.
 */
@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    @Inject
    private CatalogoService catalogo;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");
        HttpSession session = req.getSession();
        CarrelloSessionBean carrello = getOrCreateCarrello(session);

        try {
            if ("aggiungi".equals(azione)) {
                int prodottoId = Integer.parseInt(req.getParameter("prodottoId"));
                Prodotto p = catalogo.getProdottoById(prodottoId);
                if (p != null && p.isAttivo() && p.getQuantitaStock() > 0) {
                    String lang = (String) session.getAttribute("lang");
                    String nome = p.getNome(lang != null ? lang : "it");
                    carrello.aggiungi(p.getId(), nome, p.getPrezzo(), p.getImmagine());
                }

            } else if ("rimuovi".equals(azione)) {
                int prodottoId = Integer.parseInt(req.getParameter("prodottoId"));
                carrello.rimuovi(prodottoId);

            } else if ("aggiorna".equals(azione)) {
                int prodottoId = Integer.parseInt(req.getParameter("prodottoId"));
                int quantita   = Integer.parseInt(req.getParameter("quantita"));
                carrello.aggiornaQuantita(prodottoId, quantita);

            } else if ("svuota".equals(azione)) {
                carrello.svuota();
            }

        } catch (Exception e) {
            req.setAttribute("errore", "Errore carrello: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/carrello");
    }

    private CarrelloSessionBean getOrCreateCarrello(HttpSession session) {
        CarrelloSessionBean carrello = (CarrelloSessionBean) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new CarrelloSessionBean();
            session.setAttribute("carrello", carrello);
        }
        return carrello;
    }
}
