package it.paskinomercato.servlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Switches the UI language between Italian and English.
 * Stores the chosen language in the session and also
 * updates the customer preference if logged in.
 */
public class LinguaServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String lang = req.getParameter("lang");
        if (!"en".equals(lang) && !"it".equals(lang)) {
            lang = "it";
        }

        HttpSession session = req.getSession();
        session.setAttribute("lang", lang);

        // Persist preference if customer is logged in
        it.paskinomercato.model.Cliente cliente =
            (it.paskinomercato.model.Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                javax.naming.InitialContext ic = new javax.naming.InitialContext();
                it.paskinomercato.ejb.cliente.ClienteLocalHome home =
                    (it.paskinomercato.ejb.cliente.ClienteLocalHome)
                        ic.lookup("java:comp/env/ejb/ClienteBean");
                home.create().aggiornaLingua(cliente.getId(), lang);
                cliente.setLingua(lang);
                session.setAttribute("cliente", cliente);
            } catch (Exception ignored) {}
        }

        // Redirect back to referer or home
        String referer = req.getHeader("Referer");
        if (referer == null || referer.isEmpty()) {
            referer = req.getContextPath() + "/";
        }
        resp.sendRedirect(referer);
    }
}
