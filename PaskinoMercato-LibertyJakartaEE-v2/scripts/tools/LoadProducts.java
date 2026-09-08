package it.paskinomercato.tools;

import java.io.*;
import java.sql.*;

/**
 * Standalone JDBC tool to load seed products into the PaskinoMercato database.
 *
 * Compile and run OUTSIDE WebSphere (plain Java):
 *
 *   javac -cp postgresql-42.7.3.jar LoadProducts.java
 *   java  -cp .:postgresql-42.7.3.jar it.paskinomercato.tools.LoadProducts \
 *         localhost 5432 mercatodb mercato changeme \
 *         ../../paskinomercato-ejb/src/main/resources/db/seed_products.sql
 *
 * Arguments (positional):
 *   1. DB host      (default: localhost)
 *   2. DB port      (default: 5432)
 *   3. DB name      (default: mercatodb)
 *   4. DB user      (default: mercato)
 *   5. DB password  (required)
 *   6. SQL file     (default: seed_products.sql)
 */
public class LoadProducts {

    public static void main(String[] args) throws Exception {
        String host     = args.length > 0 ? args[0] : "localhost";
        String port     = args.length > 1 ? args[1] : "5432";
        String dbName   = args.length > 2 ? args[2] : "mercatodb";
        String user     = args.length > 3 ? args[3] : "mercato";
        String password = args.length > 4 ? args[4] : "changeme";
        String sqlFile  = args.length > 5 ? args[5] : "seed_products.sql";

        String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;

        System.out.println("=== PaskinoMercato Product Loader ===");
        System.out.println("Connecting to: " + url);

        Class.forName("org.postgresql.Driver");
        Connection con = DriverManager.getConnection(url, user, password);
        con.setAutoCommit(false);

        System.out.println("Connected.");
        System.out.println("Loading SQL file: " + sqlFile);

        // Read entire SQL file
        String sql = readFile(sqlFile);

        Statement st = con.createStatement();
        st.execute(sql);
        con.commit();
        st.close();

        System.out.println("SQL executed successfully.");

        // Verify count
        PreparedStatement ps = con.prepareStatement(
            "SELECT COUNT(*) FROM mercato.prodotto");
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            System.out.println("Total products in DB: " + rs.getInt(1));
        }
        rs.close(); ps.close();

        // Print per-category summary
        PreparedStatement psCat = con.prepareStatement(
            "SELECT c.nome_it, COUNT(p.id) as n " +
            "FROM mercato.categoria c " +
            "LEFT JOIN mercato.prodotto p ON p.categoria_id = c.id " +
            "GROUP BY c.nome_it ORDER BY c.nome_it");
        ResultSet rsCat = psCat.executeQuery();
        System.out.println("\nProducts per category:");
        System.out.println("-------------------------------");
        while (rsCat.next()) {
            System.out.printf("  %-30s %3d%n", rsCat.getString(1), rsCat.getInt(2));
        }
        System.out.println("-------------------------------");
        rsCat.close(); psCat.close();

        con.close();
        System.out.println("\nDone. Database ready.");
    }

    private static String readFile(String path) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(path));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();
        return sb.toString();
    }
}
