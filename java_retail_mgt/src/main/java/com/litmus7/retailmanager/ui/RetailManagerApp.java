package com.litmus7.retailmanager.ui;

import com.litmus7.retailmanager.controller.ProductController;
import com.litmus7.retailmanager.dto.ProductResponse;

public class RetailManagerApp {

    public static void main(String[] args) {
        // Creates a ProductController instance
        ProductController productController = new ProductController();
        // Runs a loop that continuously displays the menu and gets user input
        // Uses a switch statement to call the appropriate controller handler method
        // Receives a ProductResponse from the controller
        ProductResponse response = productController.handleAddProduct();
        // Prints the message and products from the response to the console
    }
}