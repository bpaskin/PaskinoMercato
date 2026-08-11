# PaskinoMercato 🛒

**Supermercato Online Italiano** — Applicazione di riferimento per un supermercato online italiano, implementata in tre varianti distinte della piattaforma Java che illustrano l'evoluzione della tecnologia enterprise attraverso le generazioni.

La stessa logica di business — vetrina bilingue (🇮🇹 / 🇬🇧), consegna solo in Italia, prezzi in Euro, catalogo di massimo **1.503 prodotti**, backend PostgreSQL — è fornita in tre implementazioni autonome: JDBC + EJB 2.1 su WebSphere, JPA 2.1 su Open Liberty e `JdbcTemplate` su Spring Boot.

---

## Indice

1. [Le Tre Varianti](#le-tre-varianti)
2. [Funzionalità dell'Applicazione](#funzionalità-dellapplicazione)
3. [Struttura del Repository](#struttura-del-repository)
4. [Architettura Generale](#architettura-generale)
5. [Modello di Dominio Condiviso](#modello-di-dominio-condiviso)
6. [Database Condiviso](#database-condiviso)
7. [Confronto Tecnologico](#confronto-tecnologico)
8. [Percorso di Modernizzazione](#percorso-di-modernizzazione)
9. [Prerequisiti](#prerequisiti)
10. [Avvio Rapido](#avvio-rapido)
11. [Documentazione Dettagliata](#documentazione-dettagliata)

---

## Le Tre Varianti

| Directory | Piattaforma | Java | Persistenza | Web Service | Porta |
|---|---|---|---|---|---|
| [`PaskinoMercato-WebSphere/`](PaskinoMercato-WebSphere/) | IBM WebSphere Application Server 8.5.5 | Java 8 | JDBC diretto | JAX-RPC 1.1 (SOAP) | 9080 |
| [`PaskinoMercato-Liberty/`](PaskinoMercato-Liberty/) | Open Liberty 26 | Java 11 | JPA 2.1 (EclipseLink) | — | 9080 |
| [`PaskinoMercato-SpringBoot/`](PaskinoMercato-SpringBoot/) | Spring Boot 4.1 / Tomcat 11 embedded | Java 25 | JdbcTemplate | — | 8080 |

Ogni directory è un progetto Maven autonomo con il proprio `README.md`, istruzioni di build e guida al deploy.

---

## Funzionalità dell'Applicazione

Tutte e tre le varianti implementano le stesse funzionalità di business:

| Funzionalità | Dettaglio |
|---|---|
| 🌍 Vetrina bilingue | Italiano (predefinito) e inglese; cambio lingua persistito in sessione e nel DB |
| 📦 Catalogo prodotti | Navigazione per categoria, ricerca testuale, paginazione — max **1.503 prodotti** |
| 🛒 Carrello della spesa | Aggiunta, rimozione, aggiornamento quantità, svuotamento |
| 💳 Checkout completo | Selezione indirizzo, validazione italiana (CAP + provincia + paese = `IT`), inserimento ordine |
| 📋 Storico ordini | Lista ordini e dettaglio righe per cliente autenticato |
| 📧 Email di conferma | HTML bilingue via JavaMail / Spring Mail (ordine + benvenuto) |
| 🇮🇹 Solo Italia | Consegna limitata agli indirizzi italiani — applicata a livello applicativo e di database |
| 💶 Solo Euro | Tutti i prezzi esclusivamente in **€** |

---

## Struttura del Repository

```
PaskinoMercato/
│
├── README.md                            ← Questo file
│
├── PaskinoMercato-WebSphere/            ← JavaEE 5 / EJB 2.1 / WAS 8.5.5
│   ├── paskinomercato-ejb/              ← Session Bean EJB 2.1 + Entity Bean BMP
│   ├── paskinomercato-war/              ← Servlet 2.5 + JSP 2.1 + JAX-RPC
│   ├── paskinomercato-ear/              ← Packaging EAR
│   ├── scripts/wsadmin/                 ← Installer wsadmin automatizzato (Jython)
│   ├── WEBSPHERE_SETUP.md               ← Guida alla configurazione risorse WAS
│   └── README.md
│
├── PaskinoMercato-Liberty/              ← CDI + JPA 2.1 / Open Liberty 26 / Java 11
│   ├── paskinomercato-ejb/              ← Servizi CDI @ApplicationScoped + entità JPA
│   ├── paskinomercato-war/              ← Servlet 3.1 + JSP 2.3
│   ├── paskinomercato-ear/              ← Packaging EAR + server.xml Liberty
│   ├── scripts/tools/                   ← Generatore immagini + caricatore prodotti JDBC
│   └── README.md
│
└── PaskinoMercato-SpringBoot/           ← Spring Boot 4.1 / Java 25
    ├── paskinomercato-spring/
    │   ├── src/main/java/               ← @Service + @Controller + @SessionScope
    │   ├── src/main/webapp/             ← Viste JSP 3 / JSTL 3
    │   └── pom.xml
    └── README.md
```

---

## Architettura Generale

Tutte e tre le varianti seguono la stessa architettura a tre livelli — la differenza è nella tecnologia usata a ciascuno strato:

```
Browser
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Web (HTTP)                                                     │
│                                                                         │
│  WebSphere: Servlet 2.5 + JSP 2.1 + JSTL 1.2                          │
│  Liberty:   Servlet 3.1 + JSP 2.3 + JSTL 1.2 + EL 3.0                │
│  Spring:    Spring MVC @Controller + JSP 3 + JSTL 3 + EL 6            │
└─────────────────────────────────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Servizi (Business Logic)                                       │
│                                                                         │
│  WebSphere: EJB 2.1 Session Bean Stateless + Entity Bean BMP           │
│  Liberty:   CDI @ApplicationScoped + JTA @Transactional                │
│  Spring:    @Service + Spring @Transactional                           │
└─────────────────────────────────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Persistenza                                                    │
│                                                                         │
│  WebSphere: JDBC diretto via JNDI DataSource                           │
│  Liberty:   JPA 2.1 (EclipseLink) — EntityManager + JPQL              │
│  Spring:    JdbcTemplate via HikariCP                                  │
└─────────────────────────────────────────────────────────────────────────┘
  │
  ▼
PostgreSQL 15+ — schema "mercato"
```

### Gestione del Carrello

| Variante | Approccio |
|---|---|
| WebSphere | EJB Stateful (`CarrelloBean`) + tabella `mercato.carrello` |
| Liberty | POJO `Serializable` in `HttpSession` (nessun EJB Stateful) |
| Spring Boot | `CarrelloSessionBean` annotato `@SessionScope` (proxy CGLIB) |

---

## Modello di Dominio Condiviso

Le tre varianti operano sulle stesse entità di business, mappate sullo stesso schema PostgreSQL:

| Classe | Tabella | Descrizione |
|---|---|---|
| `Prodotto` | `mercato.prodotto` | Prodotto (nome/descrizione bilingue, prezzo, stock) |
| `Categoria` | `mercato.categoria` | 10 categorie bilingue |
| `Cliente` | `mercato.cliente` | Account cliente con password SHA-256 |
| `Indirizzo` | `mercato.indirizzo` | Indirizzo di consegna italiano (`paese = 'IT'`) |
| `Ordine` | `mercato.ordine` | Testata ordine con stato e totale |
| `RigaOrdine` | `mercato.riga_ordine` | Riga ordine — `subtotale` è una colonna generata dal DB |

**Flusso di stato ordine** (comune a tutte le varianti):

```
IN_ATTESA → CONFERMATO → IN_PREPARAZIONE → SPEDITO → CONSEGNATO
                                                     ↘ ANNULLATO
```

---

## Database Condiviso

Tutte e tre le varianti condividono lo stesso schema PostgreSQL `mercato`. Il DDL completo e i dati di esempio si trovano in `paskinomercato-ejb/src/main/resources/db/` di ciascuna variante (file identici).

### Setup iniziale (eseguire una sola volta)

```bash
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'changeme';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"

# Applicare schema e dati di esempio (percorso dalla variante Liberty; identico nelle altre)
psql -U mercato -d mercatodb \
     -f PaskinoMercato-Liberty/paskinomercato-ejb/src/main/resources/db/schema.sql
psql -U mercato -d mercatodb \
     -f PaskinoMercato-Liberty/paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

### Tabelle

| Tabella | Righe di esempio | Note |
|---|---|---|
| `mercato.categoria` | 10 | Bilingue IT/EN |
| `mercato.prodotto` | 150 seed (max 1.503) | Trigger `CHECK` blocca oltre 1.503 righe |
| `mercato.cliente` | — | Password SHA-256 |
| `mercato.indirizzo` | — | `paese` vincolato a `'IT'` |
| `mercato.ordine` | — | Stato ENUM |
| `mercato.riga_ordine` | — | `subtotale` colonna `GENERATED ALWAYS AS STORED` |
| `mercato.carrello` | — | Solo WebSphere e Liberty (rimossa in Spring Boot) |

> **Nota:** La variante WebSphere utilizza `mercato.carrello` per il carrello EJB Stateful. La variante Liberty mantiene la tabella (svuotata alla creazione dell'ordine) ma non usa EJB Stateful. La variante Spring Boot non usa questa tabella.

---

## Confronto Tecnologico

| Aspetto | WebSphere 8.5.5 | Open Liberty 26 | Spring Boot 4.1 |
|---|---|---|---|
| **Java** | 8 | 11 | 25 |
| **Standard** | JavaEE 5 | Jakarta EE (CDI 1.2 + JPA 2.1) | Spring Framework 7 |
| **Servizi** | EJB 2.1 — solo descriptor XML | CDI `@ApplicationScoped` + JTA | Spring `@Service` + `@Transactional` |
| **Persistenza** | JDBC diretto via JNDI `DataSource` | **JPA 2.1** (EclipseLink) — `EntityManager` | `JdbcTemplate` via HikariCP |
| **Carrello** | EJB Stateful + tabella DB | POJO in `HttpSession` | `@SessionScope` Spring bean |
| **Transazioni** | CMT (Container-Managed) | JTA `@Transactional` | Spring `@Transactional` |
| **Web layer** | JSP 2.1 + JSTL 1.2 + EL 2.2 | JSP 2.3 + JSTL 1.2 + EL 3.0 | JSP 3 + JSTL 3 + EL 6 |
| **Web service** | JAX-RPC 1.1 (document/literal) | — | — |
| **Deploy** | EAR — script wsadmin Jython | EAR — Liberty Maven plugin | WAR eseguibile `java -jar` |
| **Configurazione** | Admin Console / wsadmin | `server.xml` + `bootstrap.properties` | `application.properties` |
| **Email** | JavaMail via JNDI | Jakarta Mail via JNDI | Spring Mail (`JavaMailSender`) |

---

## Percorso di Modernizzazione

Questo repository documenta un percorso realistico di modernizzazione enterprise Java in due passi:

```
WebSphere 8.5.5                Open Liberty 26              Spring Boot 4.1
(JavaEE 5 / Java 8)    ──▶    (CDI + JPA / Java 11)  ──▶  (Spring / Java 25)

EJB 2.1 + JDBC              CDI @ApplicationScoped         @Service
Entity Bean BMP             JPA 2.1 EclipseLink            JdbcTemplate
JAX-RPC 1.1 SOAP            (SOAP rimosso)                 (SOAP rimosso)
Descriptor XML              beans.xml minimale             Annotazioni pure
Stateful EJB cart           POJO in HttpSession            @SessionScope bean
```

### Cambiamenti chiave WebSphere → Liberty

| Da | A |
|---|---|
| `ejb-jar.xml` + interfacce `Local`/`LocalHome` | Bean CDI `@ApplicationScoped` annotati |
| Entity Bean BMP (`ejbCreate`, `ejbLoad`, `ejbStore`) | Entità JPA con `@Entity`, `@Table`, `@Column` |
| JDBC diretto (`ResultSet`, `PreparedStatement`) | `EntityManager` + JPQL / query native |
| EJB Stateful `CarrelloBean` + tabella DB | POJO `Serializable` in `HttpSession` |
| JAX-RPC 1.1 web service | Rimosso completamente |
| `javax.ejb.SessionBean` + CMT | `@Transactional` JTA esplicito |

### Cambiamenti chiave Liberty → Spring Boot

| Da | A |
|---|---|
| CDI `@ApplicationScoped` | Spring `@Service` singleton |
| `EntityManager` + JPQL | `JdbcTemplate` (nessun ORM) |
| JTA `@Transactional` (Jakarta) | Spring `@Transactional` |
| Packaging EAR + Liberty server | WAR eseguibile `java -jar` |
| `server.xml` + `bootstrap.properties` | `application.properties` |
| POJO in `HttpSession` | `@SessionScope` Spring bean |

---

## Prerequisiti

| Strumento | WebSphere | Liberty | Spring Boot |
|---|---|---|---|
| Java JDK | 8 | **11** | 25 |
| Maven | 3.6+ | 3.6+ | 3.9+ |
| PostgreSQL | 15+ | 15+ | 15+ |
| Application server | IBM WAS 8.5.5 (installazione locale) | Scaricato automaticamente dal plugin Maven | Tomcat 11 embedded (nel JAR) |
| Python 3 | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG |

---

## Avvio Rapido

### 1. Setup Database (una sola volta)

```bash
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'changeme';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"
psql -U mercato -d mercatodb \
     -f PaskinoMercato-Liberty/paskinomercato-ejb/src/main/resources/db/schema.sql
psql -U mercato -d mercatodb \
     -f PaskinoMercato-Liberty/paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

### 2. Open Liberty (variante raccomandata per modernizzazione)

```bash
cd PaskinoMercato-Liberty
mvn clean package
mvn -pl paskinomercato-ear io.openliberty.tools:liberty-maven-plugin:run
# App: http://localhost:9080/paskinomercato/
```

### 3. Spring Boot

```bash
cd PaskinoMercato-SpringBoot/paskinomercato-spring
mvn clean package -DskipTests
java -jar target/paskinomercato-spring-1.0.0.war
# App: http://localhost:8080/
```

### 4. WebSphere (richiede installazione WAS 8.5.5)

```bash
cd PaskinoMercato-WebSphere
mvn clean package
# Deploy tramite wsadmin — vedere WEBSPHERE_SETUP.md
```

---

## Documentazione Dettagliata

| Variante | Documento | Contenuto |
|---|---|---|
| WebSphere | [`PaskinoMercato-WebSphere/README.md`](PaskinoMercato-WebSphere/README.md) | Stack, architettura EJB 2.1, Entity Bean BMP, JAX-RPC, deploy wsadmin |
| WebSphere | [`PaskinoMercato-WebSphere/WEBSPHERE_SETUP.md`](PaskinoMercato-WebSphere/WEBSPHERE_SETUP.md) | Configurazione JDBC provider, DataSource, sessione JavaMail su WAS |
| Liberty | [`PaskinoMercato-Liberty/README.md`](PaskinoMercato-Liberty/README.md) | Stack CDI + JPA, persistence unit, bean di servizio, Liberty server.xml |
| Spring Boot | [`PaskinoMercato-SpringBoot/README.md`](PaskinoMercato-SpringBoot/README.md) | Stack Spring, controller, servizi, configurazione application.properties |

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
