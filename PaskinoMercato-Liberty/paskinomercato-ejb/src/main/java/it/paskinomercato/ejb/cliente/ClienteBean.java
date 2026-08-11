package it.paskinomercato.ejb.cliente;

import it.paskinomercato.model.Cliente;
import it.paskinomercato.model.Indirizzo;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

/**
 * CDI ApplicationScoped service — Cliente.
 * Manages customer registration, authentication and addresses via JPA.
 */
@ApplicationScoped
public class ClienteBean implements ClienteService {

    @PersistenceContext(unitName = "MercatoPU")
    private EntityManager em;

    @Transactional
    public Cliente registra(String email, String passwordHash, String nome,
                             String cognome, String telefono, String lingua) {
        Cliente c = new Cliente();
        c.setEmail(email);
        c.setPasswordHash(passwordHash);
        c.setNome(nome);
        c.setCognome(cognome);
        c.setTelefono(telefono);
        c.setLingua(lingua != null ? lingua : "it");
        c.setAttivo(true);
        em.persist(c);
        em.flush(); // ensures id is populated before returning
        return c;
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Cliente login(String email, String passwordHash) {
        List<Cliente> results = em.createQuery(
            "SELECT c FROM Cliente c WHERE c.email = :email " +
            "AND c.passwordHash = :hash AND c.attivo = true",
            Cliente.class)
            .setParameter("email", email)
            .setParameter("hash", passwordHash)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Cliente getClienteById(int id) {
        return em.find(Cliente.class, id);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Cliente getClienteByEmail(String email) {
        List<Cliente> results = em.createQuery(
            "SELECT c FROM Cliente c WHERE c.email = :email",
            Cliente.class)
            .setParameter("email", email)
            .getResultList();
        return results.isEmpty() ? null : results.get(0);
    }

    @Transactional
    public void aggiornaLingua(int clienteId, String lingua) {
        Cliente c = em.find(Cliente.class, clienteId);
        if (c != null) {
            c.setLingua(lingua);
        }
    }

    @Transactional
    public void aggiungiIndirizzo(int clienteId, String via, String civico,
                                   String citta, String cap, String provincia) {
        long count = em.createQuery(
            "SELECT COUNT(i) FROM Indirizzo i WHERE i.clienteId = :cid",
            Long.class)
            .setParameter("cid", clienteId)
            .getSingleResult();

        Indirizzo i = new Indirizzo();
        i.setClienteId(clienteId);
        i.setVia(via);
        i.setCivico(civico);
        i.setCitta(citta);
        i.setCap(cap);
        i.setProvincia(provincia.toUpperCase());
        i.setPaese("IT");
        i.setPredefinito(count == 0);
        em.persist(i);
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public List<Indirizzo> getIndirizzi(int clienteId) {
        return em.createQuery(
            "SELECT i FROM Indirizzo i WHERE i.clienteId = :cid " +
            "ORDER BY i.predefinito DESC, i.id ASC",
            Indirizzo.class)
            .setParameter("cid", clienteId)
            .getResultList();
    }

    @Transactional(Transactional.TxType.SUPPORTS)
    public Indirizzo getIndirizzo(int indirizzoId) {
        return em.find(Indirizzo.class, indirizzoId);
    }
}
