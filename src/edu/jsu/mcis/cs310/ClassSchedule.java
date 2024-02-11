package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

public class ClassSchedule {
    
    private final String CSV_FILENAME = "jsu_sp24_v1.csv";
    private final String JSON_FILENAME = "jsu_sp24_v1.json";
    
    private final String CRN_COL_HEADER = "crn";
    private final String SUBJECT_COL_HEADER = "subject";
    private final String NUM_COL_HEADER = "num";
    private final String DESCRIPTION_COL_HEADER = "description";
    private final String SECTION_COL_HEADER = "section";
    private final String TYPE_COL_HEADER = "type";
    private final String CREDITS_COL_HEADER = "credits";
    private final String START_COL_HEADER = "start";
    private final String END_COL_HEADER = "end";
    private final String DAYS_COL_HEADER = "days";
    private final String WHERE_COL_HEADER = "where";
    private final String SCHEDULE_COL_HEADER = "schedule";
    private final String INSTRUCTOR_COL_HEADER = "instructor";
    private final String SUBJECTID_COL_HEADER = "subjectid";
    
    public String convertCsvToJsonString(List<String[]> csv) {
        
        JsonObject Mainobject = new JsonObject();
        
        LinkedHashMap  course= new LinkedHashMap();
        LinkedHashMap subjects = new LinkedHashMap();
        LinkedHashMap schedultype  = new LinkedHashMap();
        JsonArray sectionArray =new JsonArray();
        
        Iterator<String[]> iterator =csv.iterator();
        
        String[] headerlist=iterator.next();
                
        
        
        while(iterator.hasNext()){
            String[] currentrow =iterator.next();
            JsonObject csvObj =new JsonObject();
            for(int i = 0; i<headerlist.length; ++i){
                csvObj.put(headerlist[i],currentrow[i]);
            }
            
            schedultype.put(csvObj.get(TYPE_COL_HEADER),csvObj.get(SCHEDULE_COL_HEADER) );
            
            String name[] = csvObj.get(NUM_COL_HEADER).toString().split(" ");
            subjects.put(name[0], csvObj.get(SUBJECT_COL_HEADER));
            
            LinkedHashMap courseMap =new LinkedHashMap<>(); 
            courseMap.put(SUBJECTID_COL_HEADER, name[0]);
            courseMap.put(NUM_COL_HEADER, name[1]);
            courseMap.put(DESCRIPTION_COL_HEADER,csvObj.get(DESCRIPTION_COL_HEADER));
            int credit = Integer.parseInt(csvObj.get(CREDITS_COL_HEADER).toString());
            courseMap.put(CREDITS_COL_HEADER, credit);
            course.put(csvObj.get(NUM_COL_HEADER), courseMap);
            
            LinkedHashMap sectionMap =new LinkedHashMap<>();
            int crn =Integer.parseInt(csvObj.get(CRN_COL_HEADER).toString());
            sectionMap.put(CRN_COL_HEADER,crn);
            sectionMap.put(SUBJECTID_COL_HEADER,name[0]);
            sectionMap.put(NUM_COL_HEADER,name[1]);
            sectionMap.put(SECTION_COL_HEADER, csvObj.get(SECTION_COL_HEADER));
            sectionMap.put(TYPE_COL_HEADER, csvObj.get(TYPE_COL_HEADER));
            sectionMap.put(START_COL_HEADER, csvObj.get(START_COL_HEADER));
            sectionMap.put(END_COL_HEADER,csvObj.get(END_COL_HEADER));
            sectionMap.put(DAYS_COL_HEADER, csvObj.get(DAYS_COL_HEADER));
            sectionMap.put(WHERE_COL_HEADER,csvObj.get(WHERE_COL_HEADER));
            
            String[] Instructor = csvObj.get(INSTRUCTOR_COL_HEADER).toString().split(", ");
            sectionMap.put(INSTRUCTOR_COL_HEADER,Instructor);
            sectionArray.add(sectionMap);
        }       
 
        Mainobject.put("scheduletype",schedultype);
        Mainobject.put("subject",subjects);
        Mainobject.put("course",course);
        Mainobject.put("section",sectionArray);
        
        String jsonString = Jsoner.serialize(Mainobject);
        return jsonString;
    
    }
    
