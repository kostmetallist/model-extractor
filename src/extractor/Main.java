package extractor;

import generating.Generator;
import generating.Log4jGenerator;
//import model.mxml.*;
import model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
		
		Canonical can = new Canonical();
		
		Event e1_1 = new Event();
		e1_1.setActivity("A");
		Event e1_2 = new Event();
		e1_2.setActivity("B");
		Event e1_3 = new Event();
		e1_3.setActivity("C");
		TaggedList tl1 = new TaggedList();
		tl1.list = Arrays.asList(e1_1, e1_2, e1_3);
		tl1.tag = 1;
		
		Event e2_1 = new Event();
		e2_1.setActivity("D");
		Event e2_2 = new Event();
		e2_2.setActivity("E");
		Event e2_3 = new Event();
		e2_3.setActivity("F");
		TaggedList tl2 = new TaggedList();
		tl2.list = Arrays.asList(e2_1, e2_2, e2_3);
		tl2.tag = 1;
		
		Event e3_1 = new Event();
		e3_1.setActivity("A");
		Event e3_2 = new Event();
		e3_2.setActivity("B");
		Event e3_3 = new Event();
		e3_3.setActivity("C");
		TaggedList tl3 = new TaggedList();
		tl3.list = Arrays.asList(e3_1, e3_2, e3_3);
		tl3.tag = 1;
		
		Event e4_1 = new Event();
		e4_1.setActivity("D");
		Event e4_2 = new Event();
		e4_2.setActivity("E");
		TaggedList tl4 = new TaggedList();
		tl4.list = Arrays.asList(e4_1, e4_2);
		tl4.tag = 1;
		
		List<TaggedList> canData = new ArrayList<>();
		canData.add(tl1);
		canData.add(tl2);
		canData.add(tl3);
		canData.add(tl4);
		
		can.setTaggedSequences(canData);
		can.refineData();
		System.out.println("Final result");
		for (TaggedList tl : can.getTaggedSequences()) {
			System.out.println(tl.tag);
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
