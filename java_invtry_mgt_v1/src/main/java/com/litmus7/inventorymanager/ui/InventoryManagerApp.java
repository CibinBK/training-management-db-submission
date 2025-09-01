package com.litmus7.inventorymanager.ui;

import com.litmus7.inventorymanager.controller.InventoryController;
import com.litmus7.inventorymanager.dto.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InventoryManagerApp {

    private static final Logger logger = LogManager.getLogger(InventoryManagerApp.class);

    public static void main(String[] args) {
        logger.info("Starting Inventory Feed Processing (Phase 1)...");

        InventoryController controller = new InventoryController();
        Response response = controller.startProcessing();

        if (response.isSuccess()) {
            logger.info("Inventory processing completed successfully.");
        } else {
            logger.error("Inventory processing failed: " + response.getMessage());
        }

        logger.info("--- Processing Summary ---");
        logger.info("Total files processed: " + response.getTotalFilesProcessed());
        logger.info("Successful files: " + response.getSuccessfulFiles());
        logger.info("Error files: " + response.getErrorFiles());
        
        logger.info("Application shutdown.");
    }
}