    public String convertJsonToCsvString(JsonObject json) {
        
        JsonObject scheduletype =(JsonObject) json.get("scheduletype");
        JsonObject subject = (JsonObject)json.get("subject");
        JsonObject course = (JsonObject)json.get("course");
        JsonArray section = (JsonArray) json.get("section");
        
        List <String[]> csvList=new ArrayList<>();
        
        // Write headers
        String[]header = {
                CRN_COL_HEADER, SUBJECT_COL_HEADER, NUM_COL_HEADER, DESCRIPTION_COL_HEADER,
                SECTION_COL_HEADER, TYPE_COL_HEADER, CREDITS_COL_HEADER, START_COL_HEADER,
                END_COL_HEADER, DAYS_COL_HEADER, WHERE_COL_HEADER, SCHEDULE_COL_HEADER,
                INSTRUCTOR_COL_HEADER
        };
        csvList.add(header);
        
       
        
        for (int a = 0; a < section.size(); a++) {
            JsonObject sectionObject = (JsonObject)section.get(a);

            String crn = sectionObject.get(CRN_COL_HEADER).toString();
            String subjects = subject.get(sectionObject.get(SUBJECTID_COL_HEADER)).toString();
            String num = (sectionObject.get(SUBJECTID_COL_HEADER) + " " + sectionObject.get(NUM_COL_HEADER));
            
            HashMap coursename = (HashMap)course.get(num);
            String description = coursename.get(DESCRIPTION_COL_HEADER).toString();
            String sections = sectionObject.get(SECTION_COL_HEADER).toString();
            String type = sectionObject.get(TYPE_COL_HEADER).toString();
            String credit = coursename.get(CREDITS_COL_HEADER).toString();
            String start = sectionObject.get(START_COL_HEADER).toString();
            String end = sectionObject.get(END_COL_HEADER).toString();
            String days = sectionObject.get(DAYS_COL_HEADER).toString();
            String where = sectionObject.get(WHERE_COL_HEADER).toString();
            String schedule = scheduletype.get(type).toString();

            JsonArray InstructorJsonArray = (JsonArray)sectionObject.get(INSTRUCTOR_COL_HEADER);
            String[] instructornames = InstructorJsonArray.toArray(new String[0]);
            String instructor = String.join(", ", instructornames);
            
            String[]csvVal={crn,subjects,num,description,sections,type,credit,start,end,days,where,schedule, instructor};
            csvList.add(csvVal);
        }
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer,'\t','"','\\',"\n");
        csvWriter.writeAll(csvList);
        
