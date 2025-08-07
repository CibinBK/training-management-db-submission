package com.litmus7.employeemanager.ui;

import com.litmus7.employeemanager.constant.AppConstants;
import com.litmus7.employeemanager.controller.EmployeeController;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;

public class EmployeeManagerApp {

    public static void main(String[] args) {

        EmployeeController controller = new EmployeeController();

        // 1. IMPORT EMPLOYEES FROM CSV
        System.out.println("\n----- Running Employee Import -----");
        ResponseDTO<List<String>> importResponse = controller.importEmployees(AppConstants.CSV_FILE_PATH);
        printImportResponse(importResponse);
        
        // 2. DIRECTLY ADD A NEW EMPLOYEE (Single)
        System.out.println("\n----- Employee Addition (Single) -----");
        EmployeeDTO newEmployee = new EmployeeDTO(
            111, "Peter", "Parker", "peter.parker@example.com", "1112223333",
            "IT", 60000.00, LocalDate.of(2023, 5, 20)
        );
        ResponseDTO<Integer> addResponse = controller.addEmployee(newEmployee);
        System.out.println("Adding Employee ID " + newEmployee.getEmployeeId() + ": " + addResponse.getMessage());

        // 3. BATCH ADD MULTIPLE EMPLOYEES
        System.out.println("\n----- Employee Addition (Batch) -----");
        List<EmployeeDTO> employeesToAdd = new ArrayList<>();
        employeesToAdd.add(new EmployeeDTO(112, "Diana", "Prince", "diana.prince@example.com", "1112224444", "Marketing", 75000.00, LocalDate.of(2023, 8, 1)));
        employeesToAdd.add(new EmployeeDTO(113, "Bruce", "Wayne", "bruce.wayne@example.com", "1112225555", "Engineering", 90000.00, LocalDate.of(2023, 8, 2)));
        
        ResponseDTO<int[]> batchResponse = controller.addEmployeesInBatch(employeesToAdd);
        printBatchResponse(batchResponse);
        
        // 4. TRANSFER DEPARTMENT (TRANSACTION)
        System.out.println("\n----- Department Transfer -----");

        // Successful Transfer
        System.out.println("-> Attempting a successful transfer for IDs 101, 103 to 'New Dept'");
        List<Integer> successfulTransferIds = Arrays.asList(101, 103);
        ResponseDTO<int[]> transferResponseSuccess = controller.transferEmployeesToDepartment(successfulTransferIds, "New Dept");
        printBatchResponse(transferResponseSuccess);


        // 5. FETCH MULTIPLE EMPLOYEES
        System.out.println("\n----- Fetching Multiple Employees (IDs 101, 104) -----");
        List<Integer> idsToFetch = Arrays.asList(101, 104);
        ResponseDTO<List<EmployeeDTO>> fetchByIdsResponse = controller.getEmployeesByIds(idsToFetch);
        printEmployeeListResponse(fetchByIdsResponse);

        // 6. FETCH AND DELETE LOGIC
        System.out.println("\n----- Demonstrating Fetch and Delete Logic -----");
        
        // Fetch an existing employee (e.g., ID 101)
        int fetchId = 101;
        ResponseDTO<EmployeeDTO> fetchResponse = controller.getEmployeeById(fetchId);
        System.out.println("Fetching Employee ID " + fetchId + ": " + fetchResponse.getMessage());

        // Delete an existing employee (e.g., ID 102)
        int deleteId = 102;
        ResponseDTO<Integer> deleteResponse = controller.deleteEmployee(deleteId);
        System.out.println("Deleting Employee ID " + deleteId + ": " + deleteResponse.getMessage());

        // 7. UPDATE LOGIC
        System.out.println("\n----- Update Logic -----");
        try (Scanner scanner = new Scanner(System.in)) {
            EmployeeDTO nonExistentEmployee = new EmployeeDTO(
                999, "Dean", "Jacob", "dean.jacob@example.com", "1234567890",
                "IT", 55000.00, LocalDate.of(2025, 1, 1)
            );
            
            ResponseDTO<Integer> updateResponse = controller.updateEmployee(nonExistentEmployee);
            
            if (updateResponse.isFailure()) {
                System.out.println(updateResponse.getMessage());
                System.out.print("This employee does not exist. Do you want to add them? (yes/no): ");
                String userResponse = scanner.nextLine().trim();
                
                if ("yes".equalsIgnoreCase(userResponse)) {
                    ResponseDTO<Integer> addResponse2 = controller.addEmployee(nonExistentEmployee);
                    if (addResponse2.isSuccess()) {
                        System.out.println(addResponse2.getMessage());
                    } else {
                        System.err.println("Failed to add employee: " + addResponse2.getMessage());
                    }
                } else {
                    System.out.println("Employee was not added.");
                }
            } else {
                System.out.println(updateResponse.getMessage());
            }
        }

        // 8. FETCH ALL EMPLOYEES
        System.out.println("\n----- Fetching All Employees -----");
        ResponseDTO<List<EmployeeDTO>> fetchAllResponse = controller.findAllEmployees();
        printEmployeeListResponse(fetchAllResponse);
        
        System.out.println("\nApplication: Program execution finished.");
    }

