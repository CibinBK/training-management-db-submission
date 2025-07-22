package com.litmus7;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

class DatabaseManager{
	private Connection connection;
	
	private static final String DB_URL = "jdbc:mysql://localhost:3306/employee_db";
	private static final String DB_USER = "root";
	private static final String DB_PASSWORD = "P@$$word";
	
	public void establishConnection() {
		try{
			connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD); // establish connection with database
			connection.setAutoCommit(false);
			System.out.println("Database connection established succesfully!");
		} catch(SQLException e) {
			System.out.println("Database connection failed!");
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try{
			if(connection != null && !connection.isClosed()) {
				connection.commit(); // commits all changes
				connection.close(); // closes the connection
				System.out.println("Database connection closed and all transactions completed!");
			}
		} catch(SQLException e) {
				System.out.println("Error closing database or commiting transactions!");
				e.printStackTrace();
			}
	}
	
    static class RecordProcessResult {
        final boolean success;
        final String message; // Message for this specific record (success or failure)

        RecordProcessResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

	public RecordProcessResult processEmployee(String values[],int lineNumber) {
		// Declare variables, will be assigned values by validator methods
		Integer employeeID = null; // Initialize to null for error messages
		String firstName;
		String lastName;
		String email;
		String phone;
		String department;
		Double employeeSalary;
		LocalDate localJoinDate;
		Date joinDate;
		
		// Validate number of fields
		if(values.length < 8) {
			return new RecordProcessResult(false, "Line " + lineNumber + ": Incomplete set of data. Expected 8 fields, got " + values.length + ".");
		}
		
		// Validate Employee ID
		employeeID = Validator.validateEmployeeId(values[0], lineNumber);
		if(employeeID == null) {
			return new RecordProcessResult(false, "Line " + lineNumber + ": Invalid Employee ID. Skipping record.");
		}
		
		// Check for duplicate entry based on emp_id
		try(PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE emp_id = ?")){
			checkStatement.setInt(1, employeeID);
			try(ResultSet resultSet = checkStatement.executeQuery()){
				if(resultSet.next() && resultSet.getInt(1) > 0) {
					return new RecordProcessResult(false, "Line " + lineNumber + ": Employee with ID " + employeeID + " already exists (Duplicate).");
				}
			} 
		} catch(SQLException e) {
			return new RecordProcessResult(false, "Line " + lineNumber + ": Database error checking duplicate for Emp ID " + employeeID + ": " + e.getMessage());
		}
		
		// Validate First Name
		firstName = Validator.validateStringField("First Name", values[1], lineNumber);
		if(firstName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": First Name is invalid. Skipping record.");
		
		// Validate Last Name
		lastName = Validator.validateStringField("Last Name", values[2], lineNumber);
		if(lastName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Last Name is invalid. Skipping record.");
		
		// Validate Email
		email = Validator.validateEmail(values[3], lineNumber);
		if(email == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Email is invalid. Skipping record.");
		
		// Validate Phone
		phone = Validator.validateStringField("Phone", values[4], lineNumber);
		if(phone == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Phone is invalid. Skipping record.");
		
		// Validate Department
		department = Validator.validateStringField("Department", values[5], lineNumber);
		if(department == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Department is invalid. Skipping record.");
		
		// Validate Salary
		employeeSalary = Validator.validateSalary(values[6], lineNumber);
		if(employeeSalary == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Salary is invalid. Skipping record.");
		
		// Validate Join Date
		localJoinDate = Validator.validateJoinDate(values[7], lineNumber);
		if(localJoinDate == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Join Date is invalid. Skipping record.");
		
		// Convert LocalDate to java.sql.Date
		joinDate = Date.valueOf(localJoinDate);
		
		try(PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO employee (emp_id,first_name,last_name,email,phone,department,salary,join_date) VALUES (?, ?, ?,?,?,?,?,?)")) {
			insertStatement.setInt(1, employeeID);
			insertStatement.setString(2, firstName);
			insertStatement.setString(3, lastName);
			insertStatement.setString(4, email);
			insertStatement.setString(5, phone);
			insertStatement.setString(6, department);
			insertStatement.setDouble(7, employeeSalary);
            insertStatement.setDate(8, joinDate);
			
			insertStatement.executeUpdate();
			return new RecordProcessResult(true, "Successfully imported Employee ID: " + employeeID);
		} catch(SQLException e) {
			return new RecordProcessResult(false, "Failed to insert Employee ID: " + employeeID + " from line " + lineNumber + ". Error: " + e.getMessage());
		}
	}
		
}
