package com.litmus7.inventorymanager.dao;

import com.litmus7.inventorymanager.constant.SqlConstants;
import com.litmus7.inventorymanager.dto.InventoryDTO;
import com.litmus7.inventorymanager.exception.DatabaseOperationException;
import com.litmus7.inventorymanager.util.ErrorCodesManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class InventoryDAO {

    public void insertRecord(Connection connection, InventoryDTO record) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(SqlConstants.INSERT_ITEM)) {
            preparedStatement.setString(1, record.getSku());
            preparedStatement.setString(2, record.getProductName());
            preparedStatement.setInt(3, record.getQuantity());
            preparedStatement.setDouble(4, record.getPrice());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            String errorCode = "302";
            String errorMessage = ErrorCodesManager.getErrorMessage(errorCode);
            throw new DatabaseOperationException(errorCode, errorMessage + " for SKU: " + record.getSku(), e);
        }
    }
}