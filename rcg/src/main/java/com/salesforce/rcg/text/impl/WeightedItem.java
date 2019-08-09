package com.salesforce.rcg.text.impl;

/** A container for an item that has a weight. Actually, three weights:
 * - Its own weight
 * - Cumulative weights in some larger container, such as a WeightedWordGenerator.
 *   There are two cumulative weights - low and high
 * @author mpreslermarshall
 *
 * @param <V>
 */
public class WeightedItem<V> {
    protected V item;
    protected double myWeight;
    protected double cumulativeWeightLow;
    // , cumulativeWeightHigh;
    
    public WeightedItem(V item) {
        this.item = item;
    }
    
    public WeightedItem(V item, double weight) {
        this(item);
        this.myWeight = weight;
    }
    
    public WeightedItem(V item, 
            double weight, 
            double cumulativeWeightLow, 
            double cumulativeWeightHigh) {
        this(item, weight);
        this.cumulativeWeightLow = cumulativeWeightLow;
        //this.cumulativeWeightHigh = cumulativeWeightHigh;
    }
    
    public V getItem() {
        return(item);
    }
    
    public double getWeight() {
        return(myWeight);
    }
    
    public WeightedItem<V> setWeight(double weight) {
        myWeight = weight;
        return(this);
    }
    
    public double getCumulativeWeightLow() {
        return(cumulativeWeightLow);
    }
    
    public WeightedItem<V> setCumulativeWeightLow(double weight) {
        cumulativeWeightLow = weight;
        return(this);
    }

    public double getCumulativeWeightHigh() {
        //return(cumulativeWeightHigh);
        return(myWeight + cumulativeWeightLow);
    }
    
    /*
    public WeightedItem<V> setCumulativeWeightHigh(double weight) {
        cumulativeWeightHigh = weight;
        return(this);
    }
     */
}
