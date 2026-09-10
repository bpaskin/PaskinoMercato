package it.paskinomercato.servlet;

import it.paskinomercato.ejb.cliente.ClienteLocal;
import it.paskinomercato.ejb.cliente.ClienteLocalHome;
import it.paskinomercato.ejb.mail.MailLocal;
import it.paskinomercato.ejb.mail.MailLocalHome;
import it.paskinomercato.model.Cliente;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * Handles login, logout, and customer registration.
 */
public class LoginServlet extends HttpServlet {

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
            InitialContext ic = new InitialContext();
            ClienteLocalHome home = (ClienteLocalHome) ic.lookup("java:comp/env/ejb/ClienteBean");
            ClienteLocal clienteBean = home.create();

            if ("registra".equals(azione)) {
                String email     = req.getParameter("email");
                String password  = req.getParameter("password");
                String nome      = req.getParameter("nome");
                String cognome   = req.getParameter("cognome");
                String telefono  = req.getParameter("telefono");
                String lingua    = req.getParameter("lingua");

                if (clienteBean.getClienteByEmail(email) != null) {
                    req.setAttribute("errore", lingua != null && lingua.equals("en")
                        ? "Email already registered."
                        : "Email già registrata.");
                    req.getRequestDispatcher("/WEB-INF/jsp/login.jsp").forward(req, resp);
                    return;
                }

                String pwHash = sha256(password);
                Cliente nuovo = clienteBean.registra(email, pwHash, nome, cognome, telefono, lingua);
                req.getSession().setAttribute("cliente", nuovo);
                req.getSession().setAttribute("lang", lingua != null ? lingua : "it");

                // Send welcome email
                MailLocalHome mHome = (MailLocalHome) ic.lookup("java:comp/env/ejb/MailBean");
                MailLocal mail = mHome.create();
                mail.inviaRegistrazioneConferma(nuovo, lingua != null ? lingua : "it");

                String redirect = req.getParameter("redirect");
                resp.sendRedirect(req.getContextPath() +
                    (redirect != null && !redirect.isEmpty() ? "/" + redirect : "/"));

            } else {
                // Login
                String email    = req.getParameter("email");
                String password = req.getParameter("password");
                String pwHash   = sha256(password);
                Cliente cliente = clienteBean.login(email, pwHash);
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
