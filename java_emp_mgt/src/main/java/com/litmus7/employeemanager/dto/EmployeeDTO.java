package com.litmus7.employeemanager.dto;

import java.time.LocalDate;

public class EmployeeDTO {
    private final int employeeId;
    private final String firstName;
    private final String lastName;
    private final String email;
    private final String phone;
    private final String department;
    private final Double salary;
    private final LocalDate joinDate;

    // Constructor for creating new EmployeeDTO objects
    public EmployeeDTO(int employeeId, String firstName, String lastName, String email,
                    String phone, String department, Double salary, LocalDate joinDate) {
        this.employeeId = employeeId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.salary = salary;
        this.joinDate = joinDate;
    }

    // Getters for all fields (no setters, making it immutable after creation)
    public int getEmployeeId() {
        return employeeId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDepartment() {
        return department;
    }

    public Double getSalary() {
        return salary;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public String toString() {
        return "EmployeeDTO{" +
               "empId=" + employeeId +
               ", firstName='" + firstName + '\'' +
               ", lastName='" + lastName + '\'' +
               ", email='" + email + '\'' +
               ", phone='" + phone + '\'' +
               ", department='" + department + '\'' +
               ", salary=" + salary +
               ", joinDate=" + joinDate +
               '}';
    }
}
