package it.paskinomercato.controller;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.service.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/lingua")
public class LinguaController {

    private final ClienteService clienteService;

    public LinguaController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping
    public String cambia(@RequestParam(defaultValue = "it") String lang,
                         HttpSession session,
                         HttpServletRequest request) {

        if (!"en".equals(lang) && !"it".equals(lang)) lang = "it";
        session.setAttribute("lang", lang);

        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            try {
                clienteService.aggiornaLingua(cliente.getId(), lang);
                cliente.setLingua(lang);
                session.setAttribute("cliente", cliente);
            } catch (Exception ignored) {}
        }

        String referer = request.getHeader("Referer");
        if (referer == null || referer.isBlank()) referer = "/";
        return "redirect:" + referer;
    }
}
