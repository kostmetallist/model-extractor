package extractor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;
import java.util.ArrayList;

@XmlRootElement(name = "log")
@XmlType(propOrder = {"events"})
public class LogXML {

	private List<Event> eventList = new ArrayList<>();
	
	@XmlElement(name = "events")
	public List<Event> getEventList() {
		return this.eventList;
	}
}
