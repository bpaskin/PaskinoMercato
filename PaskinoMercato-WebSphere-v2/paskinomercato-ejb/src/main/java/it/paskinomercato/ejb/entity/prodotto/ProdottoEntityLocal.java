package it.paskinomercato.ejb.entity.prodotto;

import javax.ejb.EJBLocalObject;
import java.math.BigDecimal;

/**
 * EJB 2.x Local interface for the Prodotto BMP Entity Bean.
 */
public interface ProdottoEntityLocal extends EJBLocalObject {

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

    BigDecimal getPrezzo();
    void setPrezzo(BigDecimal prezzo);

    String getUnitaMisura();
    void setUnitaMisura(String unitaMisura);

    int getQuantitaStock();
    void setQuantitaStock(int quantitaStock);

    int getCategoriaId();
    void setCategoriaId(int categoriaId);

    String getImmagine();
    void setImmagine(String immagine);

    boolean isAttivo();
    void setAttivo(boolean attivo);

    double getPesoKg();
    void setPesoKg(double pesoKg);
}
