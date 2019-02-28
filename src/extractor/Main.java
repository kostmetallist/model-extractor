package extractor;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import java.io.File;

import org.jgrapht.*;
import org.jgrapht.graph.*;
//import org.jgrapht.io.*;
//import org.jgrapht.traverse.*;



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
		LogXML log = app.xmlToLogXML("data/sample.xml");
		//Event event = app.xmlToEvent("data/sampleSimple.xml");
		//System.out.println(event);
		
		for (Event each : log.getEventList()) {
			System.out.println(each);
		}
		
        Graph<String, DefaultEdge> g = new SimpleGraph<>(DefaultEdge.class);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add the vertices
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        // add edges to create a circuit
        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v4);
        g.addEdge(v4, v1);
		
        GraphManager gManager = new GraphManager();
        gManager.graphToDot(g, "data/graph.dot");
	}
}
