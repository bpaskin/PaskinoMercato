# PaskinoMercato 🛒

**Supermercato Online Italiano — Jakarta EE / EJB 3.2 / Open Liberty 26**

Applicazione Java enterprise full-stack per un supermercato online in italiano e inglese, con consegna limitata all'Italia, tutti i prezzi in Euro e un catalogo di massimo 1.503 prodotti su database PostgreSQL.

> **Nota:** questo branch è la versione modernizzata su **Open Liberty**. La versione originale WebSphere Application Server 8.5.5 si trova nel branch `websphere-original`.

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
9. [Database](#database)
10. [Prodotti e Immagini](#prodotti-e-immagini)
11. [Supporto Bilingue](#supporto-bilingue)
12. [Validazione Indirizzi Italiani](#validazione-indirizzi-italiani)
13. [Prerequisiti](#prerequisiti)
14. [Build](#build)
15. [Configurazione del Database](#configurazione-del-database)
16. [Configurazione Open Liberty](#configurazione-open-liberty)
17. [Avvio e Deploy Locale](#avvio-e-deploy-locale)
18. [URL dell'Applicazione](#url-dellapplicazione)
19. [Riferimento Script](#riferimento-script)
20. [Sostituzione delle Immagini Placeholder](#sostituzione-delle-immagini-placeholder)

---

## Panoramica

PaskinoMercato è un'applicazione Java enterprise distribuita come EAR su **IBM Open Liberty 26**. Offre:

- Una vetrina di supermercato online bilingue (🇮🇹 Italiano / 🇬🇧 Inglese)
- Catalogo prodotti con navigazione per categoria, ricerca e paginazione (max **1.503 prodotti**)
- Carrello della spesa (EJB Stateful, lato server per sessione)
- Flusso di checkout completo con validazione indirizzo italiano
- Gestione ordini con storico per cliente
- Email di conferma ordine via **Jakarta Mail** (HTML, bilingue)
- Tutti i prezzi esclusivamente in **Euro (€)**
- Consegna **solo a indirizzi italiani** (applicata a livello applicativo e di database)

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Application Server | **IBM Open Liberty 26.0.0.9** |
| Java | **Java 25** (compilato con `<release>25</release>`) |
| EJB | **EJB Lite 4.0** — `@Stateless`, `@Stateful`, `@Local` (Jakarta EE 10) |
| Transazioni EJB | **Container-Managed Transactions** (CMT) |
| Persistenza | **JDBC** diretto tramite JNDI `DataSource` (nessun JPA) |
| Database | **PostgreSQL** 15+ |
| Livello Web | **Servlet 6.0** + **Jakarta Pages 3.1** |
| Tag di Vista | **JSTL 1.2** + **EL** |
| Email | **Jakarta Mail 2.1** tramite JNDI `mail/MercatoMail` |
| Build | **Maven 3.9+** (progetto EAR multi-modulo) |
| Lingue | Italiano (predefinito) + Inglese |
| Valuta | Solo Euro (€) |

---

## Struttura del Progetto

```
paskinomercato/
│
├── pom.xml                              ← POM padre (3 moduli)
│
├── paskinomercato-ejb/                  ← Modulo EJB
│   └── src/main/
│       ├── java/it/paskinomercato/
│       │   ├── ejb/
│       │   │   ├── carrello/            ← CarrelloBean (Stateful)
│       │   │   ├── catalogo/            ← CatalogoBean (Stateless)
│       │   │   ├── cliente/             ← ClienteBean  (Stateless)
│       │   │   ├── entity/              ← Stateless Session Bean per persistenza JDBC
│       │   │   │   ├── prodotto/        ← ProdottoEntityHomeBean + ProdottoEntityService/Data
│       │   │   │   ├── categoria/       ← CategoriaEntityHomeBean + CategoriaEntityService/Data
│       │   │   │   ├── cliente/         ← ClienteEntityHomeBean + ClienteEntityService/Data
│       │   │   │   └── ordine/          ← OrdineEntityHomeBean + OrdineEntityService/Data
│       │   │   ├── mail/                ← MailBean      (Stateless)
│       │   │   └── ordine/              ← OrdineBean   (Stateless)
│       │   ├── model/                   ← Value object (POJO, nessun JPA)
│       │   ├── util/                    ← IndirizzoItaliaValidator
│       │   └── ws/                      ← SEI JAX-WS, implementazione e wrapper bean
│       └── resources/
│           ├── META-INF/
│           │   ├── ejb-jar.xml          ← Descrittore di deploy EJB
│           │   └── ibm-ejb-jar-bnd.xmi  ← Binding JNDI EJB Liberty
│           └── db/
│               ├── schema.sql           ← DDL PostgreSQL
│               └── seed_products.sql    ← 150 prodotti di esempio
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
│               ├── web.xml              ← Descrittore Servlet
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
│           └── server.xml               ← Configurazione Open Liberty
│
├── scripts/
│   └── tools/
│       ├── generate_placeholder_images.py  ← Rigenera immagini SVG prodotti
│       └── LoadProducts.java            ← Caricatore JDBC prodotti standalone
│
├── LIBERTY_SETUP.md                     ← Guida alla configurazione Open Liberty
└── README.md
```

---

## Architettura

```
Browser
  │
  ▼
Open Liberty HTTP (porta 9084)
  │
  ▼
┌─────────────────────────────────────────────────────────────┐
│  paskinomercato-ear.ear                                      │
│                                                             │
│  ┌───────────────────┐     ┌────────────────────────────┐   │
│  │   paskinomercato  │     │  paskinomercato-ejb.jar     │   │
│  │       .war        │     │                            │   │
│  │                   │     │  Session EJB (@Stateless):  │   │
│  │  Servlet          │────▶│   Catalogo, Ordine, Cliente │   │
│  │  JSP/JSTL/EL      │     │   Carrello (@Stateful)      │   │
│  │  File statici     │     │   Mail                      │   │
│  │  (img/, css/)     │     │             │              │   │
│  │                   │     │             ▼              │   │
│  └───────────────────┘     │  Entity @Stateless (JDBC): │   │
│                            │   Prodotto, Categoria,     │   │
│                            │   Cliente, Ordine          │   │
│                            └────────────────────────────┘   │
│                                         │ JDBC (CMT)        │
└─────────────────────────────────────────┼───────────────────┘
                                          ▼
                                   ┌─────────────┐
                                   │  PostgreSQL  │
                                   │  mercatodb   │
                                   └─────────────┘
```

Tutte le chiamate EJB sono **locali** (stessa JVM, stesso EAR). Nessun EJB remoto. Il modulo WAR risolve i Session Bean tramite `java:comp/env/ejb/NomeBean`; i Session Bean accedono alle business interface degli entity bean tramite i rispettivi riferimenti `java:comp/env/ejb/*EntityBean`. Le transazioni sono gestite interamente dal container EJB (CMT).

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
| [`CarrelloItem`](paskinomercato-ejb/src/main/java/it/paskinomercato/model/CarrelloItem.java) | `mercato.carrello` | Elemento carrello |

---

## EJB Beans

I bean entity originali (EJB 2.x BMP) sono stati modernizzati come **Stateless Session Bean** con business interface `@Local` e persistenza JDBC diretta. I Session Bean orchestratori rimangono invariati.

### Entity Bean (modernizzati come @Stateless)

| Bean | Interfaccia di servizio | Interfaccia dati |
|---|---|---|
| `ProdottoEntityBean` | `ProdottoEntityService` | `ProdottoEntityData` |
| `CategoriaEntityBean` | `CategoriaEntityService` | `CategoriaEntityData` |
| `ClienteEntityBean` | `ClienteEntityService` | `ClienteEntityData` |
| `OrdineEntityBean` | `OrdineEntityService` | `OrdineEntityData` |

Ogni `*EntityHomeBean` implementa la rispettiva interfaccia `*EntityService` ed è annotato con `@Stateless(name="…")`. Le operazioni JDBC (create, find*, update) sono esposte direttamente come metodi business.

### CatalogoBean — Stateless

Espone il catalogo prodotti e le categorie. Accede a `ProdottoEntityBean` e `CategoriaEntityBean` tramite JNDI `java:comp/env/ejb/ProdottoEntityBean` e `java:comp/env/ejb/CategoriaEntityBean`.

### OrdineBean — Stateless

Gestisce la creazione, il recupero e l'aggiornamento di stato degli ordini. Accede a `OrdineEntityBean` e invia email di conferma tramite `MailBean`.

### ClienteBean — Stateless

Gestisce registrazione, login (password hash BCrypt-compatibile) e profilo cliente.

### CarrelloBean — Stateful

Mantiene il carrello in sessione EJB lato server. Un'istanza per sessione utente.

### MailBean — Stateless

Invia email di conferma ordine HTML+testo via `mail/MercatoMail`.

---

## Servlet

| Servlet | URL Pattern | Funzione |
|---|---|---|
| `CatalogoServlet` | `/catalogo` | Elenco prodotti con paginazione e filtro per categoria |
| `CarrelloServlet` | `/carrello` | Visualizza/modifica carrello |
| `CheckoutServlet` | `/checkout` | Raccoglie indirizzo di consegna e crea l'ordine |
| `LoginServlet` | `/login` | Login e registrazione cliente |
| `OrdineServlet` | `/ordini` | Storico ordini del cliente autenticato |
| `LinguaServlet` | `/lingua` | Commuta la lingua (IT/EN) tramite parametro `lang` |

---

## Pagine JSP

Tutte le JSP si trovano in `paskinomercato-war/src/main/webapp/WEB-INF/jsp/` e usano JSTL + EL. La lingua viene letta dall'attributo di sessione `lang` (default: `it`).

---

## Database

### Tabelle

| Tabella | Descrizione |
|---|---|
| `mercato.prodotto` | Catalogo prodotti |
| `mercato.categoria` | Categorie prodotto |
| `mercato.cliente` | Account cliente |
| `mercato.indirizzo` | Indirizzi di consegna italiani |
| `mercato.ordine` | Testata ordini |
| `mercato.riga_ordine` | Righe ordine |
| `mercato.carrello` | Carrello persistente |

### Flusso di stato ordine

`IN_ATTESA` → `CONFERMATO` → `SPEDITO` → `CONSEGNATO` (oppure `ANNULLATO`)

### JNDI DataSource

| Nome JNDI | Tipo | Riferimento |
|---|---|---|
| `jdbc/MercatoDB` | `javax.sql.ConnectionPoolDataSource` | PostgreSQL |
| `mail/MercatoMail` | `jakarta.mail.Session` | SMTP |

---

## Prodotti e Immagini

### Dati di esempio

Il file [`seed_products.sql`](paskinomercato-ejb/src/main/resources/db/seed_products.sql) carica 150 prodotti distribuiti in categorie (frutta, verdura, latticini, carne, ecc.).

### Immagini prodotti

Le immagini placeholder SVG si trovano in `paskinomercato-war/src/main/webapp/img/prodotti/`. Per rigenerarle:

```bash
python3 scripts/tools/generate_placeholder_images.py
```

---

## Supporto Bilingue

Tutte le JSP leggono l'attributo di sessione `lang` (`it` o `en`) e mostrano il campo `nome_it`/`nome_en` e `descrizione_it`/`descrizione_en` corrispondente. Il cambio lingua avviene tramite `GET /lingua?lang=en`.

---

## Validazione Indirizzi Italiani

La classe [`IndirizzoItaliaValidator`](paskinomercato-ejb/src/main/java/it/paskinomercato/util/IndirizzoItaliaValidator.java) verifica:

- Paese = `IT`
- CAP: 5 cifre numeriche
- Provincia: codice a 2 lettere valido (107 province italiane)

---

## Prerequisiti

| Strumento | Versione |
|---|---|
| Java JDK | **25** (IBM Semeru o Eclipse Temurin) |
| Maven | **3.9+** |
| PostgreSQL | **15+** |
| Open Liberty | Scaricato automaticamente dal plugin Maven (`26.0.0.9`) |

> Open Liberty viene scaricato e installato automaticamente in `paskinomercato-ear/target/liberty/` al primo `mvn liberty:run`. Non è necessaria alcuna installazione manuale del server.

---

## Build

```bash
# Build completa di tutti i moduli (skippa i test)
mvn clean install -DskipTests

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

---

## Configurazione Open Liberty

Tutta la configurazione del server è in [`paskinomercato-ear/src/main/liberty/config/server.xml`](paskinomercato-ear/src/main/liberty/config/server.xml). I valori personalizzabili sono esposti come `<variable>` con `defaultValue`:

| Variabile | Default | Descrizione |
|---|---|---|
| `httpEndpoint_port_1` | `9084` | Porta HTTP |
| `httpEndpoint_secure_port_1` | `9447` | Porta HTTPS |
| `httpEndpoint_host_1` | `localhost` | Interfaccia di ascolto |
| `PaskinoMercato_DB_…_serverName` | `localhost` | Host PostgreSQL |
| `PaskinoMercato_DB_…_portNumber` | `5432` | Porta PostgreSQL |
| `PaskinoMercato_DB_…_databaseName` | `mercatodb` | Nome database |
| `MercatoDBAlias_user` | `mercato` | Utente database |
| `MercatoDBAlias_password` | `changeme` | Password database |

Per sovrascrivere i valori senza modificare `server.xml`, usare variabili d'ambiente o il file `paskinomercato-ear/target/liberty/wlp/usr/servers/paskinomercato/server.env`.

Il driver PostgreSQL è caricato dal repository Maven locale:
```
${user.home}/.m2/repository/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar
```

---

## Avvio e Deploy Locale

```bash
# Build completa + avvio di Liberty con hot-deploy
mvn -f paskinomercato-ear/pom.xml liberty:run

# Oppure, in due passi separati:
mvn clean install -DskipTests
mvn -f paskinomercato-ear/pom.xml liberty:start

# Stop
mvn -f paskinomercato-ear/pom.xml liberty:stop
```

L'applicazione sarà disponibile su `http://localhost:9084/paskinomercato/` non appena il log mostra:

```
CWWKZ0001I: Application paskinomercato-ear started in X seconds.
```

---

## URL dell'Applicazione

| Risorsa | URL |
|---|---|
| Home page | `http://localhost:9084/paskinomercato/` |
| Catalogo prodotti | `http://localhost:9084/paskinomercato/catalogo` |
| Carrello | `http://localhost:9084/paskinomercato/carrello` |
| Checkout | `http://localhost:9084/paskinomercato/checkout` |
| Login / Registrazione | `http://localhost:9084/paskinomercato/login` |
| I miei ordini | `http://localhost:9084/paskinomercato/ordini` |
| Cambio lingua | `http://localhost:9084/paskinomercato/lingua?lang=en` |
| Metriche MicroProfile | `http://localhost:9084/metrics/` |

---

## Riferimento Script

| Script | Linguaggio | Scopo |
|---|---|---|
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
