package com.litmus7.employeemanager.controller;

import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import com.litmus7.employeemanager.exception.EmployeeNotFoundException;
import com.litmus7.employeemanager.exception.ServiceException;
import com.litmus7.employeemanager.services.EmployeeManagementService;
import com.litmus7.employeemanager.util.EmployeeValidator;
import com.litmus7.employeemanager.util.ErrorCodesManager;

import java.io.File;
import java.util.List;
import java.util.AbstractMap.SimpleEntry;

public class EmployeeController {

    private final EmployeeManagementService service;

    public EmployeeController() {
        this.service = new EmployeeManagementService();
    }
    
    public ResponseDTO<Integer> addEmployee(EmployeeDTO employee) {
        if (employee == null) {
            String message = ErrorCodesManager.getErrorMessage(100, "employee data");
            return ResponseDTO.failure(100, message, 0, null);
        }
        if (employee.getEmployeeId() <= 0) {
            String message = ErrorCodesManager.getErrorMessage(105, employee.getEmployeeId());
            return ResponseDTO.failure(105, message, 0, null);
        }
        ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
        if (validationResponse.isFailure()) {
            return ResponseDTO.failure(validationResponse.getErrorCode(), validationResponse.getMessage(), 0, null);
        }
        try {
            int rowsAffected = service.addEmployee(employee);
            String message = "Employee with ID " + employee.getEmployeeId() + " added successfully.";
            return ResponseDTO.success(message, rowsAffected, rowsAffected);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(110, employee.getEmployeeId());
            return ResponseDTO.failure(110, message, 0, null);
        }
    }
    
    public ResponseDTO<int[]> addEmployeesInBatch(List<EmployeeDTO> employeeList) {
        if (employeeList == null || employeeList.isEmpty()) {
            String message = ErrorCodesManager.getErrorMessage(100, "employee list");
            return ResponseDTO.failure(100, message, 0, null);
        }
        
        // New: Validate every employee in the list before processing
        for (EmployeeDTO employee : employeeList) {
            if (employee == null || employee.getEmployeeId() <= 0) {
                String message = ErrorCodesManager.getErrorMessage(105, employee != null ? employee.getEmployeeId() : "null");
                return ResponseDTO.failure(105, message, 0, null);
            }
            ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
            if (validationResponse.isFailure()) {
                return ResponseDTO.failure(validationResponse.getErrorCode(), validationResponse.getMessage(), 0, null);
            }
        }
        
        try {
            int[] results = service.addEmployeesInBatch(employeeList);
            int successfulInserts = 0;
            for (int result : results) {
                if (result == 1) {
                    successfulInserts++;
                }
            }
            if (successfulInserts == 0) {
                String message = ErrorCodesManager.getErrorMessage(108);
                return ResponseDTO.failure(108, message, 0, null);
            }
            String message = "Batch insertion completed.";
            return ResponseDTO.success(message, successfulInserts, results);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }
    
    public ResponseDTO<int[]> transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            String message = ErrorCodesManager.getErrorMessage(100, "Employee IDs");
            return ResponseDTO.failure(100, message, 0, null);
        }
        if (newDepartment == null || newDepartment.trim().isEmpty()) {
            String message = ErrorCodesManager.getErrorMessage(107, "new department");
            return ResponseDTO.failure(107, message, 0, null);
        }
        
        try {
            int[] results = service.transferEmployeesToDepartment(employeeIds, newDepartment);
            int successfulUpdates = 0;
            for (int result : results) {
                if (result > 0) {
                    successfulUpdates++;
                }
            }
            if (successfulUpdates == 0) {
                String message = ErrorCodesManager.getErrorMessage(112);
                return ResponseDTO.failure(112, message, 0, null);
            }
            String message = "Department transfer completed successfully.";
            return ResponseDTO.success(message, successfulUpdates, results);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }

