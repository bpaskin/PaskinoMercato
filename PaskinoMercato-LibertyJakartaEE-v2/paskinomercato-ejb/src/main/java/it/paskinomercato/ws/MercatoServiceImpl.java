package it.paskinomercato.ws;

import it.paskinomercato.ejb.catalogo.CatalogoLocal;
import it.paskinomercato.ejb.catalogo.CatalogoLocalHome;
import it.paskinomercato.ejb.ordine.OrdineLocal;
import it.paskinomercato.ejb.ordine.OrdineLocalHome;
import it.paskinomercato.model.Prodotto;
import it.paskinomercato.model.Categoria;
import it.paskinomercato.model.Ordine;

import javax.jws.WebService;
import javax.naming.InitialContext;
import java.util.List;

/**
 * JAX-WS Service Endpoint Implementation.
 * Delegates to local EJBs for data retrieval.
 */
@WebService(
    serviceName      = "MercatoService",
    portName         = "MercatoServicePort",
    targetNamespace  = "http://ws.paskinomercato.it/",
    endpointInterface = "it.paskinomercato.ws.MercatoServiceSEI",
    wsdlLocation     = "WEB-INF/wsdl/MercatoService.wsdl"
)
public class MercatoServiceImpl implements MercatoServiceSEI {

    private CatalogoLocal getCatalogo() throws Exception {
        InitialContext ic = new InitialContext();
        CatalogoLocalHome home = (CatalogoLocalHome) ic.lookup("java:comp/env/ejb/CatalogoBean");
        return home.create();
    }

    private OrdineLocal getOrdineBean() throws Exception {
        InitialContext ic = new InitialContext();
        OrdineLocalHome home = (OrdineLocalHome) ic.lookup("java:comp/env/ejb/OrdineBean");
        return home.create();
    }

    public String getProdottoXml(String codice) {
        try {
            Prodotto p = getCatalogo().getProdottoByCodice(codice);
            if (p == null) {
                return "<prodotto><errore>Non trovato / Not found</errore></prodotto>";
            }
            StringBuffer sb = new StringBuffer();
            sb.append("<prodotto>");
            sb.append("<id>").append(p.getId()).append("</id>");
            sb.append("<codice>").append(escXml(p.getCodice())).append("</codice>");
            sb.append("<nome_it>").append(escXml(p.getNomeIt())).append("</nome_it>");
            sb.append("<nome_en>").append(escXml(p.getNomeEn())).append("</nome_en>");
            sb.append("<prezzo valuta=\"EUR\">").append(p.getPrezzo()).append("</prezzo>");
            sb.append("<unita>").append(escXml(p.getUnitaMisura())).append("</unita>");
            sb.append("<stock>").append(p.getQuantitaStock()).append("</stock>");
            sb.append("</prodotto>");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("getProdottoXml failed: " + e.getMessage(), e);
        }
    }

    public String getStatoOrdine(String numeroOrdine) {
        try {
            Ordine o = getOrdineBean().getOrdineByNumero(numeroOrdine);
            if (o == null) {
                return "<ordine><errore>Non trovato / Not found</errore></ordine>";
            }
            StringBuffer sb = new StringBuffer();
            sb.append("<ordine>");
            sb.append("<numero>").append(escXml(o.getNumeroOrdine())).append("</numero>");
            sb.append("<stato>").append(escXml(o.getStato())).append("</stato>");
            sb.append("<totale valuta=\"EUR\">").append(o.getTotale()).append("</totale>");
            sb.append("</ordine>");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("getStatoOrdine failed: " + e.getMessage(), e);
        }
    }

    public String getCategorieXml() {
        try {
            List<Categoria> categorie = getCatalogo().getCategorie();
            StringBuffer sb = new StringBuffer();
            sb.append("<categorie>");
            for (int i = 0; i < categorie.size(); i++) {
                Categoria c = categorie.get(i);
                sb.append("<categoria>");
                sb.append("<id>").append(c.getId()).append("</id>");
                sb.append("<codice>").append(escXml(c.getCodice())).append("</codice>");
                sb.append("<nome_it>").append(escXml(c.getNomeIt())).append("</nome_it>");
                sb.append("<nome_en>").append(escXml(c.getNomeEn())).append("</nome_en>");
                sb.append("</categoria>");
            }
            sb.append("</categorie>");
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("getCategorieXml failed: " + e.getMessage(), e);
        }
    }

    private String escXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&apos;");
    }
}
