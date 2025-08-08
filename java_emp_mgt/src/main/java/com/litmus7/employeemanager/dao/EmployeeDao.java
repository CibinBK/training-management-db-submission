package com.litmus7.employeemanager.dao;

import com.litmus7.employeemanager.constant.SqlConstants;
import com.litmus7.employeemanager.dto.EmployeeDTO;
import com.litmus7.employeemanager.dto.RecordProcessResult;
import com.litmus7.employeemanager.exception.DAOException;
import com.litmus7.employeemanager.util.DatabaseConnectionManager;
import com.litmus7.employeemanager.util.EmployeeValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    
    private static final Logger logger = LogManager.getLogger(EmployeeDao.class);

    public int saveEmployee(EmployeeDTO employee) throws DAOException {
        logger.trace("Entering saveEmployee() for employee ID: {}", employee.getEmployeeId());
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE)) {
            
            logger.debug("Executing SQL: {}", SqlConstants.INSERT_EMPLOYEE);
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

            int rowsAffected = insertStatement.executeUpdate();
            logger.info("Successfully saved employee with ID: {}", employee.getEmployeeId());
            logger.trace("Exiting saveEmployee().");
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("Error saving employee {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            throw new DAOException("Error saving employee " + employee.getEmployeeId(), e);
        }
    }
    
    public int[] transferEmployeesToDepartment(List<Integer> employeeIds, String newDepartment) throws DAOException {
        logger.trace("Entering transferEmployeesToDepartment() for IDs: {}", employeeIds);
        if (employeeIds == null || employeeIds.isEmpty()) {
            return new int[0];
        }

        try (Connection connection = DatabaseConnectionManager.getConnection()) {
            connection.setAutoCommit(false);
            logger.info("Transaction started for department transfer.");
            int[] updateCounts;

            try (PreparedStatement preparedStatement = connection.prepareStatement(SqlConstants.UPDATE_EMPLOYEE_DEPARTMENT)) {
                logger.debug("Executing SQL in batch: {}", SqlConstants.UPDATE_EMPLOYEE_DEPARTMENT);
                for (Integer id : employeeIds) {
                    preparedStatement.setString(1, newDepartment);
                    preparedStatement.setInt(2, id);
                    preparedStatement.addBatch();
                    logger.debug("Added update for employee ID {} to batch.", id);
                }
                updateCounts = preparedStatement.executeBatch();
                connection.commit();
                logger.info("Transaction committed successfully for department transfer.");
            } catch (SQLException e) {
                logger.error("Error updating departments. Attempting rollback.", e);
                connection.rollback();
                throw new DAOException("Error updating departments for employees. Transaction rolled back.", e);
            }
            logger.trace("Exiting transferEmployeesToDepartment().");
            return updateCounts;
        } catch (SQLException e) {
            logger.error("Database connection or transaction error during department transfer.", e);
            throw new DAOException("Database connection or transaction error.", e);
        }
    }

    public int[] addEmployeesInBatch(List<EmployeeDTO> employeeList) throws DAOException {
        logger.trace("Entering addEmployeesInBatch() for {} employees.", employeeList.size());
        if (employeeList == null || employeeList.isEmpty()) {
            return new int[0];
        }

        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SqlConstants.INSERT_EMPLOYEE)) {
            
            for (EmployeeDTO employee : employeeList) {
                logger.debug("Adding employee ID {} to batch.", employee.getEmployeeId());
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
            
            logger.debug("Executing batch insert for {} employees.", employeeList.size());
            int[] results = preparedStatement.executeBatch();
            logger.info("Batch insertion completed. {} statements executed.", results.length);
            return results;
        } catch (SQLException e) {
            logger.error("Error in batch insertion of employees: {}", e.getMessage(), e);
            throw new DAOException("Error in batch insertion of employees", e);
        }
    }

    public boolean isEmployeeIdExists(int employeeId) throws DAOException {
        logger.trace("Entering isEmployeeIdExists() for ID: {}", employeeId);
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement checkStatement = connection.prepareStatement(SqlConstants.CHECK_DUPLICATE_EMPLOYEE)) {
            checkStatement.setInt(1, employeeId);
            logger.debug("Executing SQL: {}", SqlConstants.CHECK_DUPLICATE_EMPLOYEE);
            try (ResultSet resultSet = checkStatement.executeQuery()) {
                boolean exists = resultSet.next() && resultSet.getInt(1) > 0;
                logger.debug("Employee ID {} exists: {}", employeeId, exists);
                return exists;
            }
        } catch (SQLException e) {
            logger.error("Error checking for duplicate employee ID {}: {}", employeeId, e.getMessage(), e);
            throw new DAOException("Error checking for duplicate employee ID " + employeeId, e);
        }
    }
    
    public List<EmployeeDTO> findEmployeesByIds(List<Integer> employeeIds) throws DAOException {
        logger.trace("Entering findEmployeesByIds() for {} IDs.", employeeIds.size());
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
            
            logger.debug("Executing SQL: {}", sql);
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
            logger.info("Found {} employees for the given IDs.", employees.size());
        } catch (SQLException e) {
            logger.error("Error finding employees by IDs: {}", e.getMessage(), e);
            throw new DAOException("Error finding employees by IDs", e);
        }
        logger.trace("Exiting findEmployeesByIds().");
        return employees;
    }

    public EmployeeDTO findEmployeeById(int employeeId) throws DAOException {
        logger.trace("Entering findEmployeeById() for ID: {}", employeeId);
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_EMPLOYEE_BY_ID)) {
            statement.setInt(1, employeeId);
            logger.debug("Executing SQL: {}", SqlConstants.FIND_EMPLOYEE_BY_ID);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Double salary = resultSet.getObject("salary", Double.class);
                    logger.info("Successfully found employee with ID: {}", employeeId);
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
            logger.error("Error finding employee by ID {}: {}", employeeId, e.getMessage(), e);
            throw new DAOException("Error finding employee by ID " + employeeId, e);
        }
        logger.info("Employee with ID {} not found.", employeeId);
        logger.trace("Exiting findEmployeeById().");
        return null;
    }
    
    public List<EmployeeDTO> findAllEmployees() throws DAOException {
        logger.trace("Entering findAllEmployees().");
        List<EmployeeDTO> employees = new ArrayList<>();
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SqlConstants.FIND_ALL_EMPLOYEES)) {
            
            logger.debug("Executing SQL: {}", SqlConstants.FIND_ALL_EMPLOYEES);
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
            logger.info("Found {} total employees.", employees.size());
        } catch (SQLException e) {
            logger.error("Error finding all employees: {}", e.getMessage(), e);
            throw new DAOException("Error finding all employees", e);
        }
        logger.trace("Exiting findAllEmployees().");
        return employees;
    }

    public int updateEmployee(EmployeeDTO employee) throws DAOException {
        logger.trace("Entering updateEmployee() for ID: {}", employee.getEmployeeId());
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(SqlConstants.UPDATE_EMPLOYEE)) {
            
            logger.debug("Executing SQL: {}", SqlConstants.UPDATE_EMPLOYEE);
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

            int rowsAffected = updateStatement.executeUpdate();
            logger.info("Updated employee with ID {}. Rows affected: {}", employee.getEmployeeId(), rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("Error updating employee with ID {}: {}", employee.getEmployeeId(), e.getMessage(), e);
            throw new DAOException("Error updating employee " + employee.getEmployeeId(), e);
        }
    }

    public int deleteEmployee(int employeeId) throws DAOException {
        logger.trace("Entering deleteEmployee() for ID: {}", employeeId);
        try (Connection connection = DatabaseConnectionManager.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(SqlConstants.DELETE_EMPLOYEE)) {
            deleteStatement.setInt(1, employeeId);
            logger.debug("Executing SQL: {}", SqlConstants.DELETE_EMPLOYEE);
            int rowsAffected = deleteStatement.executeUpdate();
            logger.info("Deleted employee with ID {}. Rows affected: {}", employeeId, rowsAffected);
            return rowsAffected;
        } catch (SQLException e) {
            logger.error("Error deleting employee with ID {}: {}", employeeId, e.getMessage(), e);
            throw new DAOException("Error deleting employee with ID " + employeeId, e);
        }
    }

    public RecordProcessResult processEmployeeRecord(Connection connection, String values[], int lineNumber) throws DAOException {
        logger.trace("Entering processEmployeeRecord() for line: {}", lineNumber);
        Integer employeeId = null;
        String firstName;
        String lastName;
        String email;
        String phone;
        String department;
        Double salary;
        LocalDate joinDateLocal;

        if (values.length < 8) {
            logger.warn("Incomplete data on line {}. Expected 8 fields, got {}.", lineNumber, values.length);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Incomplete set of data. Expected 8 fields, got " + values.length + ".");
        }

        employeeId = EmployeeValidator.validateEmployeeId(values[0], lineNumber);
        if (employeeId == null) {
            logger.warn("Invalid employee ID on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Invalid Employee ID. Skipping record.");
        }
        firstName = EmployeeValidator.validateStringField("First Name", values[1], lineNumber);
        if (firstName == null) {
            logger.warn("Invalid first name on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": First Name is invalid. Skipping record.");
        }
        lastName = EmployeeValidator.validateStringField("Last Name", values[2], lineNumber);
        if (lastName == null) {
            logger.warn("Invalid last name on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Last Name is invalid. Skipping record.");
        }
        email = EmployeeValidator.validateEmail(values[3], lineNumber);
        if (email == null) {
            logger.warn("Invalid email on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Email is invalid. Skipping record.");
        }
        phone = EmployeeValidator.validatePhoneNumber(values[4], lineNumber);
        if (phone == null) {
            logger.warn("Invalid phone number on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Phone is invalid. Skipping record.");
        }
        department = EmployeeValidator.validateStringField("Department", values[5], lineNumber);
        if (department == null) {
            logger.warn("Invalid department on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Department is invalid. Skipping record.");
        }
        salary = EmployeeValidator.validateSalary(values[6], lineNumber);
        if (salary == null) {
            logger.warn("Invalid salary on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Salary is invalid. Skipping record.");
        }
        joinDateLocal = EmployeeValidator.validateJoinDate(values[7], lineNumber);
        if (joinDateLocal == null) {
            logger.warn("Invalid join date on line {}. Skipping record.", lineNumber);
            return new RecordProcessResult(false, "Line " + lineNumber + ": Join Date is invalid. Skipping record.");
        }
        
        try {
            if (isEmployeeIdExists(employeeId)) {
                logger.warn("Duplicate employee ID {} found on line {}.", employeeId, lineNumber);
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
            
            logger.debug("Executing SQL: {} for employee ID {}.", SqlConstants.INSERT_EMPLOYEE, employeeId);
            int rowsAffected = insertStatement.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("Successfully inserted employee with ID {} from line {}.", employeeId, lineNumber);
                return new RecordProcessResult(true, "Successfully imported Employee ID: " + employeeId);
            } else {
                logger.warn("Failed to insert employee ID {} from line {}. 0 rows affected.", employeeId, lineNumber);
                return new RecordProcessResult(false, "Line " + lineNumber + ": Failed to insert Employee ID: " + employeeId + " (0 rows affected).");
            }
        } catch (SQLException e) {
            logger.error("Failed to insert Employee ID {} from line {}: {}", employeeId, lineNumber, e.getMessage(), e);
            throw new DAOException("Failed to insert Employee ID: " + employeeId + " from line " + lineNumber, e);
        }
    }
}