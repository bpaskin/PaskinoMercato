###############################################################################
# PaskinoMercato - WebSphere 8.5.5 wsadmin installation script
# Language: Jython (compatible with the older Jython used by WAS 8.5.5)
#
# Run:
#   wsadmin.sh -lang jython -f /absolute/path/install_paskinomercato.py
#
# What this script configures:
#   1. H2 JDBC provider and DataSource (jdbc/PaskinoMercatoDS)
#   2. JavaMail Session (mail/MercatoMail)
#   3. EAR installation / update — includes all Session EJBs and the four
#      BMP Entity EJBs (ProdottoEntityBean, CategoriaEntityBean,
#      ClienteEntityBean, OrdineEntityBean), and maps the EJB 2.1 local
#      references to the ejblocal: namespace.
#   4. Configuration save + node sync
#   5. Application start (or a restart notice when JDBC configuration changed)
#
# Important:
#   Review every value in USER CONFIGURATION before running this script.
###############################################################################

import os
import sys
import time
import traceback
import zipfile
from StringIO import StringIO

# =============================================================================
# USER CONFIGURATION
# =============================================================================

# Every value can be overridden without editing this file. For example:
#   export PASKINO_EAR_PATH=/opt/deploy/paskinomercato-ear-1.0.0.ear

# This path is local to the machine on which the wsadmin client is running.
EAR_PATH = os.environ.get(
    "PASKINO_EAR_PATH",
    "/opt/deploy/paskinomercato-ear-1.0.0.ear"
)

APP_NAME = os.environ.get("PASKINO_APP_NAME", "PaskinoMercato")
CONTEXT_ROOT = os.environ.get("PASKINO_CONTEXT_ROOT", "/paskinomercato")

# Defaults match the current target. Override them for another profile.
NODE_NAME = os.environ.get("PASKINO_NODE_NAME", "paskinoNode1")
SERVER_NAME = os.environ.get("PASKINO_SERVER_NAME", "brian1")

H2_DRIVER_PATH = os.environ.get(
    "PASKINO_H2_DRIVER_PATH", "/opt/was-drivers/h2-2.2.224.jar"
)
H2_JDBC_PROVIDER_NAME = os.environ.get(
    "PASKINO_H2_JDBC_PROVIDER_NAME", "PaskinoMercato H2 JDBC Provider"
)
H2_DATA_SOURCE_NAME = os.environ.get(
    "PASKINO_H2_DATA_SOURCE_NAME", "PaskinoMercato H2 DataSource"
)
H2_DATA_SOURCE_JNDI = "jdbc/PaskinoMercatoDS"
H2_JDBC_URL = os.environ.get(
    "PASKINO_H2_JDBC_URL",
    "jdbc:h2:mem:paskinomercato;MODE=PostgreSQL;" +
    "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE"
)
H2_USER = os.environ.get("PASKINO_H2_USER", "sa")
H2_PASSWORD = os.environ.get("PASKINO_H2_PASSWORD", "")

MAIL_PROVIDER_NAME = os.environ.get(
    "PASKINO_MAIL_PROVIDER_NAME", "Built-in Mail Provider"
)
MAIL_SESSION_NAME = os.environ.get(
    "PASKINO_MAIL_SESSION_NAME", "PaskinoMercato Mail"
)
MAIL_SESSION_JNDI = os.environ.get(
    "PASKINO_MAIL_SESSION_JNDI", "mail/MercatoMail"
)
MAIL_HOST = os.environ.get(
    "PASKINO_MAIL_HOST", "smtp.paskinomercato.it"
)
MAIL_PORT = os.environ.get("PASKINO_MAIL_PORT", "587")
MAIL_USER = os.environ.get(
    "PASKINO_MAIL_USER", "noreply@paskinomercato.it"
)
MAIL_PASSWORD = os.environ.get("PASKINO_MAIL_PASSWORD", "changeme")
MAIL_FROM = os.environ.get(
    "PASKINO_MAIL_FROM", "noreply@paskinomercato.it"
)

VIRTUAL_HOST = os.environ.get("PASKINO_VIRTUAL_HOST", "default_host")

# Number of seconds to wait for application deployment expansion before start.
APP_READY_TIMEOUT = 120

# =============================================================================
# END OF USER CONFIGURATION
# =============================================================================

SCRIPT_REVISION = "1.4.0"

