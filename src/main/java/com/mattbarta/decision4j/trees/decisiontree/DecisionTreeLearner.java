package com.mattbarta.decision4j.trees.decisiontree;

import com.mattbarta.decision4j.trees.dao.CandidateCollection;
import com.mattbarta.decision4j.trees.dao.CandidateSplit;
import com.mattbarta.decisoin4j.trees.RandomFeatureSelector;
import com.mattbarta.decisoin4j.trees.Tree;
import com.mattbarta.decision4j.trees.dao.FVSortedBlock;
import com.mattbarta.decision4j.trees.splitters.ISplitter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import org.sgdtk.FeatureVector;
import org.sgdtk.Learner;
import org.sgdtk.Metrics;
import org.sgdtk.Model;

/**
 * concerns: 1. decision nodes shouldn't hold on to their data reference. 2. no
 * evaluation method provided. 3. not really using the sgtk iteration method in
 * the decision tree. 
 *
 * necessary abstractions 1. load/save formatter 3. WeightedFBSortedBlock -- to
 * handle sampleWeights.
 */
public class DecisionTreeLearner implements Learner
{

    private final int maxFeatures;

    private final int maxDepth;

    private final int maxLeafNodes;
    
    private final ISplitter splitter;

    //set weights as a property because this class will not change them.
    double[] sampleWeights;

    private final DecisionTreeFactory dtf;

    Map<Model, List<FeatureVector>> nodeList;

    private int numClasses = -1;

    private final boolean reproposal;

    private Map<Integer, Double> globalSplits = null;

    public DecisionTreeLearner(DecisionTreeFactory dtf, ISplitter splitter, int maxFeatures,
            int maxDepth,
            int maxLeafNodes, boolean reproposal)
    {
        this.dtf = dtf;
        this.splitter = splitter;
        this.maxFeatures = maxFeatures;
        this.maxDepth = maxDepth;
        this.maxLeafNodes = maxLeafNodes;
        this.reproposal = reproposal;
    }

    @Override
    public Model create(Object params) throws Exception
    {
        Model dt = dtf.newInstance();
        return dt;
    }

    @Override
    public Model trainEpoch(Model model, List<FeatureVector> list)
    {
        //sets numClasses by iterating over list. this could be improved, i'm sure.
        preprocess(model, list);
        this.splitter.getCriterionFactory().setNumClasses(numClasses);

        Queue<DecisionNode> allNodes = new LinkedList();

        RandomFeatureSelector rfs = new RandomFeatureSelector(list, maxFeatures);
        Set<Integer> featureIndices = rfs.getIndices();

        DecisionTreeModel dtm = (DecisionTreeModel) model;
        DecisionNode root = dtm.getRoot();
        root.setFVData(list);

        allNodes.offer(root);

        int height = 0;
        int numLeaves = 0;
        while (!allNodes.isEmpty() &&
                height < maxDepth &&
                numLeaves < maxLeafNodes)
        {
            DecisionNode at = allNodes.poll();
            boolean isSplit = trainNode(dtm, at, featureIndices,
                    this.globalSplits);

            if (isSplit)
            {
                allNodes.add(at.getLeftNode());
                allNodes.add(at.getRightNode());
            }

            height = dtm.height();
            numLeaves = Tree.numLeaves(root);
        }

        return model;
    }

    private boolean trainNode(DecisionTreeModel model, DecisionNode node,
            Set<Integer> fi, Map<Integer, Double> existingSplits)
    {
        List<FeatureVector> list = node.getFVData();
        List<FVSortedBlock> fvsbs = createBlocks(list, fi);

        CandidateCollection cc = this.splitter.
                findSplits(fvsbs, sampleWeights, existingSplits);

        if (cc.isEmpty())
        {
            node.setScores(cc.getTotalProb());
            node.setRule(CandidateCollection.notSplitDecision);
            return false;
        }
        CandidateSplit top = cc.poll();

        //if node is root and we aren't recalculating splits, save them here.
        if (node.getId() == 0 && !reproposal)
        {
            saveGlobalSplits(cc);
        }

        setDataInNode(model, node, top);
        return true;
    }

    private void setDataInNode(DecisionTreeModel model, DecisionNode node,
            CandidateSplit top)
    {
        if (node.getFVData() == null)
        {
            node.setFVData(top.sortedBlock);
        }
        model.splitNode(node, top);

        DecisionNode left = node.getLeftNode();
        DecisionNode right = node.getRightNode();

        List<FeatureVector> leftData = top.sortedBlock.subList(0, top.splitPos);
        List<FeatureVector> rightData = top.sortedBlock.subList(top.splitPos,
                top.sortedBlock.size());

        left.setFVData(leftData);
        right.setFVData(rightData);
    }

    private List<FVSortedBlock> createBlocks(List<FeatureVector> list,
            Set<Integer> indices)
    {
        List<FVSortedBlock> fvsbs = new ArrayList<>(indices.size());
        //sort on the features.
        Iterator<Integer> si = indices.iterator();
        for (; si.hasNext();)
        {
            int i = si.next();
            FVSortedBlock fvsb = new FVSortedBlock(list, i);
            fvsb.sort();
            fvsbs.add(fvsb);
        }

        return fvsbs;
    }

    private void saveGlobalSplits(PriorityQueue<CandidateSplit> scoredRules)
    {
        List<CandidateSplit> globals = new ArrayList<>(scoredRules);
        Map<Integer, Double> results = new HashMap<>();
        for (CandidateSplit cs : globals)
        {
            int idx = cs.rule.getFeatureIndex();
            double dec = cs.rule.getDecision();
            results.put(idx, dec);
        }
        this.globalSplits = results;
    }

    @Override
    public void trainOne(Model model, FeatureVector fv)
    {
    }

    @Override
    public void preprocess(Model model, List<FeatureVector> list)
    {
        AtomicInteger total = new AtomicInteger(0);
        Map<Double, Integer> classes = new HashMap<>();
        for (FeatureVector fv : list)
        {
            int val = classes.getOrDefault(fv.getY(), 0);
            classes.put(fv.getY(), val + 1);
            total.incrementAndGet();
        }

//        Map<Double,Long> classes = list.stream()
//                .collect(Collectors.groupingBy(FeatureVector::getY, Collectors.counting()));
        double[] scores = calculateScores(classes, total.get());

        DecisionNode root = ((DecisionTreeModel) model).getRoot();
        root.setScores(scores);

        this.numClasses = classes.size();

        //create sample weights for the time being. 
        //TODO: implement weighted model.
        sampleWeights = new double[list.size()];
        for (int i = 0; i < sampleWeights.length; i++)
        {
            sampleWeights[i] = 1.0;
        }
    }
    
    private double[] calculateScores(Map<Double, Integer> classes, int total)
    {
        double[] scores = new double[classes.size()];
        for (Double k : classes.keySet())
        {
            Integer val = classes.get(k);
            scores[k.intValue()] = val.doubleValue() / total;

        }
        return scores;
    }

    @Override
    public void evalOne(Model model, FeatureVector fv, Metrics mtrcs)
    {
    }

    @Override
    public void eval(Model model, List<FeatureVector> list, Metrics mtrcs)
    {
        //todo
        mtrcs.setCost(0.0);
        mtrcs.setTotalError(0.0);
        mtrcs.setTotalLoss(0.0);
    }

}
