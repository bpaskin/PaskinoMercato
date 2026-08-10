package it.paskinomercato.ejb.catalogo;

import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * EJB 2.0 Stateless Session Bean — Catalogo.
 * All persistence via direct JDBC using a JNDI DataSource.
 * No JPA, no annotations.
 */
public class CatalogoBean implements SessionBean {

    private SessionContext ctx;

    // ----------------------------------------------------------------
    // EJB 2.0 lifecycle
    // ----------------------------------------------------------------
    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    // ----------------------------------------------------------------
    // JNDI DataSource lookup
    // ----------------------------------------------------------------
    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    // ----------------------------------------------------------------
    // Business methods
    // ----------------------------------------------------------------

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        List<Prodotto> list = new ArrayList<Prodotto>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            int offset = (pagina - 1) * dimensionePagina;
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE attivo = true ORDER BY nome_it LIMIT ? OFFSET ?");
            ps.setInt(1, dimensionePagina);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProdotto(rs));
            }
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getProdotti failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        List<Prodotto> list = new ArrayList<Prodotto>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            int offset = (pagina - 1) * dimensionePagina;
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE attivo = true AND categoria_id = ? ORDER BY nome_it LIMIT ? OFFSET ?");
            ps.setInt(1, categoriaId);
            ps.setInt(2, dimensionePagina);
            ps.setInt(3, offset);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProdotto(rs));
            }
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getProdottiPerCategoria failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public Prodotto getProdottoById(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapProdotto(rs);
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getProdottoById failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public Prodotto getProdottoByCodice(String codice) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE codice = ?");
            ps.setString(1, codice);
            rs = ps.executeQuery();
            if (rs.next()) return mapProdotto(rs);
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getProdottoByCodice failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public List<Prodotto> cercaProdotti(String testo) {
        List<Prodotto> list = new ArrayList<Prodotto>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String pattern = "%" + testo.toLowerCase() + "%";
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE attivo = true " +
                "AND (LOWER(nome_it) LIKE ? OR LOWER(nome_en) LIKE ? OR LOWER(codice) LIKE ?) " +
                "ORDER BY nome_it LIMIT 100");
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapProdotto(rs));
            }
        } catch (Exception e) {
            throw new javax.ejb.EJBException("cercaProdotti failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return list;
    }

    public List<Categoria> getCategorie() {
        List<Categoria> list = new ArrayList<Categoria>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
                "FROM mercato.categoria ORDER BY nome_it");
            while (rs.next()) {
                list.add(mapCategoria(rs));
            }
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getCategorie failed", e);
        } finally {
            closeQuietly(rs, st, con);
        }
        return list;
    }

    public Categoria getCategoriaById(int id) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
                "FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            if (rs.next()) return mapCategoria(rs);
        } catch (Exception e) {
            throw new javax.ejb.EJBException("getCategoriaById failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return null;
    }

    public int contaProdotti() {
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT COUNT(*) FROM mercato.prodotto WHERE attivo = true");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new javax.ejb.EJBException("contaProdotti failed", e);
        } finally {
            closeQuietly(rs, st, con);
        }
        return 0;
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT COUNT(*) FROM mercato.prodotto WHERE attivo = true AND categoria_id = ?");
            ps.setInt(1, categoriaId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) {
            throw new javax.ejb.EJBException("contaProdottiPerCategoria failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return 0;
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT quantita_stock FROM mercato.prodotto WHERE id = ? AND attivo = true");
            ps.setInt(1, prodottoId);
            rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) >= quantita;
        } catch (Exception e) {
            throw new javax.ejb.EJBException("isDisponibile failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
        return false;
    }

    // ----------------------------------------------------------------
    // Private mappers
    // ----------------------------------------------------------------
    private Prodotto mapProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getInt(1));
        p.setCodice(rs.getString(2));
        p.setNomeIt(rs.getString(3));
        p.setNomeEn(rs.getString(4));
        p.setDescrizioneIt(rs.getString(5));
        p.setDescrizioneEn(rs.getString(6));
        p.setPrezzo(rs.getBigDecimal(7));
        p.setUnitaMisura(rs.getString(8));
        p.setQuantitaStock(rs.getInt(9));
        p.setCategoriaId(rs.getInt(10));
        p.setImmagine(rs.getString(11));
        p.setAttivo(rs.getBoolean(12));
        p.setPesoKg(rs.getDouble(13));
        return p;
    }

    private Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt(1));
        c.setCodice(rs.getString(2));
        c.setNomeIt(rs.getString(3));
        c.setNomeEn(rs.getString(4));
        c.setDescrizioneIt(rs.getString(5));
        c.setDescrizioneEn(rs.getString(6));
        c.setImmagine(rs.getString(7));
        return c;
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
