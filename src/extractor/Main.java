package extractor;

import generating.Generator;
import generating.Log4jGenerator;
import model.*;
import model.mnp.LogMnp;


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

//        LogMnp log = LogMnp.extractLogMnp("data/mnp_export.xml");
        LogMnp log = LogMnp.extractLogMnp("data/mnp_sample.xml");
        Canonical can = Translator.castMnp(log, true);

        System.out.println("before deleting similar: " + can.getTaggedSequences().size());
        can.refineData();
        System.out.println("after  deleting similar: " + can.getTaggedSequences().size());
        for (TaggedList pCase : can.getTaggedSequences()) {
            System.out.println(pCase.list.get(0).getCaseId() + " " + pCase.list.get(0).getActivity());
        }
        
        ReferencedSequence rSeq = new ReferencedSequence(can.getTaggedSequences().get(0).list);
        rSeq.reduceLoops();
        rSeq.showContent();

//        TransSystem tSys = new TransSystem();
//        tSys.emulateCanonical(can);
//        GraphManager.transSystemToDot(tSys, "data/mnp_model.dot");

//		for (TaggedList each : can.getTaggedSequences()) {
//			System.out.println(each.list.size());
//			System.out.println(each.list.get(0).getTimestamp());
//		}

	}
}
