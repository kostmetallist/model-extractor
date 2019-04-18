package extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
import java.util.Map;
import java.util.HashMap;

import model.TransSystem;


public class GraphManager {
	
	private static final double maxPenWidth = 7.0;
	private static final Attribute nodeStyle = 
			DefaultAttribute.createAttribute("filled");
	private static final Attribute nodeColor = 
			DefaultAttribute.createAttribute("#6699ff");
	
	
	public static void graphToDot(Graph<String, DefaultEdge> graph, 
			String path) {
		
		DOTExporter<String, DefaultEdge> exporter = new DOTExporter<>(
				new IntegerComponentNameProvider<String>(),
				new StringComponentNameProvider<String>(),
				new StringComponentNameProvider<DefaultEdge>());
	   
		try (FileWriter fw = new FileWriter(new File(path))) {
		   
			exporter.exportGraph(graph, fw);
			fw.flush();
		}
	   
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// TODO ComponentAttributeProvider for changing 'label' to 'headlabel' 
	// and setting 'penwidth' according to edge's weight
	public static void transSystemToDot(TransSystem ts, String path) {
		
		
		DOTExporter<TransSystem.TSVertex, TransSystem.TSEdge> exporter = 
				new DOTExporter<>(
					new IntegerComponentNameProvider<TransSystem.TSVertex>(), 
					new StringComponentNameProvider<TransSystem.TSVertex>(), 
					new ComponentNameProvider<TransSystem.TSEdge>() {
						@Override
						public String getName(TransSystem.TSEdge edge) {
							return "  " + edge.getLabel() + "  ";
						}
					}, 
					new ComponentAttributeProvider<TransSystem.TSVertex>() {
						@Override
						public Map<String, Attribute> getComponentAttributes(
								TransSystem.TSVertex vertex) {

							Map<String, Attribute> attributes = new HashMap<>();
							attributes.put("style", nodeStyle);
							attributes.put("fillcolor", nodeColor);
							return attributes;
						}
					}, 
					new ComponentAttributeProvider<TransSystem.TSEdge>() {
						@Override
						public Map<String, Attribute> getComponentAttributes(
								TransSystem.TSEdge edge) {
							
							Map<String, Attribute> attributes = new HashMap<>();
							double edgeWidth = 0.5 + (maxPenWidth-0.5) * 
								edge.getWeight()/ts.getMaxEdgeWeight();
							attributes.put("penwidth",
								DefaultAttribute.createAttribute(
									String.format("%.2f", edgeWidth)));
							return attributes;
						}
					});
		
		File file = new File(path);
		FileWriter fw = null;
		try {
			
			fw = new FileWriter(file);
			exporter.exportGraph(ts, fw);
			fw.flush();
		}
		
		catch (IOException e) {
			e.printStackTrace();
		}
		
		finally {
			try {
				fw.close();
			}
			
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
