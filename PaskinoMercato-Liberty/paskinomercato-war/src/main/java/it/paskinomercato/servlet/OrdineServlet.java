package it.paskinomercato.servlet;

import it.paskinomercato.ejb.ordine.OrdineService;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;

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
 * Displays the order history for a logged-in customer,
 * and the detail for a single order.
 */
@WebServlet("/ordini")
public class OrdineServlet extends HttpServlet {

    @Inject
    private OrdineService ordineService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession();
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            resp.sendRedirect(req.getContextPath() + "/login?redirect=ordini");
            return;
        }

        try {
            String numeroOrdine = req.getParameter("numero");
            if (numeroOrdine != null && !numeroOrdine.isEmpty()) {
                Ordine ordine = ordineService.getOrdineByNumero(numeroOrdine);
                if (ordine == null || ordine.getClienteId() != cliente.getId()) {
                    resp.sendRedirect(req.getContextPath() + "/ordini");
                    return;
                }
                List<RigaOrdine> righe = ordineService.getRigheOrdine(ordine.getId());
                req.setAttribute("ordine", ordine);
                req.setAttribute("righe",  righe);
                req.getRequestDispatcher("/WEB-INF/jsp/dettaglioOrdine.jsp").forward(req, resp);
            } else {
                List<Ordine> ordini = ordineService.getOrdiniCliente(cliente.getId());
                req.setAttribute("ordini", ordini);
                req.getRequestDispatcher("/WEB-INF/jsp/ordini.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            throw new ServletException("OrdineServlet error", e);
        }
    }
}
