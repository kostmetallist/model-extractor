package model;

import model.mxml.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Translator {
	
	private static DateTimeFormatter mxmlFormatter = 
		DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	

	public static Canonical castMXML(LogMXML log) {
		
		Canonical output = new Canonical();
		if (log.getProcessList().isEmpty()) { return output; }
		
		List<TaggedList> taggedSequences = new ArrayList<>();
		ProcessMXML process = log.getProcessList().get(0);
		int caseNum = 0;
		
		for (CaseMXML cMXML : process.getCaseList()) {
			
			List<Event> eventList = new ArrayList<>();
			for (EventMXML eMXML : cMXML.getEventList()) {
				
				Event event = new Event();
				List<DataMXML.Attr> attrList = eMXML.getData().getAttrList(); 
				String activity = null;
				for (DataMXML.Attr attr : attrList) {
					if (attr.getName().equals("Activity")) {
						activity = attr.getValue();
						break;
					}
				}
					
				event.setActivity(activity);
				event.setCaseId(caseNum);
				event.setTimestamp(LocalDateTime.parse(
					eMXML.getTimestamp(), mxmlFormatter));
				eventList.add(event);
			}
				
			taggedSequences.add(new TaggedList(eventList, 1));
			caseNum++;
		}

		output.setTaggedSequences(taggedSequences);
		return output;
	}
}
