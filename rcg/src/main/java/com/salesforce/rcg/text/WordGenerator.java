package com.salesforce.rcg.text;

/** A word generator creates random words based on some algorithm. The details
 * of the words it'll generate and the underlying algorithm are entirely up to
 * the implementation of the word generator.
 * 
 * For most purposes, you won't use WordGenerators directly. You'll use one of
 * the higher-level classes for generating text with specific characteristics, or 
 * a specialized generator meant for a specific purpose (such as generating 
 * an address).
 * 
 * @author mpreslermarshall
 *
 */
public interface WordGenerator {
    public String generateWord();
    
    public String getName();
    
    // TBD: what other metadata can word generators publish?
}
