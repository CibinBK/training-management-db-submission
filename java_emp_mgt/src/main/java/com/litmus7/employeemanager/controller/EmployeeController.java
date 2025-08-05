package com.litmus7.employeemanager.controller;

import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import com.litmus7.employeemanager.exception.EmployeeNotFoundException;
import com.litmus7.employeemanager.exception.ServiceException;
import com.litmus7.employeemanager.services.EmployeeManagementService;
import com.litmus7.employeemanager.util.EmployeeValidator;

import java.io.File;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class EmployeeController {

    private final EmployeeManagementService service;

    public EmployeeController() {
        this.service = new EmployeeManagementService();
    }

    public ResponseDTO<List<String>> importEmployees(String filePath) {
        File csvFile = new File(filePath);
        if (!csvFile.exists() || csvFile.isDirectory()) {
            return ResponseDTO.failure("Invalid file path: " + filePath + ". File not found or is a directory.", 0, null);
        }

        try {
            SimpleEntry<Integer, List<String>> result = service.importEmployees(filePath);
            
            int successfulCount = result.getKey();
            List<String> errors = result.getValue();
            int totalRecordsAttempted = successfulCount + errors.size();

            return ResponseDTO.createOverallResponse(totalRecordsAttempted, successfulCount, errors);
        } catch (ServiceException e) {
            return ResponseDTO.failure("An unexpected error occurred during import: " + e.getMessage(), 0, null);
        }
    }
    
    public ResponseDTO<EmployeeDTO> getEmployeeById(int employeeId) {
        if (employeeId <= 0) {
            return ResponseDTO.failure("Invalid Employee ID. Must be a positive number.", 0, null);
        }
        try {
            EmployeeDTO employee = service.getEmployeeById(employeeId);
            return ResponseDTO.success("Employee found successfully.", 1, employee);
        } catch (EmployeeNotFoundException e) {
            return ResponseDTO.failure(e.getMessage(), 0, null);
        } catch (ServiceException e) {
            return ResponseDTO.failure("A service error occurred while fetching employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        }
    }
    
    public ResponseDTO<Integer> updateEmployee(EmployeeDTO employee) {
        if (employee == null || employee.getEmployeeId() <= 0) {
            return ResponseDTO.failure("Invalid employee data provided for update.", 0, null);
        }
        
        ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
        if (validationResponse.isFailure()) {
            return ResponseDTO.failure(validationResponse.getMessage(), 0, null);
        }

        try {
            int rowsAffected = service.updateEmployee(employee);
            return ResponseDTO.success("Employee ID " + employee.getEmployeeId() + " updated successfully.", rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            return ResponseDTO.failure(e.getMessage(), 0, null);
        } catch (ServiceException e) {
            return ResponseDTO.failure("A service error occurred while updating employee ID " + employee.getEmployeeId() + ": " + e.getMessage(), 0, null);
        }
    }
    
    public ResponseDTO<Integer> deleteEmployee(int employeeId) {
        if (employeeId <= 0) {
            return ResponseDTO.failure("Invalid Employee ID. Must be a positive number.", 0, null);
        }
        try {
            int rowsAffected = service.deleteEmployee(employeeId);
            return ResponseDTO.success("Employee ID " + employeeId + " deleted successfully.", rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            return ResponseDTO.failure(e.getMessage(), 0, null);
        } catch (ServiceException e) {
            return ResponseDTO.failure("A service error occurred while deleting employee ID " + employeeId + ": " + e.getMessage(), 0, null);
        }
    }

    public ResponseDTO<List<EmployeeDTO>> findAllEmployees() {
        try {
            List<EmployeeDTO> employees = service.findAllEmployees();
            return ResponseDTO.success("All employees fetched successfully.", employees.size(), employees);
        } catch (ServiceException e) {
            return ResponseDTO.failure("A service error occurred while fetching all employees: " + e.getMessage(), 0, null);
        }
    }

    private ResponseDTO<String> validateEmployeeDTO(EmployeeDTO employee) {
        if (EmployeeValidator.validateStringField("First Name", employee.getFirstName(), 0) == null) {
            return ResponseDTO.failure("Invalid First Name.", 0, "First name invalid.");
        }
        if (EmployeeValidator.validateEmail(employee.getEmail(), 0) == null) {
            return ResponseDTO.failure("Invalid Email.", 0, "Email invalid.");
        }
        if (EmployeeValidator.validateJoinDate(employee.getJoinDate().toString(), 0) == null) {
            return ResponseDTO.failure("Invalid Join Date.", 0, "Join Date invalid.");
        }
        return ResponseDTO.success("Employee data is valid.", 0, "Data valid.");
    }
}