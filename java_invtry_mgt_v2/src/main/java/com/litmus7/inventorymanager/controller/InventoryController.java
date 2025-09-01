package com.litmus7.inventorymanager.controller;

import com.litmus7.inventorymanager.dto.Response;
import com.litmus7.inventorymanager.service.FileProcessorTask;
import com.litmus7.inventorymanager.service.InventoryProcessingService;
import com.litmus7.inventorymanager.util.FileHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class InventoryController {

    private final FileHandler fileHandler;
    private final InventoryProcessingService inventoryProcessingService;

    public InventoryController() {
        this.fileHandler = new FileHandler();
        this.inventoryProcessingService = new InventoryProcessingService();
    }

    public Response startProcessing() {
        System.out.println("Controller: Starting multithreaded inventory processing...");
        
        // Create a thread-safe queue to collect results from all threads
        Queue<Response> sharedResponseQueue = new ConcurrentLinkedQueue<>();
        
        List<Thread> threads = new ArrayList<>();
        List<File> filesToProcess = fileHandler.scanInputFolder();

        for (File file : filesToProcess) {
            Runnable task = new FileProcessorTask(file, inventoryProcessingService, sharedResponseQueue);
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                System.err.println("Main thread interrupted while waiting for child threads.");
                Thread.currentThread().interrupt();
                return new Response(false, "Processing interrupted due to an error.");
            }
        }
        
        // Aggregate the results from the shared queue
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

        // Return the final, aggregated response
        Response finalResponse = new Response(true, "Multithreaded processing started and completed.");
        if (errorFiles > 0) {
            finalResponse.setSuccess(false);
            finalResponse.setMessage("Multithreaded processing completed with errors.");
        }
        finalResponse.setTotalFilesProcessed(totalFiles);
        finalResponse.setSuccessfulFiles(successfulFiles);
        finalResponse.setErrorFiles(errorFiles);
        
        return finalResponse;
    }
}