    private static void printImportResponse(ResponseDTO<List<String>> response) {
        if (response.getStatusCode() == AppConstants.STATUS_CODE_SUCCESS) {
            System.out.println("Import Successful: " + response.getMessage());
            System.out.println("Total Employees Imported: " + response.getAffectedCount());
        } else if (response.getStatusCode() == AppConstants.STATUS_CODE_PARTIAL_SUCCESS) {
            System.out.println("Import Partially Successful: " + response.getMessage());
            System.out.println("Employees Imported: " + response.getAffectedCount());
            System.out.println("Failed/Skipped Records Details:");
            if (response.getData() != null && !response.getData().isEmpty()) {
                for (String error : response.getData()) {
                    System.out.println("  - " + error);
                }
            }
        } else {
            System.err.println("Import Failed: " + response.getMessage());
            System.err.println("Failure Details:");
            if (response.getData() != null && !response.getData().isEmpty()) {
                for (String error : response.getData()) {
                    System.err.println("  - " + error);
                }
            }
        }
    }
    
    private static void printBatchResponse(ResponseDTO<int[]> response) {
        if (response.isSuccess()) {
            System.out.println("Status: " + response.getMessage());
            System.out.println("Successful updates/inserts: " + response.getAffectedCount());
        } else {
            System.err.println("Batch operation failed: " + response.getMessage());
        }
    }

    private static void printEmployeeListResponse(ResponseDTO<List<EmployeeDTO>> response) {
        if (response.isSuccess()) {
            System.out.println("Fetch Successful: " + response.getMessage());
            List<EmployeeDTO> employees = response.getData();
            if (employees != null && !employees.isEmpty()) {
                System.out.println("--- Employees List (" + employees.size() + ") ---");
                
                System.out.printf("%-5s %-15s %-15s %-30s %-15s %-15s %-10s %-12s\n",
                                  "ID", "First Name", "Last Name", "Email", "Phone", "Dept", "Salary", "Join Date");
                System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
                for (EmployeeDTO emp : employees) {
                    System.out.printf("%-5d %-15s %-15s %-30s %-15s %-15s %-10.2f %-12s\n",
                                      emp.getEmployeeId(),
                                      emp.getFirstName(),
                                      emp.getLastName(),
                                      emp.getEmail(),
                                      emp.getPhone(),
                                      emp.getDepartment(),
                                      emp.getSalary(),
                                      emp.getJoinDate());
                }
                System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
            } else {
                System.out.println("No employees found.");
            }
        } else {
            System.err.println("Fetch Failed: " + response.getMessage());
        }
    }
}