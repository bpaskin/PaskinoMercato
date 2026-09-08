# PaskinoMercato 🛒

**Supermercato Online Italiano** — Applicazione di riferimento per un supermercato online italiano, implementata in **sei varianti** distinte della piattaforma Java che illustrano l'evoluzione della tecnologia enterprise attraverso le generazioni.

La stessa logica di business — vetrina bilingue (🇮🇹 / 🇬🇧), consegna solo in Italia, prezzi in Euro, catalogo di massimo **1.503 prodotti**, backend PostgreSQL — è fornita in sei implementazioni autonome: due varianti WebSphere (standard e avanzata con maggiori dipendenze WAS proprietarie), tre varianti Open Liberty (JPA 2.1, migrazione diretta da WebSphere-v2 con EJB 3.2 + JDBC e Jakarta EE 10 con EJB Lite 4.0 + JDBC) e `JdbcTemplate` su Spring Boot.

---

## Indice

1. [Le Sei Varianti](#le-sei-varianti)
2. [Funzionalità dell'Applicazione](#funzionalità-dellapplicazione)
3. [Struttura del Repository](#struttura-del-repository)
4. [Architettura Generale](#architettura-generale)
5. [Modello di Dominio Condiviso](#modello-di-dominio-condiviso)
6. [Database Condiviso](#database-condiviso)
7. [Confronto Tecnologico](#confronto-tecnologico)
8. [Percorso di Modernizzazione](#percorso-di-modernizzazione)
9. [WebSphere V2 — Dipendenze Proprietarie e Problemi di Migrazione](#websphere-v2--dipendenze-proprietarie-e-problemi-di-migrazione)
10. [Prerequisiti](#prerequisiti)
11. [Avvio Rapido](#avvio-rapido)
12. [Documentazione Dettagliata](#documentazione-dettagliata)

---

## Le Sei Varianti

| Directory | Piattaforma | Java | Persistenza | Web Service | Porta |
|---|---|---|---|---|---|
| [`PaskinoMercato-WebSphere/`](PaskinoMercato-WebSphere/) | IBM WebSphere Application Server 8.5.5 | Java 8 | JDBC diretto | JAX-RPC 1.1 (SOAP) | 9080 |
| [`PaskinoMercato-WebSphere-v2/`](PaskinoMercato-WebSphere-v2/) | IBM WebSphere Application Server 8.5.5 | Java 8 | JDBC diretto + API WAS proprietarie | JAX-RPC 1.1 (SOAP) | 9080 |
| [`PaskinoMercato-Liberty/`](PaskinoMercato-Liberty/) | Open Liberty 26 | Java 11 | JPA 2.1 (EclipseLink) | — | 9080 |
| [`PaskinoMercato-Liberty-v2/`](PaskinoMercato-Liberty-v2/) | Open Liberty 26 | Java 8 (bytecode) / 21+ (runtime) | JDBC diretto via JNDI | JAX-WS 2.2 (SOAP) | 9080 |
| [`PaskinoMercato-LibertyJakartaEE-v2/`](PaskinoMercato-LibertyJakartaEE-v2/) | Open Liberty 26.0.0.9 / Jakarta EE 10 | Java 22+ | JDBC diretto via JNDI | JAX-WS (SOAP) | 9084 |
| [`PaskinoMercato-SpringBoot/`](PaskinoMercato-SpringBoot/) | Spring Boot 4.1 / Tomcat 11 embedded | Java 25 | JdbcTemplate | — | 8080 |

Ogni directory è un progetto Maven autonomo con il proprio `README.md`, istruzioni di build e guida al deploy.

> **Nota:** `PaskinoMercato-WebSphere-v2` è una variante intenzionalmente più complessa di `PaskinoMercato-WebSphere`. Introduce dipendenze dirette sulle API proprietarie IBM WebSphere (`was_public.jar`) e pattern architetturali che aumentano significativamente il costo di migrazione verso Liberty. È pensata come punto di partenza realistico per esercizi di modernizzazione più impegnativi.

> **Nota:** `PaskinoMercato-Liberty-v2` è il risultato diretto della migrazione di `PaskinoMercato-WebSphere-v2` su Open Liberty. Mantiene lo stile JDBC diretto e gli EJB (aggiornati da 2.1 a 3.2), rimpiazza JAX-RPC con JAX-WS 2.2 ed elimina tutte le dipendenze sulle API proprietarie WAS. È il punto di partenza per confrontare l'impegno di migrazione rispetto alla variante Liberty standard (che usa JPA).

> **Nota:** `PaskinoMercato-LibertyJakartaEE-v2` è la variante modernizzata su Open Liberty con Jakarta EE 10, Java 22+, EJB Lite 4.0, Servlet 6.0, Jakarta Pages 3.1 e Jakarta Mail 2.1. Mantiene la persistenza JDBC diretta e utilizza la porta HTTP 9084.

---

## Funzionalità dell'Applicazione

Tutte le varianti implementano le stesse funzionalità di business:

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
├── PaskinoMercato-WebSphere-v2/         ← JavaEE 5 / EJB 2.1 / WAS 8.5.5 + API WAS proprietarie
│   ├── paskinomercato-ejb/              ← Session Bean EJB 2.1 + Entity Bean BMP
│   ├── paskinomercato-war/              ← Servlet 2.5 + JSP 2.1 + JAX-RPC + ServerNameFilter (WAS API)
│   ├── paskinomercato-ear/              ← Packaging EAR
│   ├── scripts/wsadmin/                 ← Installer wsadmin automatizzato (Jython)
│   ├── was_public.jar                   ← JAR API WAS locale (vedere sezione dedicata)
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
├── PaskinoMercato-Liberty-v2/           ← JavaEE 7 / EJB 3.2 / Open Liberty — migrato da WebSphere-v2
│   ├── paskinomercato-ejb/              ← Session Bean EJB 3.2 + JDBC diretto
│   ├── paskinomercato-war/              ← Servlet 3.1 + JSP 2.3 + JAX-WS 2.2
│   ├── paskinomercato-ear/              ← Packaging EAR + server.xml Liberty
│   ├── scripts/                         ← Script di supporto
│   ├── LIBERTY_SETUP.md                 ← Guida alla configurazione risorse Liberty
│   └── README.md
│
├── PaskinoMercato-LibertyJakartaEE-v2/  ← Jakarta EE 10 / EJB Lite 4.0 / Open Liberty 26
│   ├── paskinomercato-ejb/              ← Session Bean EJB Lite + JDBC diretto
│   ├── paskinomercato-war/              ← Servlet 6.0 + Jakarta Pages 3.1 + JAX-WS
│   ├── paskinomercato-ear/              ← Packaging EAR + server.xml Liberty
│   ├── scripts/                         ← Script di supporto
│   ├── LIBERTY_SETUP.md                 ← Guida alla configurazione risorse Liberty
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

Tutte le varianti seguono la stessa architettura a tre livelli — la differenza è nella tecnologia usata a ciascuno strato:

```
Browser
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Web (HTTP)                                                     │
│                                                                         │
│  WebSphere:    Servlet 2.5 + JSP 2.1 + JSTL 1.2                       │
│  Liberty:      Servlet 3.1 + JSP 2.3 + JSTL 1.2 + EL 3.0             │
│  Liberty-v2:   Servlet 3.1 + JSP 2.3 + JSTL 1.2 + EL 3.0             │
│  Jakarta EE:   Servlet 6.0 + Jakarta Pages 3.1 + JSTL + EL           │
│  Spring:       Spring MVC @Controller + JSP 3 + JSTL 3 + EL 6         │
└─────────────────────────────────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Servizi (Business Logic)                                       │
│                                                                         │
│  WebSphere:    EJB 2.1 Session Bean Stateless + Entity Bean BMP        │
│  Liberty:      CDI @ApplicationScoped + JTA @Transactional             │
│  Liberty-v2:   EJB 3.2 Session Bean Stateless + CMT                   │
│  Jakarta EE:   EJB Lite 4.0 Stateless/Stateful + CMT                 │
│  Spring:       @Service + Spring @Transactional                        │
└─────────────────────────────────────────────────────────────────────────┘
  │
  ▼
┌─────────────────────────────────────────────────────────────────────────┐
│  Livello Persistenza                                                    │
│                                                                         │
│  WebSphere:    JDBC diretto via JNDI DataSource                        │
│  Liberty:      JPA 2.1 (EclipseLink) — EntityManager + JPQL           │
│  Liberty-v2:   JDBC diretto via JNDI DataSource                        │
│  Jakarta EE:   JDBC diretto via JNDI DataSource                        │
│  Spring:       JdbcTemplate via HikariCP                               │
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
| Liberty-v2 | EJB 3.2 Stateful (`CarrelloBean`) + tabella `mercato.carrello` |
| Spring Boot | `CarrelloSessionBean` annotato `@SessionScope` (proxy CGLIB) |

---

## Modello di Dominio Condiviso

Tutte le varianti operano sulle stesse entità di business, mappate sullo stesso schema PostgreSQL:

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

Tutte le varianti condividono lo stesso schema PostgreSQL `mercato`. Il DDL completo e i dati di esempio si trovano in `paskinomercato-ejb/src/main/resources/db/` di ciascuna variante (file identici).

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

| Aspetto | WebSphere 8.5.5 | WebSphere 8.5.5 v2 | Open Liberty 26 | Open Liberty 26 v2 | Jakarta EE 10 / Liberty 26 | Spring Boot 4.1 |
|---|---|---|---|---|---|---|
| **Java** | 8 | 8 | 11 | 8 (bytecode) / 21+ (runtime) | 22+ | 25 |
| **Standard** | JavaEE 5 | JavaEE 5 + API WAS proprietarie | Jakarta EE (CDI 1.2 + JPA 2.1) | Java EE 7 (EJB 3.2) | Jakarta EE 10 (EJB Lite 4.0) | Spring Framework 7 |
| **Servizi** | EJB 2.1 — solo descriptor XML | EJB 2.1 + `com.ibm.websphere.*` API | CDI `@ApplicationScoped` + JTA | EJB 3.2 `@Stateless` / `@Stateful` | EJB Lite 4.0 `@Stateless` / `@Stateful` | Spring `@Service` + `@Transactional` |
| **Persistenza** | JDBC diretto via JNDI `DataSource` | JDBC diretto via JNDI `DataSource` | **JPA 2.1** (EclipseLink) — `EntityManager` | JDBC diretto via JNDI `DataSource` | JDBC diretto via JNDI `DataSource` | `JdbcTemplate` via HikariCP |
| **Carrello** | EJB Stateful + tabella DB | EJB Stateful + tabella DB | POJO in `HttpSession` | EJB 3.2 Stateful + tabella DB | EJB Lite 4.0 Stateful + tabella DB | `@SessionScope` Spring bean |
| **Transazioni** | CMT (Container-Managed) | CMT (Container-Managed) | JTA `@Transactional` | CMT (Container-Managed) | CMT (Container-Managed) | Spring `@Transactional` |
| **Web layer** | JSP 2.1 + JSTL 1.2 + EL 2.2 | JSP 2.1 + JSTL 1.2 + EL 2.2 | JSP 2.3 + JSTL 1.2 + EL 3.0 | JSP 2.3 + JSTL 1.2 + EL 3.0 | Servlet 6.0 + Jakarta Pages 3.1 | JSP 3 + JSTL 3 + EL 6 |
| **Web service** | JAX-RPC 1.1 (document/literal) | JAX-RPC 1.1 (document/literal) | — | **JAX-WS 2.2** (SOAP) | JAX-WS (SOAP) | — |
| **Deploy** | EAR — script wsadmin Jython | EAR — script wsadmin Jython | EAR — Liberty Maven plugin | EAR — Liberty Maven plugin | EAR — Liberty Maven plugin | WAR eseguibile `java -jar` |
| **Configurazione** | Admin Console / wsadmin | Admin Console / wsadmin | `server.xml` + `bootstrap.properties` | `server.xml` + `server.env` | `server.xml` + variabili d'ambiente | `application.properties` |
| **Email** | JavaMail via JNDI | JavaMail via JNDI | Jakarta Mail via JNDI | JavaMail 1.5 via JNDI | Jakarta Mail 2.1 via JNDI | Spring Mail (`JavaMailSender`) |
| **Dipendenze proprietarie** | Nessuna | **`was_public.jar`** (`com.ibm.websphere.appserver`) | Nessuna | Nessuna | Nessuna | Nessuna |

---

## WebSphere V2 — Dipendenze Proprietarie e Problemi di Migrazione

`PaskinoMercato-WebSphere-v2` introduce l'uso diretto delle **API proprietarie IBM WebSphere** tramite il JAR `was_public.jar`. Questo è il pattern più comune nelle applicazioni WAS reali e il principale ostacolo alla migrazione verso Liberty o qualsiasi altro runtime.

### Il JAR `was_public.jar` nel `pom.xml`

Il `pom.xml` radice del progetto v2 dichiara la dipendenza con `scope: system`, il che significa che Maven non la scarica da Maven Central — deve essere presente localmente nel percorso indicato:

```xml
<!-- pom.xml — radice del progetto -->
<properties>
    <!-- Percorso al JAR WAS locale: risolve a PaskinoMercato-WebSphere-v2/was_public.jar -->
    <was.public.jar>${project.basedir}/was_public.jar</was.public.jar>
</properties>

<dependencyManagement>
    <dependencies>
        <!-- WAS Public API (local JAR provided in the project root) -->
        <dependency>
            <groupId>com.ibm.websphere.appserver</groupId>
            <artifactId>was-public</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${was.public.jar}</systemPath>
        </dependency>
    </dependencies>
</dependencyManagement>
```

La proprietà `was.public.jar` viene poi **sovrascritta** in [`paskinomercato-war/pom.xml`](PaskinoMercato-WebSphere-v2/paskinomercato-war/pom.xml) per correggere il percorso relativo dal sottomodulo:

```xml
<!-- paskinomercato-war/pom.xml -->
<properties>
    <!-- Un livello sopra rispetto al modulo WAR = directory radice del progetto -->
    <was.public.jar>${project.basedir}/../was_public.jar</was.public.jar>
</properties>
```

> **Attenzione:** `scope: system` è **deprecato in Maven** e intrinsecamente fragile — il percorso deve essere corretto su ogni macchina che esegue la build. Se il file `was_public.jar` non si trova nella posizione attesa, la build fallirà con un errore simile a:
> ```
> [ERROR] 'dependencies.dependency.systemPath' for com.ibm.websphere.appserver:was-public:jar
>         must point to an existing file but could not find: /path/to/was_public.jar
> ```

#### Correggere il percorso per la propria installazione WAS

Il JAR si trova nell'installazione WebSphere Application Server locale:

| Sistema Operativo | Percorso tipico |
|---|---|
| Linux / macOS | `/opt/IBM/WebSphere/AppServer/dev/was_public.jar` |
| Windows | `C:\IBM\WebSphere\AppServer\dev\was_public.jar` |

Per puntare al JAR dell'installazione WAS locale invece del file nel repository, modificare la proprietà nel `pom.xml` radice:

```xml
<!-- Opzione A: percorso assoluto all'installazione WAS locale -->
<was.public.jar>/opt/IBM/WebSphere/AppServer/dev/was_public.jar</was.public.jar>

<!-- Opzione B: installare il JAR nel repository Maven locale e usare scope: provided -->
<!-- mvn install:install-file \
       -Dfile=/opt/IBM/WebSphere/AppServer/dev/was_public.jar \
       -DgroupId=com.ibm.websphere.appserver \
       -DartifactId=was-public \
       -Dversion=8.5.5 \
       -Dpackaging=jar
-->
```

Se si sceglie l'opzione B (installazione nel repository Maven locale), rimuovere `<scope>system</scope>` e `<systemPath>` dalla dipendenza nel `pom.xml` radice e cambiare la versione in `8.5.5`.

---

### Perché V2 è più difficile da migrare a Liberty

Rispetto a `PaskinoMercato-WebSphere`, la variante v2 aggiunge i seguenti ostacoli alla migrazione:

| Problema | Dettaglio | Impatto su Liberty |
|---|---|---|
| **`com.ibm.websphere.appserver.*` API** | Le classi del package `com.ibm.websphere` sono fornite solo da WAS; Liberty non le espone (a meno di feature specifiche) | Le classi che le importano non compilano su Liberty senza sostituzione |
| **`scope: system` JAR** | Il `pom.xml` usa `systemPath` per localizzare `was_public.jar` — non è un artefatto Maven standard | Il build pipeline di Liberty non conosce questo JAR; bisogna rimuovere la dipendenza o sostituirla |
| **`ServerNameFilter`** | Filtro Servlet che legge metadati del server WAS tramite API proprietarie (`com.ibm.websphere.runtime.ServerName`) | Su Liberty tale API non esiste; il filtro deve essere riscritto o eliminato |
| **Binding descriptor WAS** (`ibm-ejb-jar-bnd.xmi`, `ibm-application-bnd.xmi`) | File di configurazione IBM-specifici che controllano JNDI binding, sicurezza e pool | Non supportati su Liberty; vanno convertiti in `ibm-ejb-jar-bnd.xml` (formato Liberty) o rimossi |
| **JAX-RPC 1.1** | JAX-RPC è rimosso da Liberty (sostituito da JAX-WS 2.x / JAX-RS) | Il web service deve essere riscritto usando JAX-WS o REST |
| **EJB 2.1 Entity Bean BMP** | Liberty supporta EJB 3.x; gli Entity Bean 2.x in BMP (Bean-Managed Persistence) non sono supportati | Tutti gli Entity Bean vanno convertiti in entità JPA |

---

## Percorso di Modernizzazione

Questo repository documenta un percorso realistico di modernizzazione enterprise Java in due passi principali, con un percorso alternativo che preserva lo stile JDBC/EJB:

```
WebSphere 8.5.5                Open Liberty 26              Spring Boot 4.1
(JavaEE 5 / Java 8)    ──▶    (CDI + JPA / Java 11)  ──▶  (Spring / Java 25)

EJB 2.1 + JDBC              CDI @ApplicationScoped         @Service
Entity Bean BMP             JPA 2.1 EclipseLink            JdbcTemplate
JAX-RPC 1.1 SOAP            (SOAP rimosso)                 (SOAP rimosso)
Descriptor XML              beans.xml minimale             Annotazioni pure
Stateful EJB cart           POJO in HttpSession            @SessionScope bean


WebSphere 8.5.5 v2             Open Liberty 26 v2
(JavaEE 5 + API WAS / Java 8)  ──▶  (Java EE 7 / Java 8+)

EJB 2.1 + API WAS prop.      EJB 3.2 (annotazioni)
JAX-RPC 1.1 SOAP             JAX-WS 2.2 SOAP
was_public.jar (system)      nessuna dipendenza proprietaria
ibm-*-bnd.xmi (legacy)       ibm-*-bnd.xml (formato Liberty)
```

### Cambiamenti chiave WebSphere V2 → Liberty v2 (percorso di migrazione diretta)

| Da (WebSphere-v2) | A (Liberty-v2) |
|---|---|
| `com.ibm.websphere.runtime.ServerName` (API proprietaria) | Rimosso o sostituito con `java.lang.management.ManagementFactory` |
| `was_public.jar` (`scope: system`) | Dipendenza rimossa — Liberty non richiede questo JAR |
| `ServerNameFilter` (filtra su API WAS) | Riscritto senza dipendenze WAS o eliminato |
| `ibm-ejb-jar-bnd.xmi` (binding WAS legacy) | Convertito in `ibm-ejb-jar-bnd.xml` (formato Liberty) |
| `ibm-application-bnd.xmi` (binding WAS legacy) | Convertito in `ibm-application-bnd.xml` (formato Liberty) |
| JAX-RPC 1.1 web service | Riscritto come JAX-WS 2.2 SOAP |
| EJB 2.1 (`ejbCreate`, `EJBLocalHome`, descriptor XML) | EJB 3.2 (`@Stateless`, `@Stateful`, annotazioni) |

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

| Strumento | WebSphere | Liberty | Liberty-v2 | Liberty Jakarta EE | Spring Boot |
|---|---|---|---|---|---|
| Java JDK | 8 | **11** | **8 / 21 / 25** | **22+** | 25 |
| Maven | 3.6+ | 3.6+ | 3.6+ | **3.9+** | 3.9+ |
| PostgreSQL | 15+ | 15+ | 15+ | **15+** | 15+ |
| Application server | IBM WAS 8.5.5 (installazione locale) | Scaricato automaticamente dal plugin Maven | Scaricato automaticamente dal plugin Maven | Scaricato automaticamente dal plugin Maven | Tomcat 11 embedded (nel JAR) |
| Python 3 | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG | Opzionale — generazione immagini SVG |

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

### 3. Open Liberty v2 (migrazione diretta da WebSphere-v2)

```bash
cd PaskinoMercato-Liberty-v2
mvn clean package
mvn -pl paskinomercato-ear io.openliberty.tools:liberty-maven-plugin:run
# App: http://localhost:9080/paskinomercato/
```

### 4. Open Liberty Jakarta EE 10

```bash
cd PaskinoMercato-LibertyJakartaEE-v2
mvn clean install -DskipTests
mvn -f paskinomercato-ear/pom.xml liberty:run
# App: http://localhost:9084/paskinomercato/
```

### 5. Spring Boot

```bash
cd PaskinoMercato-SpringBoot/paskinomercato-spring
mvn clean package -DskipTests
java -jar target/paskinomercato-spring-1.0.0.war
# App: http://localhost:8080/
```

### 6. WebSphere (richiede installazione WAS 8.5.5)

```bash
cd PaskinoMercato-WebSphere
mvn clean package
# Deploy tramite wsadmin — vedere WEBSPHERE_SETUP.md
```

### 7. WebSphere V2 (richiede WAS 8.5.5 + `was_public.jar`)

Prima di eseguire la build, assicurarsi che il file `was_public.jar` sia presente in `PaskinoMercato-WebSphere-v2/` oppure aggiornare la proprietà `was.public.jar` nel [`pom.xml`](PaskinoMercato-WebSphere-v2/pom.xml) con il percorso dell'installazione WAS locale (tipicamente `/opt/IBM/WebSphere/AppServer/dev/was_public.jar`).

```bash
cd PaskinoMercato-WebSphere-v2
mvn clean package
# Deploy tramite wsadmin — vedere WEBSPHERE_SETUP.md
```

---

## Documentazione Dettagliata

| Variante | Documento | Contenuto |
|---|---|---|
| WebSphere | [`PaskinoMercato-WebSphere/README.md`](PaskinoMercato-WebSphere/README.md) | Stack, architettura EJB 2.1, Entity Bean BMP, JAX-RPC, deploy wsadmin |
| WebSphere | [`PaskinoMercato-WebSphere/WEBSPHERE_SETUP.md`](PaskinoMercato-WebSphere/WEBSPHERE_SETUP.md) | Configurazione JDBC provider, DataSource, sessione JavaMail su WAS |
| WebSphere V2 | [`PaskinoMercato-WebSphere-v2/README.md`](PaskinoMercato-WebSphere-v2/README.md) | Stack EJB 2.1, API WAS proprietarie, `was_public.jar`, deploy wsadmin |
| WebSphere V2 | [`PaskinoMercato-WebSphere-v2/WEBSPHERE_SETUP.md`](PaskinoMercato-WebSphere-v2/WEBSPHERE_SETUP.md) | Configurazione JDBC provider, DataSource, sessione JavaMail su WAS |
| Liberty | [`PaskinoMercato-Liberty/README.md`](PaskinoMercato-Liberty/README.md) | Stack CDI + JPA, persistence unit, bean di servizio, Liberty server.xml |
| Liberty V2 | [`PaskinoMercato-Liberty-v2/README.md`](PaskinoMercato-Liberty-v2/README.md) | Stack EJB 3.2 + JDBC, JAX-WS 2.2, migrazione da WebSphere-v2, server.xml Liberty |
| Liberty V2 | [`PaskinoMercato-Liberty-v2/LIBERTY_SETUP.md`](PaskinoMercato-Liberty-v2/LIBERTY_SETUP.md) | Configurazione DataSource, JavaMail e JAX-WS su Open Liberty |
| Liberty Jakarta EE | [`PaskinoMercato-LibertyJakartaEE-v2/README.md`](PaskinoMercato-LibertyJakartaEE-v2/README.md) | Stack Jakarta EE 10, EJB Lite 4.0, JDBC, Jakarta Mail e configurazione Open Liberty |
| Liberty Jakarta EE | [`PaskinoMercato-LibertyJakartaEE-v2/LIBERTY_SETUP.md`](PaskinoMercato-LibertyJakartaEE-v2/LIBERTY_SETUP.md) | Configurazione DataSource, Jakarta Mail e deploy su Open Liberty |
| Spring Boot | [`PaskinoMercato-SpringBoot/README.md`](PaskinoMercato-SpringBoot/README.md) | Stack Spring, controller, servizi, configurazione application.properties |

---

*PaskinoMercato — Consegna solo in Italia 🇮🇹*
