package it.paskinomercato.controller;

import it.paskinomercato.model.Categoria;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.service.CatalogoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/catalogo")
public class CatalogoController {

    private final CatalogoService catalogo;

    public CatalogoController(CatalogoService catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public String catalogo(
            @RequestParam(defaultValue = "1") int p,
            @RequestParam(defaultValue = "0") int cat,
            @RequestParam(required = false)   String cerca,
            Model model) {

        int pagina = Math.max(1, p);
        List<Prodotto> prodotti;
        int totaleProdotti;

        if (cerca != null && !cerca.isBlank()) {
            prodotti       = catalogo.cercaProdotti(cerca.trim());
            totaleProdotti = prodotti.size();
            model.addAttribute("cercaTesto", cerca.trim());
        } else if (cat > 0) {
            prodotti       = catalogo.getProdottiPerCategoria(cat, pagina, catalogo.getPaginaDim());
            totaleProdotti = catalogo.contaProdottiPerCategoria(cat);
            model.addAttribute("categoriaSelezionata", catalogo.getCategoriaById(cat));
        } else {
            prodotti       = catalogo.getProdotti(pagina, catalogo.getPaginaDim());
            totaleProdotti = catalogo.contaProdotti();
        }

        List<Categoria> categorie = catalogo.getCategorie();
        int totalePagine = (int) Math.ceil((double) totaleProdotti / catalogo.getPaginaDim());

        model.addAttribute("prodotti",       prodotti);
        model.addAttribute("categorie",      categorie);
        model.addAttribute("paginaCorrente", pagina);
        model.addAttribute("totalePagine",   totalePagine);
        model.addAttribute("totaleProdotti", totaleProdotti);
        model.addAttribute("categoriaId",    cat);

        return "catalogo";
    }
}
