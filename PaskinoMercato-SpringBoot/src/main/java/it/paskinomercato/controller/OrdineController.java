package it.paskinomercato.controller;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.service.OrdineService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/ordini")
public class OrdineController {

    private final OrdineService ordineService;

    public OrdineController(OrdineService ordineService) {
        this.ordineService = ordineService;
    }

    @GetMapping
    public String ordini(@RequestParam(required = false) String numero,
                          HttpSession session,
                          Model model) {

        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) return "redirect:/login?redirect=ordini";

        if (numero != null && !numero.isBlank()) {
            Ordine ordine = ordineService.getOrdineByNumero(numero);
            if (ordine == null || ordine.getClienteId() != cliente.getId()) {
                return "redirect:/ordini";
            }
            List<RigaOrdine> righe = ordineService.getRigheOrdine(ordine.getId());
            model.addAttribute("ordine", ordine);
            model.addAttribute("righe",  righe);
            return "dettaglioOrdine";
        }

        List<Ordine> ordini = ordineService.getOrdiniCliente(cliente.getId());
        model.addAttribute("ordini", ordini);
        return "ordini";
    }
}
