package com.litmus7.employeemanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.litmus7.employeemanager.constant.SqlConstants;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.util.EmployeeValidator;

public class EmployeeDao {

    public boolean isEmployeeIdExists(Connection connection, int employeeId) throws SQLException {
        try (PreparedStatement checkStatement = connection.prepareStatement(SqlConstants.CHECK_DUPLICATE_EMPLOYEE)) {
            checkStatement.setInt(1, employeeId);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    public int saveEmployee(Connection connection, EmployeeDTO employee) throws SQLException {
        try (PreparedStatement insertStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE)) {
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

            return insertStatement.executeUpdate(); // Returns number of rows affected
        }
    }

    public EmployeeDTO findEmployeeById(Connection connection, int employeeId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_EMPLOYEE_BY_ID)) {
            statement.setInt(1, employeeId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Double salary = resultSet.getObject("salary", Double.class);
                    return new EmployeeDTO(
                        resultSet.getInt("employeeId"),
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
        }
        return null; // Employee not found
    }

    public List<EmployeeDTO> findAllEmployees(Connection connection) throws SQLException {
        List<EmployeeDTO> employees = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_ALL_EMPLOYEES)) {
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
        }
        return employees;
    }

    public int updateEmployee(Connection connection, EmployeeDTO employee) throws SQLException {
        try (PreparedStatement updateStatement = connection.prepareStatement(SqlConstants.UPDATE_EMPLOYEE)) {
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
        }
    }

    public int deleteEmployee(Connection connection, int employeeId) throws SQLException {
        try (PreparedStatement deleteStatement = connection.prepareStatement(SqlConstants.DELETE_EMPLOYEE)) {
            deleteStatement.setInt(1, employeeId);
            return deleteStatement.executeUpdate();
        }
    }

    public RecordProcessResult processEmployeeRecord(Connection connection, String values[], int lineNumber) {
        Integer employeeId = null;
        String firstName;
        String lastName;
        String email;
        String phone;
        String department;
        Double salary;
        LocalDate joinDateLocal;

        // --- 1. Validate number of fields ---
        if (values.length < 8) {
            return new RecordProcessResult(false, "Line " + lineNumber + ": Incomplete set of data. Expected 8 fields, got " + values.length + ".");
        }

        // --- 2. Validate and parse individual fields using EmployeeValidator ---
        // Validate Employee ID
        employeeId = EmployeeValidator.validateEmployeeId(values[0], lineNumber);
        if (employeeId == null) {
            return new RecordProcessResult(false, "Line " + lineNumber + ": Invalid Employee ID. Skipping record.");
        }

        // Validate First Name
        firstName = EmployeeValidator.validateStringField("First Name", values[1], lineNumber);
        if (firstName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": First Name is invalid. Skipping record.");

        // Validate Last Name
        lastName = EmployeeValidator.validateStringField("Last Name", values[2], lineNumber);
        if (lastName == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Last Name is invalid. Skipping record.");

        // Validate Email
        email = EmployeeValidator.validateEmail(values[3], lineNumber);
        if (email == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Email is invalid. Skipping record.");

        // Validate Phone
        phone = EmployeeValidator.validatePhoneNumber(values[4], lineNumber);
        if (phone == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Phone is invalid. Skipping record.");

        // Validate Department
        department = EmployeeValidator.validateStringField("Department", values[5], lineNumber);
        if (department == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Department is invalid. Skipping record.");

        // Validate Salary
        salary = EmployeeValidator.validateSalary(values[6], lineNumber);
        if (salary == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Salary is invalid. Skipping record.");

        // Validate Join Date
        joinDateLocal = EmployeeValidator.validateJoinDate(values[7], lineNumber);
        if (joinDateLocal == null) return new RecordProcessResult(false, "Line " + lineNumber + ": Join Date is invalid. Skipping record.");
        
        // Validate Duplicate EmployeeID
        try {
            if (isEmployeeIdExists(connection, employeeId)) {
                return new RecordProcessResult(false, "Line " + lineNumber + ": Employee with ID " + employeeId + " already exists (Duplicate).");
            }
        } catch (SQLException e) {
            return new RecordProcessResult(false, "Database error checking duplicate for Emp ID " + employeeId + " at line " + lineNumber + ": " + e.getMessage());
        }

        // --- All validations passed. Create Employee DTO and insert ---
        EmployeeDTO employeeToInsert = new EmployeeDTO(
            employeeId, firstName, lastName, email, phone, department, salary, joinDateLocal
        );

        try {
            int rowsAffected = saveEmployee(connection, employeeToInsert);
            if (rowsAffected > 0) {
                return new RecordProcessResult(true, "Successfully imported Employee ID: " + employeeId);
            } else {
                return new RecordProcessResult(false, "Line " + lineNumber + ": Failed to insert Employee ID: " + employeeId + " (0 rows affected).");
            }
        } catch (SQLException e) {
            return new RecordProcessResult(false, "Failed to insert Employee ID: " + employeeId + " from line " + lineNumber + ". Error: " + e.getMessage());
        }
    }
}
