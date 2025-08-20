package com.litmus7.employeemanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class DatabaseConnectionManager {

    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE_NAME = "database.properties";

    static {
        loadProperties();
    }

    private DatabaseConnectionManager() {} // Prevent instantiation

    private static void loadProperties() {
        try (InputStream input = DatabaseConnectionManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                System.err.println("Sorry, unable to find " + PROPERTIES_FILE_NAME + " in the classpath.");
                throw new RuntimeException("Database properties file not found: " + PROPERTIES_FILE_NAME);
            }
            PROPERTIES.load(input);
        } catch (IOException ex) {
            System.err.println("Error loading database properties: " + ex.getMessage());
            throw new RuntimeException("Failed to load database properties", ex);
        }
    }

    public static Connection getConnection() throws SQLException {
        String dbUrl = PROPERTIES.getProperty("db.url");
        String dbUser = PROPERTIES.getProperty("db.user");
        String dbPassword = PROPERTIES.getProperty("db.password");
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}