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
	
	public void processEmployee(String values[],int lineNumber) {
		int employeeID;
		double employeeSalary = 0.0;
		Date joinDate = null;
		
		if(values.length < 8) { // validate number of fields
			System.err.println("Error at line " + lineNumber + ": Incomplete set of data. Skipping record.");
            return;
		}
		
		try{
			employeeID = Integer.parseInt(values[0].trim());
		} catch(NumberFormatException e) {
			System.err.println("Error at line " + lineNumber + ": Invalid employee ID '" + values[0] + "'. Skipping record.");
			return;
		}
		
		try(PreparedStatement checkStatement = connection.prepareStatement("SELECT COUNT(*) FROM employee WHERE emp_id = ?")){
			checkStatement.setInt(1, employeeID);
			try(ResultSet resultSet = checkStatement.executeQuery()){
				if(resultSet.next() && resultSet.getInt(1) > 0) {
					System.out.println("Warning at line " + lineNumber + ": Employee with ID " + employeeID + " already exists. Skipping record.");
					return;
				}
			} 
		} catch(SQLException e) {
			System.err.println("Database error checking for duplicate employee ID " + employeeID + " at line " + lineNumber + ": " + e.getMessage());
			return;
		}
		
		String firstName = values[1].trim();
		String lastName = values[2].trim();
		String email = values[3].trim();
		String phone = values[4].trim();
		String department = values[5].trim();
		
		try{
			employeeSalary = Double.parseDouble(values[6].trim());
			//System.out.println(employeeID + " salary is " + employeeSalary);
		} catch(NumberFormatException e) {
			System.err.println("Error at line " + lineNumber + ": Invalid salary '" + values[6] + "'. Skipping record.");
            return;
		}
		
		try{
			LocalDate localJoinDate = LocalDate.parse(values[7].trim());
			joinDate = Date.valueOf(localJoinDate);
		} catch (java.time.format.DateTimeParseException | IllegalArgumentException e) {
            System.err.println("Error at line " + lineNumber + ": Invalid date format '" + values[7] + "' (expected yyyy-mm-dd). Skipping record.");
            return;
		}
		
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
			System.out.println("Successfully imported Employee ID: " + employeeID + " from line " + lineNumber);
		} catch(SQLException e) {
			System.err.println("Failed to insert Employee ID: " + employeeID + " from line " + lineNumber + ". Error: " + e.getMessage());
		}
	}
		
}