package com.litmus7.employeemanager.controller;

import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import com.litmus7.employeemanager.exception.EmployeeNotFoundException;
import com.litmus7.employeemanager.exception.ServiceException;
import com.litmus7.employeemanager.services.EmployeeManagementService;
import com.litmus7.employeemanager.util.EmployeeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class EmployeeController {
    
    private static final Logger logger = LogManager.getLogger(EmployeeController.class);

    private final EmployeeManagementService service;

    public EmployeeController() {
        this.service = new EmployeeManagementService();
    }
    
    public ResponseDTO<Integer> addEmployee(EmployeeDTO employee) {
        logger.trace("Entering addEmployee() for employee ID: {}", employee.getEmployeeId());
        if (employee == null || employee.getEmployeeId() <= 0) {
            logger.warn("Attempted to add employee with invalid data.");
            return ResponseDTO.failure("Invalid employee data provided for addition.", 0, null);
        }
        ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
        if (validationResponse.isFailure()) {
            logger.warn("Validation failed for new employee: {}", validationResponse.getMessage());
            return ResponseDTO.failure(validationResponse.getMessage(), 0, null);
        }
        try {
            int rowsAffected = service.addEmployee(employee);
            logger.info("Successfully added employee with ID: {}. Rows affected: {}", employee.getEmployeeId(), rowsAffected);
            return ResponseDTO.success("Employee ID " + employee.getEmployeeId() + " added successfully.", rowsAffected, rowsAffected);
        } catch (ServiceException e) {
            logger.error("Service error adding employee ID {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while adding employee ID " + employee.getEmployeeId() + ": " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting addEmployee().");
        }
    }

    public ResponseDTO<int[]> addEmployeesInBatch(List<EmployeeDTO> employeeList) {
        logger.trace("Entering addEmployeesInBatch() for {} employees.", employeeList.size());
        if (employeeList == null || employeeList.isEmpty()) {
            logger.warn("Attempted batch insertion with empty employee list.");
            return ResponseDTO.failure("Employee list is empty or null.", 0, null);
        }
        
        try {
            int[] results = service.addEmployeesInBatch(employeeList);
            int successfulInserts = 0;
            for (int result : results) {
                if (result == 1) {
                    successfulInserts++;
                }
            }
            logger.info("Batch insertion completed. Successful inserts: {} out of {}.", successfulInserts, employeeList.size());
            return ResponseDTO.success("Batch insertion completed.", successfulInserts, results);
        } catch (ServiceException e) {
            logger.error("Service error during batch insertion: {}", e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred during batch insertion: " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting addEmployeesInBatch().");
        }
    }
    
    public ResponseDTO<int[]> transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) {
        logger.trace("Entering transferEmployeesToDepartment() for IDs: {} to department: {}", employeeIds, newDepartment);
        if (employeeIds == null || employeeIds.isEmpty()) {
            logger.warn("Attempted department transfer with an empty list of employee IDs.");
            return ResponseDTO.failure("No employee IDs provided.", 0, null);
        }
        if (newDepartment == null || newDepartment.trim().isEmpty()) {
            logger.warn("Attempted department transfer with an empty new department name.");
            return ResponseDTO.failure("New department cannot be null or empty.", 0, null);
        }
        
        try {
            int[] results = service.transferEmployeesToDepartment(employeeIds, newDepartment);
            int successfulUpdates = 0;
            for (int result : results) {
                if (result > 0) {
                    successfulUpdates++;
                }
            }
            logger.info("Department transfer completed. Successful updates: {} out of {}", successfulUpdates, employeeIds.size());
            return ResponseDTO.success("Department transfer completed successfully.", successfulUpdates, results);
        } catch (ServiceException e) {
            logger.error("Service error during department transfer: {}", e.getMessage(), e);
            return ResponseDTO.failure("Department transfer failed: " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting transferEmployeesToDepartment().");
        }
    }

    public ResponseDTO<List<String>> importEmployees(String filePath) {
        logger.trace("Entering importEmployees() for file: {}", filePath);
        File csvFile = new File(filePath);
        if (!csvFile.exists() || csvFile.isDirectory()) {
            logger.error("Invalid file path: {}. File not found or is a directory.", filePath);
            return ResponseDTO.failure("Invalid file path: " + filePath + ". File not found or is a directory.", 0, null);
        }

        try {
            SimpleEntry<Integer, List<String>> result = service.importEmployees(filePath);
            
            int successfulCount = result.getKey();
            List<String> errors = result.getValue();
            int totalRecordsAttempted = successfulCount + errors.size();

            logger.info("CSV import finished. Total records: {}, Successful: {}, Failed: {}", totalRecordsAttempted, successfulCount, errors.size());
            return ResponseDTO.createOverallResponse(totalRecordsAttempted, successfulCount, errors);
        } catch (ServiceException e) {
            logger.error("Service error during CSV import: {}", e.getMessage(), e);
            return ResponseDTO.failure("An unexpected error occurred during import: " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting importEmployees().");
        }
    }
    
    public ResponseDTO<List<EmployeeDTO>> getEmployeesByIds(List<Integer> employeeIds) {
        logger.trace("Entering getEmployeesByIds() for IDs: {}", employeeIds);
        if (employeeIds == null || employeeIds.isEmpty()) {
            logger.warn("Attempted to fetch employees with an empty list of IDs.");
            return ResponseDTO.failure("No employee IDs provided.", 0, null);
        }
        
        try {
            List<EmployeeDTO> employees = service.getEmployeesByIds(employeeIds);
            if (employees.isEmpty()) {
                logger.info("No employees found for the provided IDs: {}", employeeIds);
                return ResponseDTO.failure("No employees found for the provided IDs.", 0, null);
            }
            logger.info("Successfully fetched {} employees for IDs: {}", employees.size(), employeeIds);
            return ResponseDTO.success("Employees found successfully.", employees.size(), employees);
        } catch (ServiceException e) {
            logger.error("Service error fetching employees by IDs: {}", e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while fetching employees: " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting getEmployeesByIds().");
        }
    }
    
    public ResponseDTO<EmployeeDTO> getEmployeeById(int employeeId) {
        logger.trace("Entering getEmployeeById() for ID: {}", employeeId);
        if (employeeId <= 0) {
            logger.warn("Invalid employee ID provided: {}.", employeeId);
            return ResponseDTO.failure("Invalid Employee ID. Must be a positive number.", 0, null);
        }
        try {
            EmployeeDTO employee = service.getEmployeeById(employeeId);
            logger.info("Successfully fetched employee with ID: {}", employeeId);
            return ResponseDTO.success("Employee found successfully.", 1, employee);
        } catch (EmployeeNotFoundException e) {
            logger.info("Fetch failed: {}", e.getMessage());
            return ResponseDTO.failure(e.getMessage(), 0, null);
        } catch (ServiceException e) {
            logger.error("Service error fetching employee ID {}: {}", employeeId, e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while fetching employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting getEmployeeById().");
        }
    }
    
    public ResponseDTO<Integer> updateEmployee(EmployeeDTO employee) {
        logger.trace("Entering updateEmployee() for ID: {}", employee.getEmployeeId());
        if (employee == null || employee.getEmployeeId() <= 0) {
            logger.warn("Attempted to update employee with invalid data.");
            return ResponseDTO.failure("Invalid employee data provided for update.", 0, null);
        }
        
        ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
        if (validationResponse.isFailure()) {
            logger.warn("Validation failed for employee update: {}", validationResponse.getMessage());
            return ResponseDTO.failure(validationResponse.getMessage(), 0, null);
        }

        try {
            int rowsAffected = service.updateEmployee(employee);
            logger.info("Successfully updated employee with ID: {}. Rows affected: {}", employee.getEmployeeId(), rowsAffected);
            return ResponseDTO.success("Employee ID " + employee.getEmployeeId() + " updated successfully.", rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            logger.info("Update failed: {}", e.getMessage());
            return ResponseDTO.failure("Employee with ID " + employee.getEmployeeId() + " not found for update.", 0, null);
        } catch (ServiceException e) {
            logger.error("Service error updating employee ID {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while updating employee ID " + employee.getEmployeeId() + ": " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting updateEmployee().");
        }
    }
    
    public ResponseDTO<Integer> deleteEmployee(int employeeId) {
        logger.trace("Entering deleteEmployee() for ID: {}", employeeId);
        if (employeeId <= 0) {
            logger.warn("Invalid employee ID provided for deletion: {}.", employeeId);
            return ResponseDTO.failure("Invalid Employee ID. Must be a positive number.", 0, null);
        }
        try {
            int rowsAffected = service.deleteEmployee(employeeId);
            logger.info("Successfully deleted employee with ID: {}. Rows affected: {}", employeeId, rowsAffected);
            return ResponseDTO.success("Employee ID " + employeeId + " deleted successfully.", rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            logger.info("Delete failed: {}", e.getMessage());
            return ResponseDTO.failure(e.getMessage(), 0, null);
        } catch (ServiceException e) {
            logger.error("Service error deleting employee ID {}: {}", employeeId, e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while deleting employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting deleteEmployee().");
        }
    }

    public ResponseDTO<List<EmployeeDTO>> findAllEmployees() {
        logger.trace("Entering findAllEmployees().");
        try {
            List<EmployeeDTO> employees = service.findAllEmployees();
            logger.info("Successfully fetched all {} employees.", employees.size());
            return ResponseDTO.success("All employees fetched successfully.", employees.size(), employees);
        } catch (ServiceException e) {
            logger.error("Service error fetching all employees: {}", e.getMessage(), e);
            return ResponseDTO.failure("A service error occurred while fetching all employees: " + e.getMessage(), 0, null);
        } finally {
            logger.trace("Exiting findAllEmployees().");
        }
    }

    private ResponseDTO<String> validateEmployeeDTO(EmployeeDTO employee) {
        logger.trace("Entering validateEmployeeDTO() for employee ID: {}", employee.getEmployeeId());
        // ... (validation logic remains the same)
        if (EmployeeValidator.validateStringField("First Name", employee.getFirstName(), 0) == null) {
            logger.warn("Validation failed for employee ID {}: First name is invalid.", employee.getEmployeeId());
            return ResponseDTO.failure("Invalid First Name.", 0, "First name invalid.");
        }
        if (EmployeeValidator.validateEmail(employee.getEmail(), 0) == null) {
            logger.warn("Validation failed for employee ID {}: Email is invalid.", employee.getEmployeeId());
            return ResponseDTO.failure("Invalid Email.", 0, "Email invalid.");
        }
        if (EmployeeValidator.validateJoinDate(employee.getJoinDate().toString(), 0) == null) {
            logger.warn("Validation failed for employee ID {}: Join date is invalid.", employee.getEmployeeId());
            return ResponseDTO.failure("Invalid Join Date.", 0, "Join Date invalid.");
        }
        // ... (more validation)
        logger.debug("Validation successful for employee ID: {}", employee.getEmployeeId());
        logger.trace("Exiting validateEmployeeDTO().");
        return ResponseDTO.success("Employee data is valid.", 0, "Data valid.");
    }
}