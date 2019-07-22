package com.salesforce.rcg.text.impl;

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
    
    public String toString() {
        return "[SingleWordGenerator '" + getName() + "' returning '" + generateWord() + "']";
    }

}
