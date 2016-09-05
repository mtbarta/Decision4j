package com.mattbarta.decision4j.trees.decisiontree;

import com.mattbarta.decision4j.trees.Node;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.DoubleStream;
import org.sgdtk.FeatureVector;
import org.sgdtk.VectorN;

/**
 * While this class currently holds the FV data that's used during learning, it
 * should really get split out into a helper class. This class has no need to
 * know what data it's looking at.
 */
public class DecisionNode extends Node
{

    private int id;

    private transient List<FeatureVector> data;
    private DecisionRule rule;

    private double[] scores;

    /*
     retrieves the raw featurevector list that occupies this node.
     */
    public List<FeatureVector> getFVData()
    {
        return data;
    }

    public void setFVData(List<FeatureVector> data)
    {
        this.data = data;
    }

    @Override
    public void setId(int id)
    {
        this.id = id;
    }

    @Override
    public int getId()
    {
        return id;
    }

    public void setRule(DecisionRule rule)
    {
        this.rule = rule;
    }

    @Override
    public String toString()
    {
        List<String> sb = new ArrayList<>();
        int fi = getRule().getFeatureIndex();
        double decision = getRule().getDecision();
        String scoreStr = Arrays.toString(scores);

        sb.add(String.valueOf(fi));
        sb.add(String.valueOf(decision));
        sb.add(scoreStr);

        String res = String.join(";", sb);
        return res;

    }

    public double[] getScores()
    {
        return scores;
    }

    public void setScores(double[] scores)
    {
        this.scores = scores;
    }

    public DecisionNode()
    {
        List<Node> list = new ArrayList<>(2);
        Collections.fill(list, null);
        this.setChildren(list);
    }

    public DecisionRule getRule()
    {
        return rule;
    }

    public double getCat()
    {
        double[] scrs = this.getScores();

        if (scrs == null)
        {
            return -1;
        }

        double max = DoubleStream.of(scrs).max().getAsDouble();
        for (int i = 0; i < scrs.length; i++)
        {
            if (scrs[i] == max)
            {
                return i;
            }
        }
        return -1;
    }

    @Override
    public DecisionNode getChild(int i)
    {
        if (i == 0)
        {
            return getLeftNode();
        }
        else if (i == 1)
        {
            return getRightNode();
        }
        else
        {
            throw new RuntimeException("requested index outside requested.");
        }
    }

    public DecisionNode getLeftNode()
    {
        DecisionNode node = (DecisionNode) super.getChild(0);
        return node;
    }

    public DecisionNode getRightNode()
    {
        DecisionNode node = (DecisionNode) super.getChild(1);
        return node;
    }

    public void setRightNode(Node node)
    {
        setChild(1, node);
    }

    public void setLeftNode(Node node)
    {
        setChild(0, node);
    }

    @Override
    public void load(File file) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(File file) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void load(InputStream in) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public void save(OutputStream out) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    /*
     predict on a featureVector, calling children node predict() down the tree.
    
     This method may want to throw an error, or otherwise handle when a node is missing.
     */
    @Override
    public double predict(FeatureVector fv)
    {
        if (this.isLeaf())
        {
            return this.getCat();
        }

        VectorN v = fv.getX();
        double feat = v.at(rule.getFeatureIndex());
        int decision = rule.decide(feat);
        boolean greaterThanRule = decision >= 0;

        if (greaterThanRule && getRightNode() != null)
        {
            return getRightNode().predict(fv);
        }
        else if (!greaterThanRule && getLeftNode() != null)
        {
            return getLeftNode().predict(fv);
        }
        else
        {
            return this.getCat();
        }
    }

    @Override
    public double[] score(FeatureVector fv)
    {
        if (this.isLeaf())
        {
            return this.getScores();
        }
        // below can be cleaned up. why should this method know so much about
        // VectorN?
        VectorN v = fv.getX();
        double feat = v.at(rule.getFeatureIndex());
        int decision = rule.decide(feat);
        boolean greaterThanRule = decision >= 0;

        if (greaterThanRule && getRightNode() != null)
        {
            return getRightNode().score(fv);
        }
        else if (!greaterThanRule && getLeftNode() != null)
        {
            return getLeftNode().score(fv);
        }
        else
        {
            return this.getScores();
        }
    }

    @Override
    public DecisionNode prototype()
    {
        return new DecisionNode();
    }
}
