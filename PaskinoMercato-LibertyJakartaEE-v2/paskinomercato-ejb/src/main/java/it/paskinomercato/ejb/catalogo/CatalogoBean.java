package it.paskinomercato.ejb.catalogo;

import it.paskinomercato.ejb.entity.prodotto.ProdottoEntityData;
import it.paskinomercato.ejb.entity.prodotto.ProdottoEntityService;
import it.paskinomercato.ejb.entity.categoria.CategoriaEntityData;
import it.paskinomercato.ejb.entity.categoria.CategoriaEntityService;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;

import jakarta.ejb.SessionBean;
import jakarta.ejb.SessionContext;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import javax.naming.InitialContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * EJB 2.0 Stateless Session Bean — Catalogo.
 * Delegates all persistence to ProdottoEntityBean and CategoriaEntityBean (BMP Entity EJBs).
 */
public class CatalogoBean implements SessionBean {

    private SessionContext ctx;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    // ----------------------------------------------------------------
    // Entity home lookups through this bean's component environment.
    // The ejb-local-ref declarations use ejb-link, so WebSphere resolves
    // them directly to the entity beans in this EJB module.
    // ----------------------------------------------------------------
    private ProdottoEntityService getProdottoHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (ProdottoEntityService) ic.lookup(
            "java:comp/env/ejb/ProdottoEntityBean");
    }

    private CategoriaEntityService getCategoriaHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (CategoriaEntityService) ic.lookup(
            "java:comp/env/ejb/CategoriaEntityBean");
    }

    // ----------------------------------------------------------------
    // Business methods — now delegate to entity EJBs
    // ----------------------------------------------------------------

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        try {
            ProdottoEntityService home = getProdottoHome();
            Collection<ProdottoEntityData> all =
                (Collection<ProdottoEntityData>) home.findByAttivo(true);
            return page(toValueObjects(all), pagina, dimensionePagina);
        } catch (Exception e) {
            throw new EJBException("getProdotti failed", e);
        }
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        try {
            ProdottoEntityService home = getProdottoHome();
            Collection<ProdottoEntityData> byCategoria =
                (Collection<ProdottoEntityData>) home.findByCategoriaId(categoriaId);
            return page(toValueObjects(byCategoria), pagina, dimensionePagina);
        } catch (Exception e) {
            throw new EJBException("getProdottiPerCategoria failed", e);
        }
    }

    public Prodotto getProdottoById(int id) {
        try {
            ProdottoEntityService home = getProdottoHome();
            ProdottoEntityData entity = home.findByPrimaryKey(id);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getProdottoById failed", e);
        }
    }

    public Prodotto getProdottoByCodice(String codice) {
        try {
            ProdottoEntityService home = getProdottoHome();
            ProdottoEntityData entity = home.findByCodice(codice);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getProdottoByCodice failed", e);
        }
    }

    public List<Prodotto> cercaProdotti(String testo) {
        try {
            ProdottoEntityService home = getProdottoHome();
            Collection<ProdottoEntityData> found =
                (Collection<ProdottoEntityData>) home.findByNomeContaining(testo);
            return toValueObjects(found);
        } catch (Exception e) {
            throw new EJBException("cercaProdotti failed", e);
        }
    }

    public List<Categoria> getCategorie() {
        try {
            CategoriaEntityService home = getCategoriaHome();
            Collection<CategoriaEntityData> all =
                (Collection<CategoriaEntityData>) home.findAll();
            List<Categoria> list = new ArrayList<Categoria>();
            for (Iterator<CategoriaEntityData> it = all.iterator(); it.hasNext();) {
                list.add(toValueObject(it.next()));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("getCategorie failed", e);
        }
    }

    public Categoria getCategoriaById(int id) {
        try {
            CategoriaEntityService home = getCategoriaHome();
            CategoriaEntityData entity = home.findByPrimaryKey(id);
            return toValueObject(entity);
        } catch (FinderException _) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getCategoriaById failed", e);
        }
    }

    public int contaProdotti() {
        try {
            ProdottoEntityService home = getProdottoHome();
            return home.findByAttivo(true).size();
        } catch (Exception e) {
            throw new EJBException("contaProdotti failed", e);
        }
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        try {
            ProdottoEntityService home = getProdottoHome();
            return home.findByCategoriaId(categoriaId).size();
        } catch (Exception e) {
            throw new EJBException("contaProdottiPerCategoria failed", e);
        }
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        try {
            ProdottoEntityService home = getProdottoHome();
            ProdottoEntityData entity = home.findByPrimaryKey(prodottoId);
            return entity.isAttivo() && entity.getQuantitaStock() >= quantita;
        } catch (FinderException _) {
            return false;
        } catch (Exception e) {
            throw new EJBException("isDisponibile failed", e);
        }
    }

    // ----------------------------------------------------------------
    // Value-object mappers
    // ----------------------------------------------------------------
    private Prodotto toValueObject(ProdottoEntityData e) {
        Prodotto p = new Prodotto();
        p.setId(e.getId());
        p.setCodice(e.getCodice());
        p.setNomeIt(e.getNomeIt());
        p.setNomeEn(e.getNomeEn());
        p.setDescrizioneIt(e.getDescrizioneIt());
        p.setDescrizioneEn(e.getDescrizioneEn());
        p.setPrezzo(e.getPrezzo());
        p.setUnitaMisura(e.getUnitaMisura());
        p.setQuantitaStock(e.getQuantitaStock());
        p.setCategoriaId(e.getCategoriaId());
        p.setImmagine(e.getImmagine());
        p.setAttivo(e.isAttivo());
        p.setPesoKg(e.getPesoKg());
        return p;
    }

    private Categoria toValueObject(CategoriaEntityData e) {
        Categoria c = new Categoria();
        c.setId(e.getId());
        c.setCodice(e.getCodice());
        c.setNomeIt(e.getNomeIt());
        c.setNomeEn(e.getNomeEn());
        c.setDescrizioneIt(e.getDescrizioneIt());
        c.setDescrizioneEn(e.getDescrizioneEn());
        c.setImmagine(e.getImmagine());
        return c;
    }

    private List<Prodotto> toValueObjects(Collection<ProdottoEntityData> col) {
        List<Prodotto> list = new ArrayList<Prodotto>();
        for (Iterator<ProdottoEntityData> it = col.iterator(); it.hasNext();) {
            list.add(toValueObject(it.next()));
        }
        return list;
    }

    /** Returns a sub-list for the requested page (1-based). */
    private List<Prodotto> page(List<Prodotto> all, int pagina, int dim) {
        int offset = (pagina - 1) * dim;
        if (offset >= all.size()) return new ArrayList<Prodotto>();
        int end = Math.min(offset + dim, all.size());
        return new ArrayList<Prodotto>(all.subList(offset, end));
    }
}
