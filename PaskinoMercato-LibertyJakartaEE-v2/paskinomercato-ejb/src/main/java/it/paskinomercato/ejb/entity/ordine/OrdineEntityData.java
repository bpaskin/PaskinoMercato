package it.paskinomercato.ejb.entity.ordine;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Local data interface for the Ordine entity.
 */
public interface OrdineEntityData {

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
