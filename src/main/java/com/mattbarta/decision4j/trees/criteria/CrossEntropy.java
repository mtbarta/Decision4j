package com.mattbarta.decision4j.trees.criteria;

/**
 * right now, is the cross entropy of a decision tree node.
 */
public class CrossEntropy extends CategoricalLoss
{

    private final double SMOOTHING_FACTOR = 0.0001;

    public CrossEntropy()
    {

    }

    @Override
    public double loss(double p, double y)
    {
        double pos = -y * Math.log(p);
        double neg = (1 - y) * Math.log(1 - p);

        double res = pos - neg;

        return res;
    }

    @Override
    public double dLoss(double p, double y)
    {
        return y - p;
    }

    /*
     functions to support categorical cases.
    
     @p an array of predicted scores for each label.
     @y a one-hot vector of class labels.
     */
    @Override
    public double loss(double[] p, double[] y)
    {
        if (p.length != y.length)
        {
            throw new RuntimeException(
                    "the length of predictions does not equal the expected number of classes.");
        }

        double res = 0.0;
        for (int i = 0; i < p.length; i++)
        {
            double predicted = p[i] + SMOOTHING_FACTOR;
            double expected = y[i] + SMOOTHING_FACTOR;
            
            res += expected * Math.log(predicted);
        }

        return -res; // / p.length;
    }

    /*
     hmm. this is the derivative.
    
     unlike other functions, this result will need to be flattened into a scalar for
     certain tasks.
     */
    public double[] CategoricalDLoss(double[] p, double[] y)
    {
        if (p.length != y.length)
        {
            throw new RuntimeException(
                    "the length of predictions does not equal the expected number of classes.");
        }
        double[] res = new double[p.length];
        for (int i = 0; i < p.length; i++)
        {
            res[i] += p[i] - y[i];
        }

        return res;
    }

    @Override
    public double dloss(double[] a, double[] b)
    {
        double[] catdLoss = CategoricalDLoss(a, b);
        double result = 0;
        for (double i : catdLoss)
        {
            result += i;
        }
        return result;
    }

}
