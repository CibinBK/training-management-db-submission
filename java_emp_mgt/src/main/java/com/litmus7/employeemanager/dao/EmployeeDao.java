package com.litmus7.employeemanager.dao;

import com.litmus7.employeemanager.constant.SqlConstants;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.exception.DAOException;
import com.litmus7.employeemanager.util.DatabaseConnectionManager;
import com.litmus7.employeemanager.util.EmployeeValidator;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EmployeeDao {

    public int saveEmployee(EmployeeDTO employee) throws DAOException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE)) {
            insertStatement.setInt(1, employee.getEmployeeId());
            insertStatement.setString(2, employee.getFirstName());
            insertStatement.setString(3, employee.getLastName());
            insertStatement.setString(4, employee.getEmail());
            insertStatement.setString(5, employee.getPhone());
            insertStatement.setString(6, employee.getDepartment());

            if (employee.getSalary() != null) {
                insertStatement.setDouble(7, employee.getSalary());
            } else {
                insertStatement.setNull(7, java.sql.Types.DOUBLE);
            }
            insertStatement.setDate(8, Date.valueOf(employee.getJoinDate()));

            return insertStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error saving employee " + employee.getEmployeeId(), e);
        }
    }
    
    // New method for transaction-based department transfer
    public int[] transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) throws DAOException {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new int[0];
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false); // Start the transaction
            int[] updateCounts;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SqlConstants.UPDATE_EMPLOYEE_DEPARTMENT)) {
                for (Integer id : employeeIds) {
                    preparedStatement.setString(1, newDepartment);
                    preparedStatement.setInt(2, id);
                    preparedStatement.addBatch();
                }
                updateCounts = preparedStatement.executeBatch();
                connection.commit(); // Commit all changes if successful
            } catch (SQLException e) {
                connection.rollback(); // Rollback all changes if an exception occurs
                throw new DAOException("Error updating departments for employees. Transaction rolled back.", e);
            }
            return updateCounts;
        } catch (SQLException e) {
            throw new DAOException("Database connection or transaction error.", e);
        }
    }

    public int[] addEmployeesInBatch(List<EmployeeDTO> employeeList) throws DAOException {
        if (employeeList == null || employeeList.isEmpty()) {
            return new int[0];
        }

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE)) {
            
            for (EmployeeDTO employee : employeeList) {
                preparedStatement.setInt(1, employee.getEmployeeId());
                preparedStatement.setString(2, employee.getFirstName());
                preparedStatement.setString(3, employee.getLastName());
                preparedStatement.setString(4, employee.getEmail());
                preparedStatement.setString(5, employee.getPhone());
                preparedStatement.setString(6, employee.getDepartment());

                if (employee.getSalary() != null) {
                    preparedStatement.setDouble(7, employee.getSalary());
                } else {
                    preparedStatement.setNull(7, java.sql.Types.DOUBLE);
                }
                preparedStatement.setDate(8, Date.valueOf(employee.getJoinDate()));

                preparedStatement.addBatch();
            }
            
            return preparedStatement.executeBatch();
        } catch (SQLException e) {
            throw new DAOException("Error in batch insertion of employees", e);
        }
    }

    public boolean isEmployeeIdExists(int employeeId) throws DAOException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(SqlConstants.CHECK_DUPLICATE_EMPLOYEE)) {
            checkStatement.setInt(1, employeeId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DAOException("Error checking for duplicate employee ID " + employeeId, e);
        }
    }
    
    public List<EmployeeDTO> findEmployeesByIds(List<Integer> employeeIds) throws DAOException {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<EmployeeDTO> employees = new ArrayList<>();
        String placeholders = employeeIds.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = SqlConstants.FIND_EMPLOYEES_BY_IDS.replace("?", placeholders);

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            for (int i = 0; i < employeeIds.size(); i++) {
                statement.setInt(i + 1, employeeIds.get(i));
            }
            
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Double salary = resultSet.getObject("salary", Double.class);
                    employees.add(new EmployeeDTO(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("department"),
                        salary,
                        resultSet.getDate("join_date").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employees by IDs", e);
        }
        return employees;
    }

    public EmployeeDTO findEmployeeById(int employeeId) throws DAOException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_EMPLOYEE_BY_ID)) {
            statement.setInt(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Double salary = resultSet.getObject("salary", Double.class);
                    return new EmployeeDTO(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("department"),
                        salary,
                        resultSet.getDate("join_date").toLocalDate()
                    );
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding employee by ID " + employeeId, e);
        }
        return null;
    }
    
    public List<EmployeeDTO> findAllEmployees() throws DAOException {
        List<EmployeeDTO> employees = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_ALL_EMPLOYEES)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Double salary = resultSet.getObject("salary", Double.class);
                    employees.add(new EmployeeDTO(
                        resultSet.getInt("employee_id"),
                        resultSet.getString("first_name"),
                        resultSet.getString("last_name"),
                        resultSet.getString("email"),
                        resultSet.getString("phone"),
                        resultSet.getString("department"),
                        salary,
                        resultSet.getDate("join_date").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error finding all employees", e);
        }
        return employees;
    }

    public int updateEmployee(EmployeeDTO employee) throws DAOException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(SqlConstants.UPDATE_EMPLOYEE)) {
            updateStatement.setString(1, employee.getFirstName());
            updateStatement.setString(2, employee.getLastName());
            updateStatement.setString(3, employee.getEmail());
            updateStatement.setString(4, employee.getPhone());
            updateStatement.setString(5, employee.getDepartment());

            if (employee.getSalary() != null) {
                updateStatement.setDouble(6, employee.getSalary());
            } else {
                updateStatement.setNull(6, java.sql.Types.DOUBLE);
            }
            updateStatement.setDate(7, Date.valueOf(employee.getJoinDate()));
            updateStatement.setInt(8, employee.getEmployeeId());

            return updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error updating employee " + employee.getEmployeeId(), e);
        }
    }

    public int deleteEmployee(int employeeId) throws DAOException {
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(SqlConstants.DELETE_EMPLOYEE)) {
            deleteStatement.setInt(1, employeeId);
            return deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Error deleting employee with ID " + employeeId, e);
        }
    }

    public RecordProcessResult processEmployeeRecord(Connection connection, String values[], int lineNumber) throws DAOException {
        Integer employeeId = null;
        String firstName;
        String lastName;
        String email;
        String phone;
        String department;
        Double salary;
        LocalDate joinDateLocal;

        if (values.length < 8) {
            return new RecordProcessResult(false, "Line " + lineNumber + ": Incomplete set of data. Expected 8 fields, got " + values.length + ".");
        }

        employeeId = EmployeeValidator.validateEmployeeId(values[0], lineNumber);
        if (employeeId == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Invalid Employee ID. Skipping record.");
        firstName = EmployeeValidator.validateStringField("First Name", values[1], lineNumber);
        if (firstName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": First Name is invalid. Skipping record.");
        lastName = EmployeeValidator.validateStringField("Last Name", values[2], lineNumber);
        if (lastName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Last Name is invalid. Skipping record.");
        email = EmployeeValidator.validateEmail(values[3], lineNumber);
        if (email == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Email is invalid. Skipping record.");
        phone = EmployeeValidator.validatePhoneNumber(values[4], lineNumber);
        if (phone == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Phone is invalid. Skipping record.");
        department = EmployeeValidator.validateStringField("Department", values[5], lineNumber);
        if (department == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Department is invalid. Skipping record.");
        salary = EmployeeValidator.validateSalary(values[6], lineNumber);
        if (salary == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Salary is invalid. Skipping record.");
        joinDateLocal = EmployeeValidator.validateJoinDate(values[7], lineNumber);
        if (joinDateLocal == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Join Date is invalid. Skipping record.");
        
        try {
            if (isEmployeeIdExists(employeeId)) {
                return new RecordProcessResult(false, "Line " + lineNumber + ": Employee with ID " + employeeId + " already exists (Duplicate).");
            }
        } catch (DAOException e) {
             throw new DAOException("Database error checking for duplicate employee ID " + employeeId, e);
        }

        EmployeeDTO employeeToInsert = new EmployeeDTO(
            employeeId, firstName, lastName, email, phone, department, salary, joinDateLocal
        );

        try {
            PreparedStatement insertStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE);
            insertStatement.setInt(1, employeeToInsert.getEmployeeId());
            insertStatement.setString(2, employeeToInsert.getFirstName());
            insertStatement.setString(3, employeeToInsert.getLastName());
            insertStatement.setString(4, employeeToInsert.getEmail());
            insertStatement.setString(5, employeeToInsert.getPhone());
            insertStatement.setString(6, employeeToInsert.getDepartment());
            if (employeeToInsert.getSalary() != null) {
                insertStatement.setDouble(7, employeeToInsert.getSalary());
            } else {
                insertStatement.setNull(7, java.sql.Types.DOUBLE);
            }
            insertStatement.setDate(8, Date.valueOf(employeeToInsert.getJoinDate()));
            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                return new RecordProcessResult(true, "Successfully imported Employee ID: " + employeeId);
            } else {
                return new RecordProcessResult(false, "Line " + lineNumber + ": Failed to insert Employee ID: " + employeeId + " (0 rows affected).");
            }
        } catch (SQLException e) {
            throw new DAOException("Failed to insert Employee ID: " + employeeId + " from line " + lineNumber, e);
        }
    }
}