       /* String CUSTOM_LINE_END = "\n"; // Equivalent to 0x0D 0x0A
        StringWriter sw = new StringWriter(); 
        CSVWriter csvWriter = new CSVWriter(sw, '\t', CSVWriter.DEFAULT_QUOTE_CHARACTER,
                     CSVWriter.DEFAULT_ESCAPE_CHARACTER, CUSTOM_LINE_END);

        String[] header = new String[13];
        header[0] = CRN_COL_HEADER;
        header[1] = SUBJECT_COL_HEADER;
        header[2] = NUM_COL_HEADER;
        header[3] = DESCRIPTION_COL_HEADER;
        header[4] = SECTION_COL_HEADER;
        header[5] = TYPE_COL_HEADER;
        header[6] = CREDITS_COL_HEADER;
        header[7] = START_COL_HEADER;
        header[8] = END_COL_HEADER;
        header[9] = DAYS_COL_HEADER;
        header[10] = WHERE_COL_HEADER;
        header[11] = SCHEDULE_COL_HEADER;
        header[12] = INSTRUCTOR_COL_HEADER;
        csvWriter.writeNext(header);
        
        int counter = 0;
        for(Object objitem:  section)
        {
            counter++;
            JsonObject item = (JsonObject) objitem;
            
            String[] line = new String[13];
            line[0] =  Long.toString(((BigDecimal) item.get(CRN_COL_HEADER)).longValue());
            line[1] = (String) subject.get( (String) item.get(SUBJECTID_COL_HEADER));
            //for (Object courseitem: course)
            
            for (Object keyObj : course.keySet()) {
                String key = (String) keyObj; // Cast key object to String
                JsonObject coursechild = (JsonObject)course.get(key); // Get value for the key
                //if (((String) coursechild.get(NUM_COL_HEADER)).equalsIgnoreCase((String) item.get(NUM_COL_HEADER)))
                if (((String) coursechild.get(NUM_COL_HEADER)).equalsIgnoreCase((String) item.get(NUM_COL_HEADER)) && ((String) coursechild.get(SUBJECTID_COL_HEADER)).equalsIgnoreCase((String) item.get(SUBJECTID_COL_HEADER)))
                {
                    line[2] = key;
                    line[3] =(String) coursechild.get(DESCRIPTION_COL_HEADER);
                    line[6] = Long.toString(((BigDecimal) coursechild.get(CREDITS_COL_HEADER)).longValue());//((BigDecimal) coursechild.get(CREDITS_COL_HEADER)).toString();
                    break;
                }
            }
            
            line[4] =(String) item.get(SECTION_COL_HEADER);
            line[5] = (String) item.get(TYPE_COL_HEADER);
            line[7] = (String) item.get(START_COL_HEADER);
            line[8] = (String) item.get(END_COL_HEADER);
            line[9] = (String) item.get(DAYS_COL_HEADER);
            line[10] = (String) item.get(WHERE_COL_HEADER);
            line[11] =(String) scheduletype.get( ((String) item.get(TYPE_COL_HEADER)));
            JsonArray instructor = (JsonArray) item.get("instructor");
            
            String instructorvalue = "";
            for (Object value : instructor) {
                if (!instructorvalue.isEmpty())
                    instructorvalue += ", ";
                instructorvalue += (String) value;
            }
            line[12] = instructorvalue;

            csvWriter.writeNext(line);
        
        }
        return sw.toString();
        */
       return writer.toString();
    }
    
    
    public JsonObject getJson() {
        
        JsonObject json = getJson(getInputFileData(JSON_FILENAME));
        return json;
        
    }
    
    public JsonObject getJson(String input) {
        
        JsonObject json = null;
        
        try {
            json = (JsonObject)Jsoner.deserialize(input);
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return json;
        
    }
    
    public List<String[]> getCsv() {
        
        List<String[]> csv = getCsv(getInputFileData(CSV_FILENAME));
        return csv;
        
    }
    
    public List<String[]> getCsv(String input) {
        
        List<String[]> csv = null;
        
        try {
            
            CSVReader reader = new CSVReaderBuilder(new StringReader(input)).withCSVParser(new CSVParserBuilder().withSeparator('\t').build()).build();
            csv = reader.readAll();
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return csv;
        
    }
    
    public String getCsvString(List<String[]> csv) {
        
        StringWriter writer = new StringWriter();
        CSVWriter csvWriter = new CSVWriter(writer, '\t', '"', '\\', "\n");
        
        csvWriter.writeAll(csv);
        
        return writer.toString();
        
    }
    
    private String getInputFileData(String filename) {
        
        StringBuilder buffer = new StringBuilder();
        String line;
        
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        
        try {
        
            BufferedReader reader = new BufferedReader(new InputStreamReader(loader.getResourceAsStream("resources" + File.separator + filename)));

            while((line = reader.readLine()) != null) {
                buffer.append(line).append('\n');
            }
            
        }
        catch (Exception e) { e.printStackTrace(); }
        
        return buffer.toString();
        
    }
    
}