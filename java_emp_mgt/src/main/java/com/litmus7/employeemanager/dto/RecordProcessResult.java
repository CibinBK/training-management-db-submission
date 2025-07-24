package com.litmus7.employeemanager.dto;

public class RecordProcessResult {
    public final boolean success;
    public final String message; // Message for this specific record (success or failure)

    public RecordProcessResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
