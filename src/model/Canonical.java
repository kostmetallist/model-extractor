package model;

import java.util.ArrayList;
import java.util.List;


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
        PREFIX, 
        POSTFIX
    }

    // Content describing particular process data. TaggedList.list is a
    // sequence of events with similar caseId; corresponding TaggedList.tag
    // holds number of that sequence occurrences in the model.
    // Initially all the tags must be 1, later they may be merged with
    // appropriate counter increment.
    private List<TaggedList> taggedSequences = new ArrayList<>();
    private double prefixFrequencyThreshold = 0.1;
    private double postfixFrequencyThreshold = 0.1;
	
	
    public List<TaggedList> getTaggedSequences() {
        return this.taggedSequences;
    }

    public void setTaggedSequences(List<TaggedList> taggedSequences) {
        this.taggedSequences = taggedSequences;
    }
	
    private CompareResult compareByActivities(List<Event> first, 
            List<Event> second) {

//        for (int i = 0; i < Math.min(first.size(), second.size()); ++i) {
//            if (!first.get(i).getActivity().
//                equals(second.get(i).getActivity())) {
//
//                return CompareResult.DIFFERENT;
//            }
//        }
//
//        if (first.size() == second.size()) {
//            return CompareResult.SAME;
//        }
//
//        return CompareResult.PREFIX;
        
        if (first.size() == second.size()) {
        
            for (int i = 0; i < first.size(); ++i) {
                if (!first.get(i).getActivity().
                    equals(second.get(i).getActivity())) {
    
                    return CompareResult.DIFFERENT;
                }
            }
            
            return CompareResult.SAME;
        }
        
        else {
            
            List<Event> master = (first.size() > second.size())? first: second;
            List<Event> slave = (first == master)? second: first;
            boolean prefixChecking = true, postfixChecking = true;
            int sSize = slave.size();
            
            for (int i = 0; i < sSize; ++i) {
                if (prefixChecking) {
                    if (!master.get(i).getActivity().
                        equals(slave.get(i).getActivity())) {
                        
                        prefixChecking = false;
                    }
                }
                
                if (postfixChecking) {
                    if (!master.get(master.size()-sSize+i).getActivity().
                        equals(slave.get(i).getActivity())) {
                        
                        postfixChecking = false;
                    }
                }
            }
            
            if (prefixChecking) { return CompareResult.PREFIX; }
            if (postfixChecking) { return CompareResult.POSTFIX; }
            return CompareResult.DIFFERENT;
        }
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

            List<TaggedList> toRemove = new ArrayList<>();
            for (Integer each : idxsToRemove) {
                toRemove.add(taggedSequences.get(each));
            }

            taggedSequences.removeAll(toRemove);
            baseIdx++;
        }
    }
    
    // filtering prefix & postfix cases which are less frequent than threshold
    private void filterOutAnyfixes() {
        
        int foundPrefs = 0, foundPosts = 0, 
            filteredPrefs = 0, filteredPosts = 0;
        List<TaggedList> toRemove = new ArrayList<>();
            
        for (int i = 0; i < taggedSequences.size(); ++i) {
            for (int j = i+1; j < taggedSequences.size(); ++j) {
                
                TaggedList first  = taggedSequences.get(i);
                TaggedList second = taggedSequences.get(j);
                CompareResult res = compareByActivities(
                        first.list, second.list);
                
                // optimizing memory usage by passing non-anyfix cases
                if (res == CompareResult.DIFFERENT || res == CompareResult.SAME)
                    continue;
                
                TaggedList candidate = (first.list.size() > second.list.size())?
                        second: first;
                double patternOccurrencies = candidate.tag;
                // LRF = local relative frequency
                double lrf = patternOccurrencies / (first.tag+second.tag);
                
                if (res == CompareResult.PREFIX) {
                    
                    foundPrefs++;
                    if (lrf > prefixFrequencyThreshold) {
                        
                        filteredPrefs++;
                        toRemove.add(candidate);
                    }
                }
                
                if (res == CompareResult.POSTFIX) {
                    
                    foundPosts++;
                    if (lrf > postfixFrequencyThreshold) {
                        
                        filteredPosts++;
                        toRemove.add(candidate);
                    }
                }
            }
        }
        
        taggedSequences.removeAll(toRemove);
        System.out.println("Successfully filtered out:");
        System.out.println("    prefixes: " + filteredPrefs + "/" + foundPrefs);
        System.out.println("   postfixes: " + filteredPosts + "/" + foundPosts);
    }

    // aggregation method for summarizing all internal processing
    public void refineData() {
        
        this.mergeSimilar();
        this.filterOutAnyfixes();
    }
}
