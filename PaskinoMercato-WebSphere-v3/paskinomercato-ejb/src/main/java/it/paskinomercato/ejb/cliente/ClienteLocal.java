package it.paskinomercato.ejb.cliente;

import javax.ejb.EJBLocalObject;
import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;
import java.util.List;

public interface ClienteLocal extends EJBLocalObject {

    Cliente registra(String email, String passwordHash, String nome, String cognome, String telefono, String lingua);

    Cliente login(String email, String passwordHash);

    Cliente getClienteById(int id);

    Cliente getClienteByEmail(String email);

    void aggiornaLingua(int clienteId, String lingua);

    void aggiungiIndirizzo(int clienteId, String via, String civico, String citta, String cap, String provincia);

    List<Indirizzo> getIndirizzi(int clienteId);

    Indirizzo getIndirizzo(int indirizzoId);
}
