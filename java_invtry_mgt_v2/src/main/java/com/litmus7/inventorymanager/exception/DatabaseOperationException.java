package com.litmus7.inventorymanager.exception;

public class DatabaseOperationException extends RuntimeException {
    private final String errorCode;
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    public DatabaseOperationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}