package it.paskinomercato.ejb.entity.cliente;

import javax.ejb.EJBLocalObject;

/**
 * EJB 2.x Local interface for the Cliente BMP Entity Bean.
 */
public interface ClienteEntityLocal extends EJBLocalObject {

    Integer getId();

    String getEmail();
    void setEmail(String email);

    String getNome();
    void setNome(String nome);

    String getCognome();
    void setCognome(String cognome);

    String getTelefono();
    void setTelefono(String telefono);

    String getLingua();
    void setLingua(String lingua);

    boolean isAttivo();
    void setAttivo(boolean attivo);
}
