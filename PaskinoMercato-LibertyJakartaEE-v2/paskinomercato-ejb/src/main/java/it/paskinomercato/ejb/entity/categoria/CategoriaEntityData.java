package it.paskinomercato.ejb.entity.categoria;

/**
 * Local data interface for the Categoria entity.
 */
public interface CategoriaEntityData {

    Integer getId();

    String getCodice();
    void setCodice(String codice);

    String getNomeIt();
    void setNomeIt(String nomeIt);

    String getNomeEn();
    void setNomeEn(String nomeEn);

    String getDescrizioneIt();
    void setDescrizioneIt(String descrizioneIt);

    String getDescrizioneEn();
    void setDescrizioneEn(String descrizioneEn);

    String getImmagine();
    void setImmagine(String immagine);
}
