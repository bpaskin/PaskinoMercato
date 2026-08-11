package it.paskinomercato.servlet;

import it.paskinomercato.cart.CarrelloSessionBean;
import it.paskinomercato.ejb.cliente.ClienteService;
import it.paskinomercato.ejb.mail.MailService;
import it.paskinomercato.ejb.ordine.OrdineService;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.util.IndirizzoItaliaValidator;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Manages the multi-step checkout flow:
 *   Step 1 — address selection / input
 *   Step 2 — order summary confirmation
 *   Step 3 — order placed (sends confirmation email)
 */
@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    @Inject
    private ClienteService clienteService;

    @Inject
    private OrdineService ordineService;

    @Inject
    private MailService mailService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=checkout");
            return;
        }

        try {
            List<Indirizzo> indirizzi = clienteService.getIndirizzi(cliente.getId());
            req.setAttribute("indirizzi", indirizzi);

            CarrelloSessionBean carrello = (CarrelloSessionBean) session.getAttribute("carrello");
            if (carrello == null || carrello.getNumeroArticoli() == 0) {
                resp.sendRedirect(req.getContextPath() + "/carrello");
                return;
            }
            req.setAttribute("carrelloItems", carrello.getItems());
            req.setAttribute("totale",        carrello.getTotale());

        } catch (Exception e) {
            throw new ServletException("CheckoutServlet doGet error", e);
        }

        req.getRequestDispatcher("/WEB-INF/jsp/checkout.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=checkout");
            return;
        }

        String via              = req.getParameter("via");
        String civico           = req.getParameter("civico");
        String citta            = req.getParameter("citta");
        String cap              = req.getParameter("cap");
        String provincia        = req.getParameter("provincia");
        String paese            = "IT";
        String note             = req.getParameter("note");
        String indirizzoIdParam = req.getParameter("indirizzoId");

        try {
            int indirizzoId = 0;

            if (indirizzoIdParam != null && !indirizzoIdParam.isEmpty()) {
                indirizzoId = Integer.parseInt(indirizzoIdParam);
                Indirizzo ind = clienteService.getIndirizzo(indirizzoId);
                if (ind == null || ind.getClienteId() != cliente.getId()) {
                    req.setAttribute("errore", "Indirizzo non valido / Invalid address");
                    doGet(req, resp);
                    return;
                }
                IndirizzoItaliaValidator.ValidationResult vr =
                    IndirizzoItaliaValidator.valida(ind.getPaese(), ind.getCap(), ind.getProvincia());
                if (!vr.isValid()) {
                    req.setAttribute("errore", vr.getErrorMessage());
                    doGet(req, resp);
                    return;
                }
            } else {
                IndirizzoItaliaValidator.ValidationResult vr =
                    IndirizzoItaliaValidator.valida(paese, cap, provincia);
                if (!vr.isValid()) {
                    req.setAttribute("errore", vr.getErrorMessage());
                    doGet(req, resp);
                    return;
                }
                clienteService.aggiungiIndirizzo(cliente.getId(), via, civico, citta, cap, provincia);
                List<Indirizzo> indirizzi = clienteService.getIndirizzi(cliente.getId());
                Indirizzo last = indirizzi.get(indirizzi.size() - 1);
                indirizzoId = last.getId();
            }

            CarrelloSessionBean carrello = (CarrelloSessionBean) session.getAttribute("carrello");
            if (carrello == null || carrello.getNumeroArticoli() == 0) {
                resp.sendRedirect(req.getContextPath() + "/carrello");
                return;
            }

            String numeroOrdine = ordineService.creaOrdine(
                cliente.getId(), indirizzoId, carrello.getItems(), note);

            Ordine ordine = ordineService.getOrdineByNumero(numeroOrdine);
            List<RigaOrdine> righe = ordineService.getRigheOrdine(ordine.getId());

            String lang = (String) session.getAttribute("lang");
            mailService.inviaConfermaOrdine(cliente, ordine, righe, lang != null ? lang : "it");

            carrello.svuota();
            session.removeAttribute("carrello");

            req.setAttribute("ordine", ordine);
            req.setAttribute("righe",  righe);
            req.getRequestDispatcher("/WEB-INF/jsp/confermaOrdine.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("CheckoutServlet doPost error", e);
        }
    }
}
