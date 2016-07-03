package com.mattbarta.decision4j.trees.splitters;

import com.mattbarta.decision4j.trees.dao.CandidateCollection;
import com.mattbarta.decision4j.trees.dao.CandidateSplit;
import com.mattbarta.decision4j.trees.criteria.CategoricalCriterion;
import com.mattbarta.decision4j.trees.criteria.CriterionFactory;
import com.mattbarta.decision4j.trees.decisiontree.DecisionRule;
import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * BestSplitter finds the best split among the features.
 *
 * Here's the general idea. The tree selects a random number of features to
 consider. Then, we need to sort this feature and find the best split
 location.

 Ideas presented in the sklearn impl show that this can look for constant
 features before doing any work, which would save some time. What's
 interesting is that sklearn sorts all of the data, and at each split, they
 resort that part of the data.

 1. sort by feature value 2. consider splits while respecting tree
 constraints. e.g. minSamplesLeaf. 3. consider each split of the data for the
 best split. 4. CategoricalCriterion will evaluate the split. 5. Save the best split.
 */
public class BestSplitter implements ISplitter
{

    private final int minSamplesLeaf;

    private CriterionFactory cf;

    public CriterionFactory getCriterionFactory()
    {
        return cf;
    }

    @Override
    public void setCriterionFactory(CriterionFactory cf)
    {
        this.cf = cf;
    }

    public BestSplitter(int minSamplesLeaf)
    {
        this.minSamplesLeaf = minSamplesLeaf;
    }

    public BestSplitter(int minSamplesLeaf, CriterionFactory cf)
    {
        this.minSamplesLeaf = minSamplesLeaf;
        this.cf = cf;
    }

    @Override
    public CandidateCollection findSplits(List<FVSortedBlock> fvsbs,
            double[] sampleWeights, Map<Integer, Double> useExistingSplits)
    {
        CandidateCollection results = new CandidateCollection();

        if (useExistingSplits == null)
        {
            for (FVSortedBlock fvsb : fvsbs)
            {
                CandidateSplit decision = this.split(fvsb, sampleWeights);
                results.addIfValid(decision);
            }
        }
        else
        {
            // if existingSplits exists, then let's use them.
            for (FVSortedBlock fvsb : fvsbs)
            {
                int idx = fvsb.getCol();
                double splitVal = useExistingSplits.get(idx);

                CandidateSplit decision = splitAtValue(splitVal, fvsb,
                        sampleWeights);
                results.addIfValid(decision);
            }
        }
        
        return results;
    }

    
    private CandidateSplit split(FVSortedBlock data, double[] sampleWeights)
    {
        try
        {
            double bestLoss = Double.NEGATIVE_INFINITY;
            int idx = Integer.MIN_VALUE;
            double dec = Double.NEGATIVE_INFINITY;
            double[] leftCount = null;
            double[] rightCount = null;

            CategoricalCriterion criterion = cf.newInstance(data, sampleWeights);
            criterion.init();
            int i;
            double oldFeatureValue = 0.0;
            
            boolean perfectNode = Arrays.stream(criterion.getpCGivenR())
                    .anyMatch(j -> j == 1.0);
            if (perfectNode)
            {
                return toCandidateSplit(data, dec, criterion, bestLoss, idx, leftCount, rightCount);
            }
            for (i = minSamplesLeaf; i < data.size() - minSamplesLeaf; i++)
            {
                double featureValue = data.getFeatureOf(i);
                if (featureValue == oldFeatureValue)
                {
                    continue;
                }

                criterion.update(i);
                double loss = criterion.evaluate();

                if (loss > bestLoss)
                {
                    bestLoss = loss;
                    idx = i;
                    dec = data.getFeatureOf(i);

                    leftCount = criterion.getpCGivenL().clone();
                    rightCount = criterion.getpCGivenR().clone();
                }
                oldFeatureValue = featureValue;
            }

            CandidateSplit sr = toCandidateSplit(data, dec, criterion, bestLoss,
                    idx, leftCount, rightCount);
            return sr;
        }
        catch (InstantiationException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public CandidateSplit splitAtValue(double dec, FVSortedBlock data,
            double[] sampleWeights)
    {
        try
        {
            double[] leftCount = null;
            double[] rightCount = null;

            CategoricalCriterion criterion = cf.newInstance(data, sampleWeights);
            criterion.init();

            int idx = findIndexOfDecision(dec, data);
            criterion.update(idx);
            double loss = criterion.evaluate();

            leftCount = criterion.getpCGivenL();
            rightCount = criterion.getpCGivenR();

            CandidateSplit sr = toCandidateSplit(data, dec, criterion, loss,
                    idx, leftCount, rightCount);
            return sr;
        }
        catch (InstantiationException ex)
        {
            throw new RuntimeException("criterion failed to instantiate", ex);
        }
    }

    private CandidateSplit toCandidateSplit(FVSortedBlock data, double dec,
            CategoricalCriterion criterion, double loss,
            int idx, double[] leftCount, double[] rightCount)
    {
        DecisionRule dr = new DecisionRule(data.getCol(), dec);
        CandidateSplit sr = new CandidateSplit(loss, dr, idx, leftCount,
                rightCount, criterion.getTotalCount(), data);
        return sr;
    }

    /*
     This function serves to translate the decision function into a new data space.
     We find the index where the decision holds so that we can plug this into the criterion.
    
     if the decision boundary cannot be respected due to the nature of the data,
     or minSamplesLeaf constrains us, we return -1. This will most likely cause
     errors upstream if not respected.
     */
    private int findIndexOfDecision(double dec, FVSortedBlock data)
    {
        int i;
        for (i = minSamplesLeaf + 1; i < data.size() - minSamplesLeaf; i++)
        {
            if (dec > data.getFeatureOf(i - 1) && dec < data.getFeatureOf(i))
            {
                return i;
            }
            else if (dec < data.getFeatureOf(i - 1) && dec > data.
                    getFeatureOf(i))
            {
                return i;
            }
        }
        return -1;
    }

    
}
