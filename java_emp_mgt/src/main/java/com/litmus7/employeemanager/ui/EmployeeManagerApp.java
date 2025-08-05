package com.litmus7.employeemanager.ui;

import com.litmus7.employeemanager.constant.AppConstants;
import com.litmus7.employeemanager.controller.EmployeeController;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.ResponseDTO;
import java.util.List;

public class EmployeeManagerApp {

    public static void main(String[] args) {

        EmployeeController controller = new EmployeeController();

        // 1. Import Employees from CSV
        System.out.println("\n----- Running Employee Import -----");
        ResponseDTO<List<String>> importResponse = controller.importEmployees(AppConstants.CSV_FILE_PATH);
        
        if (importResponse.getStatusCode() == AppConstants.STATUS_CODE_SUCCESS) {
            System.out.println("Import Successful: " + importResponse.getMessage());
            System.out.println("Total Employees Imported: " + importResponse.getAffectedCount());
        } else if (importResponse.getStatusCode() == AppConstants.STATUS_CODE_PARTIAL_SUCCESS) {
            System.out.println("Import Partially Successful: " + importResponse.getMessage());
            System.out.println("Employees Imported: " + importResponse.getAffectedCount());
            System.out.println("Failed/Skipped Records Details:");
            if (importResponse.getData() instanceof List) {
                List<String> errors = (List<String>) importResponse.getData();
                for (String error : errors) {
                    System.out.println("  - " + error);
                }
            }
        } else {
            // This is where the new error messages from our exceptions will be printed.
            System.err.println("Import Failed: " + importResponse.getMessage());
            System.err.println("Employees Imported Before Failure: " + importResponse.getAffectedCount());
            System.err.println("Failure Details:");
            if (importResponse.getData() instanceof List) {
                List<String> errors = (List<String>) importResponse.getData();
                for (String error : errors) {
                    System.err.println("  - " + error);
                }
            }
        }

        // 5. Fetch All Employees
        System.out.println("\n----- Fetching All Employees -----");
        ResponseDTO<List<EmployeeDTO>> fetchAllResponse = controller.findAllEmployees();
        if (fetchAllResponse.isSuccess()) {
            System.out.println("Fetch All Successful: " + fetchAllResponse.getMessage());
            List<EmployeeDTO> allEmployees = fetchAllResponse.getData();
            if (allEmployees != null && !allEmployees.isEmpty()) {
                System.out.println("--- All Employees List (" + allEmployees.size() + ") ---");
                
                System.out.printf("%-5s %-15s %-15s %-30s %-15s %-15s %-10s %-12s\n",
                                  "ID", "First Name", "Last Name", "Email", "Phone", "Dept", "Salary", "Join Date");
                System.out.println("--------------------------------------------------------------------------------------------------------------------------------");
                for (EmployeeDTO emp : allEmployees) {
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
            // This is where error messages from new exceptions for the findAllEmployees method will be printed.
            System.err.println("Fetch All Failed: " + fetchAllResponse.getMessage());
        }
        
        System.out.println("\nApplication: Program execution finished.");
    }
}