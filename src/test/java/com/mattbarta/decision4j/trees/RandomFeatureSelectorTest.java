package com.mattbarta.decision4j.trees;

import com.mattbarta.decisoin4j.trees.RandomFeatureSelector;
import java.util.Arrays;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.sgdtk.DenseVectorN;
import org.sgdtk.FeatureVector;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
/**
 *
 */
public class RandomFeatureSelectorTest {

    private List<FeatureVector> ml;
    
    @Before
    public void setup()
    {
        FeatureVector fvA;
        FeatureVector fvB;
        
        double[] one = {0.0, 1.0};
        double[] two = {0.0, 0.0};
        fvA = new FeatureVector(1.0, new DenseVectorN(one));
        fvB = new FeatureVector(1.0,new DenseVectorN(two));
        ml = Arrays.asList(fvA, fvB);
        
    }
    @Test
    public void isConstantTest(){
        
        boolean test = RandomFeatureSelector.isConstant(ml, 0);
        assertTrue("isConstant() failed to recognize constant sequence", test);
        
        test = RandomFeatureSelector.isConstant(ml, 1);
        assertFalse("isConstant() thought non-constant sequence was constant.", test);
    }
    
    @Test
    public void generateTest()
    {        
        RandomFeatureSelector rfs = new RandomFeatureSelector(ml,2);
        assertTrue("returns non-constant column vector",rfs.getIndices().contains(1));
    }
}
