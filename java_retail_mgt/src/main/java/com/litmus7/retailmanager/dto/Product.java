package com.litmus7.retailmanager.dto;

import java.util.UUID;

public abstract class Product {
    private String productId;
    private String productName;
    private double price;
    private ProductStatus status;
    private String category;

    public Product(String productName, double price, ProductStatus status, String category) {
        // Generates a unique ID and initializes common attributes
    }

    public Product(String productId, String productName, double price, ProductStatus status, String category) {
        // Initializes all attributes, including an existing ID (for file loading)
    }

    public String getProductId() { return null; }
    public String getProductName() { return null; }
    public void setProductName(String productName) { /* sets product name */ }
    public double getPrice() { return 0.0; }
    public void setPrice(double price) { /* sets price */ }
    public ProductStatus getStatus() { return null; }
    public void setStatus(ProductStatus status) { /* sets status */ }
    public String getCategory() { return null; }
    public void setCategory(String category) { /* sets category */ }
    
    @Override
    public String toString() {
        return ""; // Returns a string representation of the object
    }
}