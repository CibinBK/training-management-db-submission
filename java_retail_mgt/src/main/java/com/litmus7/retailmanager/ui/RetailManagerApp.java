package com.litmus7.retailmanager.ui;

import com.litmus7.retailmanager.constants.FileConstants;
import com.litmus7.retailmanager.controller.ProductController;
import com.litmus7.retailmanager.dto.ProductResponse;
import com.litmus7.retailmanager.util.InputUtil;

public class RetailManagerApp {

    public static void main(String[] args) {
        ProductController productController = new ProductController();
        
        ProductResponse response;

        boolean running = true;
        
        while (running) {
            int choice = productController.getMenuChoice();
            
            switch (choice) {
                case 1:
                    response = productController.handleAddProduct();
                    break;
                case 2:
                    response = productController.handleViewAllProducts();
                    break;
                case 3:
                    response = productController.handleViewProductsByCategory();
                    break;
                case 4:
                    response = productController.handleSortProducts();
                    break;
                case 5:
                    System.out.println("Exiting the application.....");
                    InputUtil.closeScanner();
                    running = false;
                    response = null;
                    break;
                default:
                    response = new ProductResponse("Invalid choice. Please enter a number between 1 and 5.", null, FileConstants.VALIDATION_ERROR);
                    break;
            }

            if (response != null) {
                if (response.getStatus() == FileConstants.SUCCESS) {
                    System.out.println(response.getMessage());
                    if (response.getProducts() != null) {
                        response.getProducts().forEach(System.out::println);
                    }
                } else {
                    System.err.println("Operation failed: " + response.getMessage());
                }
            }
        }
    }
}