package com.litmus7.retailmanager.dao;

import com.litmus7.retailmanager.dto.Product;
import java.util.List;

public class ProductDAO {

    public void saveAllProducts(List<Product> products) {
        // Iterates through the list of products
        // Converts each product object to a comma-separated string
        // Writes each string to a new line in the inventory.txt file
    }

    public List<Product> loadAllProducts() {
        // Reads all lines from the inventory.txt file
        // For each line, parses the comma-separated string to create a Product object
        // Returns the list of all created products
        return null;
    }

    public void addProduct(Product product) {
        // Loads all products from the file
        // Adds the new product to the list
        // Saves the updated list back to the file
    }

    public List<Product> viewAllProducts() {
        // Retrieves all products from the file
        return null;
    }

    public Product getProductById(String productId) {
        // Retrieves all products and searches for a specific product by its ID
        // Throws a ProductNotFoundException if the product is not found
        return null;
    }
}