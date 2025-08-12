package com.litmus7.retailmanager.dto;

public class Electronics extends Product {
    private String brand;
    private int warrantyMonths;

    public Electronics(String productName, double price, ProductStatus status, String category, String brand, int warrantyMonths) {
        super(productName, price, status, category);
        // Initializes specific attributes
    }

    public Electronics(String productId, String productName, double price, ProductStatus status, String category, String brand, int warrantyMonths) {
        super(productId, productName, price, status, category);
        // Initializes all attributes (for file loading)
    }

    public String getBrand() { return null; }
    public void setBrand(String brand) { /* sets brand */ }
    public int getWarrantyMonths() { return 0; }
    public void setWarrantyMonths(int warrantyMonths) { /* sets warranty months */ }

    @Override
    public String toString() {
        return ""; // Returns a string representation of the object
    }
}