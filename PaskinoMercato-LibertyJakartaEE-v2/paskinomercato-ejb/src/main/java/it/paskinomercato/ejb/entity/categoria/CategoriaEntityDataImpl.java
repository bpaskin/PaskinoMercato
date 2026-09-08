package it.paskinomercato.ejb.entity.categoria;

/**
 * Modernized POJO implementation of CategoriaEntityLocal.
 */
public class CategoriaEntityDataImpl implements CategoriaEntityData {

    private Integer id;
    private String  codice;
    private String  nomeIt;
    private String  nomeEn;
    private String  descrizioneIt;
    private String  descrizioneEn;
    private String  immagine;

    public CategoriaEntityDataImpl(Integer id, String codice, String nomeIt, String nomeEn,
                                    String descrizioneIt, String descrizioneEn, String immagine) {
        this.id = id;
        this.codice = codice;
        this.nomeIt = nomeIt;
        this.nomeEn = nomeEn;
        this.descrizioneIt = descrizioneIt;
        this.descrizioneEn = descrizioneEn;
        this.immagine = immagine;
    }

    @Override
    public Integer getId() { return id; }

    @Override
    public String getCodice() { return codice; }

    @Override
    public void setCodice(String codice) { this.codice = codice; }

    @Override
    public String getNomeIt() { return nomeIt; }

    @Override
    public void setNomeIt(String nomeIt) { this.nomeIt = nomeIt; }

    @Override
    public String getNomeEn() { return nomeEn; }

    @Override
    public void setNomeEn(String nomeEn) { this.nomeEn = nomeEn; }

    @Override
    public String getDescrizioneIt() { return descrizioneIt; }

    @Override
    public void setDescrizioneIt(String descrizioneIt) { this.descrizioneIt = descrizioneIt; }

    @Override
    public String getDescrizioneEn() { return descrizioneEn; }

    @Override
    public void setDescrizioneEn(String descrizioneEn) { this.descrizioneEn = descrizioneEn; }

    @Override
    public String getImmagine() { return immagine; }

    @Override
    public void setImmagine(String immagine) { this.immagine = immagine; }
}
