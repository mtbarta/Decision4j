package com.mattbarta.decision4j.trees.dao;

import java.util.AbstractList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.sgdtk.FeatureVector;
import org.sgdtk.VectorN;

/**
 * A data container that keeps rows sorted by feature values.
 */
public class FVSortedBlock extends AbstractList  {

    private final List<Integer> indexes;
    private final int col;

    private final List<FeatureVector> data;
    
    public FVSortedBlock(List<FeatureVector> data, int indx){
        this.indexes = IntStream.range(0,data.size()).boxed().collect(Collectors.toList());
        this.col = indx;
        this.data = data;
    }
    
    public void sort()
    {
        FeatureVectorComparator fvc = new FeatureVectorComparator(this.col);
        indexes.sort(fvc);
    }
    
    @Override
    public int size(){
        return indexes.size();
    }

    @Override
    public FeatureVector get(int indx)
    {
        int fvIndex = this.indexes.get(indx);
        
        return this.data.get(fvIndex);
    }
    
    public double getFeatureOf(int indx)
    {     
        return this.get(indx).getX().at(this.col);
    }
    
    public List<Integer> getSortedIndices()
    {
        return this.indexes;
    }

    public int getIndex(int indx){
        int fvIndex = this.indexes.get(indx);
        return fvIndex;
    }
    
     public int getCol()
    {
        return col;
    }
    
    class FeatureVectorComparator implements Comparator<Integer>
    {
        private final int index;
        public FeatureVectorComparator(int index)
        {
            this.index = index;
        }

        @Override
        public int compare(Integer o1, Integer o2)
        {
            VectorN vector1 = data.get(o1).getX();
            VectorN vector2 = data.get(o2).getX();
            
            double feat1 = vector1.at(this.index);
            double feat2 = vector2.at(this.index);
            
            return Double.compare(feat1, feat2);
        }
    }
}
