package com.litmus7.employeemanager.util;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class EmployeeValidator {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern PHONE_NUMBER_PATTERN = Pattern.compile("^[0-9]+$");


    // Private constructor to prevent instantiation
    private EmployeeValidator() {}

    public static Integer validateEmployeeId(String idString, int lineNumber) {
        if (idString == null || idString.trim().isEmpty()) {
            System.err.println("Error at line " + lineNumber + ": Employee ID cannot be empty. Skipping record.");
            return null;
        }
        try {
            return Integer.parseInt(idString.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error at line " + lineNumber + ": Invalid employee ID format '" + idString + "'. Expected a number. Skipping record.");
            return null;
        }
    }

    public static Double validateSalary(String salaryString, int lineNumber) {
        if (salaryString == null || salaryString.trim().isEmpty()) {
            System.err.println("Error at line " + lineNumber + ": Salary cannot be empty. Skipping record.");
            return null;
        }
        try {
            return Double.parseDouble(salaryString.trim());
        } catch (NumberFormatException e) {
            System.err.println("Error at line " + lineNumber + ": Invalid salary format '" + salaryString + "'. Expected a number. Skipping record.");
            return null;
        }
    }

    public static LocalDate validateJoinDate(String dateString, int lineNumber) {
        if (dateString == null || dateString.trim().isEmpty()) {
            System.err.println("Error at line " + lineNumber + ": Join date cannot be empty. Skipping record.");
            return null;
        }
        try {
            // LocalDate.parse() handles YYYY-MM-DD format by default
            return LocalDate.parse(dateString.trim());
        } catch (DateTimeParseException e) {
            System.err.println("Error at line " + lineNumber + ": Invalid date format '" + dateString + "'. Expected YYYY-MM-DD. Skipping record.");
            return null;
        }
    }

    public static String validateStringField(String fieldName, String fieldValue, int lineNumber) {
        if (fieldValue == null || fieldValue.trim().isEmpty()) {
            System.err.println("Error at line " + lineNumber + ": " + fieldName + " cannot be empty. Skipping record.");
            return null;
        }
        return fieldValue.trim();
    }

    public static String validateEmail(String emailString, int lineNumber) {
        String trimmedEmail = validateStringField("Email", emailString, lineNumber);
        if (trimmedEmail == null) {
            return null;
        }
        Matcher matcher = EMAIL_PATTERN.matcher(trimmedEmail);
        if (!matcher.matches()) {
            System.err.println("Error at line " + lineNumber + ": Invalid email format '" + trimmedEmail + "'. Skipping record.");
            return null;
        }
        return trimmedEmail;
    }

    public static String validatePhoneNumber(String phoneNumberString, int lineNumber) {
        String trimmedPhone = validateStringField("Phone Number", phoneNumberString, lineNumber);
        if (trimmedPhone == null) {
            return null;
        }
        Matcher matcher = PHONE_NUMBER_PATTERN.matcher(trimmedPhone);
        if (!matcher.matches()) {
            System.err.println("Error at line " + lineNumber + ": Invalid phone number format '" + trimmedPhone + "'. Expected digits only. Skipping record.");
            return null;
        }
        return trimmedPhone;
    }
}
