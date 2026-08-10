###############################################################################
# PaskinoMercato - WebSphere 8.5.5 wsadmin installation script
# Language: Jython (compatible with the older Jython used by WAS 8.5.5)
#
# Run:
#   wsadmin.sh -lang jython -f /absolute/path/install_paskinomercato.py
#
# Important:
#   Review every value in USER CONFIGURATION before running this script.
###############################################################################

import os
import sys
import time
import traceback

# =============================================================================
# USER CONFIGURATION
# =============================================================================

# This path is local to the machine on which the wsadmin client is running.
EAR_PATH = "/opt/deploy/paskinomercato-ear-1.0.0.ear"

APP_NAME = "PaskinoMercato"
CONTEXT_ROOT = "/paskinomercato"

# Values taken from the target shown in your wsadmin output.
NODE_NAME = "node1"
SERVER_NAME = "server1"

JDBC_PROVIDER_NAME = "PostgreSQL JDBC Driver"
DATA_SOURCE_NAME = "PaskinoMercato DB"
DATA_SOURCE_JNDI = "jdbc/MercatoDB"
J2C_ALIAS = "MercatoDBAlias"

# This path must be visible to the target WebSphere application-server JVM.
POSTGRES_JAR = "/opt/jdbc/postgresql-42.7.13.jar"

DB_HOST = "localhost"
DB_PORT = "5432"
DB_NAME = "mercatodb"
DB_USER = "mercato"
DB_PASSWORD = "changeme"

MAIL_PROVIDER_NAME = "Built-in Mail Provider"
MAIL_SESSION_NAME = "PaskinoMercato Mail"
MAIL_SESSION_JNDI = "mail/MercatoMail"
MAIL_HOST = "smtp.paskinomercato.it"
MAIL_PORT = "587"
MAIL_USER = "noreply@paskinomercato.it"
MAIL_PASSWORD = "changeme"
MAIL_FROM = "noreply@paskinomercato.it"

VIRTUAL_HOST = "default_host"

# Number of seconds to wait for application deployment expansion before start.
APP_READY_TIMEOUT = 120

# =============================================================================
# GENERIC HELPERS
# =============================================================================

def log(message):
    print "[PaskinoMercato] " + toString(message)


def warn(message):
    print "[WARNING] " + toString(message)


def error(message):
    print "[ERROR] " + toString(message)


def toString(value):
    """Return a Jython string without using string membership operations."""
    if value is None:
        return ""
    return "%s" % value


def firstConfigId(value):
    """Return the first non-empty configuration ID in a wsadmin result."""
    text = toString(value)
    for line in text.split("\n"):
        item = toString(line).strip()
        if item != "":
            return item
    return ""


def configIds(value):
    """Convert newline-delimited wsadmin output to a Python list."""
    result = []
    text = toString(value)
    for line in text.split("\n"):
        item = toString(line).strip()
        if item != "":
            result.append(item)
    return result


def findByAttribute(configType, parentId, attributeName, expectedValue):
    """Find an exact child object match; never uses 'substring in string'."""
    objects = AdminConfig.list(configType, parentId)
    for objectId in configIds(objects):
        actualValue = toString(
            AdminConfig.showAttribute(objectId, attributeName)
        ).strip()
        if actualValue == expectedValue:
            return objectId
    return ""


def requireConfigId(configId, description):
    value = firstConfigId(configId)
    if value == "":
        raise Exception("Configuration object not found: " + description)
    return value


def getCellName():
    cellName = toString(AdminControl.getCell()).strip()
    if cellName != "":
        return cellName

    cellId = firstConfigId(AdminConfig.list("Cell"))
    if cellId == "":
        raise Exception("Unable to determine the WebSphere cell name")
    return toString(AdminConfig.showAttribute(cellId, "name")).strip()


def getCellId():
    return requireConfigId(
        AdminConfig.getid("/Cell:" + getCellName() + "/"),
        "cell " + getCellName()
    )


def getNodeId():
    return requireConfigId(
        AdminConfig.getid(
            "/Cell:" + getCellName() + "/Node:" + NODE_NAME + "/"
        ),
        "node " + NODE_NAME
    )


def getServerId():
    return requireConfigId(
        AdminConfig.getid(
            "/Cell:" + getCellName() +
            "/Node:" + NODE_NAME +
            "/Server:" + SERVER_NAME + "/"
        ),
        "server " + NODE_NAME + "/" + SERVER_NAME
    )


def getServerScopePath():
    return (
        "/Cell:" + getCellName() +
        "/Node:" + NODE_NAME +
        "/Server:" + SERVER_NAME + "/"
    )


