package com.litmus7;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList; // For List
import java.util.List;     // For List

class EmployeeReader{
	public Response readEmployees(String file) { 
		DatabaseManager dbManager = new DatabaseManager();
		dbManager.establishConnection();

		int successfulEntries = 0;
		int lineNumber = 1; 
        List<String> detailedErrorMessages = new ArrayList<>(); // To collect all individual record errors
        String criticalOverallErrorMessage = null; // To hold a critical error message if one occurs during file read

		try(FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader)) {
			
			String line;
			bufferReader.readLine(); // skip the header line
			
            System.out.println("\n--- Starting Employee Data Import ---");

			while((line = bufferReader.readLine()) != null) {
				lineNumber++; // Increment for each data line processed (after header)
				
				if(line.trim().isEmpty()) { 
					String msg = "Line " + lineNumber + ": SKIPPED (Empty Line)";
                    System.out.println(msg); // Print to console for immediate feedback
                    detailedErrorMessages.add(msg); // Add to list for overall report
					continue;
				}
				String values[] = line.split(",", -1); // split the contents by comma

				DatabaseManager.RecordProcessResult recordResult = dbManager.processEmployee(values, lineNumber);
                
                if (recordResult.success) {
                    successfulEntries++;
                    System.out.println(recordResult.message); // Print success message to console
                } else {
                    System.err.println(recordResult.message); // Print error message to console
                    detailedErrorMessages.add(recordResult.message); // Add error to list for overall report
                }
			}
		} catch(IOException e) {
			criticalOverallErrorMessage = "Critical Error reading CSV file: " + e.getMessage();
            System.err.println(criticalOverallErrorMessage);
			e.printStackTrace();
		} finally {
            dbManager.closeConnection(); // Ensure connection is closed
            System.out.println("--- Employee Data Import Finished ---");
        }

        // Calculate total records attempted (excluding header)
        int totalRecordsAttempted = lineNumber - 1; 

        // If a critical IOException occurred, add it to the detailed messages
        if (criticalOverallErrorMessage != null) {
            detailedErrorMessages.add("Overall System Error: " + criticalOverallErrorMessage);
        }

        return Response.createOverallResponse(
            totalRecordsAttempted,
            successfulEntries,
            detailedErrorMessages
        );
	}
}
