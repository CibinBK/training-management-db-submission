package com.litmus7.employeemanager.ui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
//import java.time.LocalDate;
import java.util.List;
import com.litmus7.employeemanager.constant.AppConstants;
import com.litmus7.employeemanager.dto.ResponseDTO;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.property.DatabaseProperties;
import com.litmus7.employeemanager.services.EmployeeManagementService;

public class EmployeeManagerApp {

    public static void main(String[] args) {
        String dbUrl = DatabaseProperties.getDbUrl();
        String dbUser = DatabaseProperties.getDbUser();
        String dbPassword = DatabaseProperties.getDbPassword();

        try (Connection connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            System.out.println("Application: Database connection established for program duration.");

            EmployeeManagementService service = new EmployeeManagementService();

            // 1. Import Employees from CSV
            System.out.println("\n----- Running Employee Import -----");
            ResponseDTO<List<String>> importResponse = service.importEmployees(connection, AppConstants.CSV_FILE_PATH);
            
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

//            // 2. Fetch an Employee by ID
//            System.out.println("\n----- Fetching Employee ID 101 -----");
//            ResponseDTO<EmployeeDTO> fetchResponse = service.getEmployeeById(connection, 101);
//            if (fetchResponse.isSuccess()) {
//                System.out.println("Fetch Successful: " + fetchResponse.getMessage());
//                System.out.println("Fetched Employee: " + fetchResponse.getData());
//            } else {
//                System.err.println("Fetch Failed: " + fetchResponse.getMessage());
//            }
//
//            System.out.println("\n----- Fetching Non-Existent Employee ID 999 -----");
//            ResponseDTO<EmployeeDTO> fetchNotFoundResponse = service.getEmployeeById(connection, 999);
//            if (fetchNotFoundResponse.isSuccess()) {
//                System.out.println("Fetch Successful: " + fetchNotFoundResponse.getMessage());
//                System.out.println("Fetched Employee: " + fetchNotFoundResponse.getData());
//            } else {
//                System.err.println("Fetch Failed: " + fetchNotFoundResponse.getMessage());
//            }

//            // 3. Update an Employee
//            System.out.println("\n----- Updating Employee ID 102 -----");
//            EmployeeDTO updatedEmployee = new EmployeeDTO(
//                102, "Jane", "Foster", "jane.foster@example.com", "9998887777",
//                "Marketing", 70000.00, LocalDate.of(2022, 1, 10)
//            );
//            ResponseDTO<Integer> updateResponse = service.updateEmployee(connection, updatedEmployee);
//
//            if (updateResponse.isSuccess()) {
//                System.out.println("Update Successful: " + updateResponse.getMessage());
//                System.out.println("Rows Affected: " + updateResponse.getAffectedCount());
//            } else {
//                System.err.println("Update Failed: " + updateResponse.getMessage());
//            }
//
//            // Verify update by fetching again
//            System.out.println("\n----- Fetching Employee ID 102 (After Update) -----");
//            ResponseDTO<EmployeeDTO> fetchUpdatedResponse = service.getEmployeeById(connection, 102);
//            if (fetchUpdatedResponse.isSuccess()) {
//                System.out.println("Fetch Successful: " + fetchUpdatedResponse.getMessage());
//                System.out.println("Fetched Employee: " + fetchUpdatedResponse.getData());
//            } else {
//                System.err.println("Fetch Failed: " + fetchUpdatedResponse.getMessage());
//            }

//            // 4. Delete an Employee
//            System.out.println("\n----- Deleting Employee ID 103 -----");
//            ResponseDTO<Integer> deleteResponse = service.deleteEmployee(connection, 103);
//            
//            if (deleteResponse.isSuccess()) {
//                System.out.println("Delete Successful: " + deleteResponse.getMessage());
//                System.out.println("Rows Affected: " + deleteResponse.getAffectedCount());
//            } else {
//                System.err.println("Delete Failed: " + deleteResponse.getMessage());
//            }
//
//            // Verify deletion by attempting to fetch
//            System.out.println("\n----- Fetching Employee ID 103 (After Delete) -----");
//            ResponseDTO<EmployeeDTO> fetchDeletedResponse = service.getEmployeeById(connection, 103);
//            if (fetchDeletedResponse.isSuccess()) {
//                System.out.println("Fetch Successful: " + fetchDeletedResponse.getMessage());
//                System.out.println("Fetched Employee: " + fetchDeletedResponse.getData());
//            } else {
//                System.err.println("Fetch Failed: " + fetchDeletedResponse.getMessage());
//            }
            
         // 5. Fetch All Employees
            System.out.println("\n----- Fetching All Employees -----");
            ResponseDTO<List<EmployeeDTO>> fetchAllResponse = service.findAllEmployees(connection);
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
                System.err.println("Fetch All Failed: " + fetchAllResponse.getMessage());
            }


        } catch (SQLException e) {
            System.err.println("Application: Critical Database Connection Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Application: An unexpected application error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            System.out.println("\nApplication: Program execution finished.");
        }
    }
}
