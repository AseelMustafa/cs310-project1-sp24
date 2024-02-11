package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import java.io.FileWriter;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        
        ClassSchedule schedule = new ClassSchedule();
        
        try {
            // Get CSV/JSON Data
            
            List<String[]> csvOriginal = schedule.getCsv();
            JsonObject jsonOriginal = schedule.getJson();
            String convertedjson = schedule.convertCsvToJsonString(csvOriginal);
            // Print Total Sections Found in CSV and JSON Data (should be equal)
            
            
            String testCsvString = schedule.convertJsonToCsvString(jsonOriginal);
            
            String filePath = "C:\\Users\\aseel\\Downloads\\JsontoCSVResult.csv"; // Specify your file path here
            FileWriter writer = new FileWriter(filePath);
            writer.write(testCsvString);
            
        
            System.out.println("Sections Found (CSV): " + (csvOriginal.size() - 1));
            
            JsonArray sections = (JsonArray)jsonOriginal.get("section");
            System.out.println("Sections Found (JSON): " + sections.size());
            
        }
        catch (Exception e) { e.printStackTrace(); }
            
    }
    
}


     /*JsonObject json =new JsonObject();
     JsonObject scheduletype =new JsonObject();
     JsonObject subject =new JsonObject();
     JsonArray section =new JsonArray();*/
     
    // CSVReader reader =new CSVReader(new StringReader(CSV_FILENAME);
     //List<String[]> full = reader.readAll();

     //Iterator<String[]> iterator = reader.iterator();