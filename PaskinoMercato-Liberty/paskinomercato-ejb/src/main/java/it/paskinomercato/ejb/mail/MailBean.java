package it.paskinomercato.ejb.mail;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.*;
import javax.mail.internet.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * CDI ApplicationScoped service — Mail.
 * Sends order confirmation and registration emails via JavaMail.
 * The email_inviata DB update runs in its own transaction so a mail
 * delivery failure does not prevent the flag being set on retry.
 */
@ApplicationScoped
public class MailBean implements MailService {

    private static final String FROM_ADDRESS = "noreply@paskinomercato.it";
    private static final String FROM_NAME    = "PaskinoMercato";

    @Resource(lookup = "mail/MercatoMail")
    private javax.mail.Session mailSession;

    @PersistenceContext(unitName = "MercatoPU")
    private EntityManager em;

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void inviaConfermaOrdine(Cliente cliente, Ordine ordine, List<RigaOrdine> righe, String lingua) {
        boolean it = !"en".equalsIgnoreCase(lingua);
        String subject = it
            ? "Conferma ordine #" + ordine.getNumeroOrdine() + " - PaskinoMercato"
            : "Order confirmation #" + ordine.getNumeroOrdine() + " - PaskinoMercato";

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style=\"font-family:Arial,sans-serif;color:#333\">");
        sb.append("<div style=\"max-width:600px;margin:0 auto;padding:20px\">");
        sb.append("<div style=\"background:#2e7d32;padding:15px;border-radius:4px 4px 0 0\">");
        sb.append("<h2 style=\"color:#fff;margin:0\">PaskinoMercato</h2></div>");
        sb.append("<div style=\"padding:20px;border:1px solid #e0e0e0\">");
        if (it) {
            sb.append("<p>Gentile <strong>").append(escHtml(cliente.getNomeCompleto())).append("</strong>,</p>");
            sb.append("<p>Il tuo ordine &egrave; stato ricevuto con successo!</p>");
            sb.append("<p><strong>Numero Ordine:</strong> ").append(ordine.getNumeroOrdine()).append("</p>");
        } else {
            sb.append("<p>Dear <strong>").append(escHtml(cliente.getNomeCompleto())).append("</strong>,</p>");
            sb.append("<p>Your order has been successfully received!</p>");
            sb.append("<p><strong>Order Number:</strong> ").append(ordine.getNumeroOrdine()).append("</p>");
        }

        sb.append("<table style=\"width:100%;border-collapse:collapse;margin-top:15px\">");
        sb.append("<thead><tr style=\"background:#f5f5f5\">");
        sb.append("<th style=\"padding:8px;border:1px solid #ddd;text-align:left\">")
          .append(it ? "Prodotto" : "Product").append("</th>");
        sb.append("<th style=\"padding:8px;border:1px solid #ddd;text-align:center\">")
          .append(it ? "Qtà" : "Qty").append("</th>");
        sb.append("<th style=\"padding:8px;border:1px solid #ddd;text-align:right\">")
          .append(it ? "Prezzo" : "Price").append("</th>");
        sb.append("<th style=\"padding:8px;border:1px solid #ddd;text-align:right\">Subtotale</th>");
        sb.append("</tr></thead><tbody>");

        NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.ITALY);
        for (int i = 0; i < righe.size(); i++) {
            RigaOrdine riga = righe.get(i);
            sb.append("<tr>");
            sb.append("<td style=\"padding:8px;border:1px solid #ddd\">")
              .append(escHtml(riga.getNomeProdotto())).append("</td>");
            sb.append("<td style=\"padding:8px;border:1px solid #ddd;text-align:center\">")
              .append(riga.getQuantita()).append("</td>");
            sb.append("<td style=\"padding:8px;border:1px solid #ddd;text-align:right\">")
              .append(nf.format(riga.getPrezzoUnitario())).append("</td>");
            sb.append("<td style=\"padding:8px;border:1px solid #ddd;text-align:right\">")
              .append(nf.format(riga.getSubtotale())).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody><tfoot>");
        sb.append("<tr style=\"font-weight:bold\"><td colspan=\"3\" style=\"padding:8px;text-align:right\">")
          .append(it ? "Totale" : "Total").append("</td>");
        sb.append("<td style=\"padding:8px;border:1px solid #ddd;text-align:right\">")
          .append(nf.format(ordine.getTotale())).append("</td></tr>");
        sb.append("</tfoot></table>");

        sb.append("<p style=\"margin-top:20px\">")
          .append(it ? "Grazie per aver scelto PaskinoMercato!" : "Thank you for choosing PaskinoMercato!")
          .append("</p>");
        sb.append("</div></div></body></html>");

        sendEmail(cliente.getEmail(), subject, sb.toString());
        segnaEmailInviata(ordine.getId());
    }

    @Transactional(Transactional.TxType.NOT_SUPPORTED)
    public void inviaRegistrazioneConferma(Cliente cliente, String lingua) {
        boolean it = !"en".equalsIgnoreCase(lingua);
        String subject = it
            ? "Benvenuto in PaskinoMercato!"
            : "Welcome to PaskinoMercato!";

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style=\"font-family:Arial,sans-serif;color:#333\">");
        sb.append("<div style=\"max-width:600px;margin:0 auto;padding:20px\">");
        sb.append("<div style=\"background:#2e7d32;padding:15px;border-radius:4px 4px 0 0\">");
        sb.append("<h2 style=\"color:#fff;margin:0\">PaskinoMercato</h2></div>");
        sb.append("<div style=\"padding:20px;border:1px solid #e0e0e0\">");
        if (it) {
            sb.append("<p>Ciao <strong>").append(escHtml(cliente.getNome())).append("</strong>,</p>");
            sb.append("<p>La tua registrazione &egrave; avvenuta con successo.</p>");
            sb.append("<p>Puoi ora fare la spesa comodamente online con consegna in tutta Italia.</p>");
        } else {
            sb.append("<p>Hello <strong>").append(escHtml(cliente.getNome())).append("</strong>,</p>");
            sb.append("<p>Your registration was successful.</p>");
            sb.append("<p>You can now shop online with delivery across Italy.</p>");
        }
        sb.append("</div></div></body></html>");

        sendEmail(cliente.getEmail(), subject, sb.toString());
    }

    /** Runs in its own independent transaction so the flag survives even if the caller rolls back. */
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public void segnaEmailInviata(int ordineId) {
        try {
            Ordine o = em.find(Ordine.class, ordineId);
            if (o != null) {
                o.setEmailInviata(true);
            }
        } catch (Exception ignored) {}
    }

    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("sendEmail failed to " + to + ": " + e.getMessage(), e);
        }
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
