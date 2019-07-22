package com.salesforce.rcg.text.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class WeightedWordGenerator extends AbstractRandomWordGenerator {
    protected List<WeightedItem<String>> items = new ArrayList<>();
    
    protected double totalWeight = 0.0;
    
    protected AtomicBoolean weightsCompiled = new AtomicBoolean(false);
    
    public WeightedWordGenerator(String name) {
        super(name);
    }
    
    public WeightedWordGenerator(String name, Random rng) {
        super(name, rng);
    }
    
    public synchronized void addWord(String word) {
        addWord(word, 1.0);
    }
    
    public synchronized void addWord(String word, double weight) {
        items.add(new WeightedItem<String>(word, weight));
        setDirty();
    }
    
    protected void setDirty() {
        weightsCompiled.set(false);
    }
    
    protected void checkIfDirty() {
        if (!weightsCompiled.get()) {
            // Our weights need to be recompiled. Probably.
            synchronized(this) {                
                if (!weightsCompiled.get()) {
                    // Yep, they really need to get recompiled. Let's do so.
                    double cumulativeWeight = 0.0;
                    
                    for (WeightedItem<String> item: items) {
                        item.setCumulativeWeightLow(cumulativeWeight);
                        cumulativeWeight += item.getWeight();
                        item.setCumulativeWeightHigh(cumulativeWeight);
                    }
                    
                    totalWeight = cumulativeWeight;
                    
                    // OK, we're done now.
                    weightsCompiled.set(true);
                }
            }
        }
    }

    @Override
    public String generateWord() {
        // Recompile weights if needed
        checkIfDirty();
        
        // Check boundary conditions
        if ((items.size() == 0) || (totalWeight == 0.0)) {
            return(null);
        }
        
        double roll = rng.nextDouble() * totalWeight;
        
        return(linearSearch(roll));        
    }
    
    private String linearSearch(double roll) {
        for (WeightedItem<String> item: items) {
            if ((roll >= item.getCumulativeWeightLow()) &&
                (roll <= item.getCumulativeWeightHigh())) {
                return(item.getItem());
            }
        }
        
        // This should not happen!
        throw new IllegalStateException("Roll " + roll + " is outside our recognized range of 0 - " + totalWeight);
    }

}
