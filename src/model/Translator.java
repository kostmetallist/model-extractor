package model;

import java.time.ZonedDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import model.log4j.*;
import model.mnp.*;
import model.mxml.*;
import model.xes.*;

public class Translator {

    private static final DateTimeFormatter log4jFormatter = 
            DateTimeFormatter.ofPattern("dd.MM.yyyy'T'HH:mm:ss");
    private static final DateTimeFormatter mxmlFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static final DateTimeFormatter mnpFormatter = 
            DateTimeFormatter.ofPattern("dd.MM.yy HH:mm:ss");
    private static final DateTimeFormatter xesFormatter = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private static String customPattern = null;
    
    public static void setCustomPattern(String pattern) {
        customPattern = pattern;
    }
    
    public static Canonical castLog4j(LogLog4j log) {
        
        List<EventLog4j> events = log.getEventList();
        Canonical output = new Canonical();
        if (events.isEmpty()) {
            return output;
        }
        
        DateTimeFormatter formatter = (customPattern == null)? log4jFormatter: 
            DateTimeFormatter.ofPattern(customPattern);
        if (formatter.getZone() == null) {
            formatter = formatter.withZone(ZoneId.of("UTC"));
        }
        
        Event earliest = new Event(), 
                latest = new Event();
        ZonedDateTime found = ZonedDateTime.parse(events.get(0).timestamp, 
                formatter);
        earliest.setTimestamp(found);
        latest.setTimestamp(found);
        
        List<TaggedList> taggedSequences = new ArrayList<>();
        Map<Integer, Integer> caseNumbers = new HashMap<>();
        
        for (EventLog4j each : events) {
            
            int caseId = Integer.parseInt(each.processIdentifier); 
            Integer correspondingIdx = caseNumbers.get(caseId);
            TaggedList tList = null;
            
            // haven't met such caseId
            if (correspondingIdx == null) {
                
                List<Event> eventList = new ArrayList<>();
                tList = new TaggedList(eventList, 1);
                caseNumbers.put(caseId, taggedSequences.size());
                taggedSequences.add(tList);
            }
            
            else {
                tList = taggedSequences.get(correspondingIdx);
            }
            
            Event event = new Event();
            event.setCaseId(caseId);
            event.setActivity(each.className + ":" + each.methodName);
            event.setTimestamp(ZonedDateTime.parse(each.timestamp, 
                    formatter));
            
            Map<String, String> extra = new HashMap<>();
            extra.put("Data", each.extraInfo);
            event.setExtra(extra);
            tList.list.add(event);
            
            if (earliest.getTimestamp().
                    until(event.getTimestamp(), ChronoUnit.MILLIS) < 0) {
                earliest = event;
            }
            
            if (event.getTimestamp().
                    until(latest.getTimestamp(), ChronoUnit.MILLIS) < 0) {
                latest = event;
            }
        }
        
        ZonedDateTime from = earliest.getTimestamp(), 
                to = latest.getTimestamp(); 
        System.out.println("parsed log with " + events.size() + " events");
        System.out.println("captured time fragment from " + 
                from.format(formatter) + " to " + 
                to.format(formatter) + " (" + 
                from.until(to, ChronoUnit.MILLIS) + " ms)");
        
        output.setTaggedSequences(taggedSequences);
        return output;
    }

