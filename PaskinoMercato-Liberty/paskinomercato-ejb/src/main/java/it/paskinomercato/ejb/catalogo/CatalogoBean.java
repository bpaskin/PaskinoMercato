package it.paskinomercato.ejb.catalogo;

import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

/**
 * CDI ApplicationScoped service — Catalogo.
 * All persistence via JPA EntityManager (persistence unit MercatoPU).
 */
@ApplicationScoped
@Transactional(Transactional.TxType.SUPPORTS)
public class CatalogoBean implements CatalogoService {

    @PersistenceContext(unitName = "MercatoPU")
    private EntityManager em;

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        int offset = (pagina - 1) * dimensionePagina;
        TypedQuery<Prodotto> q = em.createQuery(
            "SELECT p FROM Prodotto p WHERE p.attivo = true ORDER BY p.nomeIt",
            Prodotto.class);
        q.setFirstResult(offset);
        q.setMaxResults(dimensionePagina);
        return q.getResultList();
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        int offset = (pagina - 1) * dimensionePagina;
        TypedQuery<Prodotto> q = em.createQuery(
            "SELECT p FROM Prodotto p WHERE p.attivo = true AND p.categoriaId = :catId ORDER BY p.nomeIt",
            Prodotto.class);
        q.setParameter("catId", categoriaId);
        q.setFirstResult(offset);
        q.setMaxResults(dimensionePagina);
        return q.getResultList();
    }

    public Prodotto getProdottoById(int id) {
        return em.find(Prodotto.class, id);
    }

    public Prodotto getProdottoByCodice(String codice) {
        List<Prodotto> results = em.createQuery(
            "SELECT p FROM Prodotto p WHERE p.codice = :codice",
            Prodotto.class)
            .setParameter("codice", codice)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Prodotto> cercaProdotti(String testo) {
        String pattern = "%" + testo.toLowerCase() + "%";
        TypedQuery<Prodotto> q = em.createQuery(
            "SELECT p FROM Prodotto p WHERE p.attivo = true " +
            "AND (LOWER(p.nomeIt) LIKE :pat OR LOWER(p.nomeEn) LIKE :pat OR LOWER(p.codice) LIKE :pat) " +
            "ORDER BY p.nomeIt",
            Prodotto.class);
        q.setParameter("pat", pattern);
        q.setMaxResults(100);
        return q.getResultList();
    }

    public List<Categoria> getCategorie() {
        return em.createQuery(
            "SELECT c FROM Categoria c ORDER BY c.nomeIt",
            Categoria.class)
            .getResultList();
    }

    public Categoria getCategoriaById(int id) {
        return em.find(Categoria.class, id);
    }

    public int contaProdotti() {
        Long count = em.createQuery(
            "SELECT COUNT(p) FROM Prodotto p WHERE p.attivo = true",
            Long.class)
            .getSingleResult();
        return count.intValue();
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        Long count = em.createQuery(
            "SELECT COUNT(p) FROM Prodotto p WHERE p.attivo = true AND p.categoriaId = :catId",
            Long.class)
            .setParameter("catId", categoriaId)
            .getSingleResult();
        return count.intValue();
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        Prodotto p = em.find(Prodotto.class, prodottoId);
        return p != null && p.isAttivo() && p.getQuantitaStock() >= quantita;
    }
}
