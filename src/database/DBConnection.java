package database;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {
	private static final Properties properties = new Properties();

    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (input == null) {
                throw new RuntimeException("db.properties file not found in resources folder.");
            }

            properties.load(input);

            String driver = properties.getProperty("db.driver");
            Class.forName(driver);

        } catch (IOException e) {
            throw new RuntimeException("Failed to load db.properties file.", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found. Check Build Path.", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        String url = properties.getProperty("db.url");
        String username = properties.getProperty("db.username");
        String password = properties.getProperty("db.password");

        return DriverManager.getConnection(url, username, password);
    }
}