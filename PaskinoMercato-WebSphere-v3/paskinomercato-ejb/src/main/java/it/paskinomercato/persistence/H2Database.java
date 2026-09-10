package it.paskinomercato.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Shared access point for the WebSphere-managed H2 in-memory database.
 */
public final class H2Database {

    public static final String DATA_SOURCE_JNDI_NAME =
        "jdbc/PaskinoMercatoDS";

    private static volatile DataSource dataSource;

    private H2Database() {}

    public static Connection getConnection() throws SQLException {
        try {
            return getDataSource().getConnection();
        } catch (NamingException e) {
            SQLException sqlException = new SQLException(
                "Unable to look up DataSource " + DATA_SOURCE_JNDI_NAME);
            sqlException.initCause(e);
            throw sqlException;
        }
    }

    private static DataSource getDataSource() throws NamingException {
        DataSource current = dataSource;
        if (current == null) {
            synchronized (H2Database.class) {
                current = dataSource;
                if (current == null) {
                    Hashtable<String, String> env = new Hashtable<>();
                    env.put(Context.INITIAL_CONTEXT_FACTORY, "com.ibm.websphere.naming.WsnInitialContextFactory");
                    Object resource = new InitialContext(env).lookup(
                        DATA_SOURCE_JNDI_NAME);
                    if (!(resource instanceof DataSource)) {
                        throw new NamingException(
                            DATA_SOURCE_JNDI_NAME +
                            " is not bound to javax.sql.DataSource");
                    }
                    current = (DataSource) resource;
                    dataSource = current;
                }
            }
        }
        return current;
    }

    /**
     * Creates the schema and loads the bundled catalogue data when the
     * in-memory database is empty. The method is synchronized and idempotent
     * so duplicate startup callbacks do not duplicate seed rows.
     *
     * @return number of products available after initialization
     */
    public static synchronized int populateDatabase() throws SQLException, IOException {
        Connection connection = null;
        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            executeScript(connection, "/db/schema.sql");
            if (countRows(connection, "mercato.prodotto") == 0) {
                executeScript(connection, "/db/seed_products.sql");
            }

            int productCount = countRows(connection, "mercato.prodotto");
            connection.commit();
            return productCount;
        } catch (SQLException e) {
            rollbackQuietly(connection);
            throw e;
        } catch (IOException e) {
            rollbackQuietly(connection);
            throw e;
        } finally {
            closeQuietly(connection);
        }
    }

    private static void executeScript(Connection connection, String resource)
            throws SQLException, IOException {
        InputStream stream = H2Database.class.getResourceAsStream(resource);
        if (stream == null) {
            throw new IOException("Database script not found: " + resource);
        }

        Reader reader = new InputStreamReader(stream, "UTF-8");
        try {
            executeStatements(connection, readAll(reader), resource);
        } finally {
            reader.close();
        }
    }

    private static String readAll(Reader reader) throws IOException {
        StringBuilder content = new StringBuilder();
        char[] buffer = new char[4096];
        int count;
        while ((count = reader.read(buffer)) != -1) {
            content.append(buffer, 0, count);
        }
        return content.toString();
    }

    private static void executeStatements(Connection connection, String script,
            String resource) throws SQLException, IOException {
        StringBuilder sql = new StringBuilder();
        boolean quoted = false;

        for (int i = 0; i < script.length(); i++) {
            char current = script.charAt(i);

            if (!quoted && current == '-' && i + 1 < script.length()
                    && script.charAt(i + 1) == '-') {
                i = i + 2;
                while (i < script.length() && script.charAt(i) != '\n') {
                    i++;
                }
                sql.append(' ');
                continue;
            }

            if (current == '\'') {
                sql.append(current);
                if (quoted && i + 1 < script.length()
                        && script.charAt(i + 1) == '\'') {
                    sql.append(script.charAt(++i));
                } else {
                    quoted = !quoted;
                }
                continue;
            }

            if (!quoted && current == ';') {
                executeStatement(connection, sql.toString());
                sql.setLength(0);
            } else {
                sql.append(current);
            }
        }

        if (quoted) {
            throw new IOException("Unterminated SQL string in " + resource);
        }
        executeStatement(connection, sql.toString());
    }

    private static void executeStatement(Connection connection, String sql)
            throws SQLException {
        String command = sql.trim();
        if (command.length() == 0) {
            return;
        }

        Statement statement = connection.createStatement();
        try {
            statement.execute(command);
        } finally {
            statement.close();
        }
    }

    private static int countRows(Connection connection, String table) throws SQLException {
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT COUNT(*) FROM " + table);
            resultSet.next();
            return resultSet.getInt(1);
        } finally {
            if (resultSet != null) {
                try { resultSet.close(); } catch (SQLException ignored) {}
            }
            if (statement != null) {
                try { statement.close(); } catch (SQLException ignored) {}
            }
        }
    }

    private static void rollbackQuietly(Connection connection) {
        if (connection != null) {
            try { connection.rollback(); } catch (SQLException ignored) {}
        }
    }

    private static void closeQuietly(Connection connection) {
        if (connection != null) {
            try { connection.close(); } catch (SQLException ignored) {}
        }
    }
}
