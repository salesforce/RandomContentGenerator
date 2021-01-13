package com.salesforce.rcg.text;

import com.salesforce.rcg.text.impl.SingleWordGenerator;
import com.salesforce.rcg.text.impl.UnweightedWordGenerator;
import com.salesforce.rcg.text.impl.WeightedWordGenerator;

public enum WordGeneratorType {
    /** A word generator which always generates a single word.
     * Reference implementation: {@link com.salesforce.rcg.text.impl.SingleWordGenerator 
     * <tt>com.salesforce.rcg.text.impl.SingleWordGenerator</tt>}.
     */
    SINGLE_WORD("single-word"),
    UNWEIGHTED("unweighted"),
    WEIGHTED("weighted");
    
    protected final String name;
    
    private WordGeneratorType(String name) {
        this.name = name;
    }
    
    public String getName() {
        return(name);
    }
    
    /** The canonical mapping from word generator type names to 
     * their enum values. This covers both the standard names for
     * each value as well as some handy aliases defined for use in
     * word files.
     * 
     * @param sourceType
     * @return
     */
    public static WordGeneratorType from(String sourceType) {
        // Start with the predefined types.
        for (WordGeneratorType t: WordGeneratorType.values()) {
            if (t.getName().equalsIgnoreCase(sourceType)) {
                return(t);
            }
        }
        
        // Aliases
        if ("defined-weights".equalsIgnoreCase(sourceType)) {
            return(WEIGHTED);
        }

        // Nope, we don't recognize this.
        throw new IllegalArgumentException("Unrecognized word generator type: " + sourceType);
    }
    
    public static WordGenerator create(WordGeneratorType type, String name) {
        if (null == name) {
            name = "unknown";
        }
        if (type == UNWEIGHTED) {
            return new UnweightedWordGenerator(name);
        } else if (type == WEIGHTED) {
            return new WeightedWordGenerator(name);
        } else if (type == SINGLE_WORD) {
            return new SingleWordGenerator(name);
        } else {
            throw new IllegalStateException("The create() method needs to be extended to recognize type " + type + "!");
        }
            
    }
}
