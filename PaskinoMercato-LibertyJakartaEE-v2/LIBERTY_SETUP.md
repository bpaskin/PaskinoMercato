# PaskinoMercato — Open Liberty Setup Guide

## 1. Prerequisites

| Tool | Version |
|---|---|
| Java JDK | **25** (IBM Semeru or Eclipse Temurin) |
| Maven | **3.9+** |
| PostgreSQL | **15+** |

Open Liberty **26.0.0.9** is downloaded automatically by the Liberty Maven plugin on first build — no manual server installation required.

---

## 2. Database Setup

```bash
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'yourpassword';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"

psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/schema.sql

psql -U mercato -d mercatodb \
     -f paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

---

## 3. Build

```bash
mvn clean install -DskipTests
```

---

## 4. Configuration

All server configuration lives in [`paskinomercato-ear/src/main/liberty/config/server.xml`](paskinomercato-ear/src/main/liberty/config/server.xml).

Sensitive values are exposed as `<variable>` with a `defaultValue`. Override them without editing `server.xml` by setting environment variables or by creating a `server.env` file at:

```
paskinomercato-ear/target/liberty/wlp/usr/servers/paskinomercato/server.env
```

### Key variables

| Variable | Default | Description |
|---|---|---|
| `httpEndpoint_host_1` | `localhost` | HTTP listen interface |
| `httpEndpoint_port_1` | `9084` | HTTP port |
| `httpEndpoint_secure_port_1` | `9447` | HTTPS port |
| `PaskinoMercato_DB_…_serverName` | `localhost` | PostgreSQL host |
| `PaskinoMercato_DB_…_portNumber` | `5432` | PostgreSQL port |
| `PaskinoMercato_DB_…_databaseName` | `mercatodb` | Database name |
| `MercatoDBAlias_user` | `mercato` | Database user |
| `MercatoDBAlias_password` | `changeme` | Database password |
| `PaskinoMercato_Mail_…_password` | *(empty)* | SMTP password |

### PostgreSQL JDBC driver

The driver is resolved from the local Maven repository:

```
${user.home}/.m2/repository/org/postgresql/postgresql/42.7.7/postgresql-42.7.7.jar
```

Run `mvn install -DskipTests` once to populate it, or adjust the `<path>` in `server.xml` to point to a JAR already on disk.

---

## 5. Start / Stop

```bash
# Start (foreground — logs to console)
mvn -f paskinomercato-ear/pom.xml liberty:run

# Start (background)
mvn -f paskinomercato-ear/pom.xml liberty:start

# Stop
mvn -f paskinomercato-ear/pom.xml liberty:stop
```

The application is ready when `messages.log` shows:

```
CWWKZ0001I: Application paskinomercato-ear started in X seconds.
```

---

## 6. Application URLs

| Resource | URL |
|---|---|
| Home page | `http://localhost:9084/paskinomercato/` |
| Catalogo | `http://localhost:9084/paskinomercato/catalogo` |
| Carrello | `http://localhost:9084/paskinomercato/carrello` |
| Checkout | `http://localhost:9084/paskinomercato/checkout` |
| Login | `http://localhost:9084/paskinomercato/login` |
| Ordini | `http://localhost:9084/paskinomercato/ordini` |
| Language toggle | `http://localhost:9084/paskinomercato/lingua?lang=en` |
| MicroProfile Metrics | `http://localhost:9084/metrics/` |
| Liberty Admin (REST) | `http://localhost:9084/ibm/api/` |

---

## 7. Notes

- All prices are in Euro (€) only
- Delivery addresses must be Italian (IT country code, valid 5-digit CAP, valid 2-letter province code)
- Product catalog is capped at 1503 items (enforced by DB trigger)
- Persistence is pure JDBC via JNDI `jdbc/MercatoDB` — no JPA
- Language preference (IT/EN) is stored in `HttpSession` attribute `lang`
- Former EJB 2.x BMP Entity Beans are now `@Stateless` session beans with plain business interfaces (`*EntityService` / `*EntityData`)
