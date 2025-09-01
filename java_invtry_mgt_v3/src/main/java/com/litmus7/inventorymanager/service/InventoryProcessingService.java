package com.litmus7.inventorymanager.service;

import com.litmus7.inventorymanager.dao.InventoryDAO;
import com.litmus7.inventorymanager.dto.InventoryDTO;
import com.litmus7.inventorymanager.dto.Response;
import com.litmus7.inventorymanager.exception.DatabaseOperationException;
import com.litmus7.inventorymanager.exception.FileProcessingException;
import com.litmus7.inventorymanager.exception.ValidationException;
import com.litmus7.inventorymanager.util.CsvReader;
import com.litmus7.inventorymanager.util.DatabaseConnectionManager;
import com.litmus7.inventorymanager.util.ErrorCodesManager;
import com.litmus7.inventorymanager.util.FileHandler;
import com.litmus7.inventorymanager.util.InventoryValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class InventoryProcessingService {

    private static final Logger logger = LogManager.getLogger(InventoryProcessingService.class);
    private final FileHandler fileHandler;
    private final CsvReader csvReader;
    private final InventoryValidator inventoryValidator;
    private final InventoryDAO inventoryDAO;

    public InventoryProcessingService() {
        this.fileHandler = new FileHandler();
        this.csvReader = new CsvReader();
        this.inventoryValidator = new InventoryValidator();
        this.inventoryDAO = new InventoryDAO();
    }

    public Response processFiles() {
        int totalFiles = 0;
        int successfulFiles = 0;
        int errorFiles = 0;

        List<File> filesToProcess = fileHandler.scanInputFolder();
        totalFiles = filesToProcess.size();

        for (File file : filesToProcess) {
            Response fileResponse = processSingleFile(file);
            if (fileResponse.isSuccess()) {
                successfulFiles++;
            } else {
                errorFiles++;
            }
        }

        Response finalResponse = new Response(true, "Processing completed.");
        if (errorFiles > 0) {
            finalResponse.setSuccess(false);
            finalResponse.setMessage("Processing completed with errors.");
        }
        finalResponse.setTotalFilesProcessed(totalFiles);
        finalResponse.setSuccessfulFiles(successfulFiles);
        finalResponse.setErrorFiles(errorFiles);

        return finalResponse;
    }

    Response processSingleFile(File file) {
        Connection connection = null;
        try {
            logger.info("Processing started for file: {}", file.getName());
            connection = DatabaseConnectionManager.getConnection();
            connection.setAutoCommit(false);

            List<InventoryDTO> records = csvReader.read(file);
            for (InventoryDTO record : records) {
                inventoryValidator.validate(record);
                inventoryDAO.insertRecord(connection, record);
            }

            connection.commit();
            fileHandler.moveFileToProcessed(file);
            logger.info("Successfully processed and committed file: {}", file.getName());
            return new Response(true, "Successfully processed file: " + file.getName());

        } catch (FileProcessingException e) {
            logger.error("Error processing file {}: {} - {}", file.getName(), e.getErrorCode(), e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                    logger.info("Transaction for {} rolled back.", file.getName());
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error during transaction rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            fileHandler.moveFileToError(file);
            return new Response(false, "Failed to process file: " + file.getName(), 0, 0, 1, e.getErrorCode());

        } catch (ValidationException e) {
            logger.error("Error processing file {}: {} - {}", file.getName(), e.getErrorCode(), e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                    logger.info("Transaction for {} rolled back.", file.getName());
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error during transaction rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            fileHandler.moveFileToError(file);
            return new Response(false, "Failed to process file: " + file.getName(), 0, 0, 1, e.getErrorCode());
            
        } catch (DatabaseOperationException e) {
            logger.error("Error processing file {}: {} - {}", file.getName(), e.getErrorCode(), e.getMessage(), e);
            try {
                if (connection != null) {
                    connection.rollback();
                    logger.info("Transaction for {} rolled back.", file.getName());
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error during transaction rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            fileHandler.moveFileToError(file);
            return new Response(false, "Failed to process file: " + file.getName(), 0, 0, 1, e.getErrorCode());

        } catch (SQLException e) {
            String errorCode = "301";
            String errorMessage = ErrorCodesManager.getErrorMessage(errorCode);
            logger.error("SQL Error for file {}: {} - {}", file.getName(), errorCode, errorMessage, e);
            try {
                if (connection != null) {
                    connection.rollback();
                    logger.info("Transaction for {} rolled back.", file.getName());
                }
            } catch (SQLException rollbackEx) {
                logger.error("Error during transaction rollback: {}", rollbackEx.getMessage(), rollbackEx);
            }
            fileHandler.moveFileToError(file);
            return new Response(false, "SQL Error for file: " + file.getName(), 0, 0, 1, errorCode);
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("Failed to close connection: {}", e.getMessage(), e);
            }
        }
    }
}