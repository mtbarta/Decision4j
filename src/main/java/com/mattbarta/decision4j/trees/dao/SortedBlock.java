package com.mattbarta.decision4j.trees.dao;

import java.util.List;
import org.sgdtk.FeatureVector;

/**
 *
 */
public interface SortedBlock {

    int getCol();

//    double[] getColumn();

    /*
    get the featureVectors in sorted order.
     */
    FeatureVector get(int indx);

    double getFeatureOf(int indx);

    /*
    returns the original index within the data based on the sort.
     */
    int getIndex(int indx);

    int size();

    //    public FVSortedBlock(List<Integer> indexes){
    //        this.indexes = indexes;
    //    }
    void sort();
    
    List<Integer> getSortedIndices();
    

}