def validateConfiguration():
    log("Validating configuration...")

    if not os.path.isfile(EAR_PATH):
        raise Exception("EAR file does not exist: " + EAR_PATH)

    getCellId()
    getNodeId()
    getServerId()

    virtualHostId = firstConfigId(
        AdminConfig.getid(
            "/Cell:" + getCellName() +
            "/VirtualHost:" + VIRTUAL_HOST + "/"
        )
    )
    if virtualHostId == "":
        raise Exception("Virtual host does not exist: " + VIRTUAL_HOST)

    if DB_PASSWORD == "changeme":
        warn("DB_PASSWORD is still set to the example value 'changeme'.")
    if MAIL_PASSWORD == "changeme":
        warn("MAIL_PASSWORD is still set to the example value 'changeme'.")

    log("  Cell:   " + getCellName())
    log("  Target: " + NODE_NAME + "/" + SERVER_NAME)
    log("  EAR:    " + EAR_PATH)
    log("Configuration validated.")


# =============================================================================
# JDBC PROVIDER
# =============================================================================

def createOrUpdateJdbcProvider():
    log("Step 1: Creating or updating PostgreSQL JDBC Provider...")

    scopeId = getServerId()
    providerId = findByAttribute(
        "JDBCProvider", scopeId, "name", JDBC_PROVIDER_NAME
    )

    providerAttrs = [
        ["description", "PostgreSQL JDBC driver for PaskinoMercato"],
        ["implementationClassName",
         "org.postgresql.ds.PGConnectionPoolDataSource"],
        ["classpath", POSTGRES_JAR],
        ["providerType", "User-defined JDBC Provider"],
        ["xa", "false"]
    ]

    if providerId != "":
        AdminConfig.modify(providerId, providerAttrs)
        log("  Updated JDBC Provider: " + JDBC_PROVIDER_NAME)
        return providerId

    createAttrs = [["name", JDBC_PROVIDER_NAME]] + providerAttrs
    providerId = AdminConfig.create("JDBCProvider", scopeId, createAttrs)
    providerId = requireConfigId(providerId, JDBC_PROVIDER_NAME)
    log("  Created JDBC Provider: " + providerId)
    return providerId


# =============================================================================
# J2C AUTHENTICATION AND DATA SOURCE
# =============================================================================

def createOrUpdateJ2CAlias():
    log("  Creating or updating J2C authentication alias: " + J2C_ALIAS)

    securityId = requireConfigId(
        AdminConfig.getid("/Security:/"), "global security configuration"
    )
    aliasId = findByAttribute("JAASAuthData", securityId, "alias", J2C_ALIAS)

    attrs = [
        ["userId", DB_USER],
        ["password", DB_PASSWORD],
        ["description", "PaskinoMercato PostgreSQL credentials"]
    ]

    if aliasId != "":
        AdminConfig.modify(aliasId, attrs)
        log("  Updated J2C authentication alias.")
        return aliasId

    createAttrs = [["alias", J2C_ALIAS]] + attrs
    aliasId = AdminConfig.create("JAASAuthData", securityId, createAttrs)
    aliasId = requireConfigId(aliasId, J2C_ALIAS)
    log("  Created J2C authentication alias.")
    return aliasId


def getOrCreatePropertySet(resourceId):
    propertySetId = firstConfigId(
        AdminConfig.showAttribute(resourceId, "propertySet")
    )
    if propertySetId == "":
        propertySetId = firstConfigId(
            AdminConfig.create("J2EEResourcePropertySet", resourceId, [])
        )
    return requireConfigId(propertySetId, "J2EE resource property set")


def setResourceProperty(propertySetId, name, value, propertyType):
    propertyId = findByAttribute(
        "J2EEResourceProperty", propertySetId, "name", name
    )
    attrs = [
        ["value", value],
        ["type", propertyType],
        ["required", "false"]
    ]

    if propertyId != "":
        AdminConfig.modify(propertyId, attrs)
        return propertyId

    createAttrs = [["name", name]] + attrs
    return AdminConfig.create(
        "J2EEResourceProperty", propertySetId, createAttrs
    )


def configureConnectionPool(dataSourceId):
    connectionPoolId = firstConfigId(
        AdminConfig.showAttribute(dataSourceId, "connectionPool")
    )

    if connectionPoolId == "":
        connectionPoolId = firstConfigId(
            AdminConfig.list("ConnectionPool", dataSourceId)
        )

    if connectionPoolId == "":
        warn("No ConnectionPool object was found for the DataSource.")
        return

    AdminConfig.modify(connectionPoolId, [
        ["minConnections", "2"],
        ["maxConnections", "20"],
        ["connectionTimeout", "180"],
        ["agedTimeout", "0"],
        ["reapTime", "180"],
        ["unusedTimeout", "1800"]
    ])


