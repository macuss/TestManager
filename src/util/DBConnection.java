package util;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class DBConnection {

    private static Properties props = new Properties();

    static {
        try {
     
            FileInputStream fis = new FileInputStream("src/config.properties");
            props.load(fis);
        } catch (Exception e) {
            System.err.println("Error cargando config.properties: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(
            props.getProperty("db.url"),
            props.getProperty("db.user"),
            props.getProperty("db.password")
        );
    }
}