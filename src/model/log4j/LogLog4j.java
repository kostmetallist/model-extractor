package model.log4j;

import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class LogLog4j {

    private static String pattern = "%d %t %C %M %pid %m";
    private List<EventLog4j> eventList = new ArrayList<>();
    
    public static void setPattern(String newPattern) {
        pattern = newPattern;
    }
    
    public List<EventLog4j> getEventList() {
        return this.eventList;
    }
    
    public void setEventList(List<EventLog4j> eventList) {
        this.eventList = eventList;
    }
    
    public static LogLog4j extractLogLog4j(String path) {
        
        LogLog4j log = new LogLog4j();
        List<EventLog4j> events = log.getEventList();
        File file = new File(path);
        FileReader fr = null;
        BufferedReader br = null;
        
        try {
            
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            List<EventLog4j> eventList = new ArrayList<>();
            String line = br.readLine();
            
            while (line != null) {
                
                EventLog4j event = EventLog4j.parseLogEntry(line, pattern);
                events.add(event);
                line = br.readLine();
            }
        }
        
        catch (IOException e) {
            e.printStackTrace();
        }
        
        finally {
            try {
                br.close();
                fr.close();
            }
            
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        return log;
    }
}
