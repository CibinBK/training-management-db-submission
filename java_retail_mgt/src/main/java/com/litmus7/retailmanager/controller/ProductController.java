package com.litmus7.retailmanager.controller;

import com.litmus7.retailmanager.constants.FileConstants;
import com.litmus7.retailmanager.dto.Clothing;
import com.litmus7.retailmanager.dto.Electronics;
import com.litmus7.retailmanager.dto.Grocery;
import com.litmus7.retailmanager.dto.Product;
import com.litmus7.retailmanager.dto.ProductResponse;
import com.litmus7.retailmanager.dto.ProductStatus;
import com.litmus7.retailmanager.exceptions.ValidationException;
import com.litmus7.retailmanager.service.ProductService;
import com.litmus7.retailmanager.util.InputUtil;

import java.util.Date;
import java.util.List;

public class ProductController {
    
    private final ProductService productService = new ProductService();

    public int getMenuChoice() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("1. Add Product");
        System.out.println("2. View All Products");
        System.out.println("3. View Products by Category");
        System.out.println("4. Sort Products");
        System.out.println("5. Exit");
        return InputUtil.getInt("Please enter your choice: ");
    }

    public ProductResponse handleAddProduct() {
        try {
            System.out.println("\n--- Add New Product ---");
            String category = InputUtil.getString("Enter category (Electronics/Clothing/Grocery): ");
            String productName = InputUtil.getString("Enter product name: ");
            double price = InputUtil.getDouble("Enter price: ");
            
            Product newProduct = createProduct(category, productName, price);
            
            productService.addProduct(newProduct);

            return new ProductResponse("Product added successfully!", null, FileConstants.SUCCESS);
        } catch (ValidationException e) {
            return new ProductResponse("Error adding product: " + e.getMessage(), null, FileConstants.VALIDATION_ERROR);
        }
    }

    private Product createProduct(String category, String productName, double price) {
        if (category.equalsIgnoreCase("Electronics")) {
            String brand = InputUtil.getString("Enter brand: ");
            int warrantyMonths = InputUtil.getInt("Enter warranty months: ");
            return new Electronics(productName, price, ProductStatus.AVAILABLE, category, brand, warrantyMonths);
        } else if (category.equalsIgnoreCase("Clothing")) {
            String size = InputUtil.getString("Enter size: ");
            String material = InputUtil.getString("Enter material: ");
            return new Clothing(productName, price, ProductStatus.AVAILABLE, category, size, material);
        } else if (category.equalsIgnoreCase("Grocery")) {
            Date expiryDate = new Date();
            double weightKg = InputUtil.getDouble("Enter weight in kg: ");
            return new Grocery(productName, price, ProductStatus.AVAILABLE, category, expiryDate, weightKg);
        } else {
            throw new ValidationException("Invalid category entered.");
        }
    }

    public ProductResponse handleViewAllProducts() {
        List<Product> products = productService.viewAllProducts();
        
        if (products.isEmpty()) {
            return new ProductResponse("No products found in the inventory.", null, FileConstants.SUCCESS);
        } else {
            return new ProductResponse("Here are all the products:", products, FileConstants.SUCCESS);
        }
    }

    public ProductResponse handleViewProductsByCategory() {
        String category = InputUtil.getString("Enter category to view: ");
        List<Product> products = productService.viewProductsByCategory(category);
        
        if (products.isEmpty()) {
            return new ProductResponse("No products found for category: " + category, null, FileConstants.SUCCESS);
        } else {
            return new ProductResponse("Here are the products in category " + category + ":", products, FileConstants.SUCCESS);
        }
    }

    public ProductResponse handleSortProducts() {
        System.out.println("\n--- Sort Products ---");
        System.out.println("Sort by:");
        System.out.println("1. Price Ascending");
        System.out.println("2. Price Descending");
        System.out.println("3. Name (A-Z)");
        String choice = InputUtil.getString("Enter your choice (1/2/3): ");

        List<Product> products = productService.viewAllProducts();
        
        if (products.isEmpty()) {
            return new ProductResponse("No products to sort.", null, FileConstants.SUCCESS);
        }

        String sortOption;
        switch (choice) {
            case "1": sortOption = "price_asc"; break;
            case "2": sortOption = "price_desc"; break;
            case "3": sortOption = "name_az"; break;
            default: return new ProductResponse("Invalid sort option.", products, FileConstants.VALIDATION_ERROR);
        }

        List<Product> sortedProducts = productService.sortProducts(products, sortOption);
        return new ProductResponse("Products sorted successfully:", sortedProducts, FileConstants.SUCCESS);
    }
}