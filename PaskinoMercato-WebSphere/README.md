# PaskinoMercato 🛒

**Supermercato Online Italiano — JavaEE 5 / EJB 2.0 / WebSphere Application Server 8.5.5**

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
| Stile EJB | **EJB 2.0** — `SessionBean`, `EJBLocalHome`, `EJBLocalObject` (nessuna annotazione, nessun JPA) |
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
├── paskinomercato-ejb/                  ← Modulo EJB 2.0
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── ejb/
│       │   │   ├── carrello/            ← CarrelloBean (Stateful)
│       │   │   ├── catalogo/            ← CatalogoBean (Stateless)
│       │   │   ├── cliente/             ← ClienteBean  (Stateless)
│       │   │   ├── mail/                ← MailBean      (Stateless)
│       │   │   └── ordine/              ← OrdineBean   (Stateless)
│       │   ├── model/                   ← Value object (nessun JPA)
│       │   ├── util/                    ← IndirizzoItaliaValidator
│       │   └── ws/                      ← SEI JAX-RPC, implementazione e wrapper bean
│       └── resources/
│           ├── META-INF/
│           │   ├── ejb-jar.xml          ← Descrittore di deploy EJB 2.0
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
│   │   ├── install_paskinomercato.py    ← Installazione completa (wsadmin Jython)
│   │   ├── uninstall_paskinomercato.py  ← Disinstallazione completa
│   │   └── check_paskinomercato.py      ← Verifica post-deploy
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
│  │                   │     │  CatalogoBean  (Stateless) │   │
│  │  Servlet          │────▶│  OrdineBean    (Stateless) │   │
│  │  JSP/JSTL/EL      │     │  ClienteBean   (Stateless) │   │
│  │  Endpoint JAX-RPC │     │  CarrelloBean  (Stateful)  │   │
│  │  File statici     │     │  MailBean      (Stateless) │   │
│  │  (img/, css/)     │     └────────────────────────────┘   │
│  └───────────────────┘                  │                   │
│                                         │ JDBC (CMT XA)     │
└─────────────────────────────────────────┼───────────────────┘
                                          ▼
                                   ┌─────────────┐
                                   │  PostgreSQL  │
                                   │  mercatodb   │
                                   └─────────────┘
```

Tutte le chiamate EJB sono **locali** (stessa JVM, stesso EAR). Nessun EJB remoto. Il modulo WAR risolve i bean tramite `java:comp/env/ejb/NomeBean`. Le transazioni sono gestite interamente dal container EJB (CMT).

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

Tutti i bean usano lo stile **EJB 2.0**: implementano `javax.ejb.SessionBean`, espongono un'interfaccia `Local` che estende `EJBLocalObject` e un'interfaccia `LocalHome` che estende `EJBLocalHome`. Configurati interamente tramite `ejb-jar.xml` — nessuna annotazione `@Stateless` / `@EJB`.

Le transazioni sono **Container-Managed (CMT)**. Nessun bean chiama mai `connection.commit()`, `connection.rollback()` o `connection.setAutoCommit()` — queste operazioni sono vietate con connessioni XA globali. In caso di errore si usa `ctx.setRollbackOnly()`.

### CatalogoBean — Stateless

**JNDI:** `java:comp/env/ejb/CatalogoBean`

| Metodo | Descrizione |
|---|---|
| `getProdotti(pagina, dim)` | Lista prodotti paginata |
| `getProdottiPerCategoria(catId, pagina, dim)` | Prodotti filtrati per categoria |
| `getProdottoById(id)` | Ricerca singolo prodotto per ID |
| `getProdottoByCodice(codice)` | Ricerca prodotto per codice |
| `cercaProdotti(testo)` | Ricerca testuale (nomi IT + EN) |
| `getCategorie()` | Tutte le categorie |
| `contaProdotti()` | Conteggio totale prodotti attivi |
| `isDisponibile(prodottoId, qty)` | Verifica disponibilità a magazzino |

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
| `registra(...)` | Registrazione nuovo cliente |
| `login(email, pwHash)` | Autenticazione con password SHA-256 |
| `getClienteById(id)` | Ricerca cliente per ID |
| `aggiungiIndirizzo(...)` | Aggiunta indirizzo di consegna italiano |
| `getIndirizzi(clienteId)` | Rubrica indirizzi del cliente |
| `aggiornaLingua(clienteId, lang)` | Salvataggio preferenza lingua |

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
cd PaskinoMercato

# Build completa — genera l'EAR in paskinomercato-ear/target/
mvn clean package

# Artefatto deployabile
ls paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear
```

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

Queste risorse devono essere create in WebSphere **prima** di distribuire l'EAR.

### 1. JDBC Provider PostgreSQL

**WAS Admin Console → Risorse → JDBC → Provider JDBC → Nuovo**

| Campo | Valore |
|---|---|
| Tipo provider | Definito dall'utente |
| Classe di implementazione | `org.postgresql.ds.PGConnectionPoolDataSource` |
| Classpath | `/opt/jdbc/postgresql-42.7.3.jar` |
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

> **Attenzione:** non eseguire mai un aggiornamento/reinstallazione sopra un'installazione esistente. Usare sempre la sequenza **disinstalla → installa** per evitare che il config repository di WebSphere mantenga file di moduli obsoleti che causano `NoModuleFileException`.

### Installazione automatizzata

```bash
$WAS_HOME/bin/wsadmin.sh \
  -lang jython \
  -conntype SOAP \
  -host localhost \
  -port 8879 \
  -user wasadmin \
  -password waspassword \
  -f scripts/wsadmin/install_paskinomercato.py
```

Passi eseguiti automaticamente:
1. Creazione JDBC Provider PostgreSQL
2. Creazione DataSource `jdbc/MercatoDB` + alias J2C + pool di connessioni
3. Creazione sessione JavaMail `mail/MercatoMail` con proprietà SMTP
4. Installazione dell'EAR tramite `AdminApp.install`
5. Mapping dei riferimenti alle risorse per i moduli EJB e WAR
6. Salvataggio configurazione e sincronizzazione di tutti i nodi
7. Avvio dell'applicazione

### Disinstallazione

```bash
$WAS_HOME/bin/wsadmin.sh -lang jython \
  -f scripts/wsadmin/uninstall_paskinomercato.py
```

### Reinstallazione manuale

```bash
# In wsadmin (jython):
AdminApp.uninstall('PaskinoMercato')
AdminConfig.save()
# Verificare che la directory sia rimossa:
# $WAS_HOME/profiles/<profile>/config/cells/<cell>/applications/PaskinoMercato.ear/

AdminApp.install('/path/to/paskinomercato-ear-1.0.0.ear',
    ['-appname', 'PaskinoMercato', '-usedefaultbindings', '-contextroot', '/paskinomercato'])
AdminConfig.save()
```

### Verifica Salute

```bash
$WAS_HOME/bin/wsadmin.sh -lang jython \
  -f scripts/wsadmin/check_paskinomercato.py
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
| [`scripts/wsadmin/install_paskinomercato.py`](scripts/wsadmin/install_paskinomercato.py) | Jython | Installazione WAS completa: JDBC, mail, deploy EAR, avvio |
| [`scripts/wsadmin/uninstall_paskinomercato.py`](scripts/wsadmin/uninstall_paskinomercato.py) | Jython | Disinstallazione WAS completa: stop, undeploy, rimozione risorse |
| [`scripts/wsadmin/check_paskinomercato.py`](scripts/wsadmin/check_paskinomercato.py) | Jython | Verifica salute: stato app, connessione DB, sessione mail |
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
