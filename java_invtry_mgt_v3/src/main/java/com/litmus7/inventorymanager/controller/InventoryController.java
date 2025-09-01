package com.litmus7.inventorymanager.controller;

import com.litmus7.inventorymanager.dto.Response;
import com.litmus7.inventorymanager.service.FileProcessorTask;
import com.litmus7.inventorymanager.service.InventoryProcessingService;
import com.litmus7.inventorymanager.util.FileHandler;

import java.io.File;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class InventoryController {

    private final FileHandler fileHandler;
    private final InventoryProcessingService inventoryProcessingService;

    public InventoryController() {
        this.fileHandler = new FileHandler();
        this.inventoryProcessingService = new InventoryProcessingService();
    }

    public Response startProcessing() {
        System.out.println("Controller: Starting thread pool-based inventory processing...");

        Queue<Response> sharedResponseQueue = new ConcurrentLinkedQueue<>();
        ExecutorService executor = Executors.newFixedThreadPool(5);
        List<File> filesToProcess = fileHandler.scanInputFolder();

        for (File file : filesToProcess) {
            Runnable task = new FileProcessorTask(file, inventoryProcessingService, sharedResponseQueue);
            executor.submit(task);
        }

        executor.shutdown(); // Stops the executor from accepting new tasks

        try {
            executor.awaitTermination(1, TimeUnit.HOURS); // Waits for all tasks to complete
        } catch (InterruptedException e) {
            System.err.println("Processing interrupted while waiting for tasks to complete.");
            Thread.currentThread().interrupt();
            return new Response(false, "Processing interrupted due to an error.");
        }

        int totalFiles = sharedResponseQueue.size();
        int successfulFiles = 0;
        int errorFiles = 0;

        for (Response response : sharedResponseQueue) {
            if (response.isSuccess()) {
                successfulFiles++;
            } else {
                errorFiles++;
            }
        }

        Response finalResponse = new Response(true, "Thread pool processing completed.");
        if (errorFiles > 0) {
            finalResponse.setSuccess(false);
            finalResponse.setMessage("Thread pool processing completed with errors.");
        }
        finalResponse.setTotalFilesProcessed(totalFiles);
        finalResponse.setSuccessfulFiles(successfulFiles);
        finalResponse.setErrorFiles(errorFiles);

        return finalResponse;
    }
}