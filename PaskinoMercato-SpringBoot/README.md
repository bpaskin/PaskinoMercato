# PaskinoMercato 🛒

**Supermercato Online Italiano — Spring Boot 4.1 / Java 25**

Applicazione Spring Boot full-stack per un supermercato online in italiano e inglese, con consegna limitata all'Italia, tutti i prezzi in Euro e un catalogo di massimo 1.503 prodotti su database PostgreSQL.

---

## Indice

1. [Panoramica](#panoramica)
2. [Stack Tecnologico](#stack-tecnologico)
3. [Struttura del Progetto](#struttura-del-progetto)
4. [Architettura](#architettura)
5. [Modello di Dominio](#modello-di-dominio)
6. [Layer di Servizio](#layer-di-servizio)
7. [Controller](#controller)
8. [Pagine JSP](#pagine-jsp)
9. [Database](#database)
10. [Prodotti e Immagini](#prodotti-e-immagini)
11. [Supporto Bilingue](#supporto-bilingue)
12. [Validazione Indirizzi Italiani](#validazione-indirizzi-italiani)
13. [Prerequisiti](#prerequisiti)
14. [Build](#build)
15. [Configurazione del Database](#configurazione-del-database)
16. [Configurazione dell'Applicazione](#configurazione-dellapplicazione)
17. [Avvio](#avvio)
18. [URL dell'Applicazione](#url-dellapplicazione)
19. [Riferimento Script](#riferimento-script)
20. [Sostituzione delle Immagini Placeholder](#sostituzione-delle-immagini-placeholder)

---

## Panoramica

PaskinoMercato è un'applicazione **Spring Boot 4.1** eseguibile come WAR autonomo su Tomcat 11 embedded. Offre:

- Una vetrina di supermercato online bilingue (🇮🇹 Italiano / 🇬🇧 Inglese)
- Catalogo prodotti con navigazione per categoria, ricerca e paginazione (max **1.503 prodotti**)
- Carrello della spesa (`@SessionScope` Spring bean)
- Flusso di checkout completo con validazione indirizzo italiano
- Gestione ordini con storico per cliente
- Email di conferma ordine via **Spring Mail / Jakarta Mail** (HTML, bilingue)
- Tutti i prezzi esclusivamente in **Euro (€)**
- Consegna **solo a indirizzi italiani** (applicata a livello applicativo e di database)

---

## Stack Tecnologico

| Livello | Tecnologia |
|---|---|
| Framework | **Spring Boot 4.1.0** |
| Versione Java | **Java 25** |
| Server embedded | **Apache Tomcat 11** (via `spring-boot-starter-web`) |
| Persistenza | **Spring JdbcTemplate** — JDBC puro, nessun JPA / Hibernate |
| Database | **PostgreSQL** 15+ |
| Transazioni | `@Transactional` Spring (su `OrdineService.creaOrdine`) |
| Sicurezza | **Spring Security 7** — tutte le route aperte; autenticazione manuale via sessione HTTP |
| Livello Web | **Spring MVC** `@Controller` + **JSP 3** / **JSTL 3** |
| Tag di Vista | **JSTL 3** (`jakarta.tags.*`) + **EL 6** |
| Carrello | `CarrelloSessionBean` — `@SessionScope` Spring bean |
| Email | **Spring Mail** (`JavaMailSender`) + **Jakarta Mail 2** |
| Build | **Maven 3** (progetto WAR single-module) |
| Lingue | Italiano (predefinito) + Inglese |
| Valuta | Solo Euro (€) |

---

## Struttura del Progetto

```
paskinomercato-spring/
│
├── pom.xml                              ← Spring Boot 4.1.0 parent, Java 25, packaging WAR
│
└── src/main/
    ├── java/it/paskinomercato/
    │   ├── PaskinoMercatoApplication.java   ← @SpringBootApplication entry point
    │   ├── config/
    │   │   └── SecurityConfig.java          ← Spring Security: tutto aperto, CSRF off
    │   ├── model/                           ← Value object (POJO serializzabili, nessun JPA)
    │   │   ├── Prodotto.java
    │   │   ├── Categoria.java
    │   │   ├── Cliente.java
    │   │   ├── Indirizzo.java
    │   │   ├── Ordine.java
    │   │   ├── RigaOrdine.java
    │   │   └── CarrelloItem.java
    │   ├── util/
    │   │   └── IndirizzoItaliaValidator.java
    │   ├── cart/
    │   │   └── CarrelloSessionBean.java     ← @SessionScope @Component
    │   ├── service/
    │   │   ├── CatalogoService.java         ← @Service, JdbcTemplate
    │   │   ├── ClienteService.java
    │   │   ├── OrdineService.java           ← @Transactional su creaOrdine
    │   │   └── MailService.java             ← JavaMailSender
    │   └── controller/
    │       ├── HomeController.java          ← GET /
    │       ├── CatalogoController.java      ← GET /catalogo
    │       ├── CarrelloController.java      ← GET+POST /carrello
    │       ├── CheckoutController.java      ← GET+POST /checkout
    │       ├── LoginController.java         ← GET+POST /login
    │       ├── LinguaController.java        ← GET /lingua
    │       ├── OrdineController.java        ← GET /ordini
    │       └── GlobalModelAdvice.java       ← @ControllerAdvice: espone carrello a tutte le view
    ├── resources/
    │   └── application.properties           ← DataSource, mail, sessione, JSP prefix/suffix
    └── webapp/
        ├── css/style.css                    ← Foglio di stile principale
        ├── img/prodotti/                    ← 150 immagini SVG prodotti
        └── WEB-INF/
            └── jsp/
                ├── include/
                │   ├── header.jsp
                │   └── footer.jsp
                ├── index.jsp
                ├── catalogo.jsp
                ├── carrello.jsp
                ├── checkout.jsp
                ├── confermaOrdine.jsp
                ├── login.jsp
                ├── ordini.jsp
                ├── dettaglioOrdine.jsp
                ├── error404.jsp
                └── error500.jsp
```

---

## Architettura

```
Browser
  │
  ▼
Tomcat 11 embedded (porta 8080)
  │
  ▼
┌────────────────────────────────────────────────────────────┐
│  Spring Boot WAR                                           │
│                                                            │
│  ┌──────────────────┐      ┌──────────────────────────┐   │
│  │  @Controller     │      │  @Service                │   │
│  │                  │      │                          │   │
│  │  HomeController  │─────▶│  CatalogoService         │   │
│  │  CatalogoCtrl    │      │  ClienteService           │   │
│  │  CarrelloCtrl    │      │  OrdineService  (@Tx)    │   │
│  │  CheckoutCtrl    │      │  MailService             │   │
│  │  LoginCtrl       │      └──────────┬───────────────┘   │
│  │  OrdineCtrl      │                 │ JdbcTemplate       │
│  │  LinguaCtrl      │                 │                    │
│  └──────────────────┘                 ▼                    │
│                                  HikariCP pool             │
│  CarrelloSessionBean (@SessionScope)  │                    │
│  JSP/JSTL 3 views                    │                    │
└──────────────────────────────────────┼────────────────────┘
                                       ▼
                               ┌─────────────┐
                               │  PostgreSQL  │
                               │  mercatodb   │
                               └─────────────┘
```

Tutte le dipendenze sono iniettate via costruttore da Spring. Il carrello è un bean `@SessionScope` con proxy CGLIB — iniettabile in qualsiasi singleton senza `HttpSession` esplicita. `GlobalModelAdvice` espone il carrello come attributo `"carrello"` a tutte le view JSP.

---

## Modello di Dominio

Tutta la persistenza avviene tramite `JdbcTemplate` — nessuna annotazione JPA. I seguenti value object (POJO serializzabili) corrispondono alle righe del database:

| Classe | Tabella | Descrizione |
|---|---|---|
| [`Prodotto`](paskinomercato-spring/src/main/java/it/paskinomercato/model/Prodotto.java) | `mercato.prodotto` | Prodotto (nome/descrizione bilingue, prezzo, stock) |
| [`Categoria`](paskinomercato-spring/src/main/java/it/paskinomercato/model/Categoria.java) | `mercato.categoria` | Categoria prodotti (bilingue) |
| [`Cliente`](paskinomercato-spring/src/main/java/it/paskinomercato/model/Cliente.java) | `mercato.cliente` | Account cliente |
| [`Indirizzo`](paskinomercato-spring/src/main/java/it/paskinomercato/model/Indirizzo.java) | `mercato.indirizzo` | Indirizzo di consegna italiano |
| [`Ordine`](paskinomercato-spring/src/main/java/it/paskinomercato/model/Ordine.java) | `mercato.ordine` | Testata ordine |
| [`RigaOrdine`](paskinomercato-spring/src/main/java/it/paskinomercato/model/RigaOrdine.java) | `mercato.riga_ordine` | Riga ordine (prodotto + qtà + prezzo) |
| [`CarrelloItem`](paskinomercato-spring/src/main/java/it/paskinomercato/model/CarrelloItem.java) | — | Elemento carrello (in sessione HTTP) |

---

## Layer di Servizio

### CatalogoService

| Metodo | Descrizione |
|---|---|
| `getProdotti(pagina, dim)` | Lista prodotti paginata |
| `getProdottiPerCategoria(catId, pagina, dim)` | Prodotti filtrati per categoria |
| `getProdottoById(id)` | Ricerca singolo prodotto per ID |
| `cercaProdotti(testo)` | Ricerca testuale (nomi IT + EN + codice) |
| `getCategorie()` | Tutte le categorie |
| `contaProdotti()` | Conteggio totale prodotti attivi |
| `isDisponibile(prodottoId, qty)` | Verifica disponibilità a magazzino |

### OrdineService

| Metodo | Descrizione |
|---|---|
| `creaOrdine(clienteId, indirizzoId, items, note)` | Crea ordine + decrementa stock (`@Transactional`) |
| `getOrdineByNumero(numero)` | Ordine per numero |
| `getOrdiniCliente(clienteId)` | Storico ordini del cliente |
| `getRigheOrdine(ordineId)` | Righe dell'ordine |
| `aggiornaStato(ordineId, stato)` | Aggiorna stato ordine |

### ClienteService

| Metodo | Descrizione |
|---|---|
| `registra(...)` | Registrazione nuovo cliente |
| `login(email, pwHash)` | Autenticazione con password SHA-256 |
| `getClienteById(id)` | Ricerca cliente per ID |
| `aggiungiIndirizzo(...)` | Aggiunta indirizzo di consegna italiano |
| `getIndirizzi(clienteId)` | Rubrica indirizzi del cliente |
| `aggiornaLingua(clienteId, lang)` | Salvataggio preferenza lingua |

### MailService

Invia email HTML in italiano o inglese tramite `JavaMailSender`:
- `inviaConfermaOrdine(cliente, ordine, righe, lingua)` — email completa con riepilogo ordine
- `inviaRegistrazioneConferma(cliente, lingua)` — email di benvenuto alla registrazione

### CarrelloSessionBean

Bean `@SessionScope` con proxy CGLIB. Metodi: `aggiungi`, `rimuovi`, `aggiornaQuantita`, `svuota`, `getItems`, `getTotale`, `getNumeroArticoli`.

---

## Controller

| Controller | URL | Descrizione |
|---|---|---|
| [`HomeController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/HomeController.java) | `GET /` | Home page |
| [`CatalogoController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/CatalogoController.java) | `GET /catalogo` | Griglia prodotti con paginazione, filtro categoria, ricerca |
| [`CarrelloController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/CarrelloController.java) | `GET+POST /carrello` | Vista carrello e azioni: `aggiungi`, `rimuovi`, `aggiorna`, `svuota` |
| [`CheckoutController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/CheckoutController.java) | `GET+POST /checkout` | Selezione indirizzo + inserimento ordine + invio email |
| [`LoginController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/LoginController.java) | `GET+POST /login` | Login + registrazione + logout |
| [`LinguaController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/LinguaController.java) | `GET /lingua?lang=it\|en` | Cambio lingua, salvato in sessione e nel DB |
| [`OrdineController`](paskinomercato-spring/src/main/java/it/paskinomercato/controller/OrdineController.java) | `GET /ordini` | Lista storico ordini e dettaglio singolo ordine |

---

## Pagine JSP

Tutte le JSP usano **JSTL 3** (`jakarta.tags.core`, `jakarta.tags.fmt`, `jakarta.tags.functions`) e **EL 6**.

| JSP | Descrizione |
|---|---|
| [`index.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/index.jsp) | Home page con hero banner e schede funzionalità |
| [`catalogo.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/catalogo.jsp) | Griglia prodotti, sidebar categorie, paginazione |
| [`carrello.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/carrello.jsp) | Tabella carrello con aggiornamento quantità e rimozione |
| [`checkout.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/checkout.jsp) | Form indirizzo + sidebar riepilogo ordine |
| [`confermaOrdine.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/confermaOrdine.jsp) | Conferma ordine con righe dettaglio |
| [`login.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/login.jsp) | Form login + registrazione a schede |
| [`ordini.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/ordini.jsp) | Storico ordini del cliente |
| [`dettaglioOrdine.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/dettaglioOrdine.jsp) | Dettaglio righe di un singolo ordine |
| [`error404.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/error404.jsp) | Pagina errore 404 |
| [`error500.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/error500.jsp) | Pagina errore 500 |
| [`include/header.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/include/header.jsp) | Barra di navigazione: ricerca, cambio lingua, badge carrello, login/logout |
| [`include/footer.jsp`](paskinomercato-spring/src/main/webapp/WEB-INF/jsp/include/footer.jsp) | Footer: avviso consegna, informazioni pagamento |

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

### DataSource

Configurato tramite HikariCP e `application.properties`. Iniettato automaticamente da Spring Boot come `JdbcTemplate`.

---

## Prodotti e Immagini

`paskinomercato-spring/src/main/resources/db/seed_products.sql` contiene **150 prodotti originali** di un supermercato italiano, distribuiti su 10 categorie.

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

150 immagini SVG placeholder si trovano in `paskinomercato-spring/src/main/webapp/img/prodotti/`. Le JSP convertono automaticamente il nome immagine dal database (`.jpg`) al file SVG con `fn:replace`:

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
- **Email:** `MailService` genera il corpo HTML nella lingua del cliente.

---

## Validazione Indirizzi Italiani

[`IndirizzoItaliaValidator`](paskinomercato-spring/src/main/java/it/paskinomercato/util/IndirizzoItaliaValidator.java) applica tre regole:

| Regola | Validazione |
|---|---|
| Paese | Deve essere `"IT"` |
| CAP | Regex `^\d{5}$` |
| Provincia | Una delle 110 abbreviazioni ufficiali italiane (AG … VV) |

---

## Prerequisiti

| Strumento | Versione |
|---|---|
| Java JDK | **25** (IBM Semeru o OpenJDK) |
| Maven | 3.9+ |
| PostgreSQL | 15+ |
| Python | 3.x (solo per la generazione immagini SVG) |

---

## Build

```bash
cd paskinomercato-spring

# Build completa — genera il WAR
mvn clean package -DskipTests

# Artefatto deployabile
ls target/paskinomercato-spring-1.0.0.war
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
     -f paskinomercato-spring/src/main/resources/db/schema.sql

# 3. Caricare i 150 prodotti di esempio
psql -U mercato -d mercatodb \
     -f paskinomercato-spring/src/main/resources/db/seed_products.sql
```

---

## Configurazione dell'Applicazione

Tutte le impostazioni si trovano in [`paskinomercato-spring/src/main/resources/application.properties`](paskinomercato-spring/src/main/resources/application.properties):

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/mercatodb
spring.datasource.username=mercato
spring.datasource.password=changeme

# Mail SMTP
spring.mail.host=smtp.tuodominio.it
spring.mail.port=587
spring.mail.username=noreply@paskinomercato.it
spring.mail.password=tuapassword
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Porta server
server.port=8080
```

---

## Avvio

### Dev mode (hot reload)

```bash
cd paskinomercato-spring
mvn spring-boot:run
```

### Avvio dal WAR compilato

```bash
cd paskinomercato-spring
mvn clean package -DskipTests
java -jar target/paskinomercato-spring-1.0.0.war
```

---

## URL dell'Applicazione

| Risorsa | URL |
|---|---|
| Home page | `http://localhost:8080/` |
| Catalogo prodotti | `http://localhost:8080/catalogo` |
| Carrello | `http://localhost:8080/carrello` |
| Checkout | `http://localhost:8080/checkout` |
| Login / Registrazione | `http://localhost:8080/login` |
| I miei ordini | `http://localhost:8080/ordini` |
| Cambio lingua | `http://localhost:8080/lingua?lang=en` |

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
   paskinomercato-spring/src/main/webapp/img/prodotti/
   ```
2. Ricompilare il WAR — nessuna modifica al codice necessaria.

Per rigenerare le immagini SVG placeholder:

```bash
python3 scripts/tools/generate_placeholder_images.py
```

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
