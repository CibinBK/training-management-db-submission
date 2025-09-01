package com.litmus7.inventorymanager.dto;

public class InventoryDTO {

    private String sku;
    private String productName;
    private int quantity;
    private double price;

    public InventoryDTO(String sku, String productName, int quantity, double price) {
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getSku() { return sku; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return "InventoryDTO{" +
               "sku='" + sku + '\'' +
               ", productName='" + productName + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               '}';
    }
}