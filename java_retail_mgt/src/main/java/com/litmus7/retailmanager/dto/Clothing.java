package com.litmus7.retailmanager.dto;

public class Clothing extends Product {
    private String size;
    private String material;

    public Clothing(String productName, double price, ProductStatus status, String category, String size, String material) {
        super(productName, price, status, category);
        // Initializes specific attributes
    }

    public Clothing(String productId, String productName, double price, ProductStatus status, String category, String size, String material) {
        super(productId, productName, price, status, category);
        // Initializes all attributes (for file loading)
    }

    public String getSize() { return null; }
    public void setSize(String size) { /* sets size */ }
    public String getMaterial() { return null; }
    public void setMaterial(String material) { /* sets material */ }

    @Override
    public String toString() {
        return ""; // Returns a string representation of the object
    }
}