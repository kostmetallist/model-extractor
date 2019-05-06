package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Describes list of Event class objects where each of them can have a set of
 * references to another elements of the sequence.
 * 
 */
public class ReferencedSequence {

    private Pair<List<Event>, List<HashSet<Integer>>> content;
    
    // copies references to `events` into the internal structure 
    public ReferencedSequence(List<Event> events) {
        
        List<Event> eventSequence = new ArrayList<>();
        List<HashSet<Integer>> refSequence = new ArrayList<>();
        for (Event e : events) {
            eventSequence.add(e);
            refSequence.add(new HashSet<>());
        }
        
        Pair<List<Event>, List<HashSet<Integer>>> pair = new Pair<>();
        pair.x = eventSequence;
        pair.y = refSequence;
        this.content = pair; 
    }
    
    public Pair<List<Event>, List<HashSet<Integer>>> getContent() {
        return this.content;
    }

    private boolean checkPatternIntegration(List<Event> pattern, int indexAt) {

        List<Event> list = this.content.x;
        if (indexAt + pattern.size() > list.size()) { return false; }
        for (int i = 0; i < pattern.size(); ++i) {
            if (!list.get(indexAt + i).getActivity().
                equals(pattern.get(i).getActivity())) {

                return false;
            }
        }

        return true;
    }
    
    private int countPatternMatches(List<Event> pattern, int from) {

        List<Event> list = this.content.x;
        int repeats = 0, i = from;
        while (i < list.size()) {
            if (checkPatternIntegration(pattern, i)) { 
                repeats++; 
                i += pattern.size();
            }

            else { break; }
        }

        return repeats;
    }
    
    // collects references from patterns next to
    // the one starting on pivotIdx and mapping them on start pattern indices
    private void adjustReferences(int patternSize, int pivotIdx, int repeats) {

        List<HashSet<Integer>> refList = this.content.y;
        for (int n = 1; n <= repeats; ++n) {

            // inspecting n-th similar pattern, e.g.
            //
            //           _pivotIdx=4
            // a b c g | d e f | d e f | d e f |
            //                    n=1     n=2
            
            // i -- pattern-relative offset
            for (int i = 0; i < patternSize; ++i) {

                HashSet<Integer> alignedRefs = new HashSet<>();
                for (Integer ref : refList.get(pivotIdx+patternSize*n+i)) {
                    if (ref >= pivotIdx) {
                        alignedRefs.add(pivotIdx + (ref-pivotIdx)%patternSize);
                    }

                    else {
                        alignedRefs.add(ref);
                    }
                }

                refList.get(pivotIdx+i).addAll(alignedRefs);
            }
        }
    }
    
    public void reduceLoops() {

        List<Event> eventList = this.content.x;
        List<HashSet<Integer>> refList = this.content.y;

        int pivotIdx = 0;
        while (pivotIdx < eventList.size()) {

            int i = pivotIdx+1;
            while (i < eventList.size()) {

                // found possible start of similar to 
                // events.slice(pivotIdx, i-1) pattern
                if (eventList.get(pivotIdx).getActivity().
                    equals(eventList.get(i).getActivity())) {

                    final List<Event> pattern = eventList.subList(pivotIdx, i);
                    int repeats = countPatternMatches(pattern, i);
                    
                    if (repeats > 0) {

                        // adding just detected back reference
                        refList.get(i-1).add(pivotIdx);
                        // collecting & refreshing refs from patterns 
                        // that are going to reduce
                        adjustReferences(pattern.size(), pivotIdx, repeats);

                        int until = i+pattern.size()*repeats;
                        eventList.subList(i, until).clear();
                        refList.subList(i, until).clear();

                        // after the inner `while` break, next operation will
                        // be pivotIdx++; so we will start from beginning
                        pivotIdx = -1;
                        break;
                    }
                }

                i++;
            }

            pivotIdx++;
        }
    }
    
    private boolean isEqual(ReferencedSequence another) {
        
        Pair<List<Event>, List<HashSet<Integer>>> content1 = this.content;
        Pair<List<Event>, List<HashSet<Integer>>> content2 = another.content;
        if (content1.x.size() != content2.x.size()) {
            return false;
        }
        
        for (int i = 0; i < content1.x.size(); ++i) {
            
            if (!content1.x.get(i).getActivity().
                equals(content2.x.get(i).getActivity()) || 
                !content1.y.get(i).
                equals(content2.y.get(i))) {
                
                return false;
            }
        }
        
        return true;
    }
    
    public static List<ReferencedSequence> generalizeModel(Canonical can) {
        
        List<ReferencedSequence> rSeqList = new ArrayList<>();
        
        for (TaggedList tList : can.getTaggedSequences()) {
      
            ReferencedSequence rSeq = new ReferencedSequence(tList.list);
            rSeq.reduceLoops();
            
            // removing similar sequences
            boolean isUnique = true;
            for (ReferencedSequence each : rSeqList) {
                if (rSeq.isEqual(each)) {
                    isUnique = false;
                    break;
                }
            }
            
            if (isUnique) { rSeqList.add(rSeq); }
        }
        
        return rSeqList;
    }
}
