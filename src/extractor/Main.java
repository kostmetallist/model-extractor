package extractor;

//import java.io.File;

import org.jgrapht.*;
import org.jgrapht.graph.*;
//import org.jgrapht.io.*;
//import org.jgrapht.traverse.*;

import generating.LogGenerator;
import model.EventXML;
import model.LogXML;



public class Main {
	
	
	
//	public EventXML xmlToEvent(String path) {
//		
//		try {
//			
//			JAXBContext context = JAXBContext.newInstance(EventXML.class);
//			Unmarshaller unmar = context.createUnmarshaller();
//			EventXML event = (EventXML) unmar.unmarshal(new File(path));
//			return event;
//		}
//		
//		catch (JAXBException e) {
//			e.printStackTrace();
//		}
//		
//		return null;
//	}

	public static void main(String[] args) {
		
//		Main app = new Main();
		LogXML log = LogXML.xmlToLogXML("data/sample.xml");
		
		for (EventXML each : log.getEventList()) {
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
        
        LogGenerator.generateLog4j();
	}
}
