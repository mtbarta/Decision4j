package com.mattbarta.decisoin4j.trees;

import java.util.List;
import org.sgdtk.MultiClassWeightModel;

/**
 * 
 * @param <N>
 */
public abstract class Tree<N extends Node> extends MultiClassWeightModel {
    private N root;
    
    public Tree(N root){
        this.root = root;
    }
    
    public N getRoot()
    {
        return root;
    }
    
    public void setRoot(N root)
    {
        this.root = root;
    }
    
    /*
    depth is the number of ancestors, excluding Node v. The depth of root is 0.
    */
    public static int depth(Node v)
    {
        if(v.isRoot())
        {
            return 0;
        } else
        {
            return 1 + depth(v.getParent());
        }
    }
    
    /*
    the height of Node v is the maximum path length of the subtree.
    
    leaf nodes have a height of 0, internal nodes have a height of height(children) + 1,
    and the height of the tree is the height of the root node.
    */
    public static int height(Node v)
    {
        if (v.numChildren() ==  0)
        {
            return 0;
        } else 
        {
            int h = 0;
            
            List<Node> children = v.getChildren();
            for(Node child : children)
            {
                h = Math.max(h, Tree.height(child));
            }
            
            return 1 + h;
        }
    }
    
    public int height()
    {
        return Tree.height(root);
    }
    
    public static int numLeaves(Node v)
    {
        if (v.isLeaf())
        {
            return 1;
        } else
        {
            int l = 0;
            List<Node> children = v.getChildren();
            for(Node c : children)
            {
                l += Tree.numLeaves(c);
            }
            
            return l;
        }
    }
    
    /*
    TODO: finish this.
    */
    public int size()
    {
        int c = 0;
        return c;
    }
    
    public Node getParent(N node)
    {
        return node.getParent();
    }
    
    public List<Node> getChildren(Node node)
    {
        return node.getChildren();
    }
    
    /*
    TODO: write this.
    */
    public List<Node> enumerateNodes()
    {
//        Deque<Node> stack = new ArrayDeque<>();
//        return null;
        throw new UnsupportedOperationException();
    }
    
}
