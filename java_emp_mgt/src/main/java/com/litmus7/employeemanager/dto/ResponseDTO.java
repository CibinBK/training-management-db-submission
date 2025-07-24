package com.litmus7.employeemanager.dto;

import com.litmus7.employeemanager.constant.AppConstants;
import java.util.List;
import java.util.Collections;

public class ResponseDTO<T> {

    private final int statusCode;
    private final String message;
    private final int affectedCount;
    private final T data; 
    
    @SuppressWarnings("unchecked")
    private ResponseDTO(int statusCode, String message, int affectedCount, T data) {
        this.statusCode = statusCode;
        this.message = message;
        this.affectedCount = affectedCount;
        
        if (data instanceof List) {
            this.data = (T) Collections.unmodifiableList((List<?>) data); // If data is a List, ensure it's unmodifiable for immutability
        } else {
            this.data = data;
        }
    }

    public static <T> ResponseDTO<T> success(String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_SUCCESS, message, affectedCount, data);
    }

    public static <T> ResponseDTO<T> partialSuccess(String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_PARTIAL_SUCCESS, message, affectedCount, data);
    }

    public static <T> ResponseDTO<T> failure(String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_FAILURE, message, affectedCount, data);
    }

    public static ResponseDTO<List<String>> createOverallResponse(
            int totalRecordsAttempted,
            int numberOfSuccessfulEntries,
            List<String> collectedErrorMessages) {

        int statusCode;
        String summaryMessage;

        if (totalRecordsAttempted == 0) {
            statusCode = AppConstants.STATUS_CODE_FAILURE;
            summaryMessage = "No records found in CSV file to process.";
        } else if (numberOfSuccessfulEntries == totalRecordsAttempted - collectedErrorMessages.size()) {
            if (collectedErrorMessages.isEmpty()) {
                statusCode = AppConstants.STATUS_CODE_SUCCESS;
                summaryMessage = "All " + numberOfSuccessfulEntries + " employee records imported successfully.";
            } else {
                statusCode = AppConstants.STATUS_CODE_PARTIAL_SUCCESS;
                summaryMessage = "Import completed. " + numberOfSuccessfulEntries + " records imported. " + collectedErrorMessages.size() + " records skipped/failed.";
            }
        } else if (numberOfSuccessfulEntries > 0) {
            statusCode = AppConstants.STATUS_CODE_PARTIAL_SUCCESS;
            summaryMessage = "Import completed with partial success. " + numberOfSuccessfulEntries +
                             " records imported, " + collectedErrorMessages.size() + " failed.";
        } else {
            statusCode = AppConstants.STATUS_CODE_FAILURE;
            summaryMessage = "Import failed for all records. " + collectedErrorMessages.size() + " records failed.";
        }
        return new ResponseDTO<>(statusCode, summaryMessage, numberOfSuccessfulEntries, collectedErrorMessages);
    }

    // --- Getters ---

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public int getAffectedCount() {
        return affectedCount;
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return statusCode == AppConstants.STATUS_CODE_SUCCESS || statusCode == AppConstants.STATUS_CODE_PARTIAL_SUCCESS;
    }

    public boolean isFailure() {
        return statusCode == AppConstants.STATUS_CODE_FAILURE;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Operation Result ---\n");
        sb.append("Status Code: ").append(statusCode);

        if (statusCode == AppConstants.STATUS_CODE_SUCCESS) {
            sb.append(" (SUCCESS)\n");
        } else if (statusCode == AppConstants.STATUS_CODE_PARTIAL_SUCCESS) {
            sb.append(" (PARTIAL SUCCESS)\n");
        } else {
            sb.append(" (FAILURE)\n");
        }

        sb.append("Message: ").append(message).append("\n");
        sb.append("Records Affected: ").append(affectedCount).append("\n");

        if (data != null) {
            if (data instanceof List && !((List<?>) data).isEmpty()) {
                sb.append("Details:\n");
                for (Object item : (List<?>) data) {
                    sb.append("  - ").append(item.toString()).append("\n");
                }
            } else {
                sb.append("Data: ").append(data.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