def createOrUpdateDataSource(providerId):
    log("Step 2: Creating or updating DataSource " + DATA_SOURCE_JNDI + "...")

    createOrUpdateJ2CAlias()

    dataSourceId = findByAttribute(
        "DataSource", providerId, "jndiName", DATA_SOURCE_JNDI
    )

    dataSourceAttrs = [
        ["jndiName", DATA_SOURCE_JNDI],
        ["description", "PostgreSQL DataSource for PaskinoMercato"],
        ["authDataAlias", J2C_ALIAS],
        ["datasourceHelperClassname",
         "com.ibm.websphere.rsadapter.GenericDataStoreHelper"],
        ["statementCacheSize", "10"]
    ]

    if dataSourceId != "":
        AdminConfig.modify(dataSourceId, dataSourceAttrs)
        log("  Updated DataSource configuration.")
    else:
        createAttrs = [["name", DATA_SOURCE_NAME]] + dataSourceAttrs
        dataSourceId = AdminConfig.create(
            "DataSource", providerId, createAttrs
        )
        dataSourceId = requireConfigId(dataSourceId, DATA_SOURCE_NAME)
        log("  Created DataSource: " + dataSourceId)

    configureConnectionPool(dataSourceId)

    propertySetId = getOrCreatePropertySet(dataSourceId)
    setResourceProperty(
        propertySetId, "serverName", DB_HOST, "java.lang.String"
    )
    setResourceProperty(
        propertySetId, "portNumber", DB_PORT, "java.lang.Integer"
    )
    setResourceProperty(
        propertySetId, "databaseName", DB_NAME, "java.lang.String"
    )
    setResourceProperty(
        propertySetId, "ssl", "false", "java.lang.Boolean"
    )

    log("  DataSource custom properties and connection pool configured.")
    return dataSourceId


# =============================================================================
# JAVAMAIL
# =============================================================================

def findMailProvider():
    paths = [
        getServerScopePath() + "MailProvider:" + MAIL_PROVIDER_NAME + "/",
        "/Cell:" + getCellName() +
        "/Node:" + NODE_NAME +
        "/MailProvider:" + MAIL_PROVIDER_NAME + "/",
        "/Cell:" + getCellName() +
        "/MailProvider:" + MAIL_PROVIDER_NAME + "/"
    ]

    for path in paths:
        providerId = firstConfigId(AdminConfig.getid(path))
        if providerId != "":
            return providerId

    return ""


def findSmtpProtocolProvider(mailProviderId):
    try:
        providers = AdminConfig.list("ProtocolProvider", mailProviderId)
        for providerId in configIds(providers):
            protocol = toString(
                AdminConfig.showAttribute(providerId, "protocol")
            ).strip()
            providerType = toString(
                AdminConfig.showAttribute(providerId, "type")
            ).strip()
            if protocol == "smtp" and providerType == "TRANSPORT":
                return providerId
    except Exception, e:
        warn("Unable to inspect SMTP protocol providers: " + toString(e))
    return ""


def createOrUpdateMailSession():
    log("Step 3: Creating or updating JavaMail Session " +
        MAIL_SESSION_JNDI + "...")

    mailProviderId = findMailProvider()
    if mailProviderId == "":
        raise Exception(
            "Mail provider not found: " + MAIL_PROVIDER_NAME +
            ". Create it at cell, node, or server scope before running " +
            "this script."
        )

    mailSessionId = findByAttribute(
        "MailSession", mailProviderId, "jndiName", MAIL_SESSION_JNDI
    )
    smtpProtocolId = findSmtpProtocolProvider(mailProviderId)

    attrs = [
        ["jndiName", MAIL_SESSION_JNDI],
        ["description", "JavaMail session for PaskinoMercato"],
        ["mailTransportHost", MAIL_HOST],
        ["mailTransportPort", MAIL_PORT],
        ["mailTransportUser", MAIL_USER],
        ["mailTransportPassword", MAIL_PASSWORD],
        ["mailFrom", MAIL_FROM],
        ["debug", "false"],
        ["strict", "true"]
    ]

    if smtpProtocolId != "":
        attrs.append(["mailTransportProtocol", smtpProtocolId])
    else:
        warn("Built-in SMTP ProtocolProvider was not found; using the " +
             "mail.transport.protocol custom property.")

    if mailSessionId != "":
        AdminConfig.modify(mailSessionId, attrs)
        log("  Updated JavaMail Session.")
    else:
        createAttrs = [["name", MAIL_SESSION_NAME]] + attrs
        mailSessionId = AdminConfig.create(
            "MailSession", mailProviderId, createAttrs
        )
        mailSessionId = requireConfigId(mailSessionId, MAIL_SESSION_NAME)
        log("  Created JavaMail Session: " + mailSessionId)

    propertySetId = getOrCreatePropertySet(mailSessionId)
    setResourceProperty(
        propertySetId, "mail.transport.protocol", "smtp", "java.lang.String"
    )
    setResourceProperty(
        propertySetId, "mail.smtp.auth", "true", "java.lang.Boolean"
    )
    setResourceProperty(
        propertySetId,
        "mail.smtp.starttls.enable",
        "true",
        "java.lang.Boolean"
    )

    log("  JavaMail SMTP properties configured.")
    return mailSessionId


