package it.paskinomercato.servlet;

import it.paskinomercato.ejb.catalogo.CatalogoLocal;
import it.paskinomercato.ejb.catalogo.CatalogoLocalHome;
import it.paskinomercato.model.Categoria;
import it.paskinomercato.model.Prodotto;

import javax.naming.InitialContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Handles the product catalog display with pagination and category filtering.
 */
public class CatalogoServlet extends HttpServlet {

    private static final int PAGINA_DIM = 24;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        try {
            InitialContext ic = new InitialContext();
            CatalogoLocalHome home = (CatalogoLocalHome) ic.lookup("java:comp/env/ejb/CatalogoBean");
            CatalogoLocal catalogo = home.create();

            // Pagination
            int pagina = 1;
            try { pagina = Integer.parseInt(req.getParameter("p")); } catch (Exception _) {}
            if (pagina < 1) pagina = 1;

            // Category filter
            String catParam = req.getParameter("cat");
            int categoriaId = 0;
            try { categoriaId = Integer.parseInt(catParam); } catch (Exception _) {}

            // Search
            String cerca = req.getParameter("cerca");
            List<Prodotto> prodotti;
            int totaleProdotti;

            if (cerca != null && cerca.trim().length() > 0) {
                prodotti = catalogo.cercaProdotti(cerca.trim());
                totaleProdotti = prodotti.size();
                req.setAttribute("cercaTesto", cerca.trim());
            } else if (categoriaId > 0) {
                prodotti = catalogo.getProdottiPerCategoria(categoriaId, pagina, PAGINA_DIM);
                totaleProdotti = catalogo.contaProdottiPerCategoria(categoriaId);
                req.setAttribute("categoriaSelezionata", catalogo.getCategoriaById(categoriaId));
            } else {
                prodotti = catalogo.getProdotti(pagina, PAGINA_DIM);
                totaleProdotti = catalogo.contaProdotti();
            }

            List<Categoria> categorie = catalogo.getCategorie();
            int totalePagine = (int) Math.ceil((double) totaleProdotti / PAGINA_DIM);

            req.setAttribute("prodotti",      prodotti);
            req.setAttribute("categorie",     categorie);
            req.setAttribute("paginaCorrente", Integer.valueOf(pagina));
            req.setAttribute("totalePagine",   Integer.valueOf(totalePagine));
            req.setAttribute("totaleProdotti", Integer.valueOf(totaleProdotti));
            req.setAttribute("categoriaId",    Integer.valueOf(categoriaId));

            req.getRequestDispatcher("/WEB-INF/jsp/catalogo.jsp").forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("CatalogoServlet error", e);
        }
    }
}
