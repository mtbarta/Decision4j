package trees.criteria;

import com.mattbarta.decision4j.trees.criteria.CrossEntropy;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class CrossEntropyTest {

    @Test
    public void lossTest()
    {
        double[] p = {.3, .3, .4};
        double[] m = {0, 0, 1};
        
        CrossEntropy ce = new CrossEntropy();
        
        double loss = ce.loss(p, m);
        
        assertEquals(-Math.log(.4), loss, 0.01);
    }
}
