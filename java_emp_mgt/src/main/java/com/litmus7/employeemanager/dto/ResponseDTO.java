package com.litmus7.employeemanager.dto;

import com.litmus7.employeemanager.constant.AppConstants;
import java.util.List;
import java.util.Collections;

public class ResponseDTO<T> {

    private final int statusCode;
    private final int errorCode;
    private final String message;
    private final int affectedCount;
    private final T data; 
    
    @SuppressWarnings("unchecked")
    private ResponseDTO(int statusCode, int errorCode, String message, int affectedCount, T data) {
        this.statusCode = statusCode;
        this.errorCode = errorCode;
        this.message = message;
        this.affectedCount = affectedCount;
        
        if (data instanceof List) {
            this.data = (T) Collections.unmodifiableList((List<?>) data);
        } else {
            this.data = data;
        }
    }

    public static <T> ResponseDTO<T> success(String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_SUCCESS, 0, message, affectedCount, data);
    }

    public static <T> ResponseDTO<T> partialSuccess(String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_PARTIAL_SUCCESS, 0, message, affectedCount, data);
    }
    
    public static <T> ResponseDTO<T> failure(int errorCode, String message, int affectedCount, T data) {
        return new ResponseDTO<>(AppConstants.STATUS_CODE_FAILURE, errorCode, message, affectedCount, data);
    }

    public int getStatusCode() {
        return statusCode;
    }
    
    public int getErrorCode() {
        return errorCode;
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
        
        if (errorCode != 0) {
            sb.append("Error Code: ").append(errorCode).append("\n");
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