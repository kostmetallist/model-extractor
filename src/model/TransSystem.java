package model;

//import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;

public class TransSystem extends 
	DefaultDirectedWeightedGraph<TransSystem.TSVertex, TransSystem.TSEdge> {
	
	private static final long serialVersionUID = 19756912L;
	
	public static class TSVertex {
		
		private List<String> state = new ArrayList<>();
		
		
		public TSVertex() {}
			
		public TSVertex(List<String> state) {
			for (String each : state) {
				this.state.add(each);
			}
		}
		
		public List<String> getState() {
			return this.state;
		}
		
		public List<String> getStateClone() {
			
			List<String> clone = new ArrayList<>();
			for (String each : this.state) {
				clone.add(new String(each));
			}
			return clone;
		}
		
		public void setState(List<String> state) {
			for (String each : state) {
				this.state.add(each);
			}
		}
		
		@Override
		public String toString() {
			
			String result = "{";
			for (String each : this.state) {
				result += each + ", ";
			}
			
			// if we have added at least one element
			if (result.length() > 1) {
				result = result.substring(0, result.length()-2);
			}
			
			return result + "}";
		}
	}
	
	public static class TSEdge extends DefaultWeightedEdge {
		
		private static final long serialVersionUID = 81275L;
		private String label;
		
		public TSEdge(String label) {
			this.label = label;
		}
		
		public String getLabel() {
			return this.label;
		}

		@Override
		public String toString() {
			return "(" + this.getSource() + " : " + this.getTarget() + 
	        		" : " + label + ")";
		}
	}
	
	// initializes this TransSystem object with data containing in Canonical
	public void emulateCanonical(Canonical can) {
		
		TSVertex root = new TSVertex();
		this.addVertex(root);
		// posVertex describes currently inspecting vertex
		TSVertex posVertex;
		
		for (TaggedList tList : can.getTaggedSequences()) {
			
			posVertex = root;
			for (Event e : tList.list) {
				
				HashSet<TSEdge> outgoingEdges = 
						new HashSet<>(this.edgesOf(posVertex)); 
				// edgesOf method indeed returned a set containing all 
				// incident to posVertex edges. For receiving only outgoing
				// edges, we need to apply further filtering
				Set<TSEdge> ingoingEdges = new HashSet<>();
				for (TSEdge edge : outgoingEdges) {
					if (getEdgeTarget(edge) == posVertex) {
						ingoingEdges.add(edge);
					}
				}
				// now we refined true outgoing edges set
				outgoingEdges.removeAll(ingoingEdges);
				
				TSEdge matchedEdge = null;
				for (TSEdge edge : outgoingEdges) {
					if (edge.getLabel().equals(e.getActivity())) {
						matchedEdge = edge;
						break;
					}
				}
				
				// we have found edge with same marking as an event activity, 
				// so increasing this edge weight & continuing traversal 
				if (matchedEdge != null) {
					
					this.setEdgeWeight(matchedEdge, 
						getEdgeWeight(matchedEdge)+1);
					posVertex = getEdgeTarget(matchedEdge);
				}
				
				// we need to create new vertex and connect it to posVertex
				// via newly created edge with a label e.activity
				else {
					
					List<String> newState = posVertex.getStateClone(); 
					newState.add(e.getActivity());
					TSVertex newVertex = new TSVertex(newState); 
					this.addVertex(newVertex);
					TSEdge newEdge = new TSEdge(e.getActivity());
					this.addEdge(posVertex, newVertex, newEdge);
					this.setEdgeWeight(newEdge, 1);
					posVertex = newVertex;
				}
			}
		}
	}
	
	public TransSystem() {
		super(TSEdge.class);
	}
}
