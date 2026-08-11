package it.paskinomercato.ejb.mail;

import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.RigaOrdine;
import java.util.List;

/**
 * Service interface for the Mail CDI bean.
 * Replaces the EJB 2.x MailLocal + MailLocalHome pair.
 */
public interface MailService {

    void inviaConfermaOrdine(Cliente cliente, Ordine ordine, List<RigaOrdine> righe, String lingua);

    void inviaRegistrazioneConferma(Cliente cliente, String lingua);
}
