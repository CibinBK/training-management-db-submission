package com.litmus7.inventorymanager.util;

import com.litmus7.inventorymanager.dto.InventoryDTO;
import com.litmus7.inventorymanager.exception.FileProcessingException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for reading and parsing CSV files into InventoryDTO objects.
 */
public class CsvReader {

    private static final String CSV_DELIMITER = ",";

    /**
     * Reads and parses a given CSV file into a list of InventoryDTO objects.
     * The first line (header) is skipped.
     *
     * @param file The CSV file to be read.
     * @return A list of InventoryDTOs.
     * @throws FileProcessingException if there is an I/O error or parsing issue.
     */
    public List<InventoryDTO> read(File file) {
        List<InventoryDTO> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            // Skip the header line
            br.readLine();
            // Process each subsequent line
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(CSV_DELIMITER);
                if (values.length != 4) {
                    throw new FileProcessingException("Invalid record format in file: " + file.getName() + ", line: " + line);
                }
                try {
                    String sku = values[0].trim();
                    String productName = values[1].trim();
                    int quantity = Integer.parseInt(values[2].trim());
                    double price = Double.parseDouble(values[3].trim());
                    records.add(new InventoryDTO(sku, productName, quantity, price));
                } catch (NumberFormatException e) {
                    throw new FileProcessingException("Invalid number format in file: " + file.getName() + ", line: " + line, e);
                }
            }
        } catch (IOException e) {
            throw new FileProcessingException("Failed to read file: " + file.getName(), e);
        }
        return records;
    }
}