package com.litmus7.retailmanager.service;

import com.litmus7.retailmanager.dao.ProductDAO;
import com.litmus7.retailmanager.dto.Product;
import java.util.List;
import java.util.Comparator;

public class ProductService {

	private final ProductDAO productDAO = new ProductDAO();
    public void addProduct(Product product) {
        // Performs validation on the product (e.g., checks for negative price)
        // Calls the ProductDAO to save the product
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
        // Sorts the list of products based on the sort option using a Comparator
        return null;
    }
}