package com.mattbarta.decision4j.trees.decisiontree;

import org.sgdtk.Model;

/**
 *
 */
public class DecisionTreeFactory
{

    public DecisionTreeFactory()
    {
    }

    public Model newInstance() throws Exception
    {
        DecisionNode node = new DecisionNode();
        Model dt = new DecisionTreeModel(node);
        return dt;
    }

}
