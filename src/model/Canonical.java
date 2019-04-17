package model;

import java.util.List;
import java.util.ArrayList;


/**
 * This class represents canonical process model extracted from some event log.
 * Term "canonical" is considered as "unified" means that all further model
 * processing will be using only this unified representation.
 * 
 * @author kostmetallist
 */
public class Canonical {
	
	private enum CompareResult {
		SAME,
		DIFFERENT,
		PREFIX
	}

	// Content describing particular process data. TaggedList.list is a 
	// sequence of events with similar caseId; corresponding TaggedList.tag
	// holds number of that sequence occurrences in the model.
	// Initially all the tags must be 1, later they may be merged with 
	// appropriate counter increment.
	private List<TaggedList> taggedSequences = new ArrayList<>();
	
	
	public List<TaggedList> getTaggedSequences() {
		return this.taggedSequences;
	}

	public void setTaggedSequences(List<TaggedList> taggedSequences) {
		this.taggedSequences = taggedSequences;
	}
	
	private CompareResult compareByActivities(List<Event> first, 
			List<Event> second) {
		
		for (int i = 0; i < Math.min(first.size(), second.size()); ++i) {
			if (!first.get(i).getActivity().
				equals(second.get(i).getActivity())) {
				
				return CompareResult.DIFFERENT; 
			}
		}
		
		if (first.size() == second.size()) {
			return CompareResult.SAME;
		}
		
		return CompareResult.PREFIX;
	}

	// merges sequences with the same Event.activity lists
	private void mergeSimilar() {
		
		int baseIdx = 0;
		while (baseIdx < taggedSequences.size()) {
			
			TaggedList baseElem = taggedSequences.get(baseIdx);
			List<Integer> idxsToRemove = new ArrayList<>();
			for (int i = baseIdx+1; i < taggedSequences.size(); ++i) {
				
				CompareResult res = compareByActivities(baseElem.list, 
						taggedSequences.get(i).list);
				
				if (res == CompareResult.SAME) {
								
					baseElem.tag++;
					idxsToRemove.add(i);
				}
			}
			
			baseIdx++;
			for (Integer each : idxsToRemove) {
				taggedSequences.remove(each.intValue());
			}
		}
	}

	// aggregation method for summarizing all internal processing
	public void refineData() {
		this.mergeSimilar();
	}
}
