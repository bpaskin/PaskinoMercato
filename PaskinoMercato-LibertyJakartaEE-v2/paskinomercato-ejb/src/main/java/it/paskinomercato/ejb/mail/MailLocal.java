package it.paskinomercato.ejb.mail;

import jakarta.ejb.EJBLocalObject;
import it.paskinomercato.model.Ordine;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.RigaOrdine;
import java.util.List;

public interface MailLocal extends EJBLocalObject {

    void inviaConfermaOrdine(Cliente cliente, Ordine ordine, List<RigaOrdine> righe, String lingua);

    void inviaRegistrazioneConferma(Cliente cliente, String lingua);
}
