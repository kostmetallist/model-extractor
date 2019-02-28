package extractor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;
import java.util.ArrayList;


@XmlRootElement(name = "log")
@XmlType(propOrder = {"eventList"})
public class LogXML {

	private List<Event> eventList = new ArrayList<>();
	
	@XmlElement(name = "event")
	public List<Event> getEventList() {
		return this.eventList;
	}
}
