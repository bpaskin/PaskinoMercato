# PaskinoMercato — Liberty Configuration & Setup Guide

This guide provides instructions to run and configure **PaskinoMercato** on **Open Liberty** or **WebSphere Liberty** using a PostgreSQL database.

---

## 1. Liberty Feature Manager

PaskinoMercato is modernly configured for **Java EE 7** APIs. The required features are declared in the `server.xml`:

```xml
<featureManager>
    <feature>jsp-2.3</feature>
    <feature>servlet-3.1</feature>
    <feature>ejbLite-3.2</feature>
    <feature>ejbHome-3.2</feature>
    <feature>jndi-1.0</feature>
    <feature>jdbc-4.1</feature>
    <feature>javaMail-1.5</feature>
    <feature>jaxws-2.2</feature>
</featureManager>
```

---

## 2. PostgreSQL DataSource

Liberty binds to PostgreSQL using direct connection pool configuration:

- **JDBC Driver:** Declared in `<jdbcDriver id="PostgreSQLDriver">` with implementation `javax.sql.ConnectionPoolDataSource="org.postgresql.ds.PGConnectionPoolDataSource"`.
- **Driver Location:** Visited via the shared library `WasExtLib` pointing to `/tmp/postgresql-42.7.13.jar`.
- **DataSource JNDI:** Bound to `jdbc/MercatoDB`.
- **Security & Credentials:** Read dynamically from `paskinomercato-ear/src/main/liberty/config/server.env`.

---

## 3. JavaMail Session Configuration

The `<mailSession>` binds JavaMail to the JNDI lookup `mail/MercatoMail`. 

To prevent **SSL Handshake Errors** (`PKIX path building failed`) with self-signed or internal SMTP servers (like `na.relay.ibm.com`), the mail session is configured to trust the SMTP server:

```xml
<mailSession jndiName="mail/MercatoMail" ...>
    <property name="mail.smtp.auth" value="true"/>
    <property name="mail.smtp.starttls.enable" value="true"/>
    <property name="mail.smtp.ssl.trust" value="*"/>
    <property name="mail.transport.protocol" value="smtp"/>
</mailSession>
```

---

## 4. Database Setup

Ensure PostgreSQL is running locally on port `5432`, and execute the following queries:

```bash
# 1. Create database and user
psql -U postgres -c "CREATE DATABASE mercatodb;"
psql -U postgres -c "CREATE USER mercato WITH PASSWORD 'yourpassword';"
psql -U postgres -c "GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;"

# 2. Run DDL schema SQL
psql -U mercato -d mercatodb -f paskinomercato-ejb/src/main/resources/db/schema.sql

# 3. Load product seed data
psql -U mercato -d mercatodb -f paskinomercato-ejb/src/main/resources/db/seed_products.sql
```

---

## 5. Build, Run, and Manage with Liberty

Use the preconfigured `liberty-maven-plugin` on the EAR module to build, install dependencies, and start Liberty.

### Build Application
```bash
mvn clean package -DskipTests
```

### Install Liberty Server Features
```bash
mvn -pl paskinomercato-ear liberty:install-feature
```

### Deploy EAR Artifact
```bash
mvn -pl paskinomercato-ear liberty:deploy
```

### Start Server (Background)
```bash
mvn -pl paskinomercato-ear liberty:start
```

### Stop Server
```bash
mvn -pl paskinomercato-ear liberty:stop
```

### Development Mode (with Hot Reloading)
```bash
mvn -pl paskinomercato-ear liberty:dev
```

---

## 6. Endpoints & URLs

Once the server started, verify that the following endpoints respond properly:

| Resource | URL |
|---|---|
| **Home page** | `http://localhost:9084/paskinomercato/` |
| **Product catalog** | `http://localhost:9084/paskinomercato/catalogo` |
| **SOAP Endpoint** | `http://localhost:9084/paskinomercato/MercatoService` |
| **WSDL Definition** | `http://localhost:9084/paskinomercato/MercatoService?wsdl` |

---

## 7. Troubleshooting & Process Bind Conflicts

### Error: "Address already in use" on Port 9084
If a background Java process of Liberty is already running and holding port 9084, any new attempt to launch the server will fail to bind the HTTP endpoint.
- Check running Java processes:
  ```bash
  ps -ef | grep java
  ```
- Locate the process executing `ws-server.jar paskinomercato` and terminate it:
  ```bash
  kill -9 <PID>
  ```
- Stop and cleanly restart using Maven:
  ```bash
  mvn -pl paskinomercato-ear liberty:stop
  mvn -pl paskinomercato-ear liberty:start
  ```
