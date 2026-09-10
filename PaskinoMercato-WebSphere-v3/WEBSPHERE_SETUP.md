# PaskinoMercato — WebSphere 8.5.5 Setup

## Runtime requirements

- IBM WebSphere Application Server 8.5.5.9 or later (required for Java 8)
- WebSphere configured to run with Java 8
- Java EE 5 application support (EAR 5, Servlet 2.5, EJB 2.1)
- Maven 3 and a Java 8-capable JDK for the build

## WebSphere-managed H2 database

Install `h2-2.2.224.jar` at the same absolute path on the target WebSphere node,
for example `/opt/was-drivers/h2-2.2.224.jar`. The H2 driver is not packaged in
the EAR. Configure a server-scoped JDBC provider and data source with:

- provider implementation class: `org.h2.jdbcx.JdbcDataSource`
- data source JNDI name: `jdbc/PaskinoMercatoDS`
- `URL` custom property:

```text
jdbc:h2:mem:paskinomercato;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
```

- `user` custom property: `sa`
- `password` custom property: empty
- `nonTransactionalDataSource` custom property: `true`
- `commitOrRollbackOnCleanup` custom property: `rollback`

The data source is deliberately non-transactional because the legacy EJB 2.1
code explicitly controls its local JDBC transactions with
`setAutoCommit(false)`, `commit()`, and `rollback()`.

The EJBs resolve the data source through the portable component-environment
lookup `java:comp/env/jdbc/PaskinoMercatoDS`. The supplied wsadmin script creates
or updates the server-scoped provider and data source. Restart the target server
when the JDBC configuration changes so WebSphere loads the driver class path and
publishes the JNDI resource.

At application startup, `DatabaseStartupListener` invokes
`DatabaseInitializerBean.populateDatabase()`. The method creates the `mercato`
schema and loads 10 categories and 145 products from the bundled SQL files.
Initialization is idempotent within one application lifecycle.

The database is intentionally volatile. Customers, carts, and orders disappear
when the application server stops or the application is redeployed.

## JavaMail session

In the WebSphere Admin Console, configure:

- Resources → Mail → Mail Sessions → New
- JNDI name: `mail/MercatoMail`
- Mail transport host/user/from: values for your SMTP server

The supplied wsadmin script can create or update this session.

## Build

```bash
mvn clean package
```

Deploy this artifact:

```text
paskinomercato-ear/target/paskinomercato-ear-1.0.0.ear
```

The EAR must contain:

- `paskinomercato-ejb.jar`
- `paskinomercato.war`

The EAR must not contain the H2 driver; WebSphere loads it from the JDBC
provider class path.

## Automated deployment

Set the target values, then run wsadmin:

```bash
export PASKINO_EAR_PATH=/opt/deploy/paskinomercato-ear-1.0.0.ear
export PASKINO_NODE_NAME=paskinoNode1
export PASKINO_SERVER_NAME=server1
export PASKINO_H2_DRIVER_PATH=/opt/was-drivers/h2-2.2.224.jar
export PASKINO_MAIL_PASSWORD='smtp-password'

$WAS_HOME/bin/wsadmin.sh -lang jython \
  -f /opt/deploy/install_paskinomercato.py
```

If the script creates or changes the JDBC provider or data source, restart the
target application server. On the next run, the script can test the unchanged
configuration path and start the application directly.

On successful startup, the WebSphere log contains:

```text
PaskinoMercato H2 database initialized with 145 products
```

## URLs

- Application: `http://localhost:9080/paskinomercato/`
- SOAP endpoint: `http://localhost:9080/paskinomercato/MercatoService`
- WSDL: `http://localhost:9080/paskinomercato/MercatoService?wsdl`

All prices are in Euro. Delivery addresses are restricted to Italy by
application validation and database constraints.
