package model;

import model.mxml.*;
import java.util.List;
import java.util.ArrayList;


public class Translator {

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
				eventList.add(event);
			}
				
			taggedSequences.add(new TaggedList(eventList, 1));
			caseNum++;
		}

		output.setTaggedSequences(taggedSequences);
		return output;
	}
}
