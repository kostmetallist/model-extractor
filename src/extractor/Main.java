package extractor;

import generating.Generator;
import generating.Log4jGenerator;
import model.mxml.*;
import model.*;
import model.mnp.LogMnp;

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
		
//		LogMXML log = LogMXML.extractLogMXML("data/running-example.mxml");
//		Canonical can = Translator.castMXML(log);
//		can.refineData();
//		
//		TransSystem tSys = new TransSystem();
//		tSys.emulateCanonical(can);
//		GraphManager.transSystemToDot(tSys, "data/auto_model.dot");
		
		LogMnp log = LogMnp.extractLogMnp("data/mnp_export.xml");
		Canonical can = Translator.castMnp(log, true);
		
		System.out.println("before deleting similar: " + can.getTaggedSequences().size());
		can.refineData();
		System.out.println("after  deleting similar: " + can.getTaggedSequences().size());
		for (TaggedList pCase : can.getTaggedSequences()) {
			System.out.println(pCase.list.get(0).getCaseId() + " " + pCase.list.get(0).getActivity());
		}
		
		TransSystem tSys = new TransSystem();
		tSys.emulateCanonical(can);
		GraphManager.transSystemToDot(tSys, "data/mnp_model.dot");
		
//		for (TaggedList each : can.getTaggedSequences()) {
//			System.out.println(each.list.size());
//			System.out.println(each.list.get(0).getTimestamp());
//		}
		
	}
}
