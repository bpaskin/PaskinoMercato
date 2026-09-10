package it.paskinomercato.ejb.mail;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.RigaOrdine;
import it.paskinomercato.persistence.H2Database;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.EJBException;
import javax.mail.*;
import javax.mail.internet.*;
import javax.naming.InitialContext;
import java.util.List;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * EJB 2.0 Stateless Session Bean — Mail.
 * Sends order confirmation and registration emails via JavaMail.
 * Mail session bound via JNDI at java:comp/env/mail/MercatoMail.
 */
public class MailBean implements SessionBean {

    private static final String FROM_ADDRESS = "noreply@paskinomercato.it";
    private static final String FROM_NAME    = "PaskinoMercato";

    private SessionContext ctx;

    public void ejbCreate() {}
    public void ejbRemove() {}
    public void ejbActivate() {}
    public void ejbPassivate() {}
    public void setSessionContext(SessionContext ctx) { this.ctx = ctx; }

    private javax.mail.Session getMailSession() throws Exception {
        InitialContext ic = new InitialContext();
        return (javax.mail.Session) ic.lookup("java:comp/env/mail/MercatoMail");
    }

    // ----------------------------------------------------------------
    public void inviaConfermaOrdine(Cliente cliente, Ordine ordine, List<RigaOrdine> righe, String lingua) {
        boolean it = !"en".equalsIgnoreCase(lingua);
        String subject = it
            ? "Conferma ordine #" + ordine.getNumeroOrdine() + " - PaskinoMercato"
            : "Order confirmation #" + ordine.getNumeroOrdine() + " - PaskinoMercato";

        StringBuffer sb = new StringBuffer();
        sb.append("<html><body style=\"font-family:Arial,sans-serif;color:#333\">");
        sb.append("<div style=\"max-width:600px;margin:0 auto;padding:20px\">");
        sb.append("<div style=\"background:#2e7d32;padding:15px;border-radius:4px 4px 0 0\">");
        sb.append("<h2 style=\"color:#fff;margin:0\">PaskinoMercato</h2></div>");
        sb.append("<div style=\"padding:20px;border:1px solid #e0e0e0\">");
        if (it) {
            sb.append("<p>Gentile <strong>" + escHtml(cliente.getNomeCompleto()) + "</strong>,</p>");
            sb.append("<p>Il tuo ordine &egrave; stato ricevuto con successo!</p>");
            sb.append("<p><strong>Numero Ordine:</strong> " + ordine.getNumeroOrdine() + "</p>");
        } else {
            sb.append("<p>Dear <strong>" + escHtml(cliente.getNomeCompleto()) + "</strong>,</p>");
            sb.append("<p>Your order has been successfully received!</p>");
            sb.append("<p><strong>Order Number:</strong> " + ordine.getNumeroOrdine() + "</p>");
        }

        // Order table
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

        // Mark email as sent in DB — best-effort update
        try {
            java.sql.Connection con = H2Database.getConnection();
            java.sql.PreparedStatement ps = con.prepareStatement(
                "UPDATE mercato.ordine SET email_inviata = true WHERE id = ?");
            ps.setInt(1, ordine.getId());
            ps.executeUpdate();
            ps.close(); con.close();
        } catch (Exception ignored) {}
    }

    public void inviaRegistrazioneConferma(Cliente cliente, String lingua) {
        boolean it = !"en".equalsIgnoreCase(lingua);
        String subject = it
            ? "Benvenuto in PaskinoMercato!"
            : "Welcome to PaskinoMercato!";

        StringBuffer sb = new StringBuffer();
        sb.append("<html><body style=\"font-family:Arial,sans-serif;color:#333\">");
        sb.append("<div style=\"max-width:600px;margin:0 auto;padding:20px\">");
        sb.append("<div style=\"background:#2e7d32;padding:15px;border-radius:4px 4px 0 0\">");
        sb.append("<h2 style=\"color:#fff;margin:0\">PaskinoMercato</h2></div>");
        sb.append("<div style=\"padding:20px;border:1px solid #e0e0e0\">");
        if (it) {
            sb.append("<p>Ciao <strong>" + escHtml(cliente.getNome()) + "</strong>,</p>");
            sb.append("<p>La tua registrazione &egrave; avvenuta con successo.</p>");
            sb.append("<p>Puoi ora fare la spesa comodamente online con consegna in tutta Italia.</p>");
        } else {
            sb.append("<p>Hello <strong>" + escHtml(cliente.getNome()) + "</strong>,</p>");
            sb.append("<p>Your registration was successful.</p>");
            sb.append("<p>You can now shop online with delivery across Italy.</p>");
        }
        sb.append("</div></div></body></html>");

        sendEmail(cliente.getEmail(), subject, sb.toString());
    }

    // ----------------------------------------------------------------
    private void sendEmail(String to, String subject, String htmlBody) {
        try {
            javax.mail.Session mailSession = getMailSession();
            MimeMessage msg = new MimeMessage(mailSession);
            msg.setFrom(new InternetAddress(FROM_ADDRESS, FROM_NAME));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            msg.setSubject(subject, "UTF-8");
            msg.setContent(htmlBody, "text/html; charset=UTF-8");
            Transport.send(msg);
        } catch (Exception e) {
            throw new EJBException("sendEmail failed to " + to + ": " + e.getMessage(), e);
        }
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
