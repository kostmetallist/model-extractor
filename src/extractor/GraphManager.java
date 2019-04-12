package extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;
//import org.jgrapht.traverse.*;


public class GraphManager {

	public GraphManager() {}
	
	public void graphToDot(Graph<String, DefaultEdge> graph, String path) {
		
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
	
}
