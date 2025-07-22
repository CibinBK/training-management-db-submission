package com.litmus7;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

class Validator {
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
        String validatedString = validateStringField("Email", emailString, lineNumber);
        if (validatedString == null) {
            return null;
        }
        
        if (!validatedString.contains("@") || !validatedString.contains(".")) {
            System.err.println("Error at line " + lineNumber + ": Invalid email format '" + validatedString + "'. Email must contain '@' and '.'. Skipping record.");
            return null;
        }
        return validatedString;
    }
}
