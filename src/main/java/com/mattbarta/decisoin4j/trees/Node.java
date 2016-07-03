package com.mattbarta.decisoin4j.trees;

import java.util.ArrayList;
import java.util.List;
import org.sgdtk.Model;

/**
 *
 * 
 */
public abstract class Node implements Model
{

    private Node parent;

    private List<Node> children = new ArrayList<>(2);

    public abstract int getId();

    public abstract void setId(int id);

    public void setParent(Node parent)
    {
        this.parent = parent;
    }

    public void setChildren(List<Node> children)
    {
        this.children = children;
    }

    public boolean isRoot()
    {
        return parent == null;
    }

    public boolean isLeaf()
    {
        return children.isEmpty();
    }

    public List<Node> getChildren()
    {
        return children;
    }

    public int numChildren()
    {
        return children.size();
    }

    public Node getParent()
    {
        return parent;
    }

    public Node getChild(int i)
    {
        return children.get(i);
    }

    public void setChild(int i, Node n)
    {
        try
        {
            children.set(i, n);
        }
        catch (IndexOutOfBoundsException ex)
        {
            children.add(n);
        }
    }
}