    public ResponseDTO<List<String>> importEmployees(String filePath) {
        File csvFile = new File(filePath);
        if (!csvFile.exists() || csvFile.isDirectory()) {
            String message = ErrorCodesManager.getErrorMessage(102);
            return ResponseDTO.failure(102, message, 0, null);
        }

        try {
            SimpleEntry<Integer, List<String>> result = service.importEmployees(filePath);
            
            int successfulCount = result.getKey();
            List<String> errors = result.getValue();
            int totalRecordsAttempted = successfulCount + errors.size();

            if (totalRecordsAttempted == 0) {
                String message = ErrorCodesManager.getErrorMessage(103);
                return ResponseDTO.failure(103, message, 0, errors);
            } else if (successfulCount > 0 && errors.isEmpty()) {
                String message = "All " + successfulCount + " employee records imported successfully.";
                return ResponseDTO.success(message, successfulCount, errors);
            } else if (successfulCount > 0) {
                String message = ErrorCodesManager.getErrorMessage(108);
                return ResponseDTO.partialSuccess(message, successfulCount, errors);
            } else {
                String message = ErrorCodesManager.getErrorMessage(108);
                return ResponseDTO.failure(108, message, 0, errors);
            }
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }
    
    public ResponseDTO<List<EmployeeDTO>> getEmployeesByIds(List<Integer> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            String message = ErrorCodesManager.getErrorMessage(104);
            return ResponseDTO.failure(104, message, 0, null);
        }
        
        try {
            List<EmployeeDTO> employees = service.getEmployeesByIds(employeeIds);
            if (employees.isEmpty()) {
                String message = ErrorCodesManager.getErrorMessage(104);
                return ResponseDTO.failure(104, message, 0, null);
            }
            String message = "Employees found successfully.";
            return ResponseDTO.success(message, employees.size(), employees);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }
    
    public ResponseDTO<EmployeeDTO> getEmployeeById(int employeeId) {
        if (employeeId <= 0) {
            String message = ErrorCodesManager.getErrorMessage(105, employeeId);
            return ResponseDTO.failure(105, message, 0, null);
        }
        try {
            EmployeeDTO employee = service.getEmployeeById(employeeId);
            String message = "Employee found successfully.";
            return ResponseDTO.success(message, 1, employee);
        } catch (EmployeeNotFoundException e) {
            String message = ErrorCodesManager.getErrorMessage(106, employeeId);
            return ResponseDTO.failure(106, message, 0, null);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }
    
    public ResponseDTO<Integer> updateEmployee(EmployeeDTO employee) {
        if (employee == null) {
            String message = ErrorCodesManager.getErrorMessage(100, "employee data");
            return ResponseDTO.failure(100, message, 0, null);
        }
        if (employee.getEmployeeId() <= 0) {
            String message = ErrorCodesManager.getErrorMessage(105, employee.getEmployeeId());
            return ResponseDTO.failure(105, message, 0, null);
        }
        
        ResponseDTO<String> validationResponse = validateEmployeeDTO(employee);
        if (validationResponse.isFailure()) {
            return ResponseDTO.failure(validationResponse.getErrorCode(), validationResponse.getMessage(), 0, null);
        }

        try {
            int rowsAffected = service.updateEmployee(employee);
            String message = "Employee with ID " + employee.getEmployeeId() + " updated successfully.";
            return ResponseDTO.success(message, rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            String message = ErrorCodesManager.getErrorMessage(106, employee.getEmployeeId());
            return ResponseDTO.failure(106, message, 0, null);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }
    
    public ResponseDTO<Integer> deleteEmployee(int employeeId) {
        if (employeeId <= 0) {
            String message = ErrorCodesManager.getErrorMessage(105, employeeId);
            return ResponseDTO.failure(105, message, 0, null);
        }
        try {
            int rowsAffected = service.deleteEmployee(employeeId);
            String message = "Employee with ID " + employeeId + " deleted successfully.";
            return ResponseDTO.success(message, rowsAffected, rowsAffected);
        } catch (EmployeeNotFoundException e) {
            String message = ErrorCodesManager.getErrorMessage(106, employeeId);
            return ResponseDTO.failure(106, message, 0, null);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }

    public ResponseDTO<List<EmployeeDTO>> findAllEmployees() {
        try {
            List<EmployeeDTO> employees = service.findAllEmployees();
            String message = "All employees fetched successfully.";
            return ResponseDTO.success(message, employees.size(), employees);
        } catch (ServiceException e) {
            String message = ErrorCodesManager.getErrorMessage(109);
            return ResponseDTO.failure(109, message, 0, null);
        }
    }

    private ResponseDTO<String> validateEmployeeDTO(EmployeeDTO employee) {
        if (employee == null) {
            String message = ErrorCodesManager.getErrorMessage(100, "employee data");
            return ResponseDTO.failure(100, message, 0, null);
        }
        if (EmployeeValidator.validateStringField("First Name", employee.getFirstName(), 0) == null) {
            String message = ErrorCodesManager.getErrorMessage(101, "First Name");
            return ResponseDTO.failure(101, message, 0, null);
        }
        if (EmployeeValidator.validateEmail(employee.getEmail(), 0) == null) {
            String message = ErrorCodesManager.getErrorMessage(101, "Email");
            return ResponseDTO.failure(101, message, 0, null);
        }
        if (EmployeeValidator.validateJoinDate(employee.getJoinDate().toString(), 0) == null) {
            String message = ErrorCodesManager.getErrorMessage(101, "Join Date");
            return ResponseDTO.failure(101, message, 0, null);
        }
        return ResponseDTO.success("Employee data is valid.", 0, "Data valid.");
    }
}