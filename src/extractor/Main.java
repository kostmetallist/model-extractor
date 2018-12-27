package extractor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;

//import org.apache.log4j.Logger;
//import org.apache.log4j.BasicConfigurator;



public class Main {
	
	public LogXML xmlToLogXML(String path) {
		
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
	
	public Event xmlToEvent(String path) {
		
		try {
			
			JAXBContext context = JAXBContext.newInstance(Event.class);
			Unmarshaller unmar = context.createUnmarshaller();
			Event event = (Event) unmar.unmarshal(new File(path));
			return event;
		}
		
		catch (JAXBException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public static void main(String[] args) {
		
		Main app = new Main();
		//LogXML log = app.xmlToLogXML("data/sample.xml");
		Event event = app.xmlToEvent("data/sampleSimple.xml");
		System.out.println(event);
		
		//for (Event each : log.getEventList()) {
		//	System.out.println(each);
		//}
	}
}
