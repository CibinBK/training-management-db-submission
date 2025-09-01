package com.litmus7.inventorymanager.service;

import com.litmus7.inventorymanager.dto.Response;

import java.io.File;
import java.util.Queue;

/**
 * A Runnable task to process a single file in a separate thread.
 * It uses the InventoryProcessingService's core logic.
 */
public class FileProcessorTask implements Runnable {

    private final File file;
    private final InventoryProcessingService inventoryProcessingService;
    private final Queue<Response> sharedResponseQueue;

    public FileProcessorTask(File file, InventoryProcessingService service, Queue<Response> sharedResponseQueue) {
        this.file = file;
        this.inventoryProcessingService = service;
        this.sharedResponseQueue = sharedResponseQueue;
    }

    @Override
    public void run() {
        System.out.println("Processing file: " + file.getName() + " in thread: " + Thread.currentThread().getName());
        Response response = inventoryProcessingService.processSingleFile(file);
        
        // Add the response to the shared, thread-safe queue
        sharedResponseQueue.add(response);
    }
}