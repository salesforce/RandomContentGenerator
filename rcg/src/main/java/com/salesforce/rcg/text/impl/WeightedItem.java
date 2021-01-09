package com.salesforce.rcg.text.impl;

/** A container for an item that has a weight. Actually, three weights:
 * - Its own weight
 * - Cumulative weights in some larger container, such as a WeightedWordGenerator.
 *   There are two cumulative weights - low and high. However, we don't have to
 *   store all three of these. We can store two and compute the third by simple 
 *   arithmetic.
 *   
 * @author mpreslermarshall
 *
 * @param <V> The type of "item" that this refers to.
 */
public class WeightedItem<V> {
    /** The item this contains. */
    protected V item;
    protected double myWeight;
    protected double cumulativeWeightLow;
    
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
        return(myWeight + cumulativeWeightLow);
    }
    
}
