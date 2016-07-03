package com.mattbarta.decision4j.trees.dao;

import com.mattbarta.decision4j.trees.decisiontree.DecisionRule;
import java.util.Arrays;
import java.util.Collections;
import java.util.PriorityQueue;

/**
 * Contains the information necessary to go from split decisions to 
 * complete decisionNodes.
 * 
 */
public class CandidateCollection extends PriorityQueue<CandidateSplit>
{

    public static final DecisionRule notSplitDecision = new DecisionRule(
            Integer.MIN_VALUE, Double.MIN_VALUE);

    private double[] totalCounts = null;

    public CandidateCollection()
    {
        super(Collections.reverseOrder());
    }

    public boolean addIfValid(CandidateSplit cs)
    {
        totalCounts = cs.totalCounts;
        if (cs.splitPos == Integer.MIN_VALUE)
        {
            return false;
        }
        return super.add(cs);
    }

    public double[] getTotalProb()
    {
        return getTotalProb(totalCounts);
    }

    private static double[] getTotalProb(double[] counts)
    {
        double total = Arrays.stream(counts).sum();
        double[] totalProb = new double[counts.length];
        for (int i = 0; i < counts.length; i++)
        {
            totalProb[i] = counts[i] / total;
        }
        return totalProb;
    }
}
