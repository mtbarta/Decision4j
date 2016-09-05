package com.mattbarta.decision4j.trees.decisiontree;

import com.mattbarta.decision4j.trees.Tree;
import com.mattbarta.decision4j.trees.io.StringIO;
import com.mattbarta.decision4j.trees.dao.CandidateSplit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.sgdtk.FeatureVector;
import org.sgdtk.Model;

/**
 *
 */
public class DecisionTreeModel extends Tree<DecisionNode>{

    private List<Double> categories;
    private int nodeCount = 0;

    public int getNodeCount()
    {
        return nodeCount;
    }

    public void setNodeCount(int nodeCount)
    {
        this.nodeCount = nodeCount;
    }

    public List<Double> getCategories()
    {
        return categories;
    }

    public DecisionTreeModel(DecisionNode root){
        super(root);
        root.setId(nodeCount);
        nodeCount++;
    }
    
    public Model splitNode(DecisionNode node, DecisionRule rule, double[] leftProb, double[] rightProb){
        if(node.numChildren() > 0) {
            return node;
        }
        node.setRule(rule);

        DecisionNode lefty = node.prototype();
        lefty.setId(nodeCount);
        nodeCount++;
        lefty.setScores(leftProb);
        
        DecisionNode righty = node.prototype();
        righty.setId(nodeCount);
        nodeCount++;
        righty.setScores(rightProb);
        
        lefty.setParent(node);
        righty.setParent(node);
        
        node.setLeftNode(lefty);
        node.setRightNode(righty);
        
        return node;
    }
    
    public Model splitNode(DecisionNode node, CandidateSplit cs){
        if(node.numChildren() > 0) {
            return node;
        }
        
        DecisionRule rule = cs.rule;
        double[] leftProb = cs.leftScores;
        double[] rightProb = cs.rightScores;
        
        return splitNode(node,rule,leftProb,rightProb);

    }

    @Override
    public void load(File file) throws IOException
    {
        load(new FileInputStream(file));
    }

    @Override
    public void save(File file) throws IOException
    {
        save(new FileOutputStream(file));
        
    }

    @Override
    public void load(InputStream in) throws IOException
    {
        StringIO.readInputStream(in, this);
    }

    /*
    Save this model out to an outputStream.
    
    TODO: make formats more versatile and less coupled.
    */
    @Override
    public void save(OutputStream out) throws IOException
    {
        StringIO.saveToOutputStream(out, this); 
    }
    

    @Override
    public double predict(FeatureVector fv)
    {
        return this.getRoot().predict(fv);
    }

    @Override
    public double[] score(FeatureVector fv)
    {
        return this.getRoot().score(fv);
    }

    @Override
    public Model prototype()
    {
        Model dt = new DecisionTreeModel(new DecisionNode());
        
        return dt;
        
    }
}
