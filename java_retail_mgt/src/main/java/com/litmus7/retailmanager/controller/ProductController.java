package com.litmus7.retailmanager.controller;

import com.litmus7.retailmanager.dto.ProductResponse;
import com.litmus7.retailmanager.dto.Product;
import com.litmus7.retailmanager.util.InputUtil;
import com.litmus7.retailmanager.service.ProductService;
import com.litmus7.retailmanager.exceptions.ValidationException;

import java.util.Date;
import java.util.List;

public class ProductController {
    
    private final ProductService productService = new ProductService();

    public ProductResponse startApplication() {
        // Returns a ProductResponse with a welcome message
        return null;
    }

    public int getMenuChoice() {
        // Displays the menu and gets the user's choice using InputUtil
        return InputUtil.getInt("Please enter your choice: ");
    }

    public ProductResponse handleAddProduct() {
        try {
            // Gets product details from the user using InputUtil
            String category = InputUtil.getString("Enter category (Electronics/Clothing/Grocery): ");
            String productName = InputUtil.getString("Enter product name: ");
            double price = InputUtil.getDouble("Enter price: ");
            
            // Creates the appropriate Product object based on category
            Product newProduct = null;
            
            // Calls the ProductService to add the product
            productService.addProduct(newProduct);
        } catch (ValidationException e) {
            // Catches ValidationException and returns an appropriate ProductResponse
        }
        return null;
    }

    public ProductResponse handleViewAllProducts() {
        // Calls the ProductService to get all products
        List<Product> products = productService.viewAllProducts();
        // Returns a ProductResponse containing the products or an empty list message
        return null;
    }

    public ProductResponse handleViewProductsByCategory() {
        // Gets a category from the user
        String category = InputUtil.getString("Enter category to view: ");
        // Calls the ProductService to get products by category
        List<Product> products = productService.viewProductsByCategory(category);
        // Returns a ProductResponse with the results
        return null;
    }

    public ProductResponse handleSortProducts() {
        // Gets a sort option from the user
        String choice = InputUtil.getString("Enter your choice (1/2/3): ");
        // Calls the ProductService to get and sort products
        List<Product> products = productService.viewAllProducts();
        // Returns a ProductResponse with the sorted list
        return null;
    }
}