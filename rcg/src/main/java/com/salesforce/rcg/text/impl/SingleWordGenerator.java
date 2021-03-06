package com.salesforce.rcg.text.impl;

import java.util.Random;

import com.salesforce.rcg.text.WordGeneratorType;

/** The SingleWordGenerator is a trivial implementation of the
 * WordGenerator interface: it always returns the same string from
 * the generateWord method.
 * 
 * @author mpreslermarshall
 *
 */
public class SingleWordGenerator extends AbstractWordGenerator {
    protected String word;
    
    /** Create a named single-word generator without specifying the word 
     * it will generate. You must call {@link #setWord <tt>setWord</tt>}
     * before using it.
     * 
     * @param name
     */
    public SingleWordGenerator(String name) {
        super(name);
    }
    
    public SingleWordGenerator(String name, String word) {
        super(name);
        this.word = word;
    }
    
    public WordGeneratorType getType() {
        return(WordGeneratorType.SINGLE_WORD);
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

    /** Set the word to be generated by this generator. This can only be used on
     * instances that were created without a source word. If the source word has
     * already been set, this will generate an exception.
     * 
     * @param desiredWord
     */
    public void setWord(String desiredWord) {
        if (null == word) {
            word = desiredWord;
        } else {
            throw new IllegalStateException("Can't set the word to be generated by this generator - it has already been set!");
        }
    }
    
    public String toString() {
        return "[SingleWordGenerator '" + getName() + "' returning '" + generateWord() + "']";
    }

}
