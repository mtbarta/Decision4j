package trees.splitters;

import com.mattbarta.decision4j.trees.criteria.CriterionFactory;
import com.mattbarta.decision4j.trees.criteria.CrossEntropy;
import com.mattbarta.decision4j.trees.dao.CandidateCollection;
import com.mattbarta.decision4j.trees.dao.CandidateSplit;
import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import com.mattbarta.decision4j.trees.splitters.BestSplitter;
import com.mattbarta.decision4j.trees.splitters.ISplitter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import org.junit.Test;
import org.sgdtk.DenseVectorN;
import org.sgdtk.FeatureVector;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class BestSplitterTest
{

    @Test
    public void findSplitTest()
    {
        double[] first =
        {
            0.0, 0.0, 1.0
        };
        double[] second =
        {
            0.0, 1.0, 1.0
        };
        double[] third =
        {
            0.0, 1.0, 0.0
        };
        double[] fourth =
        {
            0.0, 0.0, 0.0
        };

        List<FeatureVector> lfv = new ArrayList()
        {
            {
                add(new FeatureVector(0.0, new DenseVectorN(first)));
                add(new FeatureVector(0.0, new DenseVectorN(second)));
                add(new FeatureVector(1.0, new DenseVectorN(third)));
                add(new FeatureVector(1.0, new DenseVectorN(fourth)));
            }
        };
        List<FVSortedBlock> fvsbs = new ArrayList<>(3);
        
        for (int i = 0; i < first.length; i++)
        {
            FVSortedBlock fvsb = new FVSortedBlock(lfv, i);
            fvsb.sort();
            fvsbs.add(fvsb);
        }

        CriterionFactory cf = new CriterionFactory(new CrossEntropy());
        ISplitter bs = new BestSplitter(0, cf);
        
        double[] sampleWeights = DoubleStream.generate(() -> 1.0).limit(4).toArray();
        CandidateCollection cc = bs.findSplits(fvsbs, sampleWeights, null);
        
        CandidateSplit top = cc.poll();
        assertEquals(2, top.rule.getFeatureIndex());
        
        top = cc.poll();
        assertEquals(1, top.rule.getFeatureIndex());
    }
}
