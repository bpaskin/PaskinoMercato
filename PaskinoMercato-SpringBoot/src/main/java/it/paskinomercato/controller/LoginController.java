package it.paskinomercato.controller;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.service.ClienteService;
import it.paskinomercato.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Controller
@RequestMapping("/login")
public class LoginController {

    private final ClienteService clienteService;
    private final MailService    mailService;

    public LoginController(ClienteService clienteService, MailService mailService) {
        this.clienteService = clienteService;
        this.mailService    = mailService;
    }

    @GetMapping
    public String view(@RequestParam(required = false) String azione,
                       HttpSession session) {
        if ("logout".equals(azione)) {
            session.invalidate();
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping
    public String action(
            @RequestParam String azione,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String nome,
            @RequestParam(required = false) String cognome,
            @RequestParam(required = false) String telefono,
            @RequestParam(required = false) String lingua,
            @RequestParam(required = false) String lang,
            @RequestParam(required = false) String redirect,
            HttpSession session,
            Model model) {

        if ("registra".equals(azione)) {
            if (clienteService.getClienteByEmail(email) != null) {
                String errLang = lingua != null ? lingua : "it";
                model.addAttribute("errore", "en".equals(errLang)
                    ? "Email already registered."
                    : "Email già registrata.");
                return "login";
            }
            String pwHash  = sha256(password);
            String effLang = lingua != null ? lingua : "it";
            Cliente nuovo  = clienteService.registra(email, pwHash, nome, cognome, telefono, effLang);
            session.setAttribute("cliente", nuovo);
            session.setAttribute("lang",    effLang);
            mailService.inviaRegistrazioneConferma(nuovo, effLang);
            return "redirect:/" + (redirect != null && !redirect.isBlank() ? redirect : "");
        }

        // Login
        String pwHash  = sha256(password);
        Cliente cliente = clienteService.login(email, pwHash);
        if (cliente == null) {
            model.addAttribute("errore", "it".equals(lang)
                ? "Credenziali non valide."
                : "Invalid credentials.");
            return "login";
        }
        session.setAttribute("cliente", cliente);
        session.setAttribute("lang", cliente.getLingua() != null ? cliente.getLingua() : "it");
        return "redirect:/" + (redirect != null && !redirect.isBlank() ? redirect : "");
    }

    private String sha256(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 failed", e);
        }
    }
}
