package it.paskinomercato.ejb.entity.categoria;

import jakarta.ejb.Stateless;
import jakarta.ejb.CreateException;
import jakarta.ejb.EJBException;
import jakarta.ejb.FinderException;
import jakarta.ejb.ObjectNotFoundException;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;

@Stateless(name = "CategoriaEntityBean")
public class CategoriaEntityHomeBean implements CategoriaEntityService {

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
    public CategoriaEntityData create(String codice, String nomeIt, String nomeEn,
                                        String descrizioneIt, String descrizioneEn,
                                        String immagine) throws CreateException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "INSERT INTO mercato.categoria " +
                "(codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id");
            ps.setString(1, codice);
            ps.setString(2, nomeIt);
            ps.setString(3, nomeEn);
            ps.setString(4, descrizioneIt);
            ps.setString(5, descrizioneEn);
            ps.setString(6, immagine);
            rs = ps.executeQuery();
            if (!rs.next()) throw new CreateException("INSERT categoria returned no key");
            Integer id = rs.getInt(1);
            return new CategoriaEntityDataImpl(id, codice, nomeIt, nomeEn, descrizioneIt, descrizioneEn, immagine);
        } catch (CreateException ce) {
            throw ce;
        } catch (Exception e) {
            throw new CreateException("create categoria failed: " + e.getMessage());
        } finally {
            closeQuietly(rs, ps, con);
        }
    }

    @Override
    public CategoriaEntityData findByPrimaryKey(Integer pk) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
                "FROM mercato.categoria WHERE id = ?");
            ps.setInt(1, pk);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Categoria not found: " + pk);
            return new CategoriaEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getString(7)
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
    public Collection<CategoriaEntityData> findAll() throws FinderException {
        Collection<CategoriaEntityData> list = new ArrayList<>();
        Connection con = null;
        Statement st = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            st = con.createStatement();
            rs = st.executeQuery("SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine FROM mercato.categoria ORDER BY nome_it");
            while (rs.next()) {
                list.add(new CategoriaEntityDataImpl(
                    rs.getInt(1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getString(6),
                    rs.getString(7)
                ));
            }
            return list;
        } catch (Exception e) {
            throw new EJBException("findAll categoria failed", e);
        } finally {
            closeQuietly(rs, st, con);
        }
    }

    @Override
    public CategoriaEntityData findByCodice(String codice) throws FinderException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = getConnection();
            ps = con.prepareStatement(
                "SELECT id, codice, nome_it, nome_en, descrizione_it, descrizione_en, immagine " +
                "FROM mercato.categoria WHERE codice = ?");
            ps.setString(1, codice);
            rs = ps.executeQuery();
            if (!rs.next()) throw new ObjectNotFoundException("Categoria not found by codice: " + codice);
            return new CategoriaEntityDataImpl(
                rs.getInt(1),
                rs.getString(2),
                rs.getString(3),
                rs.getString(4),
                rs.getString(5),
                rs.getString(6),
                rs.getString(7)
            );
        } catch (FinderException fe) {
            throw fe;
        } catch (Exception e) {
            throw new EJBException("findByCodice failed", e);
        } finally {
            closeQuietly(rs, ps, con);
        }
    }
}
