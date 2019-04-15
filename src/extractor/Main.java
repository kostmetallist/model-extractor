package extractor;

import generating.LogGenerator;
import model.EventXML;
import model.LogXML;
import model.TransSystem;

import java.util.Arrays;


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
		
		LogXML log = LogXML.xmlToLogXML("data/sample.xml");
		
		for (EventXML each : log.getEventList()) {
			System.out.println(each);
		}
		
		LogGenerator.generateLog4j();
//		TransSystem ts = new TransSystem();
//		
//		TransSystem.TSVertex v1 = new TransSystem.TSVertex(Arrays.asList("{}"));
//		TransSystem.TSVertex v2 = new TransSystem.TSVertex(Arrays.asList("{a}"));
//		TransSystem.TSVertex v3 = new TransSystem.TSVertex(Arrays.asList("{a, b}"));
//		TransSystem.TSVertex v4 = new TransSystem.TSVertex(Arrays.asList("{a, c}"));
//		
//		ts.addVertex(v1);
//		ts.addVertex(v2);
//		ts.addVertex(v3);
//		ts.addVertex(v4);
//		
//		TransSystem.TSEdge e12 = new TransSystem.TSEdge("a");
//		ts.addEdge(v1, v2, e12);
//		TransSystem.TSEdge e23 = new TransSystem.TSEdge("b");
//		ts.addEdge(v2, v3, e23);
//		TransSystem.TSEdge e24 = new TransSystem.TSEdge("c");
//		ts.addEdge(v2, v4, e24);
//		
//		GraphManager.transSystemToDot(ts, "data/ts-test.dot");
	}
}
