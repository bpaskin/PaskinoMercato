package it.paskinomercato.servlet;

import it.paskinomercato.ejb.ordine.OrdineLocal;
import it.paskinomercato.ejb.ordine.OrdineLocalHome;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;

import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Displays the order history for a logged-in customer,
 * and the detail for a single order.
 */
public class OrdineServlet extends HttpServlet {

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=ordini");
            return;
        }

        try {
            InitialContext ic = new InitialContext();
            OrdineLocalHome home = (OrdineLocalHome) ic.lookup("java:comp/env/ejb/OrdineBean");
            OrdineLocal ordineBean = home.create();

            String numeroOrdine = req.getParameter("numero");
            if (numeroOrdine != null && !numeroOrdine.isEmpty()) {
                Ordine ordine = ordineBean.getOrdineByNumero(numeroOrdine);
                if (ordine == null || ordine.getClienteId() != cliente.getId()) {
                    resp.sendRedirect(req.getContextPath() + "/ordini");
                    return;
                }
                List<RigaOrdine> righe = ordineBean.getRigheOrdine(ordine.getId());
                req.setAttribute("ordine", ordine);
                req.setAttribute("righe",  righe);
                req.getRequestDispatcher("/WEB-INF/jsp/dettaglioOrdine.jsp").forward(req, resp);
            } else {
                List<Ordine> ordini = ordineBean.getOrdiniCliente(cliente.getId());
                req.setAttribute("ordini", ordini);
                req.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            throw new ServletException("OrdineServlet error", e);
        }
    }
}
