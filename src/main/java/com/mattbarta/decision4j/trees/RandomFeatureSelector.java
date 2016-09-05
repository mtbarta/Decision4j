package com.mattbarta.decision4j.trees;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import org.sgdtk.FeatureVector;

/**
 *
 */
public class RandomFeatureSelector {
    Set<Integer> indices;

    public Set<Integer> getIndices()
    {
        return indices;
    }

    public int getMaxFeatures()
    {
        return maxFeatures;
    }
    int maxFeatures;
    
    public RandomFeatureSelector(List<FeatureVector> list, int maxFeatures)
    {
        this.maxFeatures = maxFeatures;
        indices = generate(list, maxFeatures);
        
    }
    
    private Set<Integer> generate(List<FeatureVector> list, int maxFeatures) throws RuntimeException
    {
        int numFeatures = list.get(0).length();
        int nFeats = Math.min(maxFeatures, numFeatures);
        Set<Integer> res = new HashSet<>(nFeats);
        
        
        Set<Integer> history = new HashSet<>();
        while (res.size() < nFeats)
        {
            if (history.size() == numFeatures)
            {
                break;
            }
            
            int index = ThreadLocalRandom.current().nextInt(0,numFeatures);
            if (!history.contains(index) && !isConstant(list,index))
            {
                res.add(index);
            }
            history.add(index);
        }
        
        if (res.isEmpty())
        {
            throw new RuntimeException("all features are constant!");
        }
        
        return res;
    }
    
    public static boolean isConstant(List<FeatureVector> list, int index)
    {       
        List<Double> distinct = list.stream()
                .map(fv -> fv.getX().at(index))
                .distinct()
                .collect(Collectors.toList());
        return distinct.size() == 1;
        
    }
}
