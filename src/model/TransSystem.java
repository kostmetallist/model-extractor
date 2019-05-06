package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.GraphIterator;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException;

public class TransSystem extends 
    DefaultDirectedWeightedGraph<TransSystem.TSVertex, TransSystem.TSEdge> {

    private static final long serialVersionUID = 19756912L;
    private double maxEdgeWeight = 1;

    public static class TSVertex {

        private static int globalId = 0;
        private int id;
        private List<Event> state = new ArrayList<>();

        public TSVertex() {
            this.id = globalId++;
        }

        public TSVertex(List<Event> state) {
            
            this.id = globalId++;
            for (Event each : state) {
                this.state.add(each);
            }
        }

        public List<Event> getState() {
            return this.state;
        }

        public List<Event> getStateClone() {

            List<Event> clone = new ArrayList<>();
            for (Event each : this.state) {
                clone.add(each);
            }
            return clone;
        }

        public void setState(List<Event> state) {
            for (Event each : state) {
                this.state.add(each);
            }
        }

        @Override
        public String toString() {
            return "state " + this.id;
        }
    }

    public static class TSEdge extends DefaultWeightedEdge {

        private static final long serialVersionUID = 81275L;
        private String label;
        private double weight;

        public TSEdge(String label, double weight) {

            this.label = label;
            this.weight = weight;
        }

        public String getLabel() {
            return this.label;
        }

        @Override
        public double getWeight() {
            return this.weight;
        }

        public void setWeight(double weight) {
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "(" + this.getSource() + " : " + 
                    this.getTarget() + " : " + label + ")";
        }
    }
    
    public TransSystem() {
        super(TSEdge.class);
    }

    // initializes this TransSystem object with data containing in Canonical
    public void emulateCanonical(Canonical can) {

        TSVertex root = new TSVertex();
        this.addVertex(root);
        // posVertex describes currently inspecting vertex
        TSVertex posVertex;
        double maxEdgeW = 1;

        for (TaggedList tList : can.getTaggedSequences()) {

            posVertex = root;
            for (Event e : tList.list) {

                TSEdge matchedEdge = null;
                for (TSEdge edge : outgoingEdgesOf(posVertex)) {
                    if (edge.getLabel().equals(e.getActivity())) {
                        matchedEdge = edge;
                        break;
                    }
                }

                // we have found edge with same marking as an event activity,
                // so increasing this edge weight & continuing traversal
                if (matchedEdge != null) {

                    double adjustedWeight = matchedEdge.getWeight() + tList.tag;
                    matchedEdge.setWeight(adjustedWeight);
                    if (adjustedWeight > maxEdgeW) {
                        maxEdgeW = adjustedWeight;
                    }

                    posVertex = getEdgeTarget(matchedEdge);
                }

                // we need to create new vertex and connect it to posVertex
                // via newly created edge with a label e.activity
                else {

					List<Event> newState = posVertex.getStateClone(); 
					newState.add(e);
					TSVertex newVertex = new TSVertex(newState); 
                    this.addVertex(newVertex);
                    TSEdge newEdge = new TSEdge(e.getActivity(), tList.tag);
                    this.addEdge(posVertex, newVertex, newEdge);
                    posVertex = newVertex;
                }
            }
        }

        this.maxEdgeWeight = maxEdgeW;
    }

    public double getMaxEdgeWeight() {
        return this.maxEdgeWeight;
    }
    
    public void exportTestData(String path) {
        
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;
        
        try {
            
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            GraphIterator<TSVertex, TSEdge> iter = 
                    new DepthFirstIterator<TSVertex, TSEdge>(this);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            
            while (iter.hasNext()) {
                
                TSVertex visited = iter.next();
                // this vertex is a leaf -> generate test case
                if (this.outgoingEdgesOf(visited).isEmpty()) {
                    
                    bw.write("<Testcase>");
                    bw.newLine();
                    for (Event each : visited.getState()) {
                        
                        bw.write("  <Action>");
                        bw.newLine();
                        bw.write("    " + each.getActivity());
                        bw.newLine();
                        bw.write("  </Action>");
                        bw.newLine();
                        
                        bw.write("  <Dataset>");
                        bw.newLine();
                        for (Map.Entry<String, String> entry : 
                            each.getExtra().entrySet()) {
                            
                            bw.write("    " + entry.getKey() + 
                                    ": " + entry.getValue());
                            bw.newLine();
                        }
                            
                        bw.write("  </Dataset>");
                        bw.newLine();
                    }
                    
                    bw.write("</Testcase>");
                    bw.newLine();
                    bw.newLine();
                }
            }
        }
        
        catch (IOException e) {
            e.printStackTrace();
        }
        
        finally {
            try {
                bw.close();
                fw.close();
            }
            
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
