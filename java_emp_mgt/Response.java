package com.litmus7;

import java.util.List;
import java.util.Collections;

public class Response {

    public static final int STATUS_SUCCESS = 200;
    public static final int STATUS_PARTIAL_SUCCESS = 206;
    public static final int STATUS_FAILURE = 400;

    private final int statusCode;
    private final String summaryMessage;
    private final int employeeCount;
    private final List<String> detailedErrorMessages;

    private Response(int statusCode, String summaryMessage, int employeeCount, List<String> detailedErrorMessages) {
        this.statusCode = statusCode;
        this.summaryMessage = summaryMessage;
        this.employeeCount = employeeCount;
        this.detailedErrorMessages = detailedErrorMessages != null ? Collections.unmodifiableList(detailedErrorMessages) : Collections.emptyList();
    }

    public static Response createOverallResponse(
            int totalRecordsAttempted,
            int numberOfSuccessfulEntries,
            List<String> collectedErrorMessages) {

        int statusCode;
        String summaryMessage;

        if (totalRecordsAttempted == 0) {
            statusCode = STATUS_FAILURE;
            summaryMessage = "No records found in CSV file to process.";
        } else if (numberOfSuccessfulEntries == totalRecordsAttempted - collectedErrorMessages.size()) {
            if (collectedErrorMessages.isEmpty()) {
                statusCode = STATUS_SUCCESS;
                summaryMessage = "All " + numberOfSuccessfulEntries + " employee records imported successfully.";
            } else {
                statusCode = STATUS_PARTIAL_SUCCESS;
                summaryMessage = "Import completed. " + numberOfSuccessfulEntries + " records imported. " + collectedErrorMessages.size() + " records skipped/failed.";
            }
        } else if (numberOfSuccessfulEntries > 0) {
            statusCode = STATUS_PARTIAL_SUCCESS;
            summaryMessage = "Import completed with partial success. " + numberOfSuccessfulEntries +
                             " records imported, " + collectedErrorMessages.size() + " failed.";
        } else {
            statusCode = STATUS_FAILURE;
            summaryMessage = "Import failed for all records. " + collectedErrorMessages.size() + " records failed.";
        }

        return new Response(statusCode, summaryMessage, numberOfSuccessfulEntries, collectedErrorMessages);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getSummaryMessage() {
        return summaryMessage;
    }

    public int getEmployeeCount() {
        return employeeCount;
    }

    public List<String> getDetailedErrorMessages() {
        return detailedErrorMessages;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("--- Import Summary ---\n");
        sb.append("Status Code: ").append(statusCode);

        if (statusCode == STATUS_SUCCESS) {
            sb.append(" (SUCCESS)\n");
        } else if (statusCode == STATUS_PARTIAL_SUCCESS) {
            sb.append(" (PARTIAL SUCCESS)\n");
        } else {
            sb.append(" (FAILURE)\n");
        }

        sb.append("Message: ").append(summaryMessage).append("\n");
        sb.append("Successfully Imported: ").append(employeeCount).append(" records.\n");
        sb.append("Failed/Skipped Records: ").append(detailedErrorMessages.size()).append(" records.\n");

        if (!detailedErrorMessages.isEmpty()) {
            sb.append("--- Detailed Errors/Warnings ---\n");
            for (String error : detailedErrorMessages) {
                sb.append("  - ").append(error).append("\n");
            }
        }
        return sb.toString();
    }
}
