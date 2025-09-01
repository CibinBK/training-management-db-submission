package com.litmus7.inventorymanager.constant;

public final class SqlConstants {

    private SqlConstants() {}

    public static final String INSERT_ITEM =
            "INSERT INTO inventory (sku, product_name, quantity, price) VALUES (?, ?, ?, ?)";
}