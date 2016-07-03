package com.mattbarta.decision4j.trees.criteria;

import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import org.sgdtk.FeatureVector;

/**
 * The point of the CategoricalCriterion is to keep track of the loss metric for each
 possible split.
 *
 * What is not figured out yet is 1.) how to handle sample weights. 2.) how
 * buckets should be created and handled (quantile sketch algorithm, etc.)
 */
public class CategoricalCriterion
{

    private final CategoricalLoss loss;

    private final FVSortedBlock fvsb;

    private final double[] sampleWeights;

    private double leftSum = 0.0;

    private final double[] leftCount;

    private int numLeft = 0;

    private double rightSum = 0.0;

    private final double[] rightCount;

    private int numRight = 0;

    //weight average of the two entropies. for our case, entropy is
    // - sum( P(class) * log(P(class)))
    private final double[] pCGivenL;

    private final double[] pCGivenR;

    private double[] totalCount;

    public double[] getTotalCount()
    {
        return totalCount;
    }

    private int numTotal = 0;

    private double initialLoss = 0.0;

    private int oldPos = 0;

    public CategoricalCriterion(CategoricalLoss loss, FVSortedBlock fvsb,
            double[] sampleWeights, int numClasses)
    {
        this.loss = loss;
        this.fvsb = fvsb;
        this.sampleWeights = sampleWeights;

        this.leftCount = new double[numClasses];
        this.rightCount = new double[numClasses];
        this.totalCount = new double[numClasses];
        this.numTotal = fvsb.size();

        pCGivenL = new double[leftCount.length];
        pCGivenR = new double[rightCount.length];
    }
    /*
     gather initial counts so that further evaluations don't have to continuously 
     cycle over the dataset.
    
     basically copied out parts of the update function. I'm not sure how to reduce these
     functions together.
     */

    public void init()
    {

        for (int i = 0; i < fvsb.size(); i++)
        {
            FeatureVector fvoi = fvsb.get(i);
            int y = (int) fvoi.getY(); // wtf am i doing here? don't cast.
            double x = fvsb.getFeatureOf(i);

            int origIndex = fvsb.getIndex(i);
            rightCount[y] += this.sampleWeights[origIndex];
            rightSum += x;
        }

        this.numRight = this.numTotal = fvsb.size();
        this.totalCount = rightCount.clone();

        for (int c = 0; c < rightCount.length; c++)
        {

            //create probabilities of each class in each node.
            //index represents the number of documents in the left node.
            pCGivenR[c] = rightCount[c] / numRight;
        }

        this.initialLoss = loss.loss(pCGivenR, pCGivenR);
    }

    /*
     update the node statistics to the desired index within the data.
     */
    public void update(int index)
    {

        for (int i = this.oldPos; i < index; i++)
        {

            FeatureVector fvoi = fvsb.get(i);
            int y = (int) fvoi.getY(); // wtf am i doing here? I don't want to cast.
            double x = fvsb.getFeatureOf(i);

            //update left node
            int origIndex = fvsb.getIndex(i);
            leftCount[y] += this.sampleWeights[origIndex];
            numLeft += 1;

            leftSum += x;
            rightSum -= x;
        }

        numRight = numTotal - numLeft;

        for (int c = 0; c < rightCount.length; c++)
        {
            rightCount[c] = totalCount[c] - leftCount[c];

            //create probabilities of each class in each node.
            //index represents the number of documents in the left node.
            pCGivenR[c] = rightCount[c] / numRight;
            pCGivenL[c] = leftCount[c] / numLeft;
        }

        this.oldPos = index;
    }

    public double evaluate()
    {
       
        
        double leftLoss = this.loss.loss(pCGivenL, pCGivenL);
        double rightLoss = this.loss.loss(pCGivenR, pCGivenR);

        // weighted average of the two entropies as the score.
        double weightedRight = (numRight / (double) fvsb.size()) * rightLoss;
        double weightedLeft = (numLeft / (double) fvsb.size()) * leftLoss;
        
        double res =  this.initialLoss - (weightedRight + weightedLeft);
        return res;
    }

    public int getNumLeft()
    {
        return numLeft;
    }

    public int getNumRight()
    {
        return numRight;
    }

    public double getLeftSum()
    {
        return leftSum;
    }

    public double getRightSum()
    {
        return rightSum;
    }

    public double[] getpCGivenL()
    {
        return pCGivenL;
    }

    public double[] getpCGivenR()
    {
        return pCGivenR;
    }
}
