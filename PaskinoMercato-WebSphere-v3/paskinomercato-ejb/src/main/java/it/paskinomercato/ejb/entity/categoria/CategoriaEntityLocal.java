package it.paskinomercato.ejb.entity.categoria;

import javax.ejb.EJBLocalObject;

/**
 * EJB 2.x Local interface for the Categoria BMP Entity Bean.
 */
public interface CategoriaEntityLocal extends EJBLocalObject {

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
