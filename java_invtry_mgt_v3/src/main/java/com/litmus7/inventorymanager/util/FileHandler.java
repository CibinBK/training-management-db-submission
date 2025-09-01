package com.litmus7.inventorymanager.util;

import com.litmus7.inventorymanager.constant.FileConstants;
import com.litmus7.inventorymanager.exception.FileProcessingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileHandler {
    private static final Logger logger = LogManager.getLogger(FileHandler.class);

    public List<File> scanInputFolder() {
        Path inputDirPath = Paths.get(FileConstants.INPUT_DIR);
        if (!Files.exists(inputDirPath)) {
            try {
                Files.createDirectories(inputDirPath);
            } catch (IOException e) {
                logger.error("Could not create input directory: {}", e.getMessage(), e);
                return Collections.emptyList();
            }
        }
        File inputDir = inputDirPath.toFile();
        return Arrays.stream(Objects.requireNonNull(inputDir.listFiles()))
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(".csv"))
                .collect(Collectors.toList());
    }

    public void moveFileToProcessed(File file) {
        moveFile(file, FileConstants.PROCESSED_DIR);
    }

    public void moveFileToError(File file) {
        moveFile(file, FileConstants.ERROR_DIR);
    }

    private void moveFile(File file, String destinationDir) {
        try {
            Path sourcePath = file.toPath();
            Path destDirPath = Paths.get(destinationDir);
            if (!Files.exists(destDirPath)) {
                Files.createDirectories(destDirPath);
            }
            Path destPath = destDirPath.resolve(sourcePath.getFileName());
            Files.move(sourcePath, destPath, StandardCopyOption.REPLACE_EXISTING);
            logger.info("File moved: {} -> {}", file.getName(), destinationDir);
        } catch (IOException e) {
            String errorMessage = ErrorCodesManager.getErrorMessage("103");
            throw new FileProcessingException("103", errorMessage + " for file: " + file.getName(), e);
        }
    }
}