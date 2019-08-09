package com.salesforce.rcg.text.impl;

import java.util.Random;

/** The SingleWordGenerator is a trivial implementation of the
 * WordGenerator interface: it always returns the same string from
 * the generateWord method.
 * 
 * @author mpreslermarshall
 *
 */
public class SingleWordGenerator extends AbstractWordGenerator {
    protected String word;
    
    public SingleWordGenerator(String name, String word) {
        super(name);
        this.word = word;
    }

    @Override
    public String generateWord() {
        return(word);        
    }
    
    public Random getRng() {
        return(null);
    }

    /** Set the random number generator used by this word generator.
     * Actually, this does nothing, since we don't use any randomness.
     * But the method is provided so that we implement the WordGenerator interface.
     */
    public SingleWordGenerator setRng(Random rng) {
        return(this);
    }

    
    public String toString() {
        return "[SingleWordGenerator '" + getName() + "' returning '" + generateWord() + "']";
    }

}
