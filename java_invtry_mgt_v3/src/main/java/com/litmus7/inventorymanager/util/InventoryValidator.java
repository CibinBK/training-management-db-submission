package com.litmus7.inventorymanager.util;

import com.litmus7.inventorymanager.dto.InventoryDTO;
import com.litmus7.inventorymanager.exception.ValidationException;

public class InventoryValidator {
    public void validate(InventoryDTO record) {
        if (record == null) {
            String errorMessage = ErrorCodesManager.getErrorMessage("202");
            throw new ValidationException("202", errorMessage);
        }
        if (record.getSku() == null || record.getSku().trim().isEmpty()) {
            String errorMessage = ErrorCodesManager.getErrorMessage("202");
            throw new ValidationException("202", errorMessage + " (SKU is null or empty) for record: " + record);
        }
        if (record.getProductName() == null || record.getProductName().trim().isEmpty()) {
            String errorMessage = ErrorCodesManager.getErrorMessage("202");
            throw new ValidationException("202", errorMessage + " (Product Name is null or empty) for record with SKU: " + record.getSku());
        }
        if (record.getQuantity() <= 0) {
            String errorMessage = ErrorCodesManager.getErrorMessage("202");
            throw new ValidationException("202", errorMessage + " (Quantity <= 0) for record with SKU: " + record.getSku());
        }
        if (record.getPrice() <= 0) {
            String errorMessage = ErrorCodesManager.getErrorMessage("202");
            throw new ValidationException("202", errorMessage + " (Price <= 0) for record with SKU: " + record.getSku());
        }
    }
}