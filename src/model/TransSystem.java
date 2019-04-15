package model;

import org.jgrapht.*;
import org.jgrapht.graph.*;
import java.util.List;
import java.util.ArrayList;


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
	
	public TransSystem() {
		super(TSEdge.class);
	}
}
