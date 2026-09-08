package it.paskinomercato.ejb.entity.prodotto;

import jakarta.ejb.Stateless;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import jakarta.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

@Stateless(name = "ProdottoEntityBean")
public class ProdottoEntityHomeBean implements ProdottoEntityService {

    private Connection getConnection() throws Exception {
        InitialContext ic = new InitialContext();
        DataSource ds = (DataSource) ic.lookup("jdbc/MercatoDB");
        return ds.getConnection();
    }

    private void closeQuietly(ResultSet rs, Statement st, Connection con) {
        try { if (rs  != null) rs.close();  } catch (Exception ignored) {}
        try { if (st  != null) st.close();  } catch (Exception ignored) {}
        try { if (con != null) con.close(); } catch (Exception ignored) {}
    }

    @Override
    public ProdottoEntityData create(String codice, String nomeIt, String nomeEn,
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
            Integer id = rs.getInt(1);
            return new ProdottoEntityDataImpl(id, codice, nomeIt, nomeEn, descrizioneIt, descrizioneEn,
                                               prezzo, unitaMisura, quantitaStock, categoriaId, immagine, true, pesoKg);
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("create prodotto failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public ProdottoEntityData findByPrimaryKey(Integer pk) throws FinderException {
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
            if (!rs.next()) throw new ObjectNotFoundException("Prodotto not found: " + pk);
            return new ProdottoEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getBigDecimal(7),
                rs.getString(8),
                rs.getInt(9),
                rs.getInt(10),
                rs.getString(11),
                rs.getBoolean(12),
                rs.getDouble(13)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByPrimaryKey failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Collection<ProdottoEntityData> findAll() throws FinderException {
        Collection<ProdottoEntityData> list = new ArrayList<>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto ORDER BY nome_it");
            while (rs.next()) {
                list.add(new ProdottoEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getBigDecimal(7),
                    rs.getString(8),
                    rs.getInt(9),
                    rs.getInt(10),
                    rs.getString(11),
                    rs.getBoolean(12),
                    rs.getDouble(13)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findAll prodotto failed", e);
        } finally {
            closeQuietly(rs, st, con);
        }
    }

    @Override
    public Collection<ProdottoEntityData> findByCategoriaId(int categoriaId) throws FinderException {
        Collection<ProdottoEntityData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE categoria_id = ? AND attivo = true ORDER BY nome_it");
            ps.setInt(1, categoriaId);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ProdottoEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getBigDecimal(7),
                    rs.getString(8),
                    rs.getInt(9),
                    rs.getInt(10),
                    rs.getString(11),
                    rs.getBoolean(12),
                    rs.getDouble(13)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findByCategoriaId failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Collection<ProdottoEntityData> findByAttivo(boolean attivo) throws FinderException {
        Collection<ProdottoEntityData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE attivo = ? ORDER BY nome_it");
            ps.setBoolean(1, attivo);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ProdottoEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getBigDecimal(7),
                    rs.getString(8),
                    rs.getInt(9),
                    rs.getInt(10),
                    rs.getString(11),
                    rs.getBoolean(12),
                    rs.getDouble(13)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findByAttivo failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public ProdottoEntityData findByCodice(String codice) throws FinderException {
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
            if (!rs.next()) throw new ObjectNotFoundException("Prodotto not found by codice: " + codice);
            return new ProdottoEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getBigDecimal(7),
                rs.getString(8),
                rs.getInt(9),
                rs.getInt(10),
                rs.getString(11),
                rs.getBoolean(12),
                rs.getDouble(13)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByCodice failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public Collection<ProdottoEntityData> findByNomeContaining(String pattern) throws FinderException {
        Collection<ProdottoEntityData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            String like = "%" + pattern.toLowerCase() + "%";
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, " +
                "prezzo, unita_misura, quantita_stock, categoria_id, immagine, attivo, COALESCE(peso_kg,0) " +
                "FROM mercato.prodotto WHERE attivo = true " +
                "AND (LOWER(nome_it) LIKE ? OR LOWER(nome_en) LIKE ? OR LOWER(codice) LIKE ?) " +
                "ORDER BY nome_it LIMIT 100");
            ps.setString(1, like);
            ps.setString(2, like);
            ps.setString(3, like);
            rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ProdottoEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getBigDecimal(7),
                    rs.getString(8),
                    rs.getInt(9),
                    rs.getInt(10),
                    rs.getString(11),
                    rs.getBoolean(12),
                    rs.getDouble(13)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findByNomeContaining failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }
}
