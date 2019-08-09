package com.salesforce.rcg.text.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.salesforce.rcg.text.ExtensibleWordGenerator;

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

    /** Add a word to this generator with the default weight (1).
     * 
     * @param word The word to add
     */
    public synchronized void addWord(String word) {
        items.add(word);
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
