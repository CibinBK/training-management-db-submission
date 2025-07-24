package com.litmus7.employeemanager.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CsvFileReader implements AutoCloseable {

    private final BufferedReader bufferedReader;

    public CsvFileReader(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        this.bufferedReader = new BufferedReader(fileReader);
    }

    public String readLine() throws IOException {
        return bufferedReader.readLine();
    }

    public void skipHeader() throws IOException {
        bufferedReader.readLine(); // Skips the header line of the CSV file.
    }
    
    public void close() throws IOException {
        if (bufferedReader != null) {
            bufferedReader.close();
        }
    }
}