EJB_MODULE_NAME = "PaskinoMercato EJB Module"
EJB_MODULE_FILE = "paskinomercato-ejb.jar"
EJB_MODULE_URI = EJB_MODULE_FILE + ",META-INF/ejb-jar.xml"
WEB_MODULE_FILE = "paskinomercato.war"

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


def requireNonEmpty(settingName, value):
    if toString(value).strip() == "":
        raise Exception("Configuration value is empty: " + settingName)


def validateEarContents():
    """Reject stale or incorrectly assembled EAR files before configuration.

    Jython 2.1 (WAS 8.5.5) does not support try/except/finally in a single
    block. The cleanup is handled by a nested try/finally inside the except.
    """
    ear = None
    ejbJar = None
    webArchive = None
    try:
        try:
            ear = zipfile.ZipFile(EAR_PATH, "r")
            earEntries = ear.namelist()
            if EJB_MODULE_FILE not in earEntries:
                raise Exception(
                    "EAR does not contain expected module: " + EJB_MODULE_FILE
                )
            if WEB_MODULE_FILE not in earEntries:
                raise Exception(
                    "EAR does not contain expected module: " + WEB_MODULE_FILE
                )

            for entryName in [
                "META-INF/application.xml",
                "META-INF/ibm-application-bnd.xmi"
            ]:
                if entryName not in earEntries:
                    raise Exception(
                        "EAR does not contain expected descriptor: " +
                        entryName
                    )

            applicationDescriptor = ear.read("META-INF/application.xml")
            applicationBinding = ear.read(
                "META-INF/ibm-application-bnd.xmi"
            )
            if applicationDescriptor.find("Application_ID") == -1:
                raise Exception("EAR application.xml is missing Application_ID")
            if applicationBinding.find(
                    "META-INF/application.xml#Application_ID") == -1:
                raise Exception(
                    "EAR application binding is not linked to application.xml"
                )

            ejbBytes = ear.read(EJB_MODULE_FILE)
            ejbJar = zipfile.ZipFile(StringIO(ejbBytes), "r")
            ejbEntries = ejbJar.namelist()

            for entryName in [
                "META-INF/ejb-jar.xml",
                "META-INF/ibm-ejb-jar-bnd.xmi"
            ]:
                if entryName not in ejbEntries:
                    raise Exception(
                        "EJB module does not contain expected descriptor: " +
                        entryName
                    )

            ejbDescriptor = ejbJar.read("META-INF/ejb-jar.xml")
            ejbBindingDescriptor = ejbJar.read(
                "META-INF/ibm-ejb-jar-bnd.xmi"
            )
            requiredEjbMarkers = [
                "EJBLocalRef_CatalogoProdottoEntity",
                "EJBLocalRef_CatalogoCategoriaEntity",
                "EJBLocalRef_OrdineEntity",
                "EJBLocalRef_ClienteEntity",
                "DatabaseInitializerBean",
                "ResourceRef_ProdottoJdbc",
                "ResourceRef_CategoriaJdbc",
                "ResourceRef_ClienteEntityJdbc",
                "ResourceRef_OrdineEntityJdbc",
                "ResourceRef_OrdineJdbc",
                "ResourceRef_ClienteJdbc",
                "ResourceRef_DatabaseInitializerJdbc",
                "ResourceRef_MailJdbc"
            ]
            for marker in requiredEjbMarkers:
                if ejbDescriptor.find(marker) == -1:
                    raise Exception(
                        "EAR is stale; missing EJB reference marker: " + marker
                    )

            if ejbBindingDescriptor.find("jdbc/PaskinoMercatoDS") == -1:
                raise Exception(
                    "EAR is stale; missing JDBC resource-reference binding"
                )

            for marker in requiredEjbMarkers[5:]:
                if ejbBindingDescriptor.find("#" + marker) == -1:
                    raise Exception(
                        "EAR is stale; EJB binding does not reference: " +
                        marker
                    )

            webBytes = ear.read(WEB_MODULE_FILE)
            webArchive = zipfile.ZipFile(StringIO(webBytes), "r")
            webEntries = webArchive.namelist()
            for entryName in [
                "WEB-INF/web.xml",
                "WEB-INF/ibm-web-bnd.xmi",
                "WEB-INF/ibm-web-ext.xmi"
            ]:
                if entryName not in webEntries:
                    raise Exception(
                        "Web module does not contain expected descriptor: " +
                        entryName
                    )

            webDescriptor = webArchive.read("WEB-INF/web.xml")
            webBinding = webArchive.read("WEB-INF/ibm-web-bnd.xmi")
            webExtension = webArchive.read("WEB-INF/ibm-web-ext.xmi")
            webReferenceMarkers = [
                "EJBLocalRef_Web_Catalogo",
                "EJBLocalRef_Web_Ordine",
                "EJBLocalRef_Web_Cliente",
                "EJBLocalRef_Web_Carrello",
                "EJBLocalRef_Web_Mail",
                "EJBLocalRef_Web_DatabaseInitializer"
            ]
            if webDescriptor.find("WebApp_ID") == -1:
                raise Exception("Web descriptor is missing WebApp_ID")
            for marker in webReferenceMarkers:
                if webDescriptor.find(marker) == -1:
                    raise Exception(
                        "Web descriptor is missing EJB reference: " + marker
                    )
                if webBinding.find("#" + marker) == -1:
                    raise Exception(
                        "Web binding does not reference: " + marker
                    )
            for componentName in [
                "CatalogoBean",
                "OrdineBean",
                "ClienteBean",
                "CarrelloBean",
                "MailBean",
                "DatabaseInitializerBean"
            ]:
                ejbLink = "../" + EJB_MODULE_FILE + "#" + componentName
                if webDescriptor.find(ejbLink) == -1:
                    raise Exception(
                        "Web descriptor is missing EJB link: " + ejbLink
                    )
            for descriptor in [webBinding, webExtension]:
                if descriptor.find("WEB-INF/web.xml#WebApp_ID") == -1:
                    raise Exception(
                        "IBM web descriptor is not linked to web.xml"
                    )

            h2Found = 0
            for entryName in earEntries:
                if (entryName.startswith("lib/") and
                        entryName.find("h2") != -1 and
                        entryName.endswith(".jar")):
                    h2Found = 1
            if h2Found:
                raise Exception(
                    "EAR still contains H2; the driver must be configured " +
                    "through the WebSphere JDBC provider class path"
                )
        except Exception, e:
            raise Exception(
                "EAR validation failed for " + EAR_PATH + ": " + toString(e)
            )
    finally:
        if webArchive is not None:
            webArchive.close()
        if ejbJar is not None:
            ejbJar.close()
        if ear is not None:
            ear.close()


