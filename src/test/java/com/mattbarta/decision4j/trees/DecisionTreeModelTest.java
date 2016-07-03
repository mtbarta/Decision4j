package com.mattbarta.decision4j.trees;

import com.mattbarta.decision4j.trees.criteria.CriterionFactory;
import com.mattbarta.decision4j.trees.criteria.CrossEntropy;
import com.mattbarta.decision4j.trees.decisiontree.DecisionTreeFactory;
import com.mattbarta.decision4j.trees.decisiontree.DecisionTreeLearner;
import com.mattbarta.decision4j.trees.splitters.BestSplitter;
import com.mattbarta.decision4j.trees.splitters.ISplitter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.sgdtk.DenseVectorN;
import org.sgdtk.FeatureVector;
import org.sgdtk.Model;
import org.sgdtk.VectorN;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class DecisionTreeModelTest {

    Map<String,Double> y;
    List<FeatureVector> fvs;
    
    @Before
    public void setup()
    {
        String DATA = 
        "5.1,3.5,1.4,0.2,Iris-setosa\n" +
        "4.9,3.0,1.4,0.2,Iris-setosa\n" +
        "4.7,3.2,1.3,0.2,Iris-setosa\n" +
        "4.6,3.1,1.5,0.2,Iris-setosa\n" +
        "5.0,3.6,1.4,0.2,Iris-setosa\n" +
        "5.4,3.9,1.7,0.4,Iris-setosa\n" +
        "4.6,3.4,1.4,0.3,Iris-setosa\n" +
        "5.0,3.4,1.5,0.2,Iris-setosa\n" +
        "4.4,2.9,1.4,0.2,Iris-setosa\n" +
        "4.9,3.1,1.5,0.1,Iris-setosa\n" +
        "5.4,3.7,1.5,0.2,Iris-setosa\n" +
        "4.8,3.4,1.6,0.2,Iris-setosa\n" +
        "4.8,3.0,1.4,0.1,Iris-setosa\n" +
        "4.3,3.0,1.1,0.1,Iris-setosa\n" +
        "5.8,4.0,1.2,0.2,Iris-setosa\n" +
        "5.7,4.4,1.5,0.4,Iris-setosa\n" +
        "5.4,3.9,1.3,0.4,Iris-setosa\n" +
        "5.1,3.5,1.4,0.3,Iris-setosa\n" +
        "5.7,3.8,1.7,0.3,Iris-setosa\n" +
        "5.1,3.8,1.5,0.3,Iris-setosa\n" +
        "5.4,3.4,1.7,0.2,Iris-setosa\n" +
        "5.1,3.7,1.5,0.4,Iris-setosa\n" +
        "4.6,3.6,1.0,0.2,Iris-setosa\n" +
        "5.1,3.3,1.7,0.5,Iris-setosa\n" +
        "4.8,3.4,1.9,0.2,Iris-setosa\n" +
        "5.0,3.0,1.6,0.2,Iris-setosa\n" +
        "5.0,3.4,1.6,0.4,Iris-setosa\n" +
        "5.2,3.5,1.5,0.2,Iris-setosa\n" +
        "5.2,3.4,1.4,0.2,Iris-setosa\n" +
        "4.7,3.2,1.6,0.2,Iris-setosa\n" +
        "4.8,3.1,1.6,0.2,Iris-setosa\n" +
        "5.4,3.4,1.5,0.4,Iris-setosa\n" +
        "5.2,4.1,1.5,0.1,Iris-setosa\n" +
        "5.5,4.2,1.4,0.2,Iris-setosa\n" +
        "4.9,3.1,1.5,0.1,Iris-setosa\n" +
        "5.0,3.2,1.2,0.2,Iris-setosa\n" +
        "5.5,3.5,1.3,0.2,Iris-setosa\n" +
        "4.9,3.1,1.5,0.1,Iris-setosa\n" +
        "4.4,3.0,1.3,0.2,Iris-setosa\n" +
        "5.1,3.4,1.5,0.2,Iris-setosa\n" +
        "5.0,3.5,1.3,0.3,Iris-setosa\n" +
        "4.5,2.3,1.3,0.3,Iris-setosa\n" +
        "4.4,3.2,1.3,0.2,Iris-setosa\n" +
        "5.0,3.5,1.6,0.6,Iris-setosa\n" +
        "5.1,3.8,1.9,0.4,Iris-setosa\n" +
        "4.8,3.0,1.4,0.3,Iris-setosa\n" +
        "5.1,3.8,1.6,0.2,Iris-setosa\n" +
        "4.6,3.2,1.4,0.2,Iris-setosa\n" +
        "5.3,3.7,1.5,0.2,Iris-setosa\n" +
        "5.0,3.3,1.4,0.2,Iris-setosa\n" +
        "7.0,3.2,4.7,1.4,Iris-versicolor\n" +
        "6.4,3.2,4.5,1.5,Iris-versicolor\n" +
        "6.9,3.1,4.9,1.5,Iris-versicolor\n" +
        "5.5,2.3,4.0,1.3,Iris-versicolor\n" +
        "6.5,2.8,4.6,1.5,Iris-versicolor\n" +
        "5.7,2.8,4.5,1.3,Iris-versicolor\n" +
        "6.3,3.3,4.7,1.6,Iris-versicolor\n" +
        "4.9,2.4,3.3,1.0,Iris-versicolor\n" +
        "6.6,2.9,4.6,1.3,Iris-versicolor\n" +
        "5.2,2.7,3.9,1.4,Iris-versicolor\n" +
        "5.0,2.0,3.5,1.0,Iris-versicolor\n" +
        "5.9,3.0,4.2,1.5,Iris-versicolor\n" +
        "6.0,2.2,4.0,1.0,Iris-versicolor\n" +
        "6.1,2.9,4.7,1.4,Iris-versicolor\n" +
        "5.6,2.9,3.6,1.3,Iris-versicolor\n" +
        "6.7,3.1,4.4,1.4,Iris-versicolor\n" +
        "5.6,3.0,4.5,1.5,Iris-versicolor\n" +
        "5.8,2.7,4.1,1.0,Iris-versicolor\n" +
        "6.2,2.2,4.5,1.5,Iris-versicolor\n" +
        "5.6,2.5,3.9,1.1,Iris-versicolor\n" +
        "5.9,3.2,4.8,1.8,Iris-versicolor\n" +
        "6.1,2.8,4.0,1.3,Iris-versicolor\n" +
        "6.3,2.5,4.9,1.5,Iris-versicolor\n" +
        "6.1,2.8,4.7,1.2,Iris-versicolor\n" +
        "6.4,2.9,4.3,1.3,Iris-versicolor\n" +
        "6.6,3.0,4.4,1.4,Iris-versicolor\n" +
        "6.8,2.8,4.8,1.4,Iris-versicolor\n" +
        "6.7,3.0,5.0,1.7,Iris-versicolor\n" +
        "6.0,2.9,4.5,1.5,Iris-versicolor\n" +
        "5.7,2.6,3.5,1.0,Iris-versicolor\n" +
        "5.5,2.4,3.8,1.1,Iris-versicolor\n" +
        "5.5,2.4,3.7,1.0,Iris-versicolor\n" +
        "5.8,2.7,3.9,1.2,Iris-versicolor\n" +
        "6.0,2.7,5.1,1.6,Iris-versicolor\n" +
        "5.4,3.0,4.5,1.5,Iris-versicolor\n" +
        "6.0,3.4,4.5,1.6,Iris-versicolor\n" +
        "6.7,3.1,4.7,1.5,Iris-versicolor\n" +
        "6.3,2.3,4.4,1.3,Iris-versicolor\n" +
        "5.6,3.0,4.1,1.3,Iris-versicolor\n" +
        "5.5,2.5,4.0,1.3,Iris-versicolor\n" +
        "5.5,2.6,4.4,1.2,Iris-versicolor\n" +
        "6.1,3.0,4.6,1.4,Iris-versicolor\n" +
        "5.8,2.6,4.0,1.2,Iris-versicolor\n" +
        "5.0,2.3,3.3,1.0,Iris-versicolor\n" +
        "5.6,2.7,4.2,1.3,Iris-versicolor\n" +
        "5.7,3.0,4.2,1.2,Iris-versicolor\n" +
        "5.7,2.9,4.2,1.3,Iris-versicolor\n" +
        "6.2,2.9,4.3,1.3,Iris-versicolor\n" +
        "5.1,2.5,3.0,1.1,Iris-versicolor\n" +
        "5.7,2.8,4.1,1.3,Iris-versicolor\n" +
        "6.3,3.3,6.0,2.5,Iris-virginica\n" +
        "5.8,2.7,5.1,1.9,Iris-virginica\n" +
        "7.1,3.0,5.9,2.1,Iris-virginica\n" +
        "6.3,2.9,5.6,1.8,Iris-virginica\n" +
        "6.5,3.0,5.8,2.2,Iris-virginica\n" +
        "7.6,3.0,6.6,2.1,Iris-virginica\n" +
        "4.9,2.5,4.5,1.7,Iris-virginica\n" +
        "7.3,2.9,6.3,1.8,Iris-virginica\n" +
        "6.7,2.5,5.8,1.8,Iris-virginica\n" +
        "7.2,3.6,6.1,2.5,Iris-virginica\n" +
        "6.5,3.2,5.1,2.0,Iris-virginica\n" +
        "6.4,2.7,5.3,1.9,Iris-virginica\n" +
        "6.8,3.0,5.5,2.1,Iris-virginica\n" +
        "5.7,2.5,5.0,2.0,Iris-virginica\n" +
        "5.8,2.8,5.1,2.4,Iris-virginica\n" +
        "6.4,3.2,5.3,2.3,Iris-virginica\n" +
        "6.5,3.0,5.5,1.8,Iris-virginica\n" +
        "7.7,3.8,6.7,2.2,Iris-virginica\n" +
        "7.7,2.6,6.9,2.3,Iris-virginica\n" +
        "6.0,2.2,5.0,1.5,Iris-virginica\n" +
        "6.9,3.2,5.7,2.3,Iris-virginica\n" +
        "5.6,2.8,4.9,2.0,Iris-virginica\n" +
        "7.7,2.8,6.7,2.0,Iris-virginica\n" +
        "6.3,2.7,4.9,1.8,Iris-virginica\n" +
        "6.7,3.3,5.7,2.1,Iris-virginica\n" +
        "7.2,3.2,6.0,1.8,Iris-virginica\n" +
        "6.2,2.8,4.8,1.8,Iris-virginica\n" +
        "6.1,3.0,4.9,1.8,Iris-virginica\n" +
        "6.4,2.8,5.6,2.1,Iris-virginica\n" +
        "7.2,3.0,5.8,1.6,Iris-virginica\n" +
        "7.4,2.8,6.1,1.9,Iris-virginica\n" +
        "7.9,3.8,6.4,2.0,Iris-virginica\n" +
        "6.4,2.8,5.6,2.2,Iris-virginica\n" +
        "6.3,2.8,5.1,1.5,Iris-virginica\n" +
        "6.1,2.6,5.6,1.4,Iris-virginica\n" +
        "7.7,3.0,6.1,2.3,Iris-virginica\n" +
        "6.3,3.4,5.6,2.4,Iris-virginica\n" +
        "6.4,3.1,5.5,1.8,Iris-virginica\n" +
        "6.0,3.0,4.8,1.8,Iris-virginica\n" +
        "6.9,3.1,5.4,2.1,Iris-virginica\n" +
        "6.7,3.1,5.6,2.4,Iris-virginica\n" +
        "6.9,3.1,5.1,2.3,Iris-virginica\n" +
        "5.8,2.7,5.1,1.9,Iris-virginica\n" +
        "6.8,3.2,5.9,2.3,Iris-virginica\n" +
        "6.7,3.3,5.7,2.5,Iris-virginica\n" +
        "6.7,3.0,5.2,2.3,Iris-virginica\n" +
        "6.3,2.5,5.0,1.9,Iris-virginica\n" +
        "6.5,3.0,5.2,2.0,Iris-virginica\n" +
        "6.2,3.4,5.4,2.3,Iris-virginica\n" +
        "5.9,3.0,5.1,1.8,Iris-virginica";
        
        String[] obs = DATA.split("\n");
        
        y = new HashMap<>();
        y.put("Iris-setosa", 0.0);
        y.put("Iris-versicolor", 1.0);
        y.put("Iris-virginica", 2.0);
        
        fvs = new ArrayList<>();
        for(int i = 0; i < obs.length; i++)
        {
            String ob = obs[i];
            String[] obSplit = ob.split(",");
            
            double[] res = new double[obSplit.length-1];
            for (int j=0; j < obSplit.length-1; j++)
            {
                res[j] = Double.parseDouble(obSplit[j]);
            }
            VectorN vec = new DenseVectorN(res);
            fvs.add(new FeatureVector(y.get(obSplit[obSplit.length-1]), vec));
        }
    }
    
    @Test
    public void DecisionTreeModelIrisTest()
    {
        
        try
        {
            CriterionFactory cf = new CriterionFactory(new CrossEntropy());
            ISplitter bs = new BestSplitter(1, cf);
            DecisionTreeFactory dtf = new DecisionTreeFactory();
            DecisionTreeLearner dtl = new DecisionTreeLearner(dtf,bs, 4, 100, 100, true);
            
            Model dtm = dtl.create(null);
            
            dtl.trainEpoch(dtm, fvs);
            
            List<Double> preds = fvs.stream()
                    .mapToDouble((FeatureVector f) -> dtm.predict(f))
                    .boxed()
                    .collect(Collectors.toList());
            
            int TP = 0;
            int FP = 0;
            for (int i = 0; i < fvs.size(); i++)
            {
                double pred = preds.get(i);
                double truth = fvs.get(i).getY();

                int compare = Double.compare(pred, truth);
                
                if (compare == 0)
                {
                    TP++;
                }
                else
                {
                    FP++;
                }
            }
            double acc = (double) TP / (TP + FP);
            assertTrue(acc > 0.90);
        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }
}

