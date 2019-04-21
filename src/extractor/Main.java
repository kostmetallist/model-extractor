package extractor;

import generating.Generator;
import generating.Log4jGenerator;
import model.*;
import model.mnp.LogMnp;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


public class Main {

    public static void main(String[] args) {

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
//        LogMnp log = LogMnp.extractLogMnp("data/mnp_sample.xml");
        Canonical can = Translator.castMnp(log, true);

        System.out.println("before deleting similar: " + can.getTaggedSequences().size());
        can.refineData();
        System.out.println("after  deleting similar: " + can.getTaggedSequences().size());
        
        List<ReferencedSequence> rSeqList = new ArrayList<>();
        for (TaggedList pCase : can.getTaggedSequences()) {
//            System.out.println(pCase.list.get(0).getCaseId() + " " + pCase.list.get(0).getActivity());
            ReferencedSequence rSeq = new ReferencedSequence(pCase.list);
            rSeq.reduceLoops();
            rSeqList.add(rSeq);
        }
        
        
//        ReferencedSequence rSeq = new ReferencedSequence(can.getTaggedSequences().get(0).list);
//        rSeq.reduceLoops();
        
        List<ActivityMap> actMaps = ActivityMap.emulateReferencedSequences(rSeqList);
        
        for (int i = 0; i < actMaps.size(); ++i) {
            GraphManager.activityMapToDot(actMaps.get(i), "data/mnp_act_" + i + ".dot");
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
