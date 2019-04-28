package extractor;

import java.util.ArrayList;
import java.util.List;

import generating.Generator;
import generating.Log4jGenerator;
import model.*;
import model.mnp.LogMnp;
import model.xes.LogXES;


public class Main {

    public static void main(String[] args) {

        Generator log4jGen = new Log4jGenerator();
        log4jGen.generate();

//		LogMXML log = LogMXML.extractLogMXML("data/running-example.mxml");
//		Canonical can = Translator.castMXML(log);
//		can.refineData();		
//		TransSystem tSys = new TransSystem();
//		tSys.emulateCanonical(can);
//		GraphManager.transSystemToDot(tSys, "data/auto_model.dot");

        long startTime = System.nanoTime();
//        LogMnp log = LogMnp.extractLogMnp("data/mnp_export.xml");
        LogXES log = LogXES.extractLogXES("data/L1.xes");
//        Canonical can = Translator.castMnp(log, true);
        Canonical can = Translator.castXES(log);
        can.refineData();
        System.out.println("refined to " + 
                can.getTaggedSequences().size() + " cases");
        
//        List<ReferencedSequence> rSeqList = new ArrayList<>();
//        for (TaggedList pCase : can.getTaggedSequences()) {
//            
//            ReferencedSequence rSeq = new ReferencedSequence(pCase.list);
//            rSeq.reduceLoops();
//            rSeqList.add(rSeq);
//        }
//        
//        List<ActivityMap> actMaps = ActivityMap.emulateReferencedSequences(rSeqList);       
//        for (int i = 0; i < actMaps.size(); ++i) {
//            GraphManager.activityMapToDot(actMaps.get(i), "data/mnp_act_" + i + ".dot");
//        }

        TransSystem tSys = new TransSystem();
        tSys.emulateCanonical(can);
        tSys.exportTestData("data/testcases.tcxml");
//        GraphManager.transSystemToDot(tSys, "data/mnp_model.dot");
        GraphManager.transSystemToDot(tSys, "data/xes_model.dot");

        long finishTime = System.nanoTime();
        System.out.println("Time elapsed: " + (finishTime-startTime)/1000000);

//		for (TaggedList each : can.getTaggedSequences()) {
//			System.out.println(each.list.size());
//			System.out.println(each.list.get(0).getTimestamp());
//		}

	}
}
