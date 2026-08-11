# PaskinoMercato — WebSphere Configuration Guide

## 1. PostgreSQL DataSource

In WebSphere Admin Console:
- Resources → JDBC → JDBC Providers → New
  - Provider type: User-defined
  - Implementation: `org.postgresql.ds.PGConnectionPoolDataSource`
  - Classpath: `/path/to/postgresql-42.7.13.jar`
- Resources → JDBC → Data Sources → New
  - JNDI name: `jdbc/MercatoDB`
  - Database: `mercatodb`
  - Host: `localhost`, Port: `5432`
  - User: `mercato`, Password: `<password>`

## 2. JavaMail Session

- Resources → Mail → Mail Sessions → New
  - JNDI name: `mail/MercatoMail`
  - Mail transport host: `smtp.yourdomain.it`
  - Mail transport user: `noreply@paskinomercato.it`
  - Mail from: `noreply@paskinomercato.it`

## 3. Database Setup

```bash
psql -U postgres
CREATE DATABASE mercatodb;
CREATE USER mercato WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE mercatodb TO mercato;
\c mercatodb
\i paskinomercato-ejb/src/main/resources/db/schema.sql
```

## 4. Build & Deploy

```bash
mvn clean package
# Deploy paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear to WebSphere
```

## 5. Application URL

```
http://localhost:9080/paskinomercato/
```

## 6. JAX-RPC SOAP Endpoint

```
http://localhost:9080/paskinomercato/MercatoService?wsdl
```

## 7. Notes

- All prices are in Euro (€) only
- Delivery addresses must be Italian (IT country code + valid 5-digit CAP + valid province code)
- Product catalog is capped at 1503 items (enforced by DB trigger)
- EJB 2.0 — no JPA annotations, pure JDBC via JNDI DataSource
- Language toggle (IT/EN) stored in HttpSession attribute `lang`
