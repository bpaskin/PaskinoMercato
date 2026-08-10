# PaskinoMercato 🛒

**Supermercato Online Italiano — JavaEE 5 / EJB 2.0 / Open Liberty**

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
9. [Web Service JAX-WS](#web-service-jax-ws)
10. [Database](#database)
11. [Prodotti e Immagini](#prodotti-e-immagini)
12. [Supporto Bilingue](#supporto-bilingue)
13. [Validazione Indirizzi Italiani](#validazione-indirizzi-italiani)
14. [Prerequisiti](#prerequisiti)
15. [Build](#build)
16. [Configurazione del Database](#configurazione-del-database)
17. [Configurazione Liberty](#configurazione-liberty)
18. [Avvio con Liberty](#avvio-con-liberty)
19. [URL dell'Applicazione](#url-dellapplicazione)
20. [Riferimento Script](#riferimento-script)
21. [Sostituzione delle Immagini Placeholder](#sostituzione-delle-immagini-placeholder)

---

## Panoramica

PaskinoMercato è un'applicazione **JavaEE 5** enterprise distribuita come EAR su **Open Liberty**. Offre:

- Una vetrina di supermercato online bilingue (🇮🇹 Italiano / 🇬🇧 Inglese)
- Catalogo prodotti con navigazione per categoria, ricerca e paginazione (max **1.503 prodotti**)
- Carrello della spesa (POJO serializzabile in sessione HTTP)
- Flusso di checkout completo con validazione indirizzo italiano
- Gestione ordini con storico per cliente
- Email di conferma ordine via **JavaMail** (HTML, bilingue)
- Web service **JAX-WS SOAP** per integrazione con sistemi esterni
- Tutti i prezzi esclusivamente in **Euro (€)**
- Consegna **solo a indirizzi italiani** (applicata a livello applicativo e di database)

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Application Server | **Open Liberty** (features: `ejb-3.2`, `servlet-3.1`, `jsp-2.3`, `jdbc-4.1`, `javaMail-1.5`, `cdi-1.2`, `jndi-1.0`) |
| Versione Java EE | JavaEE 5 (web-app 2.5, EJB 2.1) |
| Bytecode Java | **Java 8** (compilato con `<release>8</release>`) |
| Stile EJB | **EJB 2.0** — `SessionBean`, `EJBLocalHome`, `EJBLocalObject` (nessuna annotazione, nessun JPA) |
| Transazioni EJB | **Container-Managed Transactions** (CMT) — nessun `commit`/`rollback` manuale |
| Persistenza | **JDBC** diretto tramite JNDI `DataSource` (nessun JPA, nessun Hibernate) |
| Database | **PostgreSQL** 15+ |
| Web Service | **JAX-WS** (SOAP) |
| Livello Web | **Servlet 3.1** + **JSP 2.3** |
| Tag di Vista | **JSTL 1.2** + **EL 3.0** |
| Carrello | POJO `CarrelloSessionBean` serializzabile in `HttpSession` |
| Email | **JavaMail 1.5** tramite JNDI `mail/MercatoMail` |
| Build | **Maven 3** (progetto EAR multi-modulo) |
| Lingue | Italiano (predefinito) + Inglese |
| Valuta | Solo Euro (€) |

---

## Struttura del Progetto

```
PaskinoMercato-Liberty/
│
├── pom.xml                              ← POM padre (3 moduli)
│
├── paskinomercato-ejb/                  ← Modulo EJB 2.0
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── ejb/
│       │   │   ├── catalogo/            ← CatalogoBean (Stateless)
│       │   │   ├── cliente/             ← ClienteBean  (Stateless)
│       │   │   ├── mail/                ← MailBean      (Stateless)
│       │   │   └── ordine/              ← OrdineBean   (Stateless)
│       │   ├── model/                   ← Value object (nessun JPA)
│       │   ├── util/                    ← IndirizzoItaliaValidator
│       │   └── ws/                      ← SEI JAX-WS e implementazione
│       └── resources/
│           ├── META-INF/
│           │   └── ejb-jar.xml          ← Descrittore di deploy EJB 2.0
│           └── db/
│               ├── schema.sql           ← DDL PostgreSQL
│               └── seed_products.sql    ← 150 prodotti originali
│
├── paskinomercato-war/                  ← Modulo WAR
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
│           ├── css/style.css            ← Foglio di stile principale
│           ├── img/prodotti/            ← 150 immagini SVG prodotti
│           └── WEB-INF/
│               ├── web.xml              ← Descrittore Servlet 2.5
│               ├── webservices.xml      ← Descrittore endpoint JAX-WS
│               ├── wsdl/
│               │   └── MercatoService.wsdl
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
│   └── src/main/
│       ├── application/META-INF/
│       │   └── application.xml          ← Dichiara i moduli ejb + war
│       └── liberty/config/
│           ├── server.xml               ← Configurazione Open Liberty
│           └── bootstrap.properties     ← Variabili d'ambiente (DB, mail)
│
├── scripts/
│   └── tools/
│       ├── generate_placeholder_images.py  ← Rigenera immagini SVG prodotti
│       └── LoadProducts.java            ← Caricatore JDBC prodotti standalone
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
│  │   paskinomercato  │     │  paskinomercato-ejb.jar     │   │
│  │       .war        │     │                            │   │
│  │                   │     │  CatalogoBean  (Stateless) │   │
│  │  Servlet          │────▶│  OrdineBean    (Stateless) │   │
│  │  JSP/JSTL/EL      │     │  ClienteBean   (Stateless) │   │
│  │  Endpoint JAX-WS  │     │  MailBean      (Stateless) │   │
│  │  File statici     │     └────────────────────────────┘   │
│  │  (img/, css/)     │                                      │
│  │                   │  CarrelloSessionBean (HttpSession)   │
│  └───────────────────┘                  │                   │
│                                         │ JDBC (CMT)        │
└─────────────────────────────────────────┼───────────────────┘
                                          ▼
                                   ┌─────────────┐
                                   │  PostgreSQL  │
                                   │  mercatodb   │
                                   └─────────────┘
```

Tutte le chiamate EJB sono **locali** (stessa JVM, stesso EAR). Il modulo WAR risolve i bean tramite `java:comp/env/ejb/NomeBean`. Le transazioni sono gestite interamente dal container EJB (CMT). Il carrello è un POJO serializzabile (`CarrelloSessionBean`) conservato direttamente in `HttpSession`.

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
| [`CarrelloItem`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/CarrelloItem.java) | — | Elemento carrello (in sessione HTTP) |

---

## EJB Beans

Tutti i bean usano lo stile **EJB 2.0**: implementano `javax.ejb.SessionBean`, espongono un'interfaccia `Local` che estende `EJBLocalObject` e un'interfaccia `LocalHome` che estende `EJBLocalHome`. Configurati tramite `ejb-jar.xml` — nessuna annotazione `@Stateless` / `@EJB`.

Le transazioni sono **Container-Managed (CMT)**. Nessun bean chiama mai `connection.commit()` o `connection.rollback()`. In caso di errore si usa `ctx.setRollbackOnly()`.

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
| `creaOrdine(clienteId, indirizzoId, items, note)` | Crea ordine + decrementa stock (CMT `Required`) |
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

### MailBean — Stateless

**JNDI:** `java:comp/env/ejb/MailBean`  
**Sessione mail:** `java:comp/env/mail/MercatoMail`

Invia email HTML in italiano o inglese:
- `inviaConfermaOrdine(cliente, ordine, righe, lingua)` — email completa con riepilogo ordine
- `inviaRegistrazioneConferma(cliente, lingua)` — email di benvenuto alla registrazione

### Carrello — POJO in sessione

Il carrello non è più un EJB Stateful. [`CarrelloSessionBean`](paskinomercato-war/src/main/java/it/paskinomercato/cart/CarrelloSessionBean.java) è un POJO `Serializable` conservato direttamente in `HttpSession` sotto la chiave `"carrello"`. Metodi: `aggiungi`, `rimuovi`, `aggiornaQuantita`, `svuota`, `getItems`, `getTotale`, `getNumeroArticoli`.

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

---

## Pagine JSP

Tutte le JSP usano **JSTL 1.2** (`c:`, `fmt:`, `fn:`) e **EL 3.0**.

| JSP | Descrizione |
|---|---|
| [`index.jsp`](paskinomercato-war/src/main/webapp/index.jsp) | Home page con hero banner e schede funzionalità |
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

## Web Service JAX-WS

L'applicazione espone un web service **SOAP** per l'integrazione con sistemi esterni (es. partner, ERP).

**Endpoint:** `http://<host>:9080/paskinomercato/MercatoService`  
**WSDL:** `http://<host>:9080/paskinomercato/MercatoService?wsdl`

| Operazione | Input | Output | Descrizione |
|---|---|---|---|
| `getProdottoXml` | `codice: string` | Stringa XML | Dettaglio prodotto per codice |
| `getStatoOrdine` | `numeroOrdine: string` | Stringa XML | Stato e totale dell'ordine |
| `getCategorieXml` | *(nessuno)* | Stringa XML | Lista di tutte le categorie attive |

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

### Flusso di stato ordine

```
IN_ATTESA → CONFERMATO → IN_PREPARAZIONE → SPEDITO → CONSEGNATO
                                                   ↘ ANNULLATO
```

### JNDI DataSource

Tutti gli EJB ottengono la connessione tramite:

```java
InitialContext ic = new InitialContext();
DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/MercatoDB");
```

---

## Prodotti e Immagini

[`paskinomercato-ejb/src/main/resources/db/seed_products.sql`](paskinomercato-ejb/src/main/resources/db/seed_products.sql) contiene **150 prodotti originali** di un supermercato italiano, distribuiti su 10 categorie.

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

150 immagini SVG placeholder si trovano in `paskinomercato-war/src/main/webapp/img/prodotti/`. Le JSP convertono automaticamente il nome immagine dal database (`.jpg`) al file SVG con `fn:replace`:

```jsp
${fn:replace(p.immagine, '.jpg', '.svg')}
```

---

## Supporto Bilingue

La lingua è salvata nell'attributo di sessione `lang` (`"it"` o `"en"`). Il default è italiano.

- **Cambio lingua:** `GET /lingua?lang=it|en`
- **Persistenza:** se autenticato, aggiorna `mercato.cliente.lingua`
- **Pattern JSP:**
  ```jsp
  ${lang eq 'it' ? 'Testo italiano' : 'English text'}
  ```
- **Nomi prodotto:** `Prodotto.getNome(lang)` seleziona `nome_it` o `nome_en` dalla riga DB.
- **Email:** `MailBean` genera il corpo HTML nella lingua del cliente.

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
| Java JDK | **8** o superiore (bytecode Java 8) |
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
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'latuapassword';"
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
      localhost 5432 mercatodb mercato latuapassword \
      ../../paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

---

## Configurazione Liberty

Tutte le variabili d'ambiente sono in [`paskinomercato-ear/src/main/liberty/config/bootstrap.properties`](paskinomercato-ear/src/main/liberty/config/bootstrap.properties). Modificare i valori prima di avviare il server:

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

> **Nota:** aggiungendo `io.openliberty.tools` a `~/.m2/settings.xml` è possibile usare il prefisso breve `liberty:dev` / `liberty:run`:
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
| Endpoint SOAP | `http://localhost:9080/paskinomercato/MercatoService` |
| WSDL | `http://localhost:9080/paskinomercato/MercatoService?wsdl` |

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

Per rigenerare le immagini SVG placeholder:

```bash
python3 scripts/tools/generate_placeholder_images.py
```

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
