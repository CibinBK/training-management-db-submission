package com.litmus7.inventorymanager.controller;

import com.litmus7.inventorymanager.dto.Response;
import com.litmus7.inventorymanager.service.InventoryProcessingService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class InventoryController {

    private static final Logger logger = LogManager.getLogger(InventoryController.class);
    private final InventoryProcessingService inventoryProcessingService;

    public InventoryController() {
        this.inventoryProcessingService = new InventoryProcessingService();
    }

    public Response startProcessing() {
        logger.info("Controller: Initializing the inventory processing service...");
        return inventoryProcessingService.processFiles();
    }
}