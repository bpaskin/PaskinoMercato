package it.paskinomercato.servlet;

import it.paskinomercato.ejb.carrello.CarrelloLocal;
import it.paskinomercato.ejb.carrello.CarrelloLocalHome;
import it.paskinomercato.ejb.catalogo.CatalogoLocal;
import it.paskinomercato.ejb.catalogo.CatalogoLocalHome;
import it.paskinomercato.model.Prodotto;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Handles shopping cart operations: view, add, update, remove, clear.
 */
public class CarrelloServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/carrello.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");
        HttpSession session = req.getSession();

        try {
            CarrelloLocal carrello = getOrCreateCarrello(session);

            if ("aggiungi".equals(azione)) {
                int prodottoId = Integer.parseInt(req.getParameter("prodottoId"));
                // Look up product name and price for display
                InitialContext ic = new InitialContext();
                CatalogoLocalHome catHome = (CatalogoLocalHome) ic.lookup("java:comp/env/ejb/CatalogoBean");
                CatalogoLocal catalogo = catHome.create();
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
                int prodottoId  = Integer.parseInt(req.getParameter("prodottoId"));
                int quantita    = Integer.parseInt(req.getParameter("quantita"));
                carrello.aggiornaQuantita(prodottoId, quantita);

            } else if ("svuota".equals(azione)) {
                carrello.svuota();
            }

        } catch (Exception e) {
            req.setAttribute("errore", "Errore carrello: " + e.getMessage());
        }

        resp.sendRedirect(req.getContextPath() + "/carrello");
    }

    private CarrelloLocal getOrCreateCarrello(HttpSession session) throws Exception {
        CarrelloLocal carrello = (CarrelloLocal) session.getAttribute("carrello");
        if (carrello == null) {
            InitialContext ic = new InitialContext();
            CarrelloLocalHome home = (CarrelloLocalHome) ic.lookup("java:comp/env/ejb/CarrelloBean");
            carrello = home.create();
            session.setAttribute("carrello", carrello);
        }
        return carrello;
    }
}
