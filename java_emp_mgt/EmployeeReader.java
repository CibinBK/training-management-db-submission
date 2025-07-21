import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

class EmployeeReader{
	public void readEmployees(String file) {
		try(FileReader fileReader = new FileReader(file);
			BufferedReader bufferReader = new BufferedReader(fileReader)) {
			
			DatabaseManager dbManager = new DatabaseManager();
			dbManager.establishConnection(); // establish connection to database
			
			String line;
			bufferReader.readLine(); // skip the header line
			int lineNumber = 1; // to start numbering after the header
			while((line = bufferReader.readLine()) != null) {
				lineNumber++;
				if(line.trim().isEmpty()) { 
					System.out.println("Warning : Skipping empty line at line " + lineNumber); // skip empty lines
					continue;
				}
				String values[] = line.split(","); // split the contents by comma
				dbManager.processEmployee(values, lineNumber); // process the values
			}
			dbManager.closeConnection();
		} catch(IOException e) {
			System.out.println("Error reading CSV file " + e.getMessage());
			e.printStackTrace();
		}
	}
}
