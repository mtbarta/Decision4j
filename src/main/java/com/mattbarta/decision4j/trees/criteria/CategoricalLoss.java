package com.mattbarta.decision4j.trees.criteria;

import org.sgdtk.Loss;

/**
 *
 */
public abstract class CategoricalLoss implements Loss{

    public abstract double loss(double[] a, double[] b);
    public abstract double dloss(double[] a, double[] b);
}
