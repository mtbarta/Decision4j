package com.mattbarta.decision4j.trees.dao;

import com.mattbarta.decision4j.trees.decisiontree.DecisionRule;

/**
 *
 */
public class CandidateSplit implements Comparable<CandidateSplit>
{
        public Double loss;
        public DecisionRule rule;
        public double[] leftScores;
        public double[] rightScores;
        public double[] totalCounts;
        public int splitPos;
        
        public FVSortedBlock sortedBlock;
        
        public CandidateSplit(Double loss, DecisionRule rule, int splitPos, double[] leftProb, double[] rightProb, double[] totalCounts, FVSortedBlock fvsb){
            this.loss = loss;
            this.rule = rule;
            this.splitPos = splitPos;
            
            this.leftScores = leftProb;
            this.rightScores = rightProb;  
            this.totalCounts = totalCounts; // counts! not probs.
            this.sortedBlock = fvsb;
        }

    @Override
    public int compareTo(CandidateSplit o)
    {
        return Double.compare(this.loss, o.loss);
    }
}