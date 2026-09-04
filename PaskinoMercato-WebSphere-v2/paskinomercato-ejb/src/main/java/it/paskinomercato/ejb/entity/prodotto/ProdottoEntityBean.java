package it.paskinomercato.ejb.entity.prodotto;

import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * EJB 2.x BMP Entity Bean — Prodotto.
 *
 * Primary key  : Integer (mercato.prodotto.id)
 * Persistence  : Bean-Managed (all SQL coded here)
 *
 * Lifecycle:
 *   ejbCreate  → INSERT row, return generated PK
 *   ejbLoad    → SELECT row into instance fields
 *   ejbStore   → UPDATE row from instance fields
 *   ejbRemove  → DELETE row
 *   ejbFind*   → SELECT pk(s) — container calls ejbLoad afterwards
 */
public class ProdottoEntityBean implements EntityBean {

    // ----------------------------------------------------------------
    // Persistent fields (mirrored in ProdottoEntityLocal interface)
    // ----------------------------------------------------------------
    private Integer    id;
    private String     codice;
    private String     nomeIt;
    private String     nomeEn;
    private String     descrizioneIt;
    private String     descrizioneEn;
    private BigDecimal prezzo;
    private String     unitaMisura;
    private int        quantitaStock;
    private int        categoriaId;
    private String     immagine;
    private boolean    attivo;
    private double     pesoKg;

    private EntityContext ctx;

    // ----------------------------------------------------------------
    // EJB lifecycle
    // ----------------------------------------------------------------
    public void setEntityContext(EntityContext ctx) { this.ctx = ctx; }
    public void unsetEntityContext()               { this.ctx = null; }
    public void ejbActivate()  {}
    public void ejbPassivate() {}

