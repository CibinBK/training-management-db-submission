package com.litmus7.employeemanager.services;

import com.litmus7.employeemanager.dao.EmployeeDao;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.exception.DAOException;
import com.litmus7.employeemanager.exception.EmployeeNotFoundException;
import com.litmus7.employeemanager.exception.ServiceException;
import com.litmus7.employeemanager.util.CsvFileReader;
import com.litmus7.employeemanager.util.DatabaseConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeManagementService {
    
    private static final Logger logger = LogManager.getLogger(EmployeeManagementService.class);

    private final EmployeeDao employeeDao;

    public EmployeeManagementService() {
        this.employeeDao = new EmployeeDao();
    }
    
    public int addEmployee(EmployeeDTO employee) throws ServiceException {
        logger.trace("Entering addEmployee() for employee ID: {}", employee.getEmployeeId());
        try {
            if (employeeDao.isEmployeeIdExists(employee.getEmployeeId())) {
                logger.warn("Attempted to add existing employee ID {}.", employee.getEmployeeId());
                throw new ServiceException("Employee with ID " + employee.getEmployeeId() + " already exists.");
            }
            int rowsAffected = employeeDao.saveEmployee(employee);
            logger.info("Employee with ID {} added successfully.", employee.getEmployeeId());
            return rowsAffected;
        } catch (DAOException e) {
            logger.error("DAO error adding employee ID {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            throw new ServiceException("Database error adding employee ID " + employee.getEmployeeId(), e);
        } finally {
            logger.trace("Exiting addEmployee().");
        }
    }
    
    public int[] transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) throws ServiceException {
        logger.trace("Entering transferEmployeesToDepartment() for IDs: {} to department: {}", employeeIds, newDepartment);
        if (employeeIds == null || employeeIds.isEmpty()) {
            logger.warn("Attempted department transfer with an empty list of employee IDs.");
            return new int[0];
        }
        if (newDepartment == null || newDepartment.trim().isEmpty()) {
            logger.error("New department name is null or empty. Transfer not possible.");
            throw new ServiceException("New department cannot be null or empty.");
        }
        
        try {
            int[] results = employeeDao.transferEmployeesToDepartment(employeeIds, newDepartment);
            logger.info("Department transfer for {} employees completed.", employeeIds.size());
            return results;
        } catch (DAOException e) {
            logger.error("DAO error during department transfer: {}", e.getMessage(), e);
            throw new ServiceException("A database error occurred during department transfer. " + e.getMessage(), e);
        } finally {
            logger.trace("Exiting transferEmployeesToDepartment().");
        }
    }

    public int[] addEmployeesInBatch(List<EmployeeDTO> employeeList) throws ServiceException {
        logger.trace("Entering addEmployeesInBatch() for {} employees.", employeeList.size());
        if (employeeList == null || employeeList.isEmpty()) {
            return new int[0];
        }
        try {
            int[] results = employeeDao.addEmployeesInBatch(employeeList);
            logger.info("Batch creation of {} employees completed.", employeeList.size());
            return results;
        } catch (DAOException e) {
            logger.error("DAO error during batch employee creation: {}", e.getMessage(), e);
            throw new ServiceException("A database error occurred during batch employee creation.", e);
        } finally {
            logger.trace("Exiting addEmployeesInBatch().");
        }
    }

    public SimpleEntry<Integer, List<String>> importEmployees(String filePath) throws ServiceException {
        logger.trace("Entering importEmployees() for file: {}", filePath);
        int successfulEntries = 0;
        List<String> detailedErrorMessages = new ArrayList<>();
        int totalRecordsAttempted = 0;

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            logger.info("Transaction started for CSV import.");

            try (CsvFileReader csvFileReader = new CsvFileReader(filePath)) {
                csvFileReader.skipHeader();
                logger.info("CSV header skipped.");

                System.out.println("\n--- Starting Employee Data Import ---");
                String line;
                while ((line = csvFileReader.readLine()) != null) {
                    totalRecordsAttempted++;

                    if (line.trim().isEmpty()) {
                        String msg = "Line " + (totalRecordsAttempted + 1) + ": SKIPPED (Empty Line)";
                        logger.warn(msg);
                        detailedErrorMessages.add(msg);
                        continue;
                    }

                    String[] values = line.split(",", -1);
                    
                    try {
                        RecordProcessResult recordResult = employeeDao.processEmployeeRecord(connection, values, totalRecordsAttempted + 1);
                        if (recordResult.success) {
                            successfulEntries++;
                            System.out.println(recordResult.message);
                            logger.debug("Successfully processed record on line {}.", (totalRecordsAttempted + 1));
                        } else {
                            System.err.println(recordResult.message);
                            logger.warn("Failed to process record on line {}: {}", (totalRecordsAttempted + 1), recordResult.message);
                            detailedErrorMessages.add(recordResult.message);
                        }
                    } catch (DAOException e) {
                        String errorMessage = "Failed to insert record from line " + (totalRecordsAttempted + 1) + ". Error: " + e.getMessage();
                        System.err.println(errorMessage);
                        logger.error("DAO error on line {}: {}", (totalRecordsAttempted + 1), e.getMessage(), e);
                        detailedErrorMessages.add(errorMessage);
                    }
                }
            } catch (IOException e) {
                String criticalOverallErrorMessage = "Critical Error reading CSV file: " + e.getMessage();
                System.err.println(criticalOverallErrorMessage);
                logger.fatal("Critical error reading CSV file: {}", e.getMessage(), e);
                detailedErrorMessages.add(criticalOverallErrorMessage);
            }

            if (detailedErrorMessages.isEmpty()) {
                connection.commit();
                logger.info("CSV import transaction committed successfully.");
            } else {
                connection.rollback();
                logger.warn("CSV import transaction rolled back due to errors.");
            }
        } catch (SQLException e) {
            logger.error("Database transaction error during import: {}", e.getMessage(), e);
            throw new ServiceException("Database transaction error during import: " + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred during import: {}", e.getMessage(), e);
            throw new ServiceException("An unexpected error occurred during import: " + e.getMessage(), e);
        } finally {
            logger.trace("Exiting importEmployees().");
        }
        
        return new SimpleEntry<>(successfulEntries, detailedErrorMessages);
    }
    
    public List<EmployeeDTO> getEmployeesByIds(List<Integer> employeeIds) throws ServiceException {
        logger.trace("Entering getEmployeesByIds() for IDs: {}", employeeIds);
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<EmployeeDTO> employees = employeeDao.findEmployeesByIds(employeeIds);
            logger.info("Found {} employees for the given IDs.", employees.size());
            return employees != null ? employees : Collections.emptyList();
        } catch (DAOException e) {
            logger.error("DAO error fetching employees by IDs: {}", e.getMessage(), e);
            throw new ServiceException("Database error fetching employees by IDs: " + e.getMessage(), e);
        } finally {
            logger.trace("Exiting getEmployeesByIds().");
        }
    }

    public EmployeeDTO getEmployeeById(int employeeId) throws ServiceException, EmployeeNotFoundException {
        logger.trace("Entering getEmployeeById() for ID: {}", employeeId);
        try {
            EmployeeDTO employee = employeeDao.findEmployeeById(employeeId);
            if (employee == null) {
                logger.warn("Employee with ID {} not found.", employeeId);
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found.");
            }
            logger.info("Employee with ID {} found.", employeeId);
            return employee;
        } catch (DAOException e) {
            logger.error("DAO error fetching employee ID {}: {}", employeeId, e.getMessage(), e);
            throw new ServiceException("Database error fetching employee ID " + employeeId, e);
        } finally {
            logger.trace("Exiting getEmployeeById().");
        }
    }

    public int updateEmployee(EmployeeDTO employee) throws ServiceException, EmployeeNotFoundException {
        logger.trace("Entering updateEmployee() for ID: {}", employee.getEmployeeId());
        try {
            int rowsAffected = employeeDao.updateEmployee(employee);
            if (rowsAffected == 0) {
                logger.warn("Employee with ID {} not found for update.", employee.getEmployeeId());
                throw new EmployeeNotFoundException("Employee with ID " + employee.getEmployeeId() + " not found for update.");
            }
            logger.info("Employee with ID {} updated successfully.", employee.getEmployeeId());
            return rowsAffected;
        } catch (DAOException e) {
            logger.error("DAO error updating employee ID {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            throw new ServiceException("Database error updating employee ID " + employee.getEmployeeId(), e);
        } finally {
            logger.trace("Exiting updateEmployee().");
        }
    }

    public int deleteEmployee(int employeeId) throws ServiceException, EmployeeNotFoundException {
        logger.trace("Entering deleteEmployee() for ID: {}", employeeId);
        try {
            int rowsAffected = employeeDao.deleteEmployee(employeeId);
            if (rowsAffected == 0) {
                logger.warn("Employee with ID {} not found for deletion.", employeeId);
                throw new EmployeeNotFoundException("Employee with ID " + employeeId + " not found for deletion.");
            }
            logger.info("Employee with ID {} deleted successfully.", employeeId);
            return rowsAffected;
        } catch (DAOException e) {
            logger.error("DAO error deleting employee ID {}: {}", employeeId, e.getMessage(), e);
            throw new ServiceException("Database error deleting employee ID " + employeeId, e);
        } finally {
            logger.trace("Exiting deleteEmployee().");
        }
    }
    
    public List<EmployeeDTO> findAllEmployees() throws ServiceException {
        logger.trace("Entering findAllEmployees().");
        try {
            List<EmployeeDTO> employees = employeeDao.findAllEmployees();
            if (employees == null) {
                logger.error("Failed to fetch all employees: Result was null from DAO.");
                throw new ServiceException("Failed to fetch all employees: Result was null.");
            }
            logger.info("Successfully fetched all {} employees.", employees.size());
            return employees;
        } catch (DAOException e) {
            logger.error("DAO error fetching all employees: {}", e.getMessage(), e);
            throw new ServiceException("Database error fetching all employees", e);
        } finally {
            logger.trace("Exiting findAllEmployees().");
        }
    }
}