package model.log4j;

public class EventLog4j {

    public String timestamp;
    public String processIdentifier;
    public String className;
    public String methodName;
    public String extraInfo;
    
    public static EventLog4j parseLogEntry(String entry, String pattern) {
        
        EventLog4j event = new EventLog4j();
        String[] entryTokens = entry.split(" ");
        String[] patternTokens = pattern.split(" ");
        String extraField = "";
        
        if (entryTokens.length != patternTokens.length) {
            System.err.println("log4j event is not parsed:" + 
                    " pattern matching problem");
            return event;
        }
        
        for (int i = 0; i < entryTokens.length; ++i) {
            
            String pToken = patternTokens[i];
            int specifierIdx = pToken.indexOf("%");
            String specifierName = pToken.substring(specifierIdx+1);
            
            if (specifierName.equals("d")) {
                event.timestamp = entryTokens[i];
            }
            
            else if (specifierName.equals("pid")) {
                event.processIdentifier = entryTokens[i];
            }
            
            else if (specifierName.equals("C")) {
                event.className = entryTokens[i];
            }
            
            else if (specifierName.equals("M")) {
                event.methodName = entryTokens[i];
            }
            
            else {
                extraField = extraField + ";" + entryTokens[i]; 
            }
        }
        
        event.extraInfo = extraField;
        return event;
    }
}