def validateConfiguration():
    log("Validating configuration...")

    if not os.path.isfile(EAR_PATH):
        raise Exception("EAR file does not exist: " + EAR_PATH)

    for setting in [
        ["APP_NAME", APP_NAME],
        ["CONTEXT_ROOT", CONTEXT_ROOT],
        ["NODE_NAME", NODE_NAME],
        ["SERVER_NAME", SERVER_NAME],
        ["H2_DRIVER_PATH", H2_DRIVER_PATH],
        ["H2_DATA_SOURCE_JNDI", H2_DATA_SOURCE_JNDI],
        ["MAIL_SESSION_JNDI", MAIL_SESSION_JNDI],
        ["VIRTUAL_HOST", VIRTUAL_HOST]
    ]:
        requireNonEmpty(setting[0], setting[1])

    if not CONTEXT_ROOT.startswith("/"):
        raise Exception("CONTEXT_ROOT must start with '/': " + CONTEXT_ROOT)
    validateEarContents()

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

    if MAIL_PASSWORD == "changeme":
        warn("MAIL_PASSWORD is still set to the example value 'changeme'.")
    if not os.path.isfile(H2_DRIVER_PATH):
        warn(
            "H2 driver is not visible to this wsadmin process at " +
            H2_DRIVER_PATH + ". Ensure the same absolute path exists on " +
            "the target node before restarting WebSphere."
        )

    log("  Cell:   " + getCellName())
    log("  Target: " + NODE_NAME + "/" + SERVER_NAME)
    log("  EAR:    " + EAR_PATH)
    log("  JDBC:   " + H2_DATA_SOURCE_JNDI)
    log("  H2 JAR: " + H2_DRIVER_PATH)
    log("  EJB mappings: " + toString(len(getEjbReferenceMappings())))
    log("Configuration validated.")


# =============================================================================
# GENERIC RESOURCE PROPERTIES
# =============================================================================

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


# =============================================================================
# H2 JDBC PROVIDER AND DATA SOURCE
# =============================================================================

