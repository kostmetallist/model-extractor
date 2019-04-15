package extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
//import org.jgrapht.traverse.*;

import model.TransSystem;


public class GraphManager {
	
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
	
	public static void transSystemToDot(TransSystem ts, String path) {
		
		DOTExporter<TransSystem.TSVertex, TransSystem.TSEdge> exporter = 
			new DOTExporter<>(
				new IntegerComponentNameProvider<TransSystem.TSVertex>(), 
				new StringComponentNameProvider<TransSystem.TSVertex>(), 
				new ComponentNameProvider<TransSystem.TSEdge>() {
					@Override
					public String getName(TransSystem.TSEdge edge) {
						return edge.getLabel();
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
