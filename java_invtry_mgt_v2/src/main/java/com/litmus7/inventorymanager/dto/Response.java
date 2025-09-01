package com.litmus7.inventorymanager.dto;

public class Response {
    private boolean success;
    private String message;
    private int totalFilesProcessed;
    private int successfulFiles;
    private int errorFiles;
    private String errorCode;

    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Response(boolean success, String message, int totalFilesProcessed, int successfulFiles, int errorFiles, String errorCode) {
        this.success = success;
        this.message = message;
        this.totalFilesProcessed = totalFilesProcessed;
        this.successfulFiles = successfulFiles;
        this.errorFiles = errorFiles;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public int getTotalFilesProcessed() { return totalFilesProcessed; }
    public void setTotalFilesProcessed(int totalFilesProcessed) { this.totalFilesProcessed = totalFilesProcessed; }
    public int getSuccessfulFiles() { return successfulFiles; }
    public void setSuccessfulFiles(int successfulFiles) { this.successfulFiles = successfulFiles; }
    public int getErrorFiles() { return errorFiles; }
    public void setErrorFiles(int errorFiles) { this.errorFiles = errorFiles; }
    public String getErrorCode() { return errorCode; }
}