package com.mattbarta.decision4j.trees.splitters;

import com.mattbarta.decision4j.trees.dao.CandidateCollection;
import com.mattbarta.decision4j.trees.dao.CandidateSplit;
import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import com.mattbarta.decision4j.trees.criteria.CriterionFactory;
import java.util.List;
import java.util.Map;


/**
 * The Splitters are responsible for finding the decisionRule in each node.
 * 
 * This can be either the best split across all features, or it can be the best
 * split among random features. 
 */
public interface ISplitter {
    //public CandidateSplit split(FVSortedBlock data, double[] sampleWeights);
    /*
    the main method of the splitter, findSplit considers multiple columns and returns 
    a priorityQueue of candidateSplits.  If existingSplits are passed, use them.
    */
    public CandidateCollection findSplits(List<FVSortedBlock> fvsbs, double[] sampleWeights, Map<Integer,Double> useExisting);
    public CandidateSplit splitAtValue(double dec, FVSortedBlock data, double[] sampleWeights);
    
    public void setCriterionFactory(CriterionFactory cf);
    public CriterionFactory getCriterionFactory();
}
