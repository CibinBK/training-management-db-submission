package com.litmus7.employeemanager.services;

import com.litmus7.employeemanager.dao.EmployeeDao;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.exception.DAOException;
import com.litmus7.employeemanager.exception.EmployeeNotFoundException;
import com.litmus7.employeemanager.exception.ServiceException;
import com.litmus7.employeemanager.util.CsvFileReader;
import com.litmus7.employeemanager.util.DatabaseConnectionManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeManagementService {

    private final EmployeeDao employeeDao;

    public EmployeeManagementService() {
        this.employeeDao = new EmployeeDao();
    }
    
    public int addEmployee(EmployeeDTO employee) throws ServiceException {
        try {
            if (employeeDao.isEmployeeIdExists(employee.getEmployeeId())) {
                throw new ServiceException("Employee with ID " + employee.getEmployeeId() + " already exists.");
            }
            return employeeDao.saveEmployee(employee);
        } catch (DAOException e) {
            throw new ServiceException("Database error adding employee ID " + employee.getEmployeeId(), e);
        }
    }

    public int[] addEmployeesInBatch(List<EmployeeDTO> employeeList) throws ServiceException {
        if (employeeList == null || employeeList.isEmpty()) {
            return new int[0];
        }
        try {
            return employeeDao.addEmployeesInBatch(employeeList);
        } catch (DAOException e) {
            throw new ServiceException("A database error occurred during batch employee creation.", e);
        }
    }
    
    // New method for transaction-based department transfer in the service layer
    public int[] transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) throws ServiceException {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new int[0];
        }
        if (newDepartment == null || newDepartment.trim().isEmpty()) {
            throw new ServiceException("New department cannot be null or empty.");
        }
        
        try {
            return employeeDao.transferEmployeesToDepartment(employeeIds, newDepartment);
        } catch (DAOException e) {
            throw new ServiceException("A database error occurred during department transfer. " + e.getMessage(), e);
        }
    }

    public SimpleEntry<Integer, List<String>> importEmployees(String filePath) throws ServiceException {
        int successfulEntries = 0;
        List<String> detailedErrorMessages = new ArrayList<>();
        int totalRecordsAttempted = 0;

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);

            try (CsvFileReader csvFileReader = new CsvFileReader(filePath)) {
                csvFileReader.skipHeader();

                System.out.println("\n--- Starting Employee Data Import ---");
                String line;
                while ((line = csvFileReader.readLine()) != null) {
                    totalRecordsAttempted++;

                    if (line.trim().isEmpty()) {
                        String msg = "Line " + (totalRecordsAttempted + 1) + ": SKIPPED (Empty Line)";
                        System.out.println(msg);
                        detailedErrorMessages.add(msg);
                        continue;
                    }

                    String[] values = line.split(",", -1);
                    
                    try {
                        RecordProcessResult recordResult = employeeDao.processEmployeeRecord(connection, values, totalRecordsAttempted + 1);
                        if (recordResult.success) {
                            successfulEntries++;
                            System.out.println(recordResult.message);
                        } else {
                            System.err.println(recordResult.message);
                            detailedErrorMessages.add(recordResult.message);
                        }
                    } catch (DAOException e) {
                        String errorMessage = "Failed to insert record from line " + (totalRecordsAttempted + 1) + ". Error: " + e.getMessage();
                        System.err.println(errorMessage);
                        detailedErrorMessages.add(errorMessage);
                    }
                }
            } catch (IOException e) {
                String criticalOverallErrorMessage = "Critical Error reading CSV file: " + e.getMessage();
                System.err.println(criticalOverallErrorMessage);
                e.printStackTrace();
                detailedErrorMessages.add(criticalOverallErrorMessage);
            }

            if (detailedErrorMessages.isEmpty()) {
                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException e) {
            throw new ServiceException("Database transaction error during import: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ServiceException("An unexpected error occurred during import: " + e.getMessage(), e);
        } finally {
            System.out.println("--- Employee Data Import Finished ---");
        }
        
        return new SimpleEntry<>(successfulEntries, detailedErrorMessages);
    }
    
    public List<EmployeeDTO> getEmployeesByIds(List<Integer> employeeIds) throws ServiceException {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<EmployeeDTO> employees = employeeDao.findEmployeesByIds(employeeIds);
            return employees != null ? employees : Collections.emptyList();
        } catch (DAOException e) {
            throw new ServiceException("Database error fetching employees by IDs: " + e.getMessage(), e);
        }
    }

    public EmployeeDTO getEmployeeById(int employeeId) throws ServiceException, EmployeeNotFoundException {
        try {
            EmployeeDTO employee = employeeDao.findEmployeeById(employeeId);
            if (employee == null) {
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found.");
            }
            return employee;
        } catch (DAOException e) {
            throw new ServiceException("Database error fetching employee ID " + employeeId, e);
        }
    }

    public int updateEmployee(EmployeeDTO employee) throws ServiceException, EmployeeNotFoundException {
        try {
            int rowsAffected = employeeDao.updateEmployee(employee);
            if (rowsAffected == 0) {
                throw new EmployeeNotFoundException("Employee with ID " + employee.getEmployeeId() + " not found for update.");
            }
            return rowsAffected;
        } catch (DAOException e) {
            throw new ServiceException("Database error updating employee ID " + employee.getEmployeeId(), e);
        }
    }

    public int deleteEmployee(int employeeId) throws ServiceException, EmployeeNotFoundException {
        try {
            int rowsAffected = employeeDao.deleteEmployee(employeeId);
            if (rowsAffected == 0) {
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found for deletion.");
            }
            return rowsAffected;
        } catch (DAOException e) {
            throw new ServiceException("Database error deleting employee ID " + employeeId, e);
        }
    }
    
    public List<EmployeeDTO> findAllEmployees() throws ServiceException {
        try {
            List<EmployeeDTO> employees = employeeDao.findAllEmployees();
            if (employees == null) {
                throw new ServiceException("Failed to fetch all employees: Result was null.");
            }
            return employees;
        } catch (DAOException e) {
            throw new ServiceException("Database error fetching all employees", e);
        }
    }
}