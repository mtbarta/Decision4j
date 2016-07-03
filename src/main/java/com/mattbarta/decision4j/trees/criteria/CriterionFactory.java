package com.mattbarta.decision4j.trees.criteria;

import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.sgdtk.FeatureVector;

/**
 *
 */
public class CriterionFactory {
    
    private int numClasses = -1;

    public int getNumClasses()
    {
        return numClasses;
    }

    public void setNumClasses(int numClasses)
    {
        this.numClasses = numClasses;
    }
    private final CategoricalLoss cl;
    
    public CriterionFactory(CategoricalLoss cl)
    {
        this.cl = cl;
    }
    
    public CriterionFactory(CategoricalLoss cl, int numClasses)
    {
        this.numClasses = numClasses;
        this.cl = cl;
    }
    
    public CategoricalCriterion newInstance(FVSortedBlock fvsb, double[] sampleWeights) throws InstantiationException
    {
        if (numClasses == -1)
        {
            numClasses = findNumClasses(fvsb);
        }
        return new CategoricalCriterion(this.cl, fvsb, sampleWeights, numClasses);
    }
    
    private int findNumClasses(FVSortedBlock fvsb)
    {
        Iterator<FeatureVector> fvi = fvsb.iterator();
        
        Set<Double> nc = new HashSet<>();
        while (fvi.hasNext())
        {
            FeatureVector fv = fvi.next();
            double y = fv.getY();
            nc.add(y);
        }
        return nc.size();
    }
}