    public static Canonical castMXML(LogMXML log) {

        Canonical output = new Canonical();
        if (log.getProcessList().isEmpty()) {
            return output;
        }

        DateTimeFormatter formatter = (customPattern == null)? mxmlFormatter: 
            DateTimeFormatter.ofPattern(customPattern);
        if (formatter.getZone() == null) {
            formatter = formatter.withZone(ZoneId.of("UTC"));
        }
        
        int eventNumber = 0, 
                caseNum = 0;
        List<TaggedList> taggedSequences = new ArrayList<>();
        ProcessMXML process = log.getProcessList().get(0);
        ZonedDateTime from = ZonedDateTime.of(2039, 1, 1, 
                0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime to = ZonedDateTime.of(1970, 1, 1, 
                0, 0, 0, 0, ZoneId.systemDefault());

        for (CaseMXML cMXML : process.getCaseList()) {

            List<Event> eventList = new ArrayList<>();
            for (EventMXML eMXML : cMXML.getEventList()) {

                Event event = new Event();
                Map<String, String> extra = new HashMap<>();
                if (eMXML.getData() != null) {
                    
                    List<DataMXML.Attr> attrs = eMXML.getData().getAttrList();
                    for (DataMXML.Attr attr : attrs) {
                        extra.put(attr.getName(), attr.getValue());
                    }
                }

                event.setCaseId(caseNum);
                event.setActivity(eMXML.getWorkflowModelElement());
                event.setTimestamp(ZonedDateTime.parse(eMXML.getTimestamp(), 
                        formatter));
                event.setExtra(extra);
                eventList.add(event);
                eventNumber++;
                
                if (from.until(event.getTimestamp(), ChronoUnit.MILLIS) < 0) {
                    from = event.getTimestamp();
                }
                
                if (event.getTimestamp().until(to, ChronoUnit.MILLIS) < 0) {
                    to = event.getTimestamp();
                }
            }

            taggedSequences.add(new TaggedList(eventList, 1));
            caseNum++;
        }

        System.out.println("parsed log with " + eventNumber + " events");
        System.out.println("captured time fragment from " + 
                from.format(formatter) + " to " + 
                to.format(formatter) + " (" + 
                from.until(to, ChronoUnit.MILLIS) + " ms)");
        
        output.setTaggedSequences(taggedSequences);
        return output;
    }

    private static class MnpCases {
        private List<String[]> cases = new ArrayList<>();

        public int refreshOrCreate(String[] caseDescriptor) {

            for (int i = 0; i < cases.size(); ++i) {

                String[] existingDescriptor = cases.get(i);

                if (existingDescriptor[0].equals(caseDescriptor[0]) &&
                    !existingDescriptor[0].isEmpty() || 
                    existingDescriptor[1].equals(caseDescriptor[1]) &&
                    !existingDescriptor[1].isEmpty() || 
                    existingDescriptor[2].equals(caseDescriptor[2]) &&
                    !existingDescriptor[2].isEmpty()) {

                    if (existingDescriptor[0].isEmpty()) {
                        existingDescriptor[0] = caseDescriptor[0];
                    }

                    if (existingDescriptor[1].isEmpty()) {
                        existingDescriptor[1] = caseDescriptor[1];
                    }

                    if (existingDescriptor[2].isEmpty()) {
                        existingDescriptor[2] = caseDescriptor[2];
                    }

                    return i;
                }
            }

            String[] newDescriptor = new String[3];
            for (int i = 0; i < 3; ++i) {
                newDescriptor[i] = caseDescriptor[i];
            }

            cases.add(newDescriptor);
            return cases.size() - 1;
        }

        public void showCases() {
            for (String[] each : this.cases) {
                System.out.println("|" + each[0] + "|" + each[1] + 
                        "|" + each[2] + "|");
            }
        }
    }

    public static Canonical castMnp(LogMnp log, boolean isReversed) {

        Canonical output = new Canonical();
        List<EventMnp> eventList = log.getEventList();
        if (eventList.isEmpty()) {
            return output;
        }

        List<TaggedList> taggedSequences = new ArrayList<>();
        int i, step, stopIndex;

        if (isReversed) {
            i = eventList.size() - 1;
            step = -1;
            stopIndex = -1;
        } else {
            i = 0;
            step = 1;
            stopIndex = eventList.size();
        }

        MnpCases mnpCases = new MnpCases();
        while (i != stopIndex) {

            EventMnp eventMnp = eventList.get(i);
            String requestId = null, processInstanceId = null, npId = null, 
                    eventName = null, timestamp = null;
            Map<String, String> extra = new HashMap<>();

            for (EventMnp.ColumnMnp col : eventMnp.getColumnList()) {
                switch (col.getName()) {
                case "REQUEST_ID":
                    requestId = col.getValue().trim();
                    break;
                case "PROCESSINSTANCEID":
                    processInstanceId = col.getValue().trim();
                    break;
                case "NPID":
                    npId = col.getValue().trim();
                    break;
                case "EVENT_NAME":
                    eventName = col.getValue().trim();
                    break;
                case "TIMESTAMP":
                    timestamp = col.getValue().trim();
                    break;
                default: 
                    extra.put(col.getName(), col.getValue());
                }
            }

            String[] caseDescriptor = { requestId, processInstanceId, npId };
            int caseIdx = mnpCases.refreshOrCreate(caseDescriptor);

            // new case that we did not seen earlier
            if (caseIdx == taggedSequences.size()) {
                taggedSequences.add(new TaggedList(new ArrayList<Event>(), 1));
            }

            Event event = new Event();
            event.setCaseId(caseIdx);
            event.setActivity(eventName);
            event.setTimestamp(ZonedDateTime.parse(timestamp, mnpFormatter));
            event.setExtra(extra);
            taggedSequences.get(caseIdx).list.add(event);
            i += step;
        }

        //
        mnpCases.showCases();
        //
        output.setTaggedSequences(taggedSequences);
        return output;
    }
    
    public static Canonical castXES(LogXES log) {
        
        Canonical output = new Canonical();
        if (log.getCaseList().isEmpty()) {
            return output;
        }
        
        DateTimeFormatter formatter = (customPattern == null)? xesFormatter: 
            DateTimeFormatter.ofPattern(customPattern);
        if (formatter.getZone() == null) {
            formatter = formatter.withZone(ZoneId.of("UTC"));
        }
        
        int eventNumber = 0, 
                caseNum = 0;
        List<TaggedList> taggedSequences = new ArrayList<>();
        ZonedDateTime from = ZonedDateTime.of(2039, 1, 1, 
                0, 0, 0, 0, ZoneId.systemDefault());
        ZonedDateTime to = ZonedDateTime.of(1970, 1, 1, 
                0, 0, 0, 0, ZoneId.systemDefault());
        
        for (CaseXES cXES : log.getCaseList()) {

            List<Event> eventList = new ArrayList<>();
            for (EventXES eXES : cXES.getEventList()) {

                Event event = new Event();
                String activity = null;
                List<EventXES.StringXES> stringList = eXES.getStringList();
                Map<String, String> extra = new HashMap<>();
                
                for (EventXES.StringXES str : stringList) {
                    if (str.getKey().equals("concept:name")) {
                        activity = str.getValue();
                    }
                    
                    else {
                        extra.put(str.getKey(), str.getValue());
                    }
                }

                event.setCaseId(caseNum);
                event.setActivity(activity);
                event.setTimestamp(
                        ZonedDateTime.parse(eXES.getDate().getValue(), 
                        formatter));
                event.setExtra(extra);
                eventList.add(event);
                eventNumber++;
                
                if (from.until(event.getTimestamp(), ChronoUnit.MILLIS) < 0) {
                    from = event.getTimestamp();
                }
                
                if (event.getTimestamp().until(to, ChronoUnit.MILLIS) < 0) {
                    to = event.getTimestamp();
                }
            }

            taggedSequences.add(new TaggedList(eventList, 1));
            caseNum++;
        }
        
        System.out.println("parsed log with " + eventNumber + " events");
        System.out.println("captured time fragment from " + 
                from.format(formatter) + " to " + 
                to.format(formatter) + " (" + 
                from.until(to, ChronoUnit.MILLIS) + " ms)");
        
        output.setTaggedSequences(taggedSequences);
        return output;
    }
}
