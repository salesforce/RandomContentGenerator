package com.salesforce.rcg.text.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.salesforce.rcg.text.ExtensibleWordGenerator;
import com.salesforce.rcg.text.WordGeneratorType;

public class WeightedWordGenerator 
        extends AbstractRandomWordGenerator 
        implements ExtensibleWordGenerator {
    /** All of the words in this generator. */
    protected List<WeightedItem<String>> items = new ArrayList<>();
    
    /** The total weight of all our items.
     * This is lazy-evaluated - every call to generate a word will see if the
     * weights need to be recompiled, and do so if needed.
     */
    protected double totalWeight = 0.0;
    
    protected AtomicBoolean weightsCompiled = new AtomicBoolean(false);
    
    // Statistics about the number of words generated and the number of
    // searches done while trying to find the right word.
    // Note that these counters are not threadsafe, so in any multithreaded
    // use of this class, they will be wrong to some degree. Their purpose is
    // for testing, and the tests that check these aren't multithreaded.
    protected long numWordsGenerated = 0L;
    protected long numProbes = 0L;
    
    public WeightedWordGenerator() {
        super("anonymous");
    }
    
    public WeightedWordGenerator(String name) {
        super(name);
    }
    
    public WeightedWordGenerator(String name, Random rng) {
        super(name, rng);
    }
    
    public String toString() {
        return "[WeightedWordGenerator name=" + name + ", compiled=" + weightsCompiled.get() + "]";
    }
    
    public WordGeneratorType getType() {
        return(WordGeneratorType.WEIGHTED);
    }

    /** Add a word to this generator with the default weight (1).
     * 
     * @param word The word to add
     */
    public void addWord(String word) {
        addWord(word, 1.0);
    }
    
    /** Add a word to this generator, specifying the weight for this word.
     * 
     * @param word The word to add
     * @param weight The weight for this word. It must be zero or positive.
     *     Zero-weight words are allowed, though they will never be generated
     *     by generateWord, so they're a little strange.
     */
    public void addWord(String word, double weight) {
        if (weight < 0.0) {
            throw new IllegalArgumentException("Weights must be non-negative");
        }
        addItem(new WeightedItem<String>(word, weight));
    }
    
    /** The actual implementation of adding an item to a word generator.
     * 
     * @param item The WeightedItem containing the string to add
     *     and its weight.
     */
    private synchronized void addItem(WeightedItem<String> item) {
        items.add(item);
        setDirty();
    }
    

    /** Mark the word generator as 'dirty' - needing to recompile the weights
     * on the items in the generator.
     */
    protected void setDirty() {
        weightsCompiled.set(false);
    }

    /** Check if the weights are dirty (out of date). If they are, recompile
     * them so that they can be used. That will also recompute the total
     * weight in the generator.    
     */
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
                        //item.setCumulativeWeightHigh(cumulativeWeight);
                    }
                    
                    totalWeight = cumulativeWeight;
                    
                    // OK, we're done now.
                    weightsCompiled.set(true);
                }
            }
        }
    }

    /** Generate a random word from our list.
     * 
     */
    @Override
    public String generateWord() {
        ++numWordsGenerated;
        
        // Recompile weights if needed
        checkIfDirty();
        
        // Check boundary conditions
        if ((items.size() == 0) || (totalWeight == 0.0)) {
            return(null);
        }
        
        double roll = rng.nextDouble() * totalWeight;
        
        return(binarySearch(roll));
    }
    
    /** Find the word based on the random number "roll". This implements a binary search to quickly find
     * the "right" word based on the roll.
     * 
     * @param roll A random number in the range 0 - {total weight of all items in the word generator}
     * @return The corresponding string from our table of items. 
     */
    private String binarySearch(double roll) {
        assert roll >= 0.0;
        assert roll <= totalWeight;

        // Start at the middle of the list. This may be nowhere near the middle
        // of the frequency distribution, but that's OK, we'll still get to the
        // right place.
        int index = (items.size() / 2);
        // The 'distance' is how far we will move (up or down) on the next search
        // of the table.
        int distance = index / 2;
        if (distance < 1) {
            distance = 1;
        }
        
        while (true) {
            ++numProbes;
            // Is the current item the one we want? If so, we're done.
            WeightedItem<String> item = items.get(index);
            if ((roll >= item.getCumulativeWeightLow()) &&
                (roll <= item.getCumulativeWeightHigh())) {
                    return(item.getItem());
            }
            
            // Nope, this isn't the one we want. If we're below the current item,
            // move down. Otherwise move up.
            if (roll < item.getCumulativeWeightLow()) {
                index = index - distance;
            } else {
                index = index + distance;
            }
            // The distance we move on each probe gets cut in half each time - but
            // never less than 1 space.
            distance /= 2;
            if (distance < 1) {
                distance = 1;
            }
        }
    }
    
    long getNumWordsGenerated() {
        return numWordsGenerated;
    }
    
    long getNumProbes() {
        return numProbes;
    }
    
    int getNumWords() {
        return items.size();
    }

}
