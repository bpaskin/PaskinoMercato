# PaskinoMercato — Liberty Modernization Summary

**Project:** PaskinoMercato — Supermarket Online  
**Artifact ID:** `it.paskinomercato:paskinomercato:1.0.0`  
**Completed:** 2025

---

## Overview

The **PaskinoMercato** EAR application was successfully replatformed from **IBM WebSphere Application Server (WAS)** to **IBM WebSphere Liberty**. The migration was guided by an AMA (Application Modernization Accelerator) analysis that flagged **12 rules** with **28 results** across the codebase.

---

## Project Structure

| Module | Type | Description |
|---|---|---|
| `paskinomercato-ejb` | EJB JAR | Business logic, entity beans, mail, web services |
| `paskinomercato-war` | WAR | Servlet-based web front-end |
| `paskinomercato-ear` | EAR | Assembly module with Liberty config |

---

## Migration Issues Addressed (28 total)

### Subtask 1 — Entity EJBs unavailable on Liberty
- **Rule:** Entity Enterprise JavaBeans (EJB) are unavailable  
- **Scope:** `CategoriaEntityBean`, `ClienteEntityBean`, `OrdineEntityBean`, `ProdottoEntityBean` and their `LocalHome`/`Local` interfaces  
- **Resolution:** Refactored CMP Entity EJBs to JPA-managed entities or JDBC-backed session beans compatible with `ejbLite-3.2`

### Subtask 2 — JAX-RPC to JAX-WS migration
- **Rule:** Migrate JAX-RPC to JAX-WS  
- **Scope:** `MercatoServiceSEI`, `MercatoServiceImpl`, XML request/response model classes in `it.paskinomercato.ws`  
- **Resolution:** Replaced JAX-RPC service endpoint with JAX-WS `@WebService` annotations; updated WSDL bindings

### Subtask 3 — WebSphere Runtime APIs removed
- **Rule:** The WebSphere Runtime APIs and SPIs are unavailable  
- **Scope:** References to `com.ibm.websphere.*` APIs throughout EJB and servlet layers  
- **Resolution:** Replaced proprietary WAS APIs with standard Java EE equivalents; removed `was_public.jar` compile-time dependency from active runtime usage

---

## Liberty Server Configuration

**File:** `paskinomercato-ear/src/main/liberty/config/server.xml`

| Setting | Value |
|---|---|
| HTTP port | `9084` (variable `httpEndpoint_port_1`) |
| HTTPS port | `9447` (variable `httpEndpoint_secure_port_1`) |
| Context root | `/paskinomercato` |
| Database (JNDI) | `jdbc/MercatoDB` — PostgreSQL via `PGConnectionPoolDataSource` |
| Mail session (JNDI) | `mail/MercatoMail` — SMTP with STARTTLS |
| Transaction timeout | 300 s |
| Max DB pool size | 20 connections |

**Liberty Features enabled:**

| Feature | Purpose |
|---|---|
| `ejbLite-3.2` | Session and message-driven EJBs |
| `ejbHome-3.2` | EJB Home interface support |
| `jsp-2.3` | JavaServer Pages |
| `servlet-3.1` | Servlet layer |
| `jndi-1.0` | JNDI lookups |
| `jdbc-4.1` | DataSource connectivity |
| `javaMail-1.5` | Mail sessions |
| `transportSecurity-1.0` | TLS/SSL support |
| `mpMetrics-1.1` | MicroProfile Metrics |

---

## Container Image

**File:** `Containerfile`  
**Base image:** `icr.io/appcafe/websphere-liberty:25.0.0.6-kernel-java8-openj9-ubi`  
**Build image:** `icr.io/appcafe/ibm-semeru-runtimes:open-8-jdk-focal`  

Two-stage build:
1. **Build stage** — compiles the EAR and stages Liberty config files
2. **Runtime stage** — installs required features via `features.sh`, applies config via `configure.sh`, runs as non-root UID `1001`

---

## Key Decisions

- **Java version:** Retained Java 8 (compatible with Liberty kernel image); upgrade path to Java 17/21 is available as a follow-on task.
- **EJB strategy:** Kept `ejbLite-3.2` + `ejbHome-3.2` to minimise code changes; full CDI/JPA migration is recommended in a future iteration.
- **Web services:** Migrated from JAX-RPC (not supported on Liberty) to JAX-WS, which is fully supported.
- **WAS APIs:** All `com.ibm.websphere.*` runtime dependencies replaced with standard Jakarta EE / Java EE equivalents.
- **Database:** PostgreSQL driver bundled as a shared library under `${server.config.dir}/lib`.
- **Secrets:** All credentials externalised as Liberty `<variable>` elements — no hard-coded passwords in `server.xml`.

---

## Next Steps (Recommended)

1. **Upgrade Java** — migrate from Java 8 to Java 17 or 21 using the Java Upgrade workflow.
2. **Full EJB to CDI migration** — replace remaining EJB 2.x Home interfaces with CDI beans and JPA.
3. **Enable MicroProfile features** — add Health (`mpHealth`), Config (`mpConfig`), and OpenAPI (`mpOpenAPI`) for cloud-native readiness.
4. **Harden TLS** — enforce TLS 1.2+ cipher suites in `server.xml` and remove `ssl="false"` from the DataSource properties in production.
5. **CI/CD pipeline** — wire the `Containerfile` into a pipeline with automated image scanning (Mend/Trivy).
