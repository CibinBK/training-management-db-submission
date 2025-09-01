package com.litmus7.inventorymanager.exception;

public class ValidationException extends RuntimeException {
    private final String errorCode;
    public ValidationException(String message) {
        super(message);
        this.errorCode = null;
    }
    public ValidationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}