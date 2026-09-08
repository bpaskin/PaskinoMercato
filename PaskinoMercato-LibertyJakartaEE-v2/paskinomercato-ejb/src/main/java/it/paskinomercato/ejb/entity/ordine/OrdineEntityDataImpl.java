package it.paskinomercato.ejb.entity.ordine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import javax.naming.InitialContext;
import javax.sql.DataSource;

/**
 * Modernized POJO implementation of OrdineEntityLocal.
 */
public class OrdineEntityDataImpl implements OrdineEntityData {

    private Integer    id;
    private String     numeroOrdine;
    private int        clienteId;
    private int        indirizzoId;
    private String     stato;
    private BigDecimal totale;
    private String     note;
    private boolean    emailInviata;
    private Timestamp  createdAt;

    public OrdineEntityDataImpl(Integer id, String numeroOrdine, int clienteId, int indirizzoId,
                                 String stato, BigDecimal totale, String note,
                                 boolean emailInviata, Timestamp createdAt) {
        this.id = id;
        this.numeroOrdine = numeroOrdine;
        this.clienteId = clienteId;
        this.indirizzoId = indirizzoId;
        this.stato = stato;
        this.totale = totale;
        this.note = note;
        this.emailInviata = emailInviata;
        this.createdAt = createdAt;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getNumeroOrdine() { return numeroOrdine; }

    @Override
    public int getClienteId() { return clienteId; }

    @Override
    public int getIndirizzoId() { return indirizzoId; }

    @Override
    public String getStato() { return stato; }

    @Override
    public void setStato(String stato) {
        this.stato = stato;
        Connection con = null;
        PreparedStatement ps = null;
        try {
            InitialContext ic = new InitialContext();
            DataSource ds = (DataSource) ic.lookup("jdbc/MercatoDB");
            con = ds.getConnection();
            ps = con.prepareStatement("UPDATE mercato.ordine SET stato = ? WHERE id = ?");
            ps.setString(1, stato);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to update stato in database", e);
        } finally {
            try { if (ps != null) ps.close(); } catch (Exception ignored) {}
            try { if (con != null) con.close(); } catch (Exception ignored) {}
        }
    }

    @Override
    public BigDecimal getTotale() { return totale; }

    @Override
    public void setTotale(BigDecimal totale) { this.totale = totale; }

    @Override
    public String getNote() { return note; }

    @Override
    public void setNote(String note) { this.note = note; }

    @Override
    public boolean isEmailInviata() { return emailInviata; }

    @Override
    public void setEmailInviata(boolean emailInviata) { this.emailInviata = emailInviata; }

    @Override
    public Timestamp getCreatedAt() { return createdAt; }
}
