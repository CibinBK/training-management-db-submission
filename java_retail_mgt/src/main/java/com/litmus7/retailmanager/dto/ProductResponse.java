package com.litmus7.retailmanager.dto;

import java.io.Serializable;
import java.util.List;

public class ProductResponse implements Serializable {
    private String message;
    private List<Product> products;
    private int status;

    public ProductResponse(String message, List<Product> products, int status) {
        // Initializes all variables
    }

    public String getMessage() { return null; }
    public List<Product> getProducts() { return null; }
    public int getStatus() { return 0; }
}