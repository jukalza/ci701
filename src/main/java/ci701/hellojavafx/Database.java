package ci701.hellojavafx;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class Database {
    private static String URL;
    private static String USER;
    private static String PASS;

    static {
        try {
            Properties props = new Properties();
            props.load(new FileInputStream("db.properties"));

            URL = props.getProperty("db.url");
            USER = props.getProperty("db.user");
            PASS = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db.properties file", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
