package com.litmus7.inventorymanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ErrorCodesManager {
    private static final Properties PROPERTIES = new Properties();
    private static final String PROPERTIES_FILE_NAME = "errorcodes.properties";

    static {
        loadProperties();
    }

    private ErrorCodesManager() {}
    private static void loadProperties() {
        try (InputStream input = ErrorCodesManager.class.getClassLoader().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            if (input == null) {
                System.err.println("Error: Unable to find " + PROPERTIES_FILE_NAME + " in the classpath.");
                throw new RuntimeException("Error codes properties file not found: " + PROPERTIES_FILE_NAME);
            }
            PROPERTIES.load(input);
        } catch (IOException ex) {
            System.err.println("Error loading error codes properties: " + ex.getMessage());
            throw new RuntimeException("Failed to load error codes properties", ex);
        }
    }
    public static String getErrorMessage(String errorCode) {
        return PROPERTIES.getProperty(errorCode, "Error code not found: " + errorCode);
    }
}