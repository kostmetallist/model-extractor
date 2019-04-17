package extractor;

import generating.Generator;
import generating.Log4jGenerator;
import model.mxml.*;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.time.LocalDateTime;


public class Main {

	public static void main(String[] args) {
		
//		LogMXML log = LogMXML.extractLogMXML("data/running-example.mxml");
//		List<DataMXML.Attr> attrList = log.getProcessList().get(0).
//			getCaseList().get(0).getEventList().get(0).getData().getAttrList();
//		
//		for (DataMXML.Attr each : attrList) {
//			if (each.getName().equals("Costs")) {
//				System.out.println("RESULT: " + each.getValue());
//			}
//		}
		
		
		Generator log4jGen = new Log4jGenerator();
		log4jGen.generate();
		
		LogMXML log = LogMXML.extractLogMXML("data/running-example.mxml");
		Canonical can = Translator.castMXML(log);
		
		for (TaggedList each : can.getTaggedSequences()) {
			System.out.println(each.list.size());
			System.out.println(each.list.get(0).getTimestamp());
		}
		
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
