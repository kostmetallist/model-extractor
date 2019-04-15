package model.mxml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import java.util.List;
import java.util.ArrayList;


@XmlType(propOrder = {"eventList"})
public class CaseMXML {

	private List<EventMXML> eventList = new ArrayList<>();
	@XmlAttribute
	private int id;
	
	@XmlElement(name = "AuditTrailEntry")
	public List<EventMXML> getEventList() {
		return this.eventList;
	}
	
	public int getId() {
		return this.id;
	}
}
