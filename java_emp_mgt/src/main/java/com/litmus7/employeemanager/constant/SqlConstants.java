package com.litmus7.employeemanager.constant;

public final class SqlConstants {

    public static final String INSERT_EMPLOYEE =
            "INSERT INTO employee (emp_id,first_name,last_name,email,phone,department,salary,join_date) VALUES (?, ?, ?,?,?,?,?,?)";

    public static final String CHECK_DUPLICATE_EMPLOYEE =
            "SELECT COUNT(*) FROM employee WHERE emp_id = ?";

    public static final String FIND_EMPLOYEE_BY_ID =
            "SELECT emp_id, first_name, last_name, email, phone, department, salary, join_date FROM employee WHERE emp_id = ?";

    public static final String FIND_ALL_EMPLOYEES =
            "SELECT emp_id, first_name, last_name, email, phone, department, salary, join_date FROM employee";

    public static final String UPDATE_EMPLOYEE =
            "UPDATE employee SET first_name = ?, last_name = ?, email = ?, phone = ?, department = ?, salary = ?, join_date = ? WHERE emp_id = ?";

    public static final String DELETE_EMPLOYEE =
            "DELETE FROM employee WHERE emp_id = ?";

    // Private constructor to prevent instantiation
    private SqlConstants() {}
}
