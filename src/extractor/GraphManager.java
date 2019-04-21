package extractor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.io.Attribute;
import org.jgrapht.io.ComponentAttributeProvider;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.DOTExporter;
import org.jgrapht.io.DefaultAttribute;
import org.jgrapht.io.IntegerComponentNameProvider;
import org.jgrapht.io.StringComponentNameProvider;

import model.TransSystem;
import model.ActivityMap;


public class GraphManager {
	
    private static final double maxPenWidth = 7.0;
    private static final Attribute nodeStyle = 
            DefaultAttribute.createAttribute("rounded, filled");
    private static final Attribute nodeColor1 = 
            DefaultAttribute.createAttribute("#6699ff");
    private static final Attribute nodeColor2 = 
            DefaultAttribute.createAttribute("#66ff66");
    private static final Attribute nodeShape = 
            DefaultAttribute.createAttribute("box");

    
    public static void graphToDot(Graph<String, DefaultEdge> graph, 
            String path) {

        DOTExporter<String, DefaultEdge> exporter = 
            new DOTExporter<>(
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
                        return "  " + edge.getLabel() + " " + 
                                (int) edge.getWeight() + "  ";
                    }
                }, 
                new ComponentAttributeProvider<TransSystem.TSVertex>() {
                    @Override
                    public Map<String, Attribute> getComponentAttributes(
                            TransSystem.TSVertex vertex) {

                        Map<String, Attribute> attributes = new HashMap<>();
                        attributes.put("style", nodeStyle);
                        attributes.put("fillcolor", nodeColor1);
                        attributes.put("shape", nodeShape);
                        return attributes;
                    }   
                }, new ComponentAttributeProvider<TransSystem.TSEdge>() {
                    @Override
                    public Map<String, Attribute> getComponentAttributes(
                            TransSystem.TSEdge edge) {

                        Map<String, Attribute> attributes = new HashMap<>();
                        double edgeWidth = 1 + (maxPenWidth - 1) * 
                                edge.getWeight() / ts.getMaxEdgeWeight();
                        attributes.put("penwidth", DefaultAttribute.
                                createAttribute(String.format("%.2f", 
                                        edgeWidth)));
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
	
	public static void activityMapToDot(ActivityMap am, String path) {
	    
        DOTExporter<ActivityMap.AMVertex, DefaultEdge> exporter = 
                new DOTExporter<>(
                    new IntegerComponentNameProvider<ActivityMap.AMVertex>(),
                    new ComponentNameProvider<ActivityMap.AMVertex>() {
                        @Override
                        public String getName(ActivityMap.AMVertex vertex) {
                            return " " + vertex.getActivity() + " ";
                        }
                    },
                    new ComponentNameProvider<DefaultEdge>() {
                        @Override
                        public String getName(DefaultEdge edge) {
                            return "";
                        }
                    },
                    new ComponentAttributeProvider<ActivityMap.AMVertex>() {
                        @Override
                        public Map<String, Attribute> getComponentAttributes(
                                ActivityMap.AMVertex vertex) {

                            Map<String, Attribute> attributes = new HashMap<>();
                            attributes.put("style", nodeStyle);
                            attributes.put("fillcolor", nodeColor2);
                            attributes.put("shape", nodeShape);
                            return attributes;
                        }   
                    }, 
                    new ComponentAttributeProvider<DefaultEdge>() {
                        @Override
                        public Map<String, Attribute> getComponentAttributes(
                                DefaultEdge edge) {

                            Map<String, Attribute> attributes = new HashMap<>();
                            return attributes;
                        }
                    });

        File file = new File(path);
        FileWriter fw = null;
        try {

            fw = new FileWriter(file);
            exporter.exportGraph(am, fw);
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
