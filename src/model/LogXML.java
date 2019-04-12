package model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.util.List;
import java.io.File;
import java.util.ArrayList;


@XmlRootElement(name = "log")
@XmlType(propOrder = {"eventList"})
public class LogXML {

	private List<EventXML> eventList = new ArrayList<>();
	
	@XmlElement(name = "event")
	public List<EventXML> getEventList() {
		return this.eventList;
	}
	
	public static LogXML xmlToLogXML(String path) {	
		try {
			
			JAXBContext context = JAXBContext.newInstance(LogXML.class);
			Unmarshaller unmar = context.createUnmarshaller();
			LogXML log = (LogXML) unmar.unmarshal(new File(path));
			return log;
		}
		
		catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}
