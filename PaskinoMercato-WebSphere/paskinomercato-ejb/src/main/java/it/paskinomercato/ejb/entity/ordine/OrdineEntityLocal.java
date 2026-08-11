package it.paskinomercato.ejb.entity.ordine;

import javax.ejb.EJBLocalObject;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * EJB 2.x Local interface for the Ordine BMP Entity Bean.
 */
public interface OrdineEntityLocal extends EJBLocalObject {

    Integer getId();

    String getNumeroOrdine();

    int getClienteId();

    int getIndirizzoId();

    String getStato();
    void setStato(String stato);

    BigDecimal getTotale();
    void setTotale(BigDecimal totale);

    String getNote();
    void setNote(String note);

    boolean isEmailInviata();
    void setEmailInviata(boolean emailInviata);

    Timestamp getCreatedAt();
}
