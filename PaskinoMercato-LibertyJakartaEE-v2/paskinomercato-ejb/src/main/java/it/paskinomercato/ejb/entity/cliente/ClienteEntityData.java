package it.paskinomercato.ejb.entity.cliente;

/**
 * Local data interface for the Cliente entity.
 */
public interface ClienteEntityData {

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
