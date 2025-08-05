package com.litmus7.employeemanager.util;

import com.litmus7.employeemanager.property.DatabaseProperties;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnectionManager {

    private DatabaseConnectionManager() {} // Prevent instantiation

    public static Connection getConnection() throws SQLException {
        String dbUrl = DatabaseProperties.getDbUrl();
        String dbUser = DatabaseProperties.getDbUser();
        String dbPassword = DatabaseProperties.getDbPassword();
        return DriverManager.getConnection(dbUrl, dbUser, dbPassword);
    }
}