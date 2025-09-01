package com.litmus7.inventorymanager.exception;

public class FileProcessingException extends RuntimeException {
    private final String errorCode;
    public FileProcessingException(String message) {
        super(message);
        this.errorCode = null;
    }
    public FileProcessingException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = null;
    }
    public FileProcessingException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    public FileProcessingException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    public String getErrorCode() {
        return errorCode;
    }
}