package it.paskinomercato.ejb.catalogo;

import javax.ejb.EJBLocalObject;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;
import java.util.List;

/**
 * EJB 2.0 Local Object interface for CatalogoBean.
 */
public interface CatalogoLocal extends EJBLocalObject {

    List<Prodotto> getProdotti(int pagina, int dimensionePagina);

    List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina);

    Prodotto getProdottoById(int id);

    Prodotto getProdottoByCodice(String codice);

    List<Prodotto> cercaProdotti(String testo);

    List<Categoria> getCategorie();

    Categoria getCategoriaById(int id);

    int contaProdotti();

    int contaProdottiPerCategoria(int categoriaId);

    boolean isDisponibile(int prodottoId, int quantita);
}