def setResourcePropertyIfChanged(
        propertySetId, name, value, propertyType):
    propertyId = findByAttribute(
        "J2EEResourceProperty", propertySetId, "name", name
    )
    attrs = [
        ["value", value],
        ["type", propertyType],
        ["required", "false"]
    ]

    if propertyId == "":
        AdminConfig.create(
            "J2EEResourceProperty",
            propertySetId,
            [["name", name]] + attrs
        )
        return 1

    currentValue = toString(
        AdminConfig.showAttribute(propertyId, "value")
    )
    currentType = toString(
        AdminConfig.showAttribute(propertyId, "type")
    )
    if currentValue != value or currentType != propertyType:
        AdminConfig.modify(propertyId, attrs)
        return 1
    return 0


def createOrUpdateH2DataSource():
    log("Step 1: Creating or updating H2 DataSource " +
        H2_DATA_SOURCE_JNDI + "...")

    serverId = getServerId()
    changed = 0
    providerId = findByAttribute(
        "JDBCProvider", serverId, "name", H2_JDBC_PROVIDER_NAME
    )

    providerAttrs = [
        ["description", "H2 JDBC provider for PaskinoMercato"],
        ["implementationClassName", "org.h2.jdbcx.JdbcDataSource"],
        ["classpath", H2_DRIVER_PATH],
        ["xa", "false"]
    ]

    if providerId == "":
        providerId = AdminConfig.create(
            "JDBCProvider",
            serverId,
            [["name", H2_JDBC_PROVIDER_NAME]] + providerAttrs
        )
        providerId = requireConfigId(providerId, H2_JDBC_PROVIDER_NAME)
        changed = 1
        log("  Created JDBC provider: " + providerId)
    else:
        providerChanges = []
        implementationClass = toString(
            AdminConfig.showAttribute(
                providerId, "implementationClassName"
            )
        ).strip()
        classpath = toString(
            AdminConfig.showAttribute(providerId, "classpath")
        )
        xa = toString(
            AdminConfig.showAttribute(providerId, "xa")
        ).strip().lower()
        if implementationClass != "org.h2.jdbcx.JdbcDataSource":
            providerChanges.append([
                "implementationClassName", "org.h2.jdbcx.JdbcDataSource"
            ])
        if classpath.find(H2_DRIVER_PATH) == -1:
            AdminConfig.unsetAttributes(providerId, ["classpath"])
            providerChanges.append(["classpath", H2_DRIVER_PATH])
        if xa != "false":
            providerChanges.append(["xa", "false"])
        if len(providerChanges) > 0:
            AdminConfig.modify(providerId, providerChanges)
            changed = 1
            log("  Updated JDBC provider implementation or class path.")
        else:
            log("  JDBC provider already configured.")

    dataSourceId = findByAttribute(
        "DataSource", providerId, "jndiName", H2_DATA_SOURCE_JNDI
    )
    if dataSourceId == "":
        dataSourceId = AdminConfig.create(
            "DataSource",
            providerId,
            [
                ["name", H2_DATA_SOURCE_NAME],
                ["jndiName", H2_DATA_SOURCE_JNDI],
                ["description", "H2 in-memory DataSource for PaskinoMercato"],
                ["statementCacheSize", "20"],
                [
                    "datasourceHelperClassname",
                    "com.ibm.websphere.rsadapter.GenericDataStoreHelper"
                ]
            ]
        )
        dataSourceId = requireConfigId(
            dataSourceId, H2_DATA_SOURCE_NAME
        )
        changed = 1
        log("  Created DataSource: " + dataSourceId)
    else:
        log("  DataSource already configured.")

    propertySetId = getOrCreatePropertySet(dataSourceId)
    if setResourcePropertyIfChanged(
            propertySetId, "URL", H2_JDBC_URL, "java.lang.String"):
        changed = 1
    if setResourcePropertyIfChanged(
            propertySetId, "user", H2_USER, "java.lang.String"):
        changed = 1
    if setResourcePropertyIfChanged(
            propertySetId, "password", H2_PASSWORD, "java.lang.String"):
        changed = 1
    if setResourcePropertyIfChanged(
            propertySetId,
            "nonTransactionalDataSource",
            "true",
            "java.lang.Boolean"):
        changed = 1
    if setResourcePropertyIfChanged(
            propertySetId,
            "commitOrRollbackOnCleanup",
            "rollback",
            "java.lang.String"):
        changed = 1

    log("  H2 URL: " + H2_JDBC_URL)
    return changed


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
    log("Step 2: Creating or updating JavaMail Session " +
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


def getEjbReferenceMappings():
    """Map EJB 2.1 local references to their target local interfaces.

    WebSphere's MapEJBRefToEJB task accepts exactly five fields per row:
      [module-name, bean-name, module-uri, ejb-ref-name, target-local-interface]

    The target-local-interface must be the EJBLocalObject interface, not the
    LocalHome, and no ejblocal: JNDI URI should be included here.
    """
    return [
        [
            EJB_MODULE_NAME,
            "CatalogoBean",
            EJB_MODULE_URI,
            "ejb/ProdottoEntityBean",
            "it.paskinomercato.ejb.entity.prodotto.ProdottoEntityLocal"
        ],
        [
            EJB_MODULE_NAME,
            "CatalogoBean",
            EJB_MODULE_URI,
            "ejb/CategoriaEntityBean",
            "it.paskinomercato.ejb.entity.categoria.CategoriaEntityLocal"
        ],
        [
            EJB_MODULE_NAME,
            "OrdineBean",
            EJB_MODULE_URI,
            "ejb/OrdineEntityBean",
            "it.paskinomercato.ejb.entity.ordine.OrdineEntityLocal"
        ],
        [
            EJB_MODULE_NAME,
            "ClienteBean",
            EJB_MODULE_URI,
            "ejb/ClienteEntityBean",
            "it.paskinomercato.ejb.entity.cliente.ClienteEntityLocal"
        ]
    ]


def printInstallTaskInfo(taskName):
    """Print task rows after a deployment validation failure."""
    try:
        warn("AdminApp task information for " + taskName + ":")
        print AdminApp.taskInfo(EAR_PATH, taskName)
    except Exception, e:
        warn(
            "Unable to read task information for " + taskName + ": " +
            toString(e)
        )


def getApplicationMappings():
    # WebSphere 8.5.5 requires explicit task data for references originating in
    # this EJB 2.1 module, even when ejb-link is present in ejb-jar.xml.
    return [
        "-MapModulesToServers",
        [[".*", ".*", getDeploymentTarget()]],
        "-MapWebModToVH",
        [[".*", ".*", VIRTUAL_HOST]],
        "-CtxRootForWebMod",
        [[".*", ".*", CONTEXT_ROOT]],
        "-MapEJBRefToEJB",
        getEjbReferenceMappings()
    ]


def installOrUpdateApplication():
    log("Step 3: Installing or updating application from " + EAR_PATH + "...")

    try:
        if applicationIsInstalled():
            log("  Existing application detected; performing EAR update.")
            updateOptions = [
                "-operation", "update",
                "-contents", EAR_PATH,
                "-MapEJBRefToEJB", getEjbReferenceMappings()
            ]
            # Mapping data must be passed to update itself. AdminApp validates
            # the new descriptors before a later AdminApp.edit can run.
            AdminApp.update(APP_NAME, "app", updateOptions)
            AdminApp.edit(APP_NAME, getApplicationMappings())
            log("  Application content and mappings updated.")
            return

        log("  Application is not installed; performing fresh installation.")
        options = [
            "-appname", APP_NAME,
            "-usedefaultbindings"
        ] + getApplicationMappings()

        AdminApp.install(EAR_PATH, options)
        log("  Application installed with explicit EJB bindings.")
    except:
        printInstallTaskInfo("MapEJBRefToEJB")
        printInstallTaskInfo("MapResRefToEJB")
        raise


# =============================================================================
# SAVE, NODE SYNCHRONIZATION, AND START
# =============================================================================

def saveAndSync():
    log("Step 4: Saving configuration and synchronizing the target node...")
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
    log("Step 5: Starting application " + APP_NAME + "...")

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
        jdbcConfigurationChanged = createOrUpdateH2DataSource()
        createOrUpdateMailSession()
        installOrUpdateApplication()
        saveAndSync()
        if jdbcConfigurationChanged:
            warn(
                "The JDBC provider or DataSource changed. Restart server " +
                NODE_NAME + "/" + SERVER_NAME +
                " so WebSphere loads the H2 driver and publishes the JNDI " +
                "resource. The application will start during server startup."
            )
        else:
            startApplication()

        log("============================================================")
        log("  INSTALLATION CONFIGURATION COMPLETE")
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
