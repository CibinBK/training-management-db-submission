package com.litmus7.retailmanager.dto;

import java.util.Date;

public class Grocery extends Product {
    private Date expiryDate;
    private double weightKg;

    public Grocery(String productName, double price, ProductStatus status, String category, Date expiryDate, double weightKg) {
        super(productName, price, status, category);
        // Initializes specific attributes
    }

    public Grocery(String productId, String productName, double price, ProductStatus status, String category, Date expiryDate, double weightKg) {
        super(productId, productName, price, status, category);
        // Initializes all attributes (for file loading)
    }

    public Date getExpiryDate() { return null; }
    public void setExpiryDate(Date expiryDate) { /* sets expiry date */ }
    public double getWeightKg() { return 0.0; }
    public void setWeightKg(double weightKg) { /* sets weight in kg */ }

    @Override
    public String toString() {
        return ""; // Returns a string representation of the object
    }
}