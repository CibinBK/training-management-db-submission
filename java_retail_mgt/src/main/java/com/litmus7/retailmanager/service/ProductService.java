package com.litmus7.retailmanager.service;

import com.litmus7.retailmanager.dao.ProductDAO;
import com.litmus7.retailmanager.dto.Product;
import com.litmus7.retailmanager.exceptions.ValidationException;
import com.litmus7.retailmanager.util.ComparatorUtil;

import java.util.Comparator;
import java.util.List;

public class ProductService {
    private final ProductDAO productDAO = new ProductDAO();

    public void addProduct(Product product) {
        // Performs validation on the product (e.g., checks for negative price)
        if (product.getPrice() < 0) {
            throw new ValidationException("Product price cannot be negative.");
        }
        productDAO.addProduct(product);
    }

    public List<Product> viewAllProducts() {
        // Calls the ProductDAO to retrieve all products
        List<Product> products = productDAO.viewAllProducts();
        return products;
    }

    public List<Product> viewProductsByCategory(String category) {
        // Calls the ProductDAO to retrieve all products
        // Filters the list to return only products of the specified category
        List<Product> products = productDAO.viewAllProducts();
        return null; 
    }

    public List<Product> sortProducts(List<Product> products, String sortOption) {
        // Gets the appropriate comparator from ComparatorUtil
        Comparator<Product> comparator = ComparatorUtil.getProductComparator(sortOption);
        
        
        if (comparator != null) {
        	// Sorts the list of products using the obtained comparator
            // products.sort(comparator); // Call the sort method here
        }
        return products;
    }
}