    /**
     * ejbCreate: INSERT a new row; return the generated primary key.
     */
    public Integer ejbCreate(String codice, String nomeIt, String nomeEn,
                             String descrizioneIt, String descrizioneEn,
                             BigDecimal prezzo, String unitaMisura,
                             int quantitaStock, int categoriaId,
                             String immagine, double pesoKg) throws CreateException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.prodotto " +
                "(codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                " prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, peso_kg) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, true, ?) RETURNING id");
            ps.setString(1, codice);
            ps.setString(2, nomeIt);
            ps.setString(3, nomeEn);
            ps.setString(4, descrizioneIt);
            ps.setString(5, descrizioneEn);
            ps.setBigDecimal(6, prezzo);
            ps.setString(7, unitaMisura);
            ps.setInt(8, quantitaStock);
            ps.setInt(9, categoriaId);
            ps.setString(10, immagine);
            ps.setDouble(11, pesoKg);
            rs = ps.executeQuery();
            if (!rs.next()) throw new CreateException("INSERT prodotto returned no key");
            this.id           = rs.getInt(1);
            this.codice       = codice;
            this.nomeIt       = nomeIt;
            this.nomeEn       = nomeEn;
            this.descrizioneIt = descrizioneIt;
            this.descrizioneEn = descrizioneEn;
            this.prezzo       = prezzo;
            this.unitaMisura  = unitaMisura;
            this.quantitaStock = quantitaStock;
            this.categoriaId  = categoriaId;
            this.immagine     = immagine;
            this.attivo       = true;
            this.pesoKg       = pesoKg;
            return this.id;
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("ejbCreate prodotto failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public void ejbPostCreate(String codice, String nomeIt, String nomeEn,
                              String descrizioneIt, String descrizioneEn,
                              BigDecimal prezzo, String unitaMisura,
                              int quantitaStock, int categoriaId,
                              String immagine, double pesoKg) {}

    /**
     * ejbLoad: SELECT current row into fields.
     */
    public void ejbLoad() {
        Integer pk = (Integer) ctx.getPrimaryKey();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new EJBException("Prodotto not found: " + pk);
            this.id           = rs.getInt(1);
            this.codice       = rs.getString(2);
            this.nomeIt       = rs.getString(3);
            this.nomeEn       = rs.getString(4);
            this.descrizioneIt = rs.getString(5);
            this.descrizioneEn = rs.getString(6);
            this.prezzo       = rs.getBigDecimal(7);
            this.unitaMisura  = rs.getString(8);
            this.quantitaStock = rs.getInt(9);
            this.categoriaId  = rs.getInt(10);
            this.immagine     = rs.getString(11);
            this.attivo       = rs.getBoolean(12);
            this.pesoKg       = rs.getDouble(13);
        } catch (EJBException ee) {
            throw ee;
        } catch (Exception e) {
            throw new EJBException("ejbLoad prodotto failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    /**
     * ejbStore: UPDATE the row from current field values.
     */
    public void ejbStore() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "UPDATE mercato.prodotto SET " +
                "codice=?, nome_it=?, nome_en=?, descrizione_it=?, descrizione_en=?, " +
                "prezzo=?, unita_misura=?, quantita_stock=?, categoria_id=?, " +
                "immagine=?, attivo=?, peso_kg=? WHERE id=?");
            ps.setString(1, codice);
            ps.setString(2, nomeIt);
            ps.setString(3, nomeEn);
            ps.setString(4, descrizioneIt);
            ps.setString(5, descrizioneEn);
            ps.setBigDecimal(6, prezzo);
            ps.setString(7, unitaMisura);
            ps.setInt(8, quantitaStock);
            ps.setInt(9, categoriaId);
            ps.setString(10, immagine);
            ps.setBoolean(11, attivo);
            ps.setDouble(12, pesoKg);
            ps.setInt(13, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbStore prodotto failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    /**
     * ejbRemove: DELETE the row.
     */
    public void ejbRemove() {
        Connection con = null;
        PreparedStatement ps = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("DELETE FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EJBException("ejbRemove prodotto failed", e);
        } finally {
            closeQuietly(null, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Finder methods (return PKs; container loads instances via ejbLoad)
    // ----------------------------------------------------------------
    public Integer ejbFindByPrimaryKey(Integer pk) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.prodotto WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Prodotto not found: " + pk);
            return pk;
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Collection<Integer> ejbFindAll() throws FinderException {
        return findPks("SELECT id FROM mercato.prodotto ORDER BY nome_it", null, null);
    }

    public Collection<Integer> ejbFindByCategoriaId(int categoriaId) throws FinderException {
        return findPks(
            "SELECT id FROM mercato.prodotto WHERE categoria_id = ? AND attivo = true ORDER BY nome_it",
            "int", categoriaId);
    }

    public Collection<Integer> ejbFindByAttivo(boolean attivo) throws FinderException {
        return findPks(
            "SELECT id FROM mercato.prodotto WHERE attivo = ? ORDER BY nome_it",
            "bool", attivo);
    }

    public Integer ejbFindByCodice(String codice) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement("SELECT id FROM mercato.prodotto WHERE codice = ?");
            ps.setString(1, codice);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Prodotto not found by codice: " + codice);
            return rs.getInt(1);
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("ejbFindByCodice failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    public Collection<Integer> ejbFindByNomeContaining(String pattern) throws FinderException {
        Collection<Integer> pks = new ArrayList<Integer>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String like = "%" + pattern.toLowerCase() + "%";
            ps = con.prepareStatement(
                "SELECT id FROM mercato.prodotto WHERE attivo = true " +
                "AND (LOWER(nome_it) LIKE ? OR LOWER(nome_en) LIKE ? OR LOWER(codice) LIKE ?) " +
                "ORDER BY nome_it LIMIT 100");
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            rs = ps.executeQuery();
            while (rs.next()) pks.add(rs.getInt(1));
            return pks;
        } catch (Exception e) {
            throw new EJBException("ejbFindByNomeContaining failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    // ----------------------------------------------------------------
    // Business method accessors (exposed via ProdottoEntityLocal)
    // ----------------------------------------------------------------
    public Integer getId()                        { return id; }

    public String getCodice()                     { return codice; }
    public void setCodice(String codice)          { this.codice = codice; }

    public String getNomeIt()                     { return nomeIt; }
    public void setNomeIt(String nomeIt)          { this.nomeIt = nomeIt; }

    public String getNomeEn()                     { return nomeEn; }
    public void setNomeEn(String nomeEn)          { this.nomeEn = nomeEn; }

    public String getDescrizioneIt()              { return descrizioneIt; }
    public void setDescrizioneIt(String d)        { this.descrizioneIt = d; }

    public String getDescrizioneEn()              { return descrizioneEn; }
    public void setDescrizioneEn(String d)        { this.descrizioneEn = d; }

    public BigDecimal getPrezzo()                 { return prezzo; }
    public void setPrezzo(BigDecimal prezzo)      { this.prezzo = prezzo; }

    public String getUnitaMisura()                { return unitaMisura; }
    public void setUnitaMisura(String u)          { this.unitaMisura = u; }

    public int getQuantitaStock()                 { return quantitaStock; }
    public void setQuantitaStock(int q)           { this.quantitaStock = q; }

    public int getCategoriaId()                   { return categoriaId; }
    public void setCategoriaId(int c)             { this.categoriaId = c; }

    public String getImmagine()                   { return immagine; }
    public void setImmagine(String immagine)      { this.immagine = immagine; }

    public boolean isAttivo()                     { return attivo; }
    public void setAttivo(boolean attivo)         { this.attivo = attivo; }

    public double getPesoKg()                     { return pesoKg; }
    public void setPesoKg(double pesoKg)          { this.pesoKg = pesoKg; }

    // ----------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------
    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
        return ds.getConnection();
    }

    /** Generic single-param PK collector. type = "int" | "bool" | "str" | null */
    private Collection<Integer> findPks(String sql, String type, Object param) throws FinderException {
        Collection<Integer> pks = new ArrayList<Integer>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(sql);
            if (type != null) {
                if ("int".equals(type))   ps.setInt(1, (Integer) param);
                else if ("bool".equals(type)) ps.setBoolean(1, (Boolean) param);
                else ps.setString(1, (String) param);
            }
            rs = ps.executeQuery();
            while (rs.next()) pks.add(rs.getInt(1));
            return pks;
        } catch (Exception e) {
            throw new EJBException("findPks failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }
}
