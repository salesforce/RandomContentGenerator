package com.salesforce.rcg.text.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.salesforce.rcg.text.ExtensibleWordGenerator;
import com.salesforce.rcg.text.WordGeneratorType;

public class UnweightedWordGenerator 
        extends AbstractRandomWordGenerator 
        implements ExtensibleWordGenerator {
    /** All of the words in this generator. */
    protected List<String> items = new ArrayList<>();
    
    public UnweightedWordGenerator() {
        super("anonymous");
    }
    
    public UnweightedWordGenerator(String name) {
        super(name);
    }
    
    public UnweightedWordGenerator(String name, Random rng) {
        super(name, rng);
    }
    
    public String toString() {
        return "UnweightedWordGenerator '" + name + "'";
    }
    
    public WordGeneratorType getType() {
        return(WordGeneratorType.UNWEIGHTED);
    }

    /** Add a word to this generator.
     * 
     * @param word The word to add
     */
    public synchronized void addWord(String word) {
        items.add(word);
    }
    
    /** Add a word to this generator. The specified weight will be ignored, as
     * this generator doesn't understand the concept of weights associated with 
     * the items.
     */
    public void addWord(String word, double weight) {
        addWord(word);
    }
    
    /** Generate a random word from our list.
     * 
     */
    @Override
    public String generateWord() {
        // Check boundary conditions
        if (items.size() == 0) {
            return(null);
        }
        
        int roll = rng.nextInt(items.size());
        
        return(items.get(roll));
    }
    
}
