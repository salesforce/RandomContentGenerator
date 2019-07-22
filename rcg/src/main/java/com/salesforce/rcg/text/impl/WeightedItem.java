package com.salesforce.rcg.text.impl;

public class WeightedItem<V> {
    protected V item;
    protected double myWeight;
    protected double cumulativeWeightLow, cumulativeWeightHigh;
    
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
        this.cumulativeWeightHigh = cumulativeWeightHigh;
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
        return(cumulativeWeightHigh);
    }
    
    public WeightedItem<V> setCumulativeWeightHigh(double weight) {
        cumulativeWeightHigh = weight;
        return(this);
    }
}
