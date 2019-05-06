package model;

import java.util.ArrayList;
import java.util.List;
import java.time.temporal.ChronoUnit;


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
    private double prefixFrequencyThreshold;
    private double postfixFrequencyThreshold;
    private double missedFrequencyThreshold;
    private long deltaT;
	
    public List<TaggedList> getTaggedSequences() {
        return this.taggedSequences;
    }

    public void setTaggedSequences(List<TaggedList> taggedSequences) {
        this.taggedSequences = taggedSequences;
    }
    
    public void setPrefixFrequencyThreshold(double prefixFrequencyThreshold) {
        this.prefixFrequencyThreshold = prefixFrequencyThreshold;
    }

    public void setPostfixFrequencyThreshold(double postfixFrequencyThreshold) {
        this.postfixFrequencyThreshold = postfixFrequencyThreshold;
    }

    public void setMissedFrequencyThreshold(double missedFrequencyThreshold) {
        this.missedFrequencyThreshold = missedFrequencyThreshold;
    }

    public void setDeltaT(long deltaT) {
        this.deltaT = deltaT;
    }

    private CompareResult compareByActivities(List<Event> first, 
            List<Event> second) {
        
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
                    if (lrf < prefixFrequencyThreshold) {
                        
                        filteredPrefs++;
                        toRemove.add(candidate);
                    }
                }
                
                if (res == CompareResult.POSTFIX) {
                    
                    foundPosts++;
                    if (lrf < postfixFrequencyThreshold) {
                        
                        filteredPosts++;
                        toRemove.add(candidate);
                    }
                }
            }
        }
        
        taggedSequences.removeAll(toRemove);
        System.out.println("successfully filtered out:");
        System.out.println("--prefixes: " + filteredPrefs + "/" + foundPrefs);
        System.out.println("--postfixes: " + filteredPosts + "/" + foundPosts);
    }
    
    // returns null if considered as not containing missed events
    private TaggedList locateMissedEvent(TaggedList tList1, TaggedList tList2) {
        
        int n1 = tList1.list.size();
        int n2 = tList2.list.size();
        
        // checking sequences that are different by one event
        if (Math.abs(n1 - n2) != 1) {
            return null;
        }
        
        TaggedList candidate = (n1 > n2)? tList2: tList1;
        TaggedList masterList = (candidate == tList1)? tList2: tList1;
        double lrf = candidate.tag / (masterList.tag + candidate.tag);
        if (lrf > this.missedFrequencyThreshold) {
            return null;
        }
        
        List<Event> reduced = candidate.list;
        List<Event> sterling = masterList.list;
        boolean untilMode = true;
        
        for (int i = 0; i < candidate.list.size(); ++i) {
            
            // until-missed mode
            if (untilMode) {
                if (!reduced.get(i).getActivity().
                    equals(sterling.get(i).getActivity())) {
                    
                    // skipping such sequence pair because postfix cases
                    // were filtered on the previous refinement stage 
                    if (i == 0) {
                        return null;
                    }
                    
                    List<Event> reducedFragment = reduced.subList(i-1, i+1);
                    List<Event> sterlingFragment = sterling.subList(i-1, i+2);
                    
                    // further event equality checking
                    if (!reducedFragment.get(1).getActivity().
                        equals(sterlingFragment.get(2).getActivity())) {
                        
                        return null;
                    }
                    
                    // timestamp checking 
                    if (Math.abs(reducedFragment.get(0).getTimestamp().
                            until(reducedFragment.get(1).getTimestamp(), 
                                    ChronoUnit.MILLIS) - 
                            sterlingFragment.get(0).getTimestamp().
                            until(sterlingFragment.get(2).getTimestamp(), 
                                    ChronoUnit.MILLIS)) > this.deltaT) {
                        
                        return null;
                    }
                    
                    untilMode = false;
                }
            }
            
            // after-missed mode
            else {
                if (!reduced.get(i).getActivity().
                    equals(sterling.get(i+1).getActivity())) {
                        
                    return null;
                }
            }
        }
        
        return candidate;
    }
    
    public void filterOutMissedEvents() {
        
        int filteredCases = 0;
        List<TaggedList> toRemove = new ArrayList<>();
        
        for (int i = 0; i < this.taggedSequences.size(); ++i) {
            for (int j = i+1; j < this.taggedSequences.size(); ++j) {
                
                TaggedList tList1 = this.taggedSequences.get(i);
                TaggedList tList2 = this.taggedSequences.get(j);
                TaggedList candidate = locateMissedEvent(tList1, tList2);
                
                if (candidate != null) {
                    filteredCases++;
                    toRemove.add(candidate);
                }
            }
        }
        
        this.taggedSequences.removeAll(toRemove);
        System.out.println("successfully filtered out:");
        System.out.println("--cases w/missed event: " + filteredCases);
    }

    // aggregation method for summarizing all internal processing
    public void refineData() {
        
        System.out.println("refining canonical model...");
        System.out.println("initial cases number: " + 
                this.taggedSequences.size());
        this.mergeSimilar();
        System.out.println("merged to " + 
                this.taggedSequences.size() + " case classes");
        this.filterOutAnyfixes();
        this.filterOutMissedEvents();
    }
}
