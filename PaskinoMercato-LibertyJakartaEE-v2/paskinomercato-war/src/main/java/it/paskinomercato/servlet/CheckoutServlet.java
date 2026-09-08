package it.paskinomercato.servlet;

import it.paskinomercato.ejb.carrello.CarrelloLocal;
import it.paskinomercato.ejb.cliente.ClienteLocal;
import it.paskinomercato.ejb.cliente.ClienteLocalHome;
import it.paskinomercato.ejb.mail.MailLocal;
import it.paskinomercato.ejb.mail.MailLocalHome;
import it.paskinomercato.ejb.ordine.OrdineLocal;
import it.paskinomercato.ejb.ordine.OrdineLocalHome;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.util.IndirizzoItaliaValidator;

import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Manages the multi-step checkout flow:
 *   Step 1 — address selection / input
 *   Step 2 — order summary confirmation
 *   Step 3 — order placed (sends confirmation email)
 */
public class CheckoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=checkout");
            return;
        }

        // Load addresses for the customer
        try {
            InitialContext ic = new InitialContext();
            ClienteLocalHome cHome = (ClienteLocalHome) ic.lookup("java:comp/env/ejb/ClienteBean");
            ClienteLocal clienteBean = cHome.create();
            List<Indirizzo> indirizzi = clienteBean.getIndirizzi(cliente.getId());
            req.setAttribute("indirizzi", indirizzi);

            CarrelloLocal carrello = (CarrelloLocal) session.getAttribute("carrello");
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

        String via       = req.getParameter("via");
        String civico    = req.getParameter("civico");
        String citta     = req.getParameter("citta");
        String cap       = req.getParameter("cap");
        String provincia = req.getParameter("provincia");
        String paese     = "IT"; // forced — delivery Italy only
        String note      = req.getParameter("note");
        String indirizzoIdParam = req.getParameter("indirizzoId");

        try {
            InitialContext ic = new InitialContext();
            ClienteLocalHome cHome = (ClienteLocalHome) ic.lookup("java:comp/env/ejb/ClienteBean");
            ClienteLocal clienteBean = cHome.create();

            int indirizzoId = 0;

            if (indirizzoIdParam != null && !indirizzoIdParam.isEmpty()) {
                // Existing address selected
                indirizzoId = Integer.parseInt(indirizzoIdParam);
                Indirizzo ind = clienteBean.getIndirizzo(indirizzoId);
                if (ind == null || ind.getClienteId() != cliente.getId()) {
                    req.setAttribute("errore", "Indirizzo non valido / Invalid address");
                    doGet(req, resp);
                    return;
                }
                // Validate it is Italian
                IndirizzoItaliaValidator.ValidationResult vr =
                    IndirizzoItaliaValidator.valida(ind.getPaese(), ind.getCap(), ind.getProvincia());
                if (!vr.isValid()) {
                    req.setAttribute("errore", vr.getErrorMessage());
                    doGet(req, resp);
                    return;
                }
            } else {
                // New address entered
                IndirizzoItaliaValidator.ValidationResult vr =
                    IndirizzoItaliaValidator.valida(paese, cap, provincia);
                if (!vr.isValid()) {
                    req.setAttribute("errore", vr.getErrorMessage());
                    doGet(req, resp);
                    return;
                }
                clienteBean.aggiungiIndirizzo(cliente.getId(), via, civico, citta, cap, provincia);
                // Retrieve the newly created address id
                List<Indirizzo> indirizzi = clienteBean.getIndirizzi(cliente.getId());
                Indirizzo last = indirizzi.getLast();
                indirizzoId = last.getId();
            }

            // Place order
            CarrelloLocal carrello = (CarrelloLocal) session.getAttribute("carrello");
            if (carrello == null || carrello.getNumeroArticoli() == 0) {
                resp.sendRedirect(req.getContextPath() + "/carrello");
                return;
            }

            OrdineLocalHome oHome = (OrdineLocalHome) ic.lookup("java:comp/env/ejb/OrdineBean");
            OrdineLocal ordineBean = oHome.create();
            String numeroOrdine = ordineBean.creaOrdine(
                cliente.getId(), indirizzoId, carrello.getItems(), note);

            // Send confirmation email
            Ordine ordine = ordineBean.getOrdineByNumero(numeroOrdine);
            List<RigaOrdine> righe = ordineBean.getRigheOrdine(ordine.getId());

            MailLocalHome mHome = (MailLocalHome) ic.lookup("java:comp/env/ejb/MailBean");
            MailLocal mail = mHome.create();
            String lang = (String) session.getAttribute("lang");
            mail.inviaConfermaOrdine(cliente, ordine, righe, lang != null ? lang : "it");

            // Clear cart from session
            carrello.svuota();
            session.removeAttribute("carrello");

            // Forward to confirmation page
            req.setAttribute("ordine", ordine);
            req.setAttribute("righe",  righe);
            req.getRequestDispatcher("/WEB-INF/jsp/confermaOrdine.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("CheckoutServlet doPost error", e);
        }
    }
}
