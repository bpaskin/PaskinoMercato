package it.paskinomercato.controller;

import it.paskinomercato.cart.CarrelloSessionBean;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.service.ClienteService;
import it.paskinomercato.service.MailService;
import it.paskinomercato.service.OrdineService;
import it.paskinomercato.util.IndirizzoItaliaValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/checkout")
public class CheckoutController {

    private final ClienteService      clienteService;
    private final OrdineService       ordineService;
    private final MailService         mailService;
    private final CarrelloSessionBean carrello;

    public CheckoutController(ClienteService clienteService,
                               OrdineService ordineService,
                               MailService mailService,
                               CarrelloSessionBean carrello) {
        this.clienteService = clienteService;
        this.ordineService  = ordineService;
        this.mailService    = mailService;
        this.carrello       = carrello;
    }

    @GetMapping
    public String view(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) return "redirect:/login?redirect=checkout";

        if (carrello.getNumeroArticoli() == 0) return "redirect:/carrello";

        List<Indirizzo> indirizzi = clienteService.getIndirizzi(cliente.getId());
        model.addAttribute("indirizzi",     indirizzi);
        model.addAttribute("carrelloItems", carrello.getItems());
        model.addAttribute("totale",        carrello.getTotale());
        return "checkout";
    }

    @PostMapping
    public String conferma(
            @RequestParam(required = false) String indirizzoId,
            @RequestParam(required = false) String via,
            @RequestParam(required = false) String civico,
            @RequestParam(required = false) String citta,
            @RequestParam(required = false) String cap,
            @RequestParam(required = false) String provincia,
            @RequestParam(required = false) String note,
            HttpSession session,
            Model model) {

        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) return "redirect:/login?redirect=checkout";
        if (carrello.getNumeroArticoli() == 0) return "redirect:/carrello";

        int resolvedIndirizzoId = 0;

        if (indirizzoId != null && !indirizzoId.isBlank()) {
            resolvedIndirizzoId = Integer.parseInt(indirizzoId);
            Indirizzo ind = clienteService.getIndirizzo(resolvedIndirizzoId);
            if (ind == null || ind.getClienteId() != cliente.getId()) {
                model.addAttribute("errore", "Indirizzo non valido / Invalid address");
                return view(session, model);
            }
            IndirizzoItaliaValidator.ValidationResult vr =
                IndirizzoItaliaValidator.valida(ind.getPaese(), ind.getCap(), ind.getProvincia());
            if (!vr.isValid()) {
                model.addAttribute("errore", vr.getErrorMessage());
                return view(session, model);
            }
        } else {
            IndirizzoItaliaValidator.ValidationResult vr =
                IndirizzoItaliaValidator.valida("IT", cap, provincia);
            if (!vr.isValid()) {
                model.addAttribute("errore", vr.getErrorMessage());
                return view(session, model);
            }
            clienteService.aggiungiIndirizzo(cliente.getId(), via, civico, citta, cap, provincia);
            List<Indirizzo> indirizzi = clienteService.getIndirizzi(cliente.getId());
            resolvedIndirizzoId = indirizzi.get(indirizzi.size() - 1).getId();
        }

        String numeroOrdine = ordineService.creaOrdine(
            cliente.getId(), resolvedIndirizzoId, carrello.getItems(), note);

        Ordine ordine        = ordineService.getOrdineByNumero(numeroOrdine);
        List<RigaOrdine> righe = ordineService.getRigheOrdine(ordine.getId());
        String lang          = (String) session.getAttribute("lang");
        mailService.inviaConfermaOrdine(cliente, ordine, righe, lang != null ? lang : "it");

        carrello.svuota();

        model.addAttribute("ordine", ordine);
        model.addAttribute("righe",  righe);
        return "confermaOrdine";
    }
}
