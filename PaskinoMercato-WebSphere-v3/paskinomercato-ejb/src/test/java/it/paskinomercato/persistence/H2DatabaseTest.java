package it.paskinomercato.persistence;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.OperationNotSupportedException;
import javax.naming.spi.InitialContextFactory;
import javax.sql.DataSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class H2DatabaseTest {

    private static final DataSource TEST_DATA_SOURCE = createDataSource();
    private static String previousInitialContextFactory;

    @BeforeClass
    public static void bindDataSource() {
        previousInitialContextFactory = System.getProperty(
            Context.INITIAL_CONTEXT_FACTORY);
        System.setProperty(
            Context.INITIAL_CONTEXT_FACTORY,
            TestInitialContextFactory.class.getName());
    }

    @AfterClass
    public static void restoreInitialContextFactory() {
        if (previousInitialContextFactory == null) {
            System.clearProperty(Context.INITIAL_CONTEXT_FACTORY);
        } else {
            System.setProperty(
                Context.INITIAL_CONTEXT_FACTORY,
                previousInitialContextFactory);
        }
    }

    @Test
    public void populatesOnceAndSupportsGeneratedKeys() throws Exception {
        assertEquals(145, H2Database.populateDatabase());
        assertEquals(145, H2Database.populateDatabase());

        Connection connection = H2Database.getConnection();
        try {
            assertEquals(10, countRows(connection, "mercato.categoria"));

            PreparedStatement statement = connection.prepareStatement(
                "INSERT INTO mercato.cliente " +
                "(email, password_hash, nome, cognome, lingua) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS);
            try {
                statement.setString(1, "smoke@example.test");
                statement.setString(2, "hash");
                statement.setString(3, "Smoke");
                statement.setString(4, "Test");
                statement.setString(5, "it");
                assertEquals(1, statement.executeUpdate());

                ResultSet keys = statement.getGeneratedKeys();
                try {
                    assertTrue(keys.next());
                    assertTrue(keys.getInt(1) > 0);
                } finally {
                    keys.close();
                }
            } finally {
                statement.close();
            }
        } finally {
            connection.close();
        }
    }

    private int countRows(Connection connection, String table) throws Exception {
        Statement statement = connection.createStatement();
        try {
            ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);
            try {
                resultSet.next();
                return resultSet.getInt(1);
            } finally {
                resultSet.close();
            }
        } finally {
            statement.close();
        }
    }

    private static JdbcDataSource createDataSource() {
        JdbcDataSource result = new JdbcDataSource();
        result.setURL(
            "jdbc:h2:mem:paskinomercato-test;MODE=PostgreSQL;" +
            "DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        result.setUser("sa");
        result.setPassword("");
        return result;
    }

    public static final class TestInitialContextFactory
            implements InitialContextFactory {

        public Context getInitialContext(Hashtable<?, ?> environment) {
            return (Context) Proxy.newProxyInstance(
                H2DatabaseTest.class.getClassLoader(),
                new Class<?>[] {Context.class},
                new TestContextHandler());
        }
    }

    private static final class TestContextHandler implements InvocationHandler {

        public Object invoke(Object proxy, Method method, Object[] arguments)
                throws Throwable {
            String methodName = method.getName();

            if ("lookup".equals(methodName) && arguments != null
                    && arguments.length == 1) {
                String name = arguments[0] instanceof Name
                    ? arguments[0].toString()
                    : String.valueOf(arguments[0]);
                if (H2Database.DATA_SOURCE_JNDI_NAME.equals(name)) {
                    return TEST_DATA_SOURCE;
                }
                throw new NamingException("Name not bound in test: " + name);
            }
            if ("close".equals(methodName)) {
                return null;
            }
            if ("getEnvironment".equals(methodName)) {
                return new Hashtable<Object, Object>();
            }
            if ("getNameInNamespace".equals(methodName)) {
                return "";
            }
            if ("toString".equals(methodName)) {
                return "PaskinoMercato test naming context";
            }

            throw new OperationNotSupportedException(
                "Test context does not implement " + methodName);
        }
    }
}
