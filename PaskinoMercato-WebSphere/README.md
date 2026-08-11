# PaskinoMercato 🛒

**Supermercato Online Italiano — JavaEE 5 / EJB 2.1 / WebSphere Application Server 8.5.5**

Applicazione Java enterprise full-stack per un supermercato online in italiano e inglese, con consegna limitata all'Italia, tutti i prezzi in Euro e un catalogo di massimo 1.503 prodotti su database PostgreSQL.

---

## Indice

1. [Panoramica](#panoramica)
2. [Stack Tecnologico](#stack-tecnologico)
3. [Struttura del Progetto](#struttura-del-progetto)
4. [Architettura](#architettura)
5. [Modello di Dominio](#modello-di-dominio)
6. [EJB Beans](#ejb-beans)
7. [Servlet](#servlet)
8. [Pagine JSP](#pagine-jsp)
9. [Web Service JAX-RPC](#web-service-jax-rpc)
10. [Database](#database)
11. [Prodotti e Immagini](#prodotti-e-immagini)
12. [Supporto Bilingue](#supporto-bilingue)
13. [Validazione Indirizzi Italiani](#validazione-indirizzi-italiani)
14. [Prerequisiti](#prerequisiti)
15. [Build](#build)
16. [Configurazione del Database](#configurazione-del-database)
17. [Configurazione WebSphere](#configurazione-websphere)
18. [Deploy su WebSphere (wsadmin)](#deploy-su-websphere-wsadmin)
19. [URL dell'Applicazione](#url-dellapplicazione)
20. [Riferimento Script](#riferimento-script)
21. [Sostituzione delle Immagini Placeholder](#sostituzione-delle-immagini-placeholder)

---

## Panoramica

PaskinoMercato è una classica applicazione **JavaEE 5** enterprise distribuita come EAR su **IBM WebSphere Application Server 8.5.5**. Offre:

- Una vetrina di supermercato online bilingue (🇮🇹 Italiano / 🇬🇧 Inglese)
- Catalogo prodotti con navigazione per categoria, ricerca e paginazione (max **1.503 prodotti**)
- Carrello della spesa (EJB stateful, lato server per sessione)
- Flusso di checkout completo con validazione indirizzo italiano
- Gestione ordini con storico per cliente
- Email di conferma ordine via **JavaMail** (HTML, bilingue)
- Web service **JAX-RPC SOAP** per integrazione con sistemi esterni
- Tutti i prezzi esclusivamente in **Euro (€)**
- Consegna **solo a indirizzi italiani** (applicata a livello applicativo e di database)

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Application Server | IBM WebSphere Application Server **8.5.5** |
| Versione Java EE | JavaEE 5 (web-app 2.5, EJB 2.1) |
| Bytecode Java | **Java 8** (compilato con `<release>8</release>`) |
| Stile EJB | **EJB 2.1** — `SessionBean`, `EJBLocalHome`, `EJBLocalObject` (nessuna annotazione, nessun JPA) |
| Transazioni EJB | **Container-Managed Transactions** (CMT) — nessun `commit`/`rollback` manuale |
| Persistenza | **JDBC** diretto tramite JNDI `DataSource` (nessun JPA, nessun Hibernate) |
| Database | **PostgreSQL** 15+ |
| Web Service | **JAX-RPC 1.1** (SOAP/Document-Literal Wrapped) |
| Livello Web | **Servlet 2.5** + **JSP 2.1** |
| Tag di Vista | **JSTL 1.2** + **EL 2.2** (forniti da WebSphere — non inclusi nel WAR) |
| File statici | Serviti dal meccanismo nativo di WebSphere (`fileServingEnabled=true` in `ibm-web-ext.xmi`) |
| Email | **JavaMail 1.4** tramite JNDI `mail/MercatoMail` |
| Build | **Maven 3** (progetto EAR multi-modulo) |
| Lingue | Italiano (predefinito) + Inglese |
| Valuta | Solo Euro (€) |

---

## Struttura del Progetto

```
paskinomercato/
│
├── pom.xml                              ← POM padre (3 moduli)
│
├── paskinomercato-ejb/                  ← Modulo EJB 2.1
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── ejb/
│       │   │   ├── carrello/            ← CarrelloBean (Stateful)
│       │   │   ├── catalogo/            ← CatalogoBean (Stateless)
│       │   │   ├── cliente/             ← ClienteBean  (Stateless)
│       │   │   ├── entity/              ← Entity Bean BMP EJB 2.1
│       │   │   │   ├── prodotto/        ← ProdottoEntityBean + Local/Home
│       │   │   │   ├── categoria/       ← CategoriaEntityBean + Local/Home
│       │   │   │   ├── cliente/         ← ClienteEntityBean + Local/Home
│       │   │   │   └── ordine/          ← OrdineEntityBean + Local/Home
│       │   │   ├── mail/                ← MailBean      (Stateless)
│       │   │   └── ordine/              ← OrdineBean   (Stateless)
│       │   ├── model/                   ← Value object (nessun JPA)
│       │   ├── util/                    ← IndirizzoItaliaValidator
│       │   └── ws/                      ← SEI JAX-RPC, implementazione e wrapper bean
│       └── resources/
│           ├── META-INF/
│           │   ├── ejb-jar.xml          ← Descrittore di deploy EJB 2.1
│           │   └── ibm-ejb-jar-bnd.xmi  ← Binding JNDI EJB WebSphere
│           └── db/
│               ├── schema.sql           ← DDL PostgreSQL
│               └── seed_products.sql    ← 150 prodotti originali
│
├── paskinomercato-war/                  ← Modulo WAR
│   └── src/main/
│       ├── java/it/paskinomercato/servlet/
│       │   ├── CarrelloServlet.java
│       │   ├── CatalogoServlet.java
│       │   ├── CheckoutServlet.java
│       │   ├── LinguaServlet.java
│       │   ├── LoginServlet.java
│       │   └── OrdineServlet.java
│       └── webapp/
│           ├── index.jsp                ← Home page
│           ├── css/style.css            ← Foglio di stile principale
│           ├── img/prodotti/            ← 150 immagini SVG prodotti
│           └── WEB-INF/
│               ├── web.xml              ← Descrittore Servlet 2.5
│               ├── ibm-web-bnd.xmi      ← Binding WAR WebSphere (riferimenti EJB e DataSource)
│               ├── ibm-web-ext.xmi      ← Estensioni WAR WebSphere (fileServingEnabled=true)
│               ├── webservices.xml      ← Descrittore endpoint JAX-RPC
│               ├── wsdl/
│               │   └── MercatoService.wsdl
│               ├── MercatoService-mapping.xml
│               └── jsp/
│                   ├── include/
│                   │   ├── header.jsp
│                   │   └── footer.jsp
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
│   └── src/main/application/
│       └── META-INF/
│           ├── application.xml          ← Dichiara i moduli ejb + war
│           └── ibm-application-bnd.xmi  ← Binding applicazione WebSphere
│
├── scripts/
│   ├── wsadmin/
│   │   └── install_paskinomercato.py    ← Installazione/aggiornamento/disinstallazione (wsadmin Jython)
│   └── tools/
│       ├── generate_placeholder_images.py  ← Rigenera immagini SVG prodotti
│       └── LoadProducts.java            ← Caricatore JDBC prodotti standalone
│
├── WEBSPHERE_SETUP.md                   ← Guida rapida alla configurazione WebSphere
└── README.md
```

---

## Architettura

```
Browser
  │
  ▼
WebSphere HTTP Server (porta 9080)
  │
  ▼
┌─────────────────────────────────────────────────────────────┐
│  paskinomercato.ear                                         │
│                                                             │
│  ┌───────────────────┐     ┌────────────────────────────┐   │
│  │   paskinomercato  │     │  paskinomercato-ejb.jar     │   │
│  │       .war        │     │                            │   │
│  │                   │     │  Session EJB:              │   │
│  │  Servlet          │────▶│   Catalogo, Ordine, Cliente  │   │
│  │  JSP/JSTL/EL      │     │   Carrello, Mail            │   │
│  │  Endpoint JAX-RPC │     │             │              │   │
│  │  File statici     │     │             ▼              │   │
│  │  (img/, css/)     │     │  BMP Entity EJB:           │   │
│  │                   │     │   Prodotto, Categoria,      │   │
│  │                   │     │   Cliente, Ordine           │   │
│  └───────────────────┘     └────────────────────────────┘   │
│                                         │ JDBC (CMT XA)     │
└─────────────────────────────────────────┼───────────────────┘
                                          ▼
                                   ┌─────────────┐
                                   │  PostgreSQL  │
                                   │  mercatodb   │
                                   └─────────────┘
```

Tutte le chiamate EJB sono **locali** (stessa JVM, stesso EAR). Nessun EJB remoto. Il modulo WAR risolve i Session Bean tramite `java:comp/env/ejb/NomeBean`; i Session Bean accedono agli Entity Bean tramite i rispettivi riferimenti `java:comp/env/ejb/*EntityBean`. Le transazioni sono gestite interamente dal container EJB (CMT).

---

## Modello di Dominio

Tutta la persistenza avviene tramite JDBC puro — nessuna annotazione JPA. I seguenti value object (POJO serializzabili) corrispondono alle righe del database:

| Classe | Tabella | Descrizione |
|---|---|---|
| [`Prodotto`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Prodotto.java) | `mercato.prodotto` | Prodotto (nome/descrizione bilingue, prezzo, stock) |
| [`Categoria`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Categoria.java) | `mercato.categoria` | Categoria prodotti (bilingue) |
| [`Cliente`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Cliente.java) | `mercato.cliente` | Account cliente |
| [`Indirizzo`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Indirizzo.java) | `mercato.indirizzo` | Indirizzo di consegna italiano |
| [`Ordine`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/Ordine.java) | `mercato.ordine` | Testata ordine |
| [`RigaOrdine`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/RigaOrdine.java) | `mercato.riga_ordine` | Riga ordine (prodotto + qtà + prezzo) |
| [`CarrelloItem`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/CarrelloItem.java) | `mercato.carrello` | Elemento carrello (anche nell'EJB stateful) |

---

## EJB Beans

Tutti i bean usano lo stile **EJB 2.1** e sono configurati tramite `ejb-jar.xml`, senza annotazioni `@Stateless`, `@Entity`, `@EJB` o JPA. I cinque Session Bean implementano `javax.ejb.SessionBean`; i quattro Entity Bean BMP implementano `javax.ejb.EntityBean`. Tutti espongono esclusivamente interfacce `Local`/`LocalHome`.

Le transazioni sono **Container-Managed (CMT)**. Nessun bean chiama mai `connection.commit()`, `connection.rollback()` o `connection.setAutoCommit()` — queste operazioni sono vietate con connessioni XA globali. In caso di errore si usa `ctx.setRollbackOnly()`.

### Entity Beans BMP

Il modulo contiene quattro **Bean-Managed Persistence Entity Bean**. Non sono
entità JPA: ogni bean implementa esplicitamente il ciclo di vita EJB 2.1 e le
operazioni SQL nei metodi `ejbCreate`, `ejbLoad`, `ejbStore`, `ejbRemove` ed
`ejbFind*`. La chiave primaria di tutti gli Entity Bean è `java.lang.Integer`.

| Entity Bean | Tabella | Finder principali | Session Bean utilizzatore |
|---|---|---|---|
| `ProdottoEntityBean` | `mercato.prodotto` | `findByPrimaryKey`, `findAll`, `findByCategoriaId`, `findByAttivo`, `findByCodice`, `findByNomeContaining` | `CatalogoBean` |
| `CategoriaEntityBean` | `mercato.categoria` | `findByPrimaryKey`, `findAll`, `findByCodice` | `CatalogoBean` |
| `ClienteEntityBean` | `mercato.cliente` | `findByPrimaryKey`, `findByEmail`, `findByAttivo` | `ClienteBean` |
| `OrdineEntityBean` | `mercato.ordine` | `findByPrimaryKey`, `findByNumeroOrdine`, `findByClienteId` | `OrdineBean` |

Ogni Entity Bean dichiara il riferimento DataSource
`java:comp/env/jdbc/MercatoDB`, associato dal binding WebSphere alla risorsa
globale `jdbc/MercatoDB`. I riferimenti usati dai Session Bean sono:

| Componente chiamante | Riferimento component environment | Home locale WebSphere |
|---|---|---|
| `CatalogoBean` | `java:comp/env/ejb/ProdottoEntityBean` | `ejblocal:ejb/it/paskinomercato/ejb/entity/prodotto/ProdottoEntityLocalHome` |
| `CatalogoBean` | `java:comp/env/ejb/CategoriaEntityBean` | `ejblocal:ejb/it/paskinomercato/ejb/entity/categoria/CategoriaEntityLocalHome` |
| `ClienteBean` | `java:comp/env/ejb/ClienteEntityBean` | `ejblocal:ejb/it/paskinomercato/ejb/entity/cliente/ClienteEntityLocalHome` |
| `OrdineBean` | `java:comp/env/ejb/OrdineEntityBean` | `ejblocal:ejb/it/paskinomercato/ejb/entity/ordine/OrdineEntityLocalHome` |

`ibm-ejb-jar-bnd.xmi` contiene i binding delle home e dei resource reference.
Durante installazione o aggiornamento,
`scripts/wsadmin/install_paskinomercato.py` fornisce esplicitamente a
`MapEJBRefToEJB` i quattro mapping verso il namespace JVM-scoped `ejblocal:`.
Questo passaggio è obbligatorio su WebSphere 8.5.5 per il modulo EJB 2.1.

### CatalogoBean — Stateless

**JNDI:** `java:comp/env/ejb/CatalogoBean`

| Metodo | Descrizione |
|---|---|
| `getProdotti(pagina, dimensionePagina)` | Lista prodotti paginata |
| `getProdottiPerCategoria(categoriaId, pagina, dimensionePagina)` | Prodotti filtrati per categoria |
| `getProdottoById(id)` | Ricerca singolo prodotto per ID |
| `getProdottoByCodice(codice)` | Ricerca prodotto per codice |
| `cercaProdotti(testo)` | Ricerca testuale (nomi IT + EN) |
| `getCategorie()` | Tutte le categorie |
| `getCategoriaById(id)` | Singola categoria per ID |
| `contaProdotti()` | Conteggio totale prodotti attivi |
| `contaProdottiPerCategoria(categoriaId)` | Conteggio prodotti per categoria |
| `isDisponibile(prodottoId, quantita)` | Verifica disponibilità a magazzino |

### OrdineBean — Stateless

**JNDI:** `java:comp/env/ejb/OrdineBean`

| Metodo | Descrizione |
|---|---|
| `creaOrdine(clienteId, indirizzoId, items, note)` | Crea ordine + decrementa stock + svuota carrello (CMT `Required`) |
| `getOrdineByNumero(numero)` | Ordine per numero |
| `getOrdiniCliente(clienteId)` | Storico ordini del cliente |
| `getRigheOrdine(ordineId)` | Righe dell'ordine |
| `aggiornaStato(ordineId, stato)` | Aggiorna stato ordine |

### ClienteBean — Stateless

**JNDI:** `java:comp/env/ejb/ClienteBean`

| Metodo | Descrizione |
|---|---|
| `registra(email, passwordHash, nome, cognome, telefono, lingua)` | Registrazione nuovo cliente tramite `ClienteEntityBean` |
| `login(email, passwordHash)` | Autenticazione con password SHA-256 (JDBC diretto) |
| `getClienteById(id)` | Ricerca cliente per ID tramite entity bean |
| `getClienteByEmail(email)` | Ricerca cliente per indirizzo email |
| `aggiornaLingua(clienteId, lingua)` | Salvataggio preferenza lingua (aggiorna entity bean) |
| `aggiungiIndirizzo(clienteId, via, civico, citta, cap, provincia)` | Aggiunta indirizzo di consegna italiano (JDBC diretto) |
| `getIndirizzi(clienteId)` | Rubrica indirizzi del cliente |
| `getIndirizzo(indirizzoId)` | Singolo indirizzo per ID |

### CarrelloBean — **Stateful**

**JNDI:** `java:comp/env/ejb/CarrelloBean`

Mantiene il carrello nello stato lato server dell'EJB. Un'istanza per sessione utente, salvata in `HttpSession`. Metodi: `aggiungi`, `rimuovi`, `aggiornaQuantita`, `svuota`, `getItems`, `getTotale`, `getNumeroArticoli`.

### MailBean — Stateless

**JNDI:** `java:comp/env/ejb/MailBean`  
**Sessione mail:** `java:comp/env/mail/MercatoMail`

Invia email HTML in italiano o inglese:
- `inviaConfermaOrdine(cliente, ordine, righe, lingua)` — email completa con riepilogo ordine
- `inviaRegistrazioneConferma(cliente, lingua)` — email di benvenuto alla registrazione

---

## Servlet

| Servlet | URL | Descrizione |
|---|---|---|
| [`CatalogoServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CatalogoServlet.java) | `/catalogo` | Griglia prodotti con paginazione, filtro categoria, ricerca |
| [`CarrelloServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CarrelloServlet.java) | `/carrello` | Vista carrello (GET) e azioni: `aggiungi`, `rimuovi`, `aggiorna`, `svuota` (POST) |
| [`CheckoutServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/CheckoutServlet.java) | `/checkout` | Selezione indirizzo + inserimento ordine + invio email |
| [`LoginServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/LoginServlet.java) | `/login` | Login + registrazione + logout |
| [`LinguaServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/LinguaServlet.java) | `/lingua?lang=it\|en` | Cambio lingua, salvato in sessione e nel DB |
| [`OrdineServlet`](paskinomercato-war/src/main/java/it/paskinomercato/servlet/OrdineServlet.java) | `/ordini` | Lista storico ordini e dettaglio singolo ordine |

> **Nota:** `CatalogoServlet` non è più mappato su `/`. La home page (`/`) è servita direttamente da `index.jsp` tramite `<welcome-file-list>`. I file statici (`/img/*`, `/css/*`) sono serviti dal motore nativo di WebSphere grazie a `fileServingEnabled=true` in `ibm-web-ext.xmi`.

---

## Pagine JSP

Tutte le JSP usano **JSTL 1.2** (`c:`, `fmt:`, `fn:`) e **EL 2.2**. La lingua è determinata da `${sessionScope.lang}` (predefinito: `it`).

> **Vincolo EL su WAS 8.5.5:** le espressioni `${}` devono stare su **una sola riga** — l'implementazione EL 2.2 di WebSphere non accetta newline all'interno di un'espressione.

| JSP | Descrizione |
|---|---|
| [`index.jsp`](paskinomercato-war/src/main/webapp/index.jsp) | Home page con hero banner e schede funzionalità |
| [`catalogo.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/catalogo.jsp) | Griglia prodotti, sidebar categorie, paginazione. Usa `fn:replace` per convertire i nomi immagine `.jpg` → `.svg` |
| [`carrello.jsp`](paskinomercato-war/src/main/webapp/WEB-INF/jsp/carrello.jsp) | Tabella carrello con aggiornamento quantità e rimozione. Usa `fn:replace` per le immagini prodotto |
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

## Web Service JAX-RPC

L'applicazione espone un web service **SOAP/Document-Literal Wrapped** per l'integrazione con sistemi esterni (es. partner, ERP).

**Endpoint:** `http://<host>:9080/paskinomercato/MercatoService`  
**WSDL:** `http://<host>:9080/paskinomercato/MercatoService?wsdl`  
**Namespace:** `http://ws.paskinomercato.it/`

| Operazione | Input | Output | Descrizione |
|---|---|---|---|
| `getProdottoXml` | `codice: string` | Stringa XML | Dettaglio prodotto per codice |
| `getStatoOrdine` | `numeroOrdine: string` | Stringa XML | Stato e totale dell'ordine |
| `getCategorieXml` | *(nessuno)* | Stringa XML | Lista di tutte le categorie attive |

Per ogni operazione esistono due **wrapper bean** in `it.paskinomercato.ws` che il runtime JAX-RPC di WebSphere usa per serializzare/deserializzare il payload SOAP (obbligatori con stile document/literal wrapped):

| Classe | Elemento WSDL |
|---|---|
| `GetProdottoXmlRequest` / `GetProdottoXmlResponse` | `tns:getProdottoXmlRequest` / `tns:getProdottoXmlResponse` |
| `GetStatoOrdineRequest` / `GetStatoOrdineResponse` | `tns:getStatoOrdineRequest` / `tns:getStatoOrdineResponse` |
| `GetCategorieXmlRequest` / `GetCategorieXmlResponse` | `tns:getCategorieXmlRequest` / `tns:getCategorieXmlResponse` |

File descrittori:
- [`WEB-INF/webservices.xml`](paskinomercato-war/src/main/webapp/WEB-INF/webservices.xml)
- [`WEB-INF/wsdl/MercatoService.wsdl`](paskinomercato-war/src/main/webapp/WEB-INF/wsdl/MercatoService.wsdl)
- [`WEB-INF/MercatoService-mapping.xml`](paskinomercato-war/src/main/webapp/WEB-INF/MercatoService-mapping.xml)

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

### Limite prodotti

Un trigger PostgreSQL (`trg_max_prodotti`) si attiva `BEFORE INSERT` su `mercato.prodotto` e lancia un'eccezione se il conteggio supererebbe 1.503:

```sql
IF (SELECT COUNT(*) FROM mercato.prodotto) >= 1503 THEN
    RAISE EXCEPTION 'Maximum product limit of 1503 reached';
END IF;
```

### Flusso di stato ordine

```
IN_ATTESA → CONFERMATO → IN_PREPARAZIONE → SPEDITO → CONSEGNATO
                                                   ↘ ANNULLATO
```

### JNDI DataSource

Tutti gli EJB e le Servlet ottengono la connessione tramite:

```java
InitialContext ic = new InitialContext();
DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
```

---

## Prodotti e Immagini

### Dati di esempio

[`paskinomercato-ejb/src/main/resources/db/seed_products.sql`](paskinomercato-ejb/src/main/resources/db/seed_products.sql) contiene **150 prodotti originali** di un supermercato italiano, distribuiti su 10 categorie. Tutti i nomi, le descrizioni e i prezzi sono contenuto originale.

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

### Immagini prodotti

150 immagini SVG placeholder pre-generate si trovano in [`paskinomercato-war/src/main/webapp/img/prodotti/`](paskinomercato-war/src/main/webapp/img/prodotti/). Ogni SVG contiene:
- L'emoji appropriata per il prodotto (unica per prodotto)
- Nome prodotto in italiano e inglese
- Colore di sfondo codificato per categoria
- Branding footer `PaskinoMercato`

Il database memorizza i nomi immagine con estensione `.jpg` (es. `fv001_mele_golden.jpg`). Le JSP convertono automaticamente il nome al volo con `fn:replace`:

```jsp
${fn:replace(p.immagine, '.jpg', '.svg')}
```

Per rigenerare le immagini SVG placeholder:

```bash
python3 scripts/tools/generate_placeholder_images.py
```

---

## Supporto Bilingue

La lingua dell'interfaccia è salvata nell'attributo di sessione `HttpSession` `lang` (`"it"` o `"en"`). Il valore predefinito è italiano.

- **Cambio lingua:** cliccando IT / EN nella barra di navigazione si chiama `GET /lingua?lang=it|en`
- **Persistenza:** se il cliente è autenticato, la preferenza viene aggiornata in `mercato.cliente.lingua`
- **Pattern JSP:** ogni stringa bilingue usa EL inline su **una sola riga** (vincolo WAS 8.5.5 EL 2.2):

```jsp
${lang eq 'it' ? 'Testo italiano' : 'English text'}
```

- **Nomi prodotto:** `Prodotto.getNome(lang)` e `getDescrizione(lang)` selezionano `nome_it`/`nome_en` dalla riga DB.
- **Email:** `MailBean` genera il corpo HTML dell'email nella lingua scelta dal cliente.

---

## Validazione Indirizzi Italiani

[`IndirizzoItaliaValidator`](paskinomercato-ejb/src/main/java/it/paskinomercato/util/IndirizzoItaliaValidator.java) applica tre regole:

| Regola | Validazione |
|---|---|
| Paese | Deve essere `"IT"` (case-insensitive). Altrimenti: *"La consegna è disponibile solo in Italia. / Delivery is only available in Italy."* |
| CAP | Deve corrispondere alla regex `^\d{5}$` (esattamente 5 cifre) |
| Provincia | Deve essere una delle **110 abbreviazioni ufficiali** delle province italiane (AG, AL, AN … VV) |

Il database applica inoltre `paese = 'IT'` tramite un vincolo `CHECK` su `mercato.indirizzo.paese`.

---

## Prerequisiti

| Strumento | Versione |
|---|---|
| Java JDK | **8** (il codice sorgente e il bytecode devono essere Java 8) |
| Maven | 3.6+ |
| PostgreSQL | 15+ |
| IBM WebSphere Application Server | **8.5.5** |
| Python | 3.x (solo per la generazione immagini SVG) |
| `wsadmin` | Incluso nell'installazione WAS |

> **Importante:** anche se si compila con un JDK più recente (es. JDK 21 o 25), il POM è configurato con `<release>8</release>` e `Build-Jdk-Spec: 1.8` nel MANIFEST per garantire la compatibilità con WAS 8.5.5, che accetta solo bytecode Java 8.

---

## Build

```bash
# Navigare nella cartella radice del progetto
cd PaskinoMercato-WebSphere

# Build completa — genera l'EAR in paskinomercato-ear/target/
mvn clean package

# Artefatto deployabile
ls paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear

# Registrare l'hash dell'EAR che verrà copiato sul server
sha256sum paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear
```

L'installer controlla il contenuto dell'EAR prima di modificare WebSphere e rifiuta
automaticamente artefatti vecchi privi dei binding EJB/resource richiesti.

---

## Configurazione del Database

```bash
# 1. Creare il database e l'utente
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'latuapassword';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"

# 2. Eseguire il DDL dello schema
psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/schema.sql

# 3. Caricare i 150 prodotti di esempio
psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

In alternativa, usare il caricatore Java JDBC standalone (non richiede application server):

```bash
cd scripts/tools
javac -cp postgresql-42.7.3.jar LoadProducts.java
java  -cp .:postgresql-42.7.3.jar it.paskinomercato.tools.LoadProducts \
      localhost 5432 mercatodb mercato latuapassword \
      ../../paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

---

## Configurazione WebSphere

Lo script `install_paskinomercato.py` crea o aggiorna automaticamente JDBC Provider,
alias J2C, DataSource e sessione JavaMail. Prima dell'esecuzione devono esistere solo:

- il nodo e il server di destinazione;
- il virtual host (per impostazione predefinita `default_host`);
- il `Built-in Mail Provider` a livello cella, nodo o server;
- il driver PostgreSQL nel percorso visibile alla JVM del server.

Le sezioni seguenti descrivono le risorse generate dallo script e sono utili anche
per verificarle dalla console amministrativa.

### 1. JDBC Provider PostgreSQL

**WAS Admin Console → Risorse → JDBC → Provider JDBC → Nuovo**

| Campo | Valore |
|---|---|
| Tipo provider | Definito dall'utente |
| Classe di implementazione | `org.postgresql.ds.PGConnectionPoolDataSource` |
| Classpath predefinito | `/opt/jdbc/postgresql-42.7.13.jar` |
| Nome | `PostgreSQL JDBC Driver` |

### 2. DataSource

**Risorse → JDBC → Origini dati → Nuova**

| Campo | Valore |
|---|---|
| Nome JNDI | `jdbc/MercatoDB` |
| Nome database | `mercatodb` |
| Nome server | `localhost` |
| Numero porta | `5432` |
| Alias autenticazione J2C | `MercatoDBAlias` (utente: `mercato`) |
| Connessioni minime | 2 |
| Connessioni massime | 20 |

### 3. Sessione JavaMail

**Risorse → Mail → Sessioni mail → Nuova**

| Campo | Valore |
|---|---|
| Nome JNDI | `mail/MercatoMail` |
| Host di trasporto mail | `smtp.tuodominio.it` |
| Porta di trasporto mail | `587` |
| Mail from | `noreply@paskinomercato.it` |
| STARTTLS | `mail.smtp.starttls.enable = true` |

---

## Deploy su WebSphere (wsadmin)

L'installer è idempotente: usa `AdminApp.install` se `PaskinoMercato` non esiste e
`AdminApp.update` se è già installata. In entrambi i casi passa i mapping EJB prima
della validazione WebSphere. Non è necessario disinstallare per un normale aggiornamento.

### Copia degli artefatti

Copiare sul sistema dove viene eseguito `wsadmin` sia l'EAR appena compilato sia
lo script della stessa revisione:

```bash
scp paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear \
    brian@lovecraft:/opt/deploy/
scp scripts/wsadmin/install_paskinomercato.py \
    brian@lovecraft:/opt/deploy/

# Sul server: l'hash deve coincidere con quello calcolato dopo il build
sha256sum /opt/deploy/paskinomercato-ear-1.0.0.ear
```

### Configurazione tramite variabili d'ambiente

Tutti i valori possono essere modificati direttamente nella sezione `USER
CONFIGURATION` oppure sovrascritti tramite variabili d'ambiente. L'uso delle
variabili è consigliato soprattutto per le password.

| Variabile | Predefinito | Scopo |
|---|---|---|
| `PASKINO_EAR_PATH` | `/opt/deploy/paskinomercato-ear-1.0.0.ear` | EAR letto da `wsadmin` |
| `PASKINO_NODE_NAME` | `paskinoNode1` | Nodo WebSphere |
| `PASKINO_SERVER_NAME` | `brian1` | Application server |
| `PASKINO_POSTGRES_JAR` | `/opt/jdbc/postgresql-42.7.13.jar` | Driver visibile alla JVM target |
| `PASKINO_DB_HOST` | `localhost` | Host PostgreSQL |
| `PASKINO_DB_PORT` | `5432` | Porta PostgreSQL |
| `PASKINO_DB_NAME` | `mercatodb` | Database |
| `PASKINO_DB_USER` | `mercato` | Utente database |
| `PASKINO_DB_PASSWORD` | `changeme` | Password database |
| `PASKINO_MAIL_HOST` | `smtp.paskinomercato.it` | Server SMTP |
| `PASKINO_MAIL_PORT` | `587` | Porta SMTP |
| `PASKINO_MAIL_USER` | `noreply@paskinomercato.it` | Utente SMTP |
| `PASKINO_MAIL_PASSWORD` | `changeme` | Password SMTP |

Esempio:

```bash
export PASKINO_EAR_PATH=/opt/deploy/paskinomercato-ear-1.0.0.ear
export PASKINO_NODE_NAME=paskinoNode1
export PASKINO_SERVER_NAME=brian1
export PASKINO_DB_PASSWORD='password-database'
export PASKINO_MAIL_PASSWORD='password-smtp'
```

### Installazione automatizzata

```bash
$WAS_HOME/bin/wsadmin.sh \
  -lang jython \
  -conntype SOAP \
  -host localhost \
  -port 8879 \
  -user wasadmin \
  -password waspassword \
  -f /opt/deploy/install_paskinomercato.py
```

Passi eseguiti automaticamente:

1. Validazione di configurazione, target e contenuto dell'EAR.
2. Creazione/aggiornamento del JDBC Provider PostgreSQL.
3. Creazione/aggiornamento di alias J2C, DataSource `jdbc/MercatoDB` e pool.
4. Creazione/aggiornamento della sessione `mail/MercatoMail`.
5. Installazione o aggiornamento dell'EAR.
6. Mapping esplicito dei quattro riferimenti EJB 2.1 tramite `MapEJBRefToEJB`.
7. Mapping dei moduli, virtual host e context root.
8. Salvataggio, sincronizzazione del nodo e avvio dell'applicazione.

I quattro riferimenti locali vengono risolti sotto `java:comp/env/ejb/*` e
puntano alle home locali nel namespace JVM-scoped `ejblocal:`. Non sostituire
questi target con i nomi globali `ejb/...`: le interfacce EJB locali non sono
pubblicate nel namespace globale del server.

### Disinstallazione

Usare la disinstallazione solo per una rimozione completa o se si desidera
ripartire deliberatamente da una configurazione applicativa vuota:

La disinstallazione può essere eseguita manualmente dalla WAS Admin Console oppure tramite wsadmin:

```bash
$WAS_HOME/bin/wsadmin.sh -lang jython -conntype SOAP \
  -host localhost -port 8879 -user wasadmin -password waspassword \
  -c "AdminApp.uninstall('PaskinoMercato'); AdminConfig.save()"
```

### Diagnostica deploy

Se `AdminApp` restituisce `ADMA0007E` o `WASX7109E`, lo script stampa
automaticamente le righe di `taskInfo` per `MapEJBRefToEJB` e
`MapResRefToEJB`, quindi annulla tutte le modifiche non salvate.

Controllare nell'ordine:

1. Che l'output mostri `Script revision: 1.2.0`.
2. Che `PASKINO_EAR_PATH` punti all'EAR appena compilato e copiato.
3. Che l'hash SHA-256 sul server coincida con quello del build.
4. Che modulo e URI siano `PaskinoMercato EJB Module` e
   `paskinomercato-ejb.jar,META-INF/ejb-jar.xml`.
5. Che i target dei riferimenti EJB inizino con `ejblocal:`.

Il messaggio `[WARNING] Unsaved configuration changes were discarded` indica
che `AdminConfig.reset()` ha ripristinato la sessione dopo il fallimento; è
quindi possibile correggere configurazione o artefatto e rieseguire lo script.

### Verifica Salute

Verificare lo stato dell'applicazione dalla WAS Admin Console oppure tramite wsadmin:

```bash
$WAS_HOME/bin/wsadmin.sh -lang jython -conntype SOAP \
  -host localhost -port 8879 -user wasadmin -password waspassword \
  -c "print AdminApp.list()"
```

Verifica: stato STARTED dell'applicazione, test connessione DataSource, presenza sessione mail, deploy modulo EJB.

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
| Endpoint SOAP | `http://localhost:9080/paskinomercato/MercatoService` |
| WSDL | `http://localhost:9080/paskinomercato/MercatoService?wsdl` |

---

## Riferimento Script

| Script | Linguaggio | Scopo |
|---|---|---|
| [`scripts/wsadmin/install_paskinomercato.py`](scripts/wsadmin/install_paskinomercato.py) | Jython | Install/update WAS 8.5.5: preflight EAR, JDBC, mail, mapping EJB 2.1, deploy e avvio |
| [`scripts/tools/generate_placeholder_images.py`](scripts/tools/generate_placeholder_images.py) | Python 3 | Rigenera le 150 immagini SVG placeholder prodotti |
| [`scripts/tools/LoadProducts.java`](scripts/tools/LoadProducts.java) | Java | Caricatore JDBC standalone per `seed_products.sql` |

---

## Sostituzione delle Immagini Placeholder

1. Inserire un file SVG con il nome corrispondente al codice prodotto (es. `fv001_mele_golden.svg`) in:
   ```
   paskinomercato-war/src/main/webapp/img/prodotti/
   ```
2. Ricompilare e ridistribuire l'EAR — nessuna modifica al codice necessaria.

> Il database memorizza i nomi immagine con estensione `.jpg`. Le JSP (`catalogo.jsp`, `carrello.jsp`) usano `fn:replace` per convertire automaticamente `.jpg` → `.svg` al momento del rendering.

Per aggiornare la colonna `immagine` di un prodotto nel database:

```sql
UPDATE mercato.prodotto
SET immagine = 'fv001_mele_golden.jpg'
WHERE codice = 'FV001';
```

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