# =============================================================================
# APPLICATION INSTALLATION
# =============================================================================

def applicationIsInstalled():
    applications = configIds(AdminApp.list())
    for applicationName in applications:
        if applicationName == APP_NAME:
            return 1
    return 0


def getDeploymentTarget():
    return (
        "WebSphere:cell=" + getCellName() +
        ",node=" + NODE_NAME +
        ",server=" + SERVER_NAME
    )


def getApplicationMappings():
    return [
        "-MapModulesToServers",
        [[".*", ".*", getDeploymentTarget()]],
        "-MapWebModToVH",
        [[".*", ".*", VIRTUAL_HOST]],
        "-CtxRootForWebMod",
        [[".*", ".*", CONTEXT_ROOT]]
    ]


def installOrUpdateApplication():
    log("Step 4: Installing or updating application from " + EAR_PATH + "...")

    if applicationIsInstalled():
        AdminApp.update(APP_NAME, "app", [
            "-operation", "update",
            "-contents", EAR_PATH
        ])
        AdminApp.edit(APP_NAME, getApplicationMappings())
        log("  Application content and mappings updated.")
        return

    options = [
        "-appname", APP_NAME,
        "-usedefaultbindings"
    ] + getApplicationMappings()

    AdminApp.install(EAR_PATH, options)
    log("  Application installed with default resource bindings.")


# =============================================================================
# SAVE, NODE SYNCHRONIZATION, AND START
# =============================================================================

def saveAndSync():
    log("Step 5: Saving configuration and synchronizing the target node...")
    AdminConfig.save()
    log("  Configuration saved.")

    nodeSyncs = AdminControl.queryNames(
        "type=NodeSync,node=" + NODE_NAME + ",*"
    )
    nodeSyncId = firstConfigId(nodeSyncs)

    if nodeSyncId == "":
        warn("NodeSync MBean not found. This is normal for a stand-alone " +
             "profile; otherwise ensure the node agent is running.")
        return

    AdminControl.invoke(nodeSyncId, "sync")
    log("  Node synchronized: " + NODE_NAME)


def waitForApplicationReady():
    waited = 0
    while waited < APP_READY_TIMEOUT:
        ready = toString(AdminApp.isAppReady(APP_NAME)).strip()
        if ready == "true":
            return 1
        time.sleep(5)
        waited = waited + 5
    return 0


def startApplication():
    log("Step 6: Starting application " + APP_NAME + "...")

    if not waitForApplicationReady():
        warn("Application did not report ready within " +
             toString(APP_READY_TIMEOUT) + " seconds.")

    applicationManagers = AdminControl.queryNames(
        "cell=" + getCellName() +
        ",node=" + NODE_NAME +
        ",type=ApplicationManager" +
        ",process=" + SERVER_NAME + ",*"
    )
    applicationManagerId = firstConfigId(applicationManagers)

    if applicationManagerId == "":
        warn("ApplicationManager MBean not found. Ensure server " +
             SERVER_NAME + " is running, then start " + APP_NAME +
             " manually.")
        return

    try:
        AdminControl.invoke(
            applicationManagerId, "startApplication", APP_NAME
        )
        log("  Application started: " + APP_NAME)
    except Exception, e:
        # An already-running application can also produce an exception here.
        warn("Application start returned: " + toString(e))


# =============================================================================
# MAIN
# =============================================================================

def main():
    log("============================================================")
    log("  PaskinoMercato - WebSphere Installation Script")
    log("  Script revision: " + SCRIPT_REVISION)
    log("  Target: " + NODE_NAME + "/" + SERVER_NAME)
    log("  EAR:    " + EAR_PATH)
    log("============================================================")

    try:
        validateConfiguration()
        providerId = createOrUpdateJdbcProvider()
        createOrUpdateDataSource(providerId)
        createOrUpdateMailSession()
        installOrUpdateApplication()
        saveAndSync()
        startApplication()

        log("============================================================")
        log("  INSTALLATION COMPLETE")
        log("  Application URL: http://<host>:9080" +
            CONTEXT_ROOT + "/")
        log("============================================================")

    except:
        traceback.print_exc()
        exceptionInfo = sys.exc_info()
        exceptionMessage = toString(exceptionInfo[1])

        # Discard unsaved changes from this wsadmin configuration session.
        try:
            AdminConfig.reset()
            warn("Unsaved configuration changes were discarded.")
        except:
            pass

        error("Installation failed: " + exceptionMessage)
        sys.exit(1)


main()
