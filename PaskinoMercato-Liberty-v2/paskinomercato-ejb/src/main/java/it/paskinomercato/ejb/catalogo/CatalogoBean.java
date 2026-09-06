package it.paskinomercato.ejb.catalogo;

import it.paskinomercato.model.Categoria;
import it.paskinomercato.model.Prodotto;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;

/**
 * EJB 2.x Stateless Session Bean — Catalogo.
 * Declared entirely via ejb-jar.xml; uses JDBC against jdbc/MercatoDB.
 */
public class CatalogoBean implements SessionBean {

    private SessionContext ctx;

    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }
    public void ejbCreate()    {}
    public void ejbRemove()    {}
    public void ejbActivate()  {}
    public void ejbPassivate() {}

    // EJBLocalObject stubs — the container proxy overrides these at runtime
    public EJBLocalHome getEJBLocalHome()              { throw new UnsupportedOperationException(); }
    public Object getPrimaryKey()                       { throw new UnsupportedOperationException(); }
    public void remove()                                { throw new UnsupportedOperationException(); }
    public boolean isIdentical(javax.ejb.EJBLocalObject o) { throw new UnsupportedOperationException(); }

    // -------------------------------------------------------------------------

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    private static Prodotto mapProdotto(ResultSet rs) throws SQLException {
        Prodotto p = new Prodotto();
        p.setId(rs.getInt("id"));
        p.setCodice(rs.getString("codice"));
        p.setNomeIt(rs.getString("nome_it"));
        p.setNomeEn(rs.getString("nome_en"));
        p.setDescrizioneIt(rs.getString("descrizione_it"));
        p.setDescrizioneEn(rs.getString("descrizione_en"));
        p.setPrezzo(rs.getBigDecimal("prezzo"));
        p.setUnitaMisura(rs.getString("unita_misura"));
        p.setQuantitaStock(rs.getInt("quantita_stock"));
        p.setCategoriaId(rs.getInt("categoria_id"));
        p.setImmagine(rs.getString("immagine"));
        p.setAttivo(rs.getBoolean("attivo"));
        p.setPesoKg(rs.getDouble("peso_kg"));
        return p;
    }

    private static Categoria mapCategoria(ResultSet rs) throws SQLException {
        Categoria c = new Categoria();
        c.setId(rs.getInt("id"));
        c.setCodice(rs.getString("codice"));
        c.setNomeIt(rs.getString("nome_it"));
        c.setNomeEn(rs.getString("nome_en"));
        c.setDescrizioneIt(rs.getString("descrizione_it"));
        c.setDescrizioneEn(rs.getString("descrizione_en"));
        c.setImmagine(rs.getString("immagine"));
        return c;
    }

    private static void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }

    public List<Prodotto> getProdotti(int pagina, int dimensionePagina) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            int offset = (pagina - 1) * dimensionePagina;
            ps = con.prepareStatement(
                "SELECT * FROM mercato.prodotto WHERE attivo = true ORDER BY nome_it LIMIT ? OFFSET ?");
            ps.setInt(1, dimensionePagina);
            ps.setInt(2, offset);
            rs = ps.executeQuery();
            List<Prodotto> list = new ArrayList<Prodotto>();
            while (rs.next()) list.add(mapProdotto(rs));
            return list;
        } catch (Exception e) { throw new EJBException("getProdotti failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public List<Prodotto> getProdottiPerCategoria(int categoriaId, int pagina, int dimensionePagina) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            int offset = (pagina - 1) * dimensionePagina;
            ps = con.prepareStatement(
                "SELECT * FROM mercato.prodotto WHERE categoria_id = ? AND attivo = true ORDER BY nome_it LIMIT ? OFFSET ?");
            ps.setInt(1, categoriaId);
            ps.setInt(2, dimensionePagina);
            ps.setInt(3, offset);
            rs = ps.executeQuery();
            List<Prodotto> list = new ArrayList<Prodotto>();
            while (rs.next()) list.add(mapProdotto(rs));
            return list;
        } catch (Exception e) { throw new EJBException("getProdottiPerCategoria failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public Prodotto getProdottoById(int id) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT * FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapProdotto(rs) : null;
        } catch (Exception e) { throw new EJBException("getProdottoById failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public Prodotto getProdottoByCodice(String codice) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT * FROM mercato.prodotto WHERE codice = ?");
            ps.setString(1, codice);
            rs = ps.executeQuery();
            return rs.next() ? mapProdotto(rs) : null;
        } catch (Exception e) { throw new EJBException("getProdottoByCodice failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public List<Prodotto> cercaProdotti(String testo) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            String like = "%" + testo.toLowerCase() + "%";
            ps = con.prepareStatement(
                "SELECT * FROM mercato.prodotto WHERE attivo = true " +
                "AND (LOWER(nome_it) LIKE ? OR LOWER(nome_en) LIKE ? OR LOWER(codice) LIKE ?) " +
                "ORDER BY nome_it LIMIT 100");
            ps.setString(1, like); ps.setString(2, like); ps.setString(3, like);
            rs = ps.executeQuery();
            List<Prodotto> list = new ArrayList<Prodotto>();
            while (rs.next()) list.add(mapProdotto(rs));
            return list;
        } catch (Exception e) { throw new EJBException("cercaProdotti failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public List<Categoria> getCategorie() {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT * FROM mercato.categoria ORDER BY nome_it");
            rs = ps.executeQuery();
            List<Categoria> list = new ArrayList<Categoria>();
            while (rs.next()) list.add(mapCategoria(rs));
            return list;
        } catch (Exception e) { throw new EJBException("getCategorie failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public Categoria getCategoriaById(int id) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT * FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, id);
            rs = ps.executeQuery();
            return rs.next() ? mapCategoria(rs) : null;
        } catch (Exception e) { throw new EJBException("getCategoriaById failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public int contaProdotti() {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT COUNT(*) FROM mercato.prodotto WHERE attivo = true");
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { throw new EJBException("contaProdotti failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public int contaProdottiPerCategoria(int categoriaId) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT COUNT(*) FROM mercato.prodotto WHERE categoria_id = ? AND attivo = true");
            ps.setInt(1, categoriaId);
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { throw new EJBException("contaProdottiPerCategoria failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }

    public boolean isDisponibile(int prodottoId, int quantita) {
        Connection con = null; PreparedStatement ps = null; ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT quantita_stock, attivo FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, prodottoId);
            rs = ps.executeQuery();
            if (!rs.next()) return false;
            return rs.getBoolean("attivo") && rs.getInt("quantita_stock") >= quantita;
        } catch (Exception e) { throw new EJBException("isDisponibile failed", e);
        } finally { closeQuietly(rs, ps, con); }
    }
}
