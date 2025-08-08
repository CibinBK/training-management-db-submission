package com.litmus7.employeemanager.property;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class DatabaseProperties {

    private static final Logger logger = LogManager.getLogger(DatabaseProperties.class);

    private static final Properties PROPERTIES = new Properties(); // Stores loaded properties
    private static final String PROPERTIES_FILE_NAME = "database.properties";

    static {
        loadProperties();
    }

    private DatabaseProperties() {}
    
    private static void loadProperties() {
        logger.info("Attempting to load database properties from file: {}", PROPERTIES_FILE_NAME);
        try (InputStream input = DatabaseProperties.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                logger.fatal("Unable to find {} in the classpath.", PROPERTIES_FILE_NAME);
                throw new RuntimeException("Database properties file not found: " + PROPERTIES_FILE_NAME);
            }
            PROPERTIES.load(input);
            logger.info("Database properties loaded successfully.");
        } catch (IOException ex) {
            logger.fatal("Error loading database properties: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to load database properties", ex);
        }
    }

    public static String getDbUrl() {
        return PROPERTIES.getProperty("db.url");
    }

    public static String getDbUser() {
        return PROPERTIES.getProperty("db.user");
    }

    public static String getDbPassword() {
        return PROPERTIES.getProperty("db.password");
    }
}