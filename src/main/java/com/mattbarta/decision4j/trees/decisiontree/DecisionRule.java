package com.mattbarta.decision4j.trees.decisiontree;

/**
 *
 */
public class DecisionRule {
    private int featureIndex;
    private double decision;
    
    public DecisionRule(int featureIndex, double decision)
    {
        this.featureIndex = featureIndex;
        this.decision = decision;
    }
    
    public int decide(double input)
    {
        int sort = Double.compare(input, decision);
        return sort;
    }

    public int getFeatureIndex()
    {
        return featureIndex;
    }

    public void setFeatureIndex(int featureIndex)
    {
        this.featureIndex = featureIndex;
    }

    public double getDecision()
    {
        return decision;
    }

    public void setDecision(double decision)
    {
        this.decision = decision;
    }
}
