package it.paskinomercato.servlet;

import it.paskinomercato.ejb.cliente.ClienteService;
import it.paskinomercato.ejb.mail.MailService;
import it.paskinomercato.model.Cliente;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Handles login, logout, and customer registration.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @Inject
    private ClienteService clienteService;

    @Inject
    private MailService mailService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String azione = req.getParameter("azione");
        if ("logout".equals(azione)) {
            req.getSession().invalidate();
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }
        req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String azione = req.getParameter("azione");

        try {
            if ("registra".equals(azione)) {
                String email    = req.getParameter("email");
                String password = req.getParameter("password");
                String nome     = req.getParameter("nome");
                String cognome  = req.getParameter("cognome");
                String telefono = req.getParameter("telefono");
                String lingua   = req.getParameter("lingua");

                if (clienteService.getClienteByEmail(email) != null) {
                    req.setAttribute("errore", lingua != null && lingua.equals("en")
                        ? "Email already registered."
                        : "Email già registrata.");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                    return;
                }

                String pwHash = sha256(password);
                Cliente nuovo = clienteService.registra(email, pwHash, nome, cognome, telefono, lingua);
                req.getSession().setAttribute("cliente", nuovo);
                req.getSession().setAttribute("lang", lingua != null ? lingua : "it");

                mailService.inviaRegistrazioneConferma(nuovo, lingua != null ? lingua : "it");

                String redirect = req.getParameter("redirect");
                resp.sendRedirect(req.getContextPath() +
                    (redirect != null && !redirect.isEmpty() ? "/" + redirect : "/"));

            } else {
                String email    = req.getParameter("email");
                String password = req.getParameter("password");
                String pwHash   = sha256(password);
                Cliente cliente = clienteService.login(email, pwHash);
                String lang     = req.getParameter("lang");

                if (cliente == null) {
                    req.setAttribute("errore", "it".equals(lang)
                        ? "Credenziali non valide."
                        : "Invalid credentials.");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                    return;
                }

                HttpSession session = req.getSession();
                session.setAttribute("cliente", cliente);
                session.setAttribute("lang", cliente.getLingua() != null ? cliente.getLingua() : "it");

                String redirect = req.getParameter("redirect");
                resp.sendRedirect(req.getContextPath() +
                    (redirect != null && !redirect.isEmpty() ? "/" + redirect : "/"));
            }

        } catch (Exception e) {
            throw new ServletException("LoginServlet error", e);
        }
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes("UTF-8"));
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                sb.append(String.format("%02x", hash[i]));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 failed", e);
        }
    }
}
