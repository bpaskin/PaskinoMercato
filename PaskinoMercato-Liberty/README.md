# PaskinoMercato 🛒

**Supermercato Online Italiano — CDI / JPA 2.1 / Open Liberty**

Applicazione Java EE enterprise per un supermercato online italiano, bilingue (🇮🇹 / 🇬🇧), consegna solo in Italia, tutti i prezzi in Euro, catalogo di massimo 1.503 prodotti su database PostgreSQL.

---

## Indice

1. [Panoramica](#panoramica)
2. [Stack Tecnologico](#stack-tecnologico)
3. [Struttura del Progetto](#struttura-del-progetto)
4. [Architettura](#architettura)
5. [Modello di Dominio](#modello-di-dominio)
6. [Bean di Servizio](#bean-di-servizio)
7. [Servlet](#servlet)
8. [Pagine JSP](#pagine-jsp)
9. [Database](#database)
10. [Prodotti e Immagini](#prodotti-e-immagini)
11. [Supporto Bilingue](#supporto-bilingue)
12. [Validazione Indirizzi Italiani](#validazione-indirizzi-italiani)
13. [Prerequisiti](#prerequisiti)
14. [Build](#build)
15. [Configurazione del Database](#configurazione-del-database)
16. [Configurazione Liberty](#configurazione-liberty)
17. [Avvio con Liberty](#avvio-con-liberty)
18. [URL dell'Applicazione](#url-dellapplicazione)
19. [Riferimento Script](#riferimento-script)
20. [Sostituzione delle Immagini Placeholder](#sostituzione-delle-immagini-placeholder)

---

## Panoramica

PaskinoMercato è un'applicazione Java EE enterprise distribuita come EAR su **Open Liberty**. Offre:

- Una vetrina di supermercato online bilingue (🇮🇹 Italiano / 🇬🇧 Inglese)
- Catalogo prodotti con navigazione per categoria, ricerca e paginazione (max **1.503 prodotti**)
- Carrello della spesa (POJO serializzabile in sessione HTTP)
- Flusso di checkout completo con validazione indirizzo italiano
- Gestione ordini con storico per cliente
- Email HTML di conferma ordine e registrazione via **JavaMail** (bilingue)
- Tutti i prezzi esclusivamente in **Euro (€)**
- Consegna **solo a indirizzi italiani** — applicata a livello applicativo e di database

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Application Server | **Open Liberty 26** (feature: `servlet-3.1`, `jsp-2.3`, `jpa-2.1`, `jdbc-4.1`, `javaMail-1.5`, `cdi-1.2`, `jndi-1.0`, `el-3.0`) |
| Sorgente / Bytecode Java | **Java 11** (`<release>11</release>`) |
| Livello servizi | Bean CDI 1.2 `@ApplicationScoped` con JTA `@Transactional` |
| Persistenza | **JPA 2.1** (EclipseLink) — `EntityManager` + JPQL; nessun JDBC grezzo |
| Persistence unit | `MercatoPU` — JTA, associata a `jdbc/MercatoDB` |
| Database | **PostgreSQL 15+** |
| Livello web | **Servlet 3.1** + **JSP 2.3** |
| Tag di vista | **JSTL 1.2** + **EL 3.0** |
| Carrello | `CarrelloSessionBean` — POJO serializzabile in `HttpSession` |
| Email | **JavaMail 1.5** tramite JNDI `mail/MercatoMail` |
| Build | **Maven 3** — progetto EAR multi-modulo (`ejb` jar + `war` + `ear`) |
| Lingue | Italiano (predefinito) + Inglese |
| Valuta | Solo Euro (€) |

---

## Struttura del Progetto

```
PaskinoMercato-Liberty/
│
├── pom.xml                              ← POM padre (3 moduli)
│
├── paskinomercato-ejb/                  ← Modulo servizi (JAR)
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── ejb/
│       │   │   ├── catalogo/            ← CatalogoBean + CatalogoService
│       │   │   ├── cliente/             ← ClienteBean  + ClienteService
│       │   │   ├── mail/                ← MailBean      + MailService
│       │   │   └── ordine/              ← OrdineBean   + OrdineService
│       │   ├── model/                   ← Entità JPA (nessun JDBC grezzo)
│       │   ├── util/                    ← IndirizzoItaliaValidator
│       │   └── cart/                    ← Value object CarrelloItem
│       └── resources/
│           ├── META-INF/
│           │   ├── beans.xml            ← Attivazione CDI (modalità annotated)
│           │   └── persistence.xml      ← Persistence unit JPA MercatoPU
│           └── db/
│               ├── schema.sql           ← DDL PostgreSQL
│               └── seed_products.sql    ← 150 prodotti di esempio
│
├── paskinomercato-war/                  ← Modulo web (WAR)
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── cart/
│       │   │   └── CarrelloSessionBean.java  ← POJO carrello in sessione
│       │   └── servlet/
│       │       ├── CarrelloServlet.java
│       │       ├── CatalogoServlet.java
│       │       ├── CheckoutServlet.java
│       │       ├── LinguaServlet.java
│       │       ├── LoginServlet.java
│       │       └── OrdineServlet.java
│       └── webapp/
│           ├── index.jsp                ← Home page
│           ├── css/style.css
│           ├── img/prodotti/            ← 150 immagini SVG prodotti
│           └── WEB-INF/
│               ├── web.xml
│               ├── beans.xml
│               └── jsp/
│                   ├── include/header.jsp
│                   ├── include/footer.jsp
│                   ├── catalogo.jsp
│                   ├── carrello.jsp
│                   ├── checkout.jsp
│                   ├── confermaOrdine.jsp
│                   ├── dettaglioOrdine.jsp
│                   ├── login.jsp
│                   ├── ordini.jsp
│                   ├── error404.jsp
│                   └── error500.jsp
│
├── paskinomercato-ear/                  ← Modulo EAR
│   └── src/main/
│       ├── application/META-INF/
│       │   └── application.xml
│       └── liberty/config/
│           ├── server.xml               ← Configurazione server Open Liberty
│           └── bootstrap.properties     ← Variabili d'ambiente (DB, mail)
│
├── scripts/tools/
│   ├── generate_placeholder_images.py  ← Rigenera le immagini SVG prodotti
│   └── LoadProducts.java               ← Caricatore JDBC prodotti standalone
│
└── README.md
```

---

## Architettura

```
Browser
  │
  ▼
Open Liberty (porta 9080)
  │
  ▼
┌─────────────────────────────────────────────────────────────┐
│  paskinomercato-ear-1.0.0.ear                               │
│                                                             │
│  ┌───────────────────┐     ┌────────────────────────────┐   │
│  │  paskinomercato   │     │  paskinomercato-ejb.jar     │   │
│  │      .war         │     │                            │   │
│  │                   │     │  CDI @ApplicationScoped:   │   │
│  │  Servlet 3.1      │────▶│  CatalogoBean              │   │
│  │  JSP/JSTL/EL      │     │  ClienteBean               │   │
│  │  Risorse statiche │     │  OrdineBean                │   │
│  │  (img/, css/)     │     │  MailBean                  │   │
│  │                   │     └────────────┬───────────────┘   │
│  │  CarrelloSession  │                  │ JPA / EclipseLink  │
│  │  Bean (HttpSess.) │                  │ (MercatoPU / JTA)  │
│  └───────────────────┘                  ▼                   │
│                                  jdbc/MercatoDB             │
└──────────────────────────────────────┬──────────────────────┘
                                       ▼
                                ┌─────────────┐
                                │  PostgreSQL  │
                                │  mercatodb   │
                                └─────────────┘
```

Tutte le chiamate ai servizi sono locali (stessa JVM, stesso EAR). Il modulo WAR inietta i bean CDI direttamente. Le transazioni JTA sono gestite dal container — nessun `commit`/`rollback` manuale. Il carrello è un POJO serializzabile (`CarrelloSessionBean`) conservato direttamente in `HttpSession`.

---

## Modello di Dominio

Tutta la persistenza avviene tramite JPA — nessun JDBC grezzo. Ogni entità corrisponde a una tabella `mercato.*`:

| Entità | Tabella | Descrizione |
|---|---|---|
| [`Prodotto`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Prodotto.java) | `mercato.prodotto` | Prodotto (nome/descrizione bilingue, prezzo, stock) |
| [`Categoria`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Categoria.java) | `mercato.categoria` | Categoria prodotti (bilingue) |
| [`Cliente`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Cliente.java) | `mercato.cliente` | Account cliente |
| [`Indirizzo`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Indirizzo.java) | `mercato.indirizzo` | Indirizzo di consegna italiano |
| [`Ordine`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Ordine.java) | `mercato.ordine` | Testata ordine |
| [`RigaOrdine`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/RigaOrdine.java) | `mercato.riga_ordine` | Riga ordine (prodotto + qtà + prezzo) |
| [`CarrelloItem`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/CarrelloItem.java) | — | Elemento carrello (in sessione HTTP, non persistito) |

`RigaOrdine.subtotale` è una colonna generata e memorizzata da PostgreSQL; è mappata con `insertable=false, updatable=false` affinché JPA non la scriva mai. `RigaOrdine.nomeProdotto` è un campo `@Transient` per la visualizzazione, popolato da una native query con join.

---

## Bean di Servizio

Tutti i bean sono CDI `@ApplicationScoped` con JTA `@Transactional`. Vengono iniettati nelle servlet tramite `@Inject`.

### CatalogoBean

| Metodo | Descrizione |
|---|---|
| `getProdotti(pagina, dim)` | Lista prodotti paginata |
| `getProdottiPerCategoria(catId, pagina, dim)` | Prodotti filtrati per categoria |
| `getProdottoById(id)` | Singolo prodotto per ID |
| `getProdottoByCodice(codice)` | Prodotto per codice |
| `cercaProdotti(testo)` | Ricerca testuale (nomi IT + EN + codice) |
| `getCategorie()` | Tutte le categorie |
| `getCategoriaById(id)` | Singola categoria per ID |
| `contaProdotti()` | Conteggio prodotti attivi |
| `contaProdottiPerCategoria(catId)` | Conteggio prodotti attivi per categoria |
| `isDisponibile(prodottoId, qty)` | Verifica disponibilità a magazzino |

### OrdineBean

| Metodo | Descrizione |
|---|---|
| `creaOrdine(clienteId, indirizzoId, items, note)` | Crea ordine, inserisce righe, decrementa stock (`@Transactional REQUIRED`) |
| `getOrdineByNumero(numero)` | Ordine per numero |
| `getOrdiniCliente(clienteId)` | Storico ordini del cliente |
| `getRigheOrdine(ordineId)` | Righe dell'ordine con nome prodotto |
| `aggiornaStato(ordineId, stato)` | Aggiorna stato ordine |

### ClienteBean

| Metodo | Descrizione |
|---|---|
| `registra(...)` | Registrazione nuovo cliente |
| `login(email, pwHash)` | Autenticazione con password SHA-256 |
| `getClienteById(id)` | Cliente per ID |
| `getClienteByEmail(email)` | Cliente per email |
| `aggiungiIndirizzo(...)` | Aggiunta indirizzo di consegna italiano |
| `getIndirizzi(clienteId)` | Rubrica indirizzi del cliente |
| `getIndirizzo(indirizzoId)` | Singolo indirizzo per ID |
| `aggiornaLingua(clienteId, lang)` | Salvataggio preferenza lingua |

### MailBean

Sessione mail JNDI: `mail/MercatoMail`

Invia email HTML in italiano o inglese:
- `inviaConfermaOrdine(cliente, ordine, righe, lingua)` — email completa con riepilogo ordine
- `inviaRegistrazioneConferma(cliente, lingua)` — email di benvenuto alla registrazione
- `segnaEmailInviata(ordineId)` — imposta `email_inviata = true` nella propria transazione `REQUIRES_NEW`

### Carrello — POJO in sessione

[`CarrelloSessionBean`](paskinomercato-war/src/main/java/it/paskinomercato/cart/CarrelloSessionBean.java) è un POJO serializzabile conservato direttamente in `HttpSession` sotto la chiave `"carrello"`. Metodi: `aggiungi`, `rimuovi`, `aggiornaQuantita`, `svuota`, `getItems`, `getTotale`, `getNumeroArticoli`.

---

## Servlet

| Servlet | URL | Descrizione |
|---|---|---|
| [`CatalogoServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CatalogoServlet.java) | `/catalogo` | Griglia prodotti con paginazione, filtro categoria, ricerca |
| [`CarrelloServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CarrelloServlet.java) | `/carrello` | Vista carrello (GET) e azioni: `aggiungi`, `rimuovi`, `aggiorna`, `svuota` (POST) |
| [`CheckoutServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CheckoutServlet.java) | `/checkout` | Selezione indirizzo + creazione ordine + invio email |
| [`LoginServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/LoginServlet.java) | `/login` | Login + registrazione + logout |
| [`LinguaServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/LinguaServlet.java) | `/lingua?lang=it\|en` | Cambio lingua, salvato in sessione e nel DB |
| [`OrdineServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/OrdineServlet.java) | `/ordini` | Lista storico ordini e dettaglio singolo ordine |

---

## Pagine JSP

Tutte le JSP usano **JSTL 1.2** (`c:`, `fmt:`, `fn:`) e **EL 3.0**.

| JSP | Descrizione |
|---|---|
| [`index.jsp`](paskinomercato-war/src/main/webapp/index.jsp) | Home page con hero banner |
| [`catalogo.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/catalogo.jsp) | Griglia prodotti, sidebar categorie, paginazione |
| [`carrello.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/carrello.jsp) | Tabella carrello con aggiornamento quantità e rimozione |
| [`checkout.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/checkout.jsp) | Form indirizzo + sidebar riepilogo ordine |
| [`confermaOrdine.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/confermaOrdine.jsp) | Conferma ordine con righe dettaglio |
| [`login.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/login.jsp) | Form login + registrazione a schede |
| [`ordini.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/ordini.jsp) | Storico ordini del cliente |
| [`dettaglioOrdine.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/dettaglioOrdine.jsp) | Dettaglio righe di un singolo ordine |
| [`error404.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/error404.jsp) | Pagina errore 404 |
| [`error500.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/error500.jsp) | Pagina errore 500 |
| [`include/header.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/include/header.jsp) | Barra di navigazione: ricerca, cambio lingua, badge carrello, login/logout |
| [`include/footer.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/include/footer.jsp) | Footer: avviso consegna, informazioni pagamento |

---

## Database

**Motore:** PostgreSQL 15+  
**Schema:** `mercato`

### Tabelle

| Tabella | Descrizione |
|---|---|
| `mercato.categoria` | 10 categorie prodotto (bilingue) |
| `mercato.prodotto` | Prodotti — max **1.503 righe** applicate tramite trigger |
| `mercato.cliente` | Account clienti (password SHA-256) |
| `mercato.indirizzo` | Indirizzi di consegna — colonna `paese` vincolata a `'IT'` |
| `mercato.ordine` | Ordini con flusso di stato |
| `mercato.riga_ordine` | Righe ordine — `subtotale` è una colonna generata e memorizzata |
| `mercato.carrello` | Carrello persistito (svuotato alla creazione dell'ordine) |

### Flusso di stato ordine

```
IN_ATTESA → CONFERMATO → IN_PREPARAZIONE → SPEDITO → CONSEGNATO
                                                    ↘ ANNULLATO
```

### Persistence Unit JPA

Dichiarata in [`META-INF/persistence.xml`](paskinomercato-ejb/src/main/resources/META-INF/persistence.xml):

```xml
<persistence-unit name="MercatoPU" transaction-type="JTA">
    <jta-data-source>jdbc/MercatoDB</jta-data-source>
    ...
</persistence-unit>
```

I bean di servizio ottengono l'`EntityManager` tramite:

```java
@PersistenceContext(unitName = "MercatoPU")
private EntityManager em;
```

---

## Prodotti e Immagini

[`paskinomercato-ejb/src/main/resources/db/seed_products.sql`](paskinomercato-ejb/src/main/resources/db/seed_products.sql) contiene **150 prodotti originali** di un supermercato italiano distribuiti su 10 categorie.

| Categoria | Quantità | Fascia di prezzo |
|---|---|---|
| Frutta e Verdura | 15 | €1,29 – €3,99 |
| Carne e Pesce | 15 | €4,99 – €18,99 |
| Formaggi e Salumi | 15 | €2,49 – €24,99 |
| Pane e Pasta | 15 | €1,99 – €4,49 |
| Bevande | 15 | €0,49 – €12,99 |
| Surgelati | 15 | €1,79 – €7,99 |
| Pulizia Casa | 10 | €1,89 – €6,49 |
| Igiene Persona | 10 | €1,99 – €7,99 |
| Dispensa | 15 | €1,29 – €12,99 |
| Dolci e Snack | 20 | €1,99 – €14,99 |

150 immagini SVG placeholder si trovano in `paskinomercato-war/src/main/webapp/img/prodotti/`. Il database memorizza i nomi immagine con estensione `.jpg`; le JSP convertono al volo:

```jsp
${fn:replace(p.immagine, '.jpg', '.svg')}
```

---

## Supporto Bilingue

La lingua è salvata nell'attributo di sessione HTTP `lang` (`"it"` o `"en"`). Il default è italiano.

- **Cambio lingua:** `GET /lingua?lang=it|en`
- **Persistenza:** se autenticato, aggiorna `mercato.cliente.lingua` tramite JPA
- **Pattern JSP:**
  ```jsp
  ${lang eq 'it' ? 'Testo italiano' : 'English text'}
  ```
- **Nomi prodotto:** `Prodotto.getNome(lang)` restituisce `nomeIt` o `nomeEn`
- **Email:** `MailBean` genera il corpo HTML nella lingua del cliente

---

## Validazione Indirizzi Italiani

[`IndirizzoItaliaValidator`](paskinomercato-ejb/src/main/java/it/paskinomercato/util/IndirizzoItaliaValidator.java) applica tre regole:

| Regola | Validazione |
|---|---|
| Paese | Deve essere `"IT"` |
| CAP | Regex `^\d{5}$` |
| Provincia | Una delle 110 abbreviazioni ufficiali italiane (AG … VV) |

---

## Prerequisiti

| Strumento | Versione |
|---|---|
| Java JDK | **11** o superiore |
| Maven | 3.6+ |
| PostgreSQL | 15+ |
| Python | 3.x (solo per la generazione immagini SVG) |

---

## Build

```bash
cd PaskinoMercato-Liberty

# Build completa — genera l'EAR
mvn clean package

# Artefatto deployabile
ls paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear
```

---

## Configurazione del Database

```bash
# 1. Creare il database e l'utente
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'changeme';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"

# 2. Eseguire il DDL dello schema
psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/schema.sql

# 3. Caricare i 150 prodotti di esempio
psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

In alternativa, usare il caricatore Java JDBC standalone:

```bash
cd scripts/tools
javac -cp postgresql-42.7.3.jar LoadProducts.java
java  -cp .:postgresql-42.7.3.jar it.paskinomercato.tools.LoadProducts \
      localhost 5432 mercatodb mercato changeme \
      ../../paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

---

## Configurazione Liberty

Tutte le variabili d'ambiente si trovano in [`paskinomercato-ear/src/main/liberty/config/bootstrap.properties`](paskinomercato-ear/src/main/liberty/config/bootstrap.properties). Modificare i valori prima di avviare il server:

```properties
# PostgreSQL
db.host=localhost
db.port=5432
db.name=mercatodb
db.user=mercato
db.password=changeme

# JavaMail SMTP
mail.host=smtp.tuodominio.it
mail.smtp.port=587
mail.user=noreply@paskinomercato.it
mail.password=tuapassword
mail.smtp.auth=true
mail.smtp.starttls=true
```

Il driver PostgreSQL viene copiato automaticamente in `${server.config.dir}/lib` dal `maven-dependency-plugin` durante la fase `prepare-package`.

---

## Avvio con Liberty

### Dev mode (hot reload)

```bash
cd PaskinoMercato-Liberty
mvn -pl paskinomercato-ear io.openliberty.tools:liberty-maven-plugin:dev
```

### Build e avvio standard

```bash
cd PaskinoMercato-Liberty
mvn clean package
mvn -pl paskinomercato-ear io.openliberty.tools:liberty-maven-plugin:run
```

> **Suggerimento:** aggiungendo il gruppo plugin a `~/.m2/settings.xml` è possibile usare il prefisso breve `liberty:dev` / `liberty:run`:
> ```xml
> <pluginGroups>
>     <pluginGroup>io.openliberty.tools</pluginGroup>
> </pluginGroups>
> ```

---

## URL dell'Applicazione

| Risorsa | URL |
|---|---|
| Home page | `http://localhost:9080/paskinomercato/` |
| Catalogo prodotti | `http://localhost:9080/paskinomercato/catalogo` |
| Carrello | `http://localhost:9080/paskinomercato/carrello` |
| Checkout | `http://localhost:9080/paskinomercato/checkout` |
| Login / Registrazione | `http://localhost:9080/paskinomercato/login` |
| I miei ordini | `http://localhost:9080/paskinomercato/ordini` |
| Cambio lingua | `http://localhost:9080/paskinomercato/lingua?lang=en` |

---

## Riferimento Script

| Script | Linguaggio | Scopo |
|---|---|---|
| [`scripts/tools/generate_placeholder_images.py`](scripts/tools/generate_placeholder_images.py) | Python 3 | Rigenera le 150 immagini SVG placeholder prodotti |
| [`scripts/tools/LoadProducts.java`](scripts/tools/LoadProducts.java) | Java | Caricatore JDBC standalone per `seed_products.sql` |

---

## Sostituzione delle Immagini Placeholder

1. Inserire un file SVG con il nome del codice prodotto (es. `fv001_mele_golden.svg`) in:
   ```
   paskinomercato-war/src/main/webapp/img/prodotti/
   ```
2. Ricompilare l'EAR — nessuna modifica al codice necessaria.

Per rigenerare tutte le immagini SVG placeholder:

```bash
python3 scripts/tools/generate_placeholder_images.py
```

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
