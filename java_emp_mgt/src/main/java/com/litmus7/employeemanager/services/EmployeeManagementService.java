package com.litmus7.employeemanager.services;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import com.litmus7.employeemanager.dao.EmployeeDao;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.util.CsvFileReader;

public class EmployeeManagementService {

    private final EmployeeDao employeeDao;

    public EmployeeManagementService() {
        this.employeeDao = new EmployeeDao();
    }

    public ResponseDTO<List<String>> importEmployees(Connection connection, String filePath) {
        int successfulEntries = 0;
        List<String> detailedErrorMessages = new ArrayList<>();
        String criticalOverallErrorMessage = null;
        int totalRecordsAttempted = 0; // Tracks total lines read, excluding header

        try {
            connection.setAutoCommit(false);

            try (CsvFileReader csvFileReader = new CsvFileReader(filePath)) {
                csvFileReader.skipHeader(); // Skip the header line

                System.out.println("\n--- Starting Employee Data Import ---");
                String line;
                while ((line = csvFileReader.readLine()) != null) {
                    totalRecordsAttempted++; // Increment for each data line processed

                    if (line.trim().isEmpty()) {
                        String msg = "Line " + (totalRecordsAttempted + 1) + ": SKIPPED (Empty Line)";
                        System.out.println(msg);
                        detailedErrorMessages.add(msg);
                        continue;
                    }

                    String[] values = line.split(",", -1);

                    RecordProcessResult recordResult = employeeDao.processEmployeeRecord(connection, values, totalRecordsAttempted + 1); // Pass actual line number

                    if (recordResult.success) {
                        successfulEntries++;
                        System.out.println(recordResult.message);
                    } else {
                        System.err.println(recordResult.message);
                        detailedErrorMessages.add(recordResult.message);
                    }
                }
            } catch (IOException e) {
                criticalOverallErrorMessage = "Critical Error reading CSV file: " + e.getMessage();
                System.err.println(criticalOverallErrorMessage);
                e.printStackTrace();
            }

            if (criticalOverallErrorMessage == null) {
                connection.commit();
            } else {
                connection.rollback();
            }

        } catch (SQLException e) {
            criticalOverallErrorMessage = "Database transaction error during import: " + e.getMessage();
            System.err.println(criticalOverallErrorMessage);
            e.printStackTrace();
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                System.err.println("Error during rollback: " + rollbackEx.getMessage());
            }
        } finally {
            try {
                if (connection != null && !connection.isClosed()) {
                    connection.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
            System.out.println("--- Employee Data Import Finished ---");
        }

        if (criticalOverallErrorMessage == null) {
            return ResponseDTO.createOverallResponse(
                totalRecordsAttempted,
                successfulEntries,
                detailedErrorMessages
            );
        } else {
            return ResponseDTO.failure(criticalOverallErrorMessage, successfulEntries, detailedErrorMessages);
        }
    }

    public ResponseDTO<EmployeeDTO> getEmployeeById(Connection connection, int employeeId) {
        try {
            EmployeeDTO employee = employeeDao.findEmployeeById(connection, employeeId);
            if (employee != null) {
                return ResponseDTO.success("Employee found successfully.", 1, employee);
            } else {
                return ResponseDTO.failure("Employee with ID " + employeeId + " not found.", 0, null);
            }
        } catch (SQLException e) {
            return ResponseDTO.failure("Database error fetching employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        }
    }

    public ResponseDTO<Integer> updateEmployee(Connection connection, EmployeeDTO employee) {
        try {
            int rowsAffected = employeeDao.updateEmployee(connection, employee);
            if (rowsAffected > 0) {
                return ResponseDTO.success("Employee ID " + employee.getEmpId() + " updated successfully.", rowsAffected, rowsAffected);
            } else {
                return ResponseDTO.failure("Employee with ID " + employee.getEmpId() + " not found for update (0 rows affected).", 0, null);
            }
        } catch (SQLException e) {
            return ResponseDTO.failure("Database error updating employee ID " + employee.getEmpId() + ": " + e.getMessage(), 0, null);
        }
    }

    public ResponseDTO<Integer> deleteEmployee(Connection connection, int employeeId) {
        try {
            int rowsAffected = employeeDao.deleteEmployee(connection, employeeId);
            if (rowsAffected > 0) {
                return ResponseDTO.success("Employee ID " + employeeId + " deleted successfully.", rowsAffected, rowsAffected);
            } else {
                return ResponseDTO.failure("Employee with ID " + employeeId + " not found for deletion.", 0, null);
            }
        } catch (SQLException e) {
            return ResponseDTO.failure("Database error deleting employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        }
    }
    
    public ResponseDTO<List<EmployeeDTO>> findAllEmployees(Connection connection) {
        try {
            List<EmployeeDTO> employees = employeeDao.findAllEmployees(connection);
            if (employees != null) {
                return ResponseDTO.success("All employees fetched successfully.", employees.size(), employees);
            } else {
                return ResponseDTO.failure("Failed to fetch all employees: Result was null.", 0, null);
            }
        } catch (SQLException e) {
            return ResponseDTO.failure("Database error fetching all employees: " + e.getMessage(), 0, null);
        }
    }
}
