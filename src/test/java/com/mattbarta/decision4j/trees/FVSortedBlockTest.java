package com.mattbarta.decision4j.trees;

import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.sgdtk.DenseVectorN;
import org.sgdtk.FeatureVector;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class FVSortedBlockTest {

    @Test
    public void SortTest()
    {
        double[] first = {0.0};
        double[] second = {.4564};
        double[] third = {1.2345};
        double[] fourth = {1.2567};
        List<FeatureVector> lfv = new ArrayList()
        {
            {
                add(new FeatureVector(0.0, new DenseVectorN(first)));
                add(new FeatureVector(0.0, new DenseVectorN(second)));
                add(new FeatureVector(1.0, new DenseVectorN(fourth)));
                add(new FeatureVector(1.0, new DenseVectorN(third)));
            }
        };
        
        FVSortedBlock fvsb = new FVSortedBlock(lfv, 0);
        fvsb.sort();
        
//        double[] res = IntStream.range(0, 4).mapToDouble(i -> fvsb.getFeatureOf(
//                i)).toArray();
        assertEquals(0.0, fvsb.getFeatureOf(0), 0.0);
        assertEquals(1.2345, fvsb.getFeatureOf(2), 0.0);
        assertEquals(1.2567, fvsb.getFeatureOf(3), 0.0);
        assertEquals(.4564, fvsb.getFeatureOf(1), 0.0);
        
        
    }
}
