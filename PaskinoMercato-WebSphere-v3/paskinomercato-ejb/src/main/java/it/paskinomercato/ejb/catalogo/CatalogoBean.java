package it.paskinomercato.ejb.catalogo;

import it.paskinomercato.ejb.entity.prodotto.ProdottoEntityLocal;
import it.paskinomercato.ejb.entity.prodotto.ProdottoEntityLocalHome;
import it.paskinomercato.ejb.entity.categoria.CategoriaEntityLocal;
import it.paskinomercato.ejb.entity.categoria.CategoriaEntityLocalHome;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
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
    private ProdottoEntityLocalHome getProdottoHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (ProdottoEntityLocalHome) ic.lookup(
            "java:comp/env/ejb/ProdottoEntityBean");
    }

    private CategoriaEntityLocalHome getCategoriaHome() throws Exception {
        InitialContext ic = new InitialContext();
        return (CategoriaEntityLocalHome) ic.lookup(
            "java:comp/env/ejb/CategoriaEntityBean");
    }

    // ----------------------------------------------------------------
    // Business methods — now delegate to entity EJBs
    // ----------------------------------------------------------------

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            Collection<ProdottoEntityLocal> all =
                (Collection<ProdottoEntityLocal>) home.findByAttivo(true);
            return page(toValueObjects(all), pagina, dimensionePagina);
        } catch (Exception e) {
            throw new EJBException("getProdotti failed", e);
        }
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            Collection<ProdottoEntityLocal> byCategoria =
                (Collection<ProdottoEntityLocal>) home.findByCategoriaId(categoriaId);
            return page(toValueObjects(byCategoria), pagina, dimensionePagina);
        } catch (Exception e) {
            throw new EJBException("getProdottiPerCategoria failed", e);
        }
    }

    public Prodotto getProdottoById(int id) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            ProdottoEntityLocal entity = home.findByPrimaryKey(id);
            return toValueObject(entity);
        } catch (FinderException fe) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getProdottoById failed", e);
        }
    }

    public Prodotto getProdottoByCodice(String codice) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            ProdottoEntityLocal entity = home.findByCodice(codice);
            return toValueObject(entity);
        } catch (FinderException fe) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getProdottoByCodice failed", e);
        }
    }

    public List<Prodotto> cercaProdotti(String testo) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            Collection<ProdottoEntityLocal> found =
                (Collection<ProdottoEntityLocal>) home.findByNomeContaining(testo);
            return toValueObjects(found);
        } catch (Exception e) {
            throw new EJBException("cercaProdotti failed", e);
        }
    }

    public List<Categoria> getCategorie() {
        try {
            CategoriaEntityLocalHome home = getCategoriaHome();
            Collection<CategoriaEntityLocal> all =
                (Collection<CategoriaEntityLocal>) home.findAll();
            List<Categoria> list = new ArrayList<Categoria>();
            for (Iterator<CategoriaEntityLocal> it = all.iterator(); it.hasNext();) {
                list.add(toValueObject(it.next()));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("getCategorie failed", e);
        }
    }

    public Categoria getCategoriaById(int id) {
        try {
            CategoriaEntityLocalHome home = getCategoriaHome();
            CategoriaEntityLocal entity = home.findByPrimaryKey(id);
            return toValueObject(entity);
        } catch (FinderException fe) {
            return null;
        } catch (Exception e) {
            throw new EJBException("getCategoriaById failed", e);
        }
    }

    public int contaProdotti() {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            return home.findByAttivo(true).size();
        } catch (Exception e) {
            throw new EJBException("contaProdotti failed", e);
        }
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            return home.findByCategoriaId(categoriaId).size();
        } catch (Exception e) {
            throw new EJBException("contaProdottiPerCategoria failed", e);
        }
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        try {
            ProdottoEntityLocalHome home = getProdottoHome();
            ProdottoEntityLocal entity = home.findByPrimaryKey(prodottoId);
            return entity.isAttivo() && entity.getQuantitaStock() >= quantita;
        } catch (FinderException fe) {
            return false;
        } catch (Exception e) {
            throw new EJBException("isDisponibile failed", e);
        }
    }

    // ----------------------------------------------------------------
    // Value-object mappers
    // ----------------------------------------------------------------
    private Prodotto toValueObject(ProdottoEntityLocal e) {
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

    private Categoria toValueObject(CategoriaEntityLocal e) {
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

    private List<Prodotto> toValueObjects(Collection<ProdottoEntityLocal> col) {
        List<Prodotto> list = new ArrayList<Prodotto>();
        for (Iterator<ProdottoEntityLocal> it = col.iterator(); it.hasNext();) {
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
