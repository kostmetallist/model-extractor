package extractor;

import java.util.ArrayList;
import java.util.List;

import generating.Generator;
import generating.Log4jGenerator;
import model.ActivityMap;
import model.Canonical;
import model.ReferencedSequence;
import model.TaggedList;
import model.TransSystem;
import model.Translator;
import model.mnp.LogMnp;


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

        LogMnp log = LogMnp.extractLogMnp("data/mnp_export.xml");
        Canonical can = Translator.castMnp(log, true);
        can.refineData();
        System.out.println("refined to " + 
                can.getTaggedSequences().size() + " cases");
        
        List<ReferencedSequence> rSeqList = new ArrayList<>();
        for (TaggedList pCase : can.getTaggedSequences()) {
            
            ReferencedSequence rSeq = new ReferencedSequence(pCase.list);
            rSeq.reduceLoops();
            rSeqList.add(rSeq);
        }
        
        List<ActivityMap> actMaps = ActivityMap.emulateReferencedSequences(rSeqList);       
        for (int i = 0; i < actMaps.size(); ++i) {
            GraphManager.activityMapToDot(actMaps.get(i), "data/mnp_act_" + i + ".dot");
        }

        TransSystem tSys = new TransSystem();
        tSys.emulateCanonical(can);
        tSys.exportTestData("data/testcases.tcxml");
        GraphManager.transSystemToDot(tSys, "data/mnp_model.dot");


//		for (TaggedList each : can.getTaggedSequences()) {
//			System.out.println(each.list.size());
//			System.out.println(each.list.get(0).getTimestamp());
//		}

	}
}
