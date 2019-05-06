package model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class ActivityMap extends 
    DefaultDirectedGraph<ActivityMap.AMVertex, DefaultEdge> {
    
    private static final long serialVersionUID = 19756914L;
    private List<ReferencedSequence> relatedContent; 

    public static class AMVertex {        
        private String activity;
        
        public AMVertex() {}
        
        public AMVertex(String activity) {
            this.activity = activity;
        }

        public String getActivity() {
            return activity;
        }

        public void setActivity(String activity) {
            this.activity = activity;
        }
    }
    
    public ActivityMap() {
        super(DefaultEdge.class);
    }
    
    public List<ReferencedSequence> getRelatedContent() {
        return this.relatedContent;
    }
    
    public static List<ActivityMap> emulateReferencedSequences(
            List<ReferencedSequence> refSeqList) {
        
        List<ActivityMap> atlas = new ArrayList<>();
        Map<String, Set<Integer>> mapCatalog = new HashMap<>();
        for (int i = 0; i < refSeqList.size(); ++i) {
            
            ReferencedSequence rs = refSeqList.get(i);
            String firstActivity = rs.getContent().x.get(0).getActivity();
            Set<Integer> associatedIndices = mapCatalog.get(firstActivity);
            if (associatedIndices == null) {            
                associatedIndices = new HashSet<>();
            }
            
            associatedIndices.add(i);
            mapCatalog.put(firstActivity, associatedIndices);
        }
        
        for (Map.Entry<String, Set<Integer>> entry : mapCatalog.entrySet()) {

            ActivityMap actMap = new ActivityMap();
            AMVertex root = new AMVertex(entry.getKey());
            actMap.relatedContent = new ArrayList<>();
            actMap.addVertex(root);
            
            // iterating over cases having the same first activity
            for (Integer idx : entry.getValue()) {
                
                ReferencedSequence rs = refSeqList.get(idx);
                actMap.relatedContent.add(rs);

                List<Event> eventList = rs.getContent().x;
                List<HashSet<Integer>> refList = rs.getContent().y;
                List<AMVertex> visitedVertices = new ArrayList<>();
                List<HashSet<AMVertex>> pendingReferals = new ArrayList<>();
                
                for (int i = 0; i < refList.size(); ++i) {
                    pendingReferals.add(new HashSet<>());
                }
                
                AMVertex curVertex = root;
                visitedVertices.add(curVertex);
                
                // references in refList[0] may be only to the same index 0 
                if (!refList.get(0).isEmpty()) {
                    DefaultEdge e = new DefaultEdge();
                    actMap.addEdge(root, root, e);
                }
                    
                for (int i = 1; i < eventList.size(); ++i) {
                    
                    String activity = eventList.get(i).getActivity();
                    AMVertex toVertex = null;
                    for (DefaultEdge outgoingE : 
                        actMap.outgoingEdgesOf(curVertex)) {
                        
                        AMVertex outgoingV = actMap.getEdgeTarget(outgoingE);
                        if (outgoingV.getActivity().equals(activity)) {
                            toVertex = outgoingV;
                            break;
                        }
                    }
                    
                    // not found
                    if (toVertex == null) {
                        toVertex = new AMVertex(activity);
                        actMap.addVertex(toVertex);
                    }
                    
                    DefaultEdge edge = new DefaultEdge();
                    actMap.addEdge(curVertex, toVertex, edge);
                    curVertex = toVertex;
                    visitedVertices.add(curVertex);
                    
                    for (Integer refIdx : refList.get(i)) {
                        if (refIdx < visitedVertices.size()) {
                            DefaultEdge refEdge = new DefaultEdge();
                            actMap.addEdge(curVertex, 
                                    visitedVertices.get(refIdx), refEdge);
                        }
                        
                        else {
                            if (refIdx < refList.size()) {
                                pendingReferals.get(refIdx).add(curVertex);
                            }
                        }
                    }
                    
                    Set<AMVertex> pending = pendingReferals.get(i);
                    for (AMVertex referal : pending) {
                        DefaultEdge refEdge = new DefaultEdge();
                        actMap.addEdge(referal, curVertex, refEdge);
                    }
                    pending.clear();
                }
            }
            
            atlas.add(actMap);
        }
        
        return atlas;
    }
    
    public void exportTestData(String path) {
        
        File file = new File(path);
        FileWriter fw = null;
        BufferedWriter bw = null;
        
        try {
            
            fw = new FileWriter(file);
            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
            bw.newLine();
            
            for (ReferencedSequence rSeq : this.relatedContent) {
                
                bw.write("<Traversal>");
                bw.newLine();
                Pair<List<Event>, List<HashSet<Integer>>> content = 
                        rSeq.getContent();
                for (int i = 0; i < content.x.size(); ++i) {
                    
                    bw.write("  <Action>");
                    bw.write(content.x.get(i).getActivity());
                    bw.write("  </Action>");
                    bw.newLine();
                    bw.write("  <References>");
                    for (Integer refIdx : content.y.get(i)) {
                        bw.write(refIdx + " ");
                    }
                    
                    bw.write("  </References>");
                    bw.newLine();
                }
                
                bw.write("</Traversal>");
                bw.newLine();
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
