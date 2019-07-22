package com.salesforce.rcg.text;

import java.util.HashMap;
import java.util.Map;

/**
 * A WordGeneratorRegistry provides registration and lookup functions for 
 * {@link <tt>WordGenerator</tt>s}. 
 * 
 * This class is threadsafe. For most purposes, it's expected that an application
 * will only need a single instance of this class.
 * 
 * @author mpreslermarshall
 *
 */
public class WordGeneratorRegistry {
    public WordGeneratorRegistry STANDARD = new WordGeneratorRegistry().setupStandard();
    
    protected Map<String, WordGenerator> generators = new HashMap<>();
    
    /** Create a new, empty word generator registry.
     * 
     */
    public WordGeneratorRegistry() {
    }
    
    /**
     * Setup the word generator registry with the standard set of word generators.
     * 
     * @return
     */
    public WordGeneratorRegistry setupStandard() {
        return(this);
    }
}
