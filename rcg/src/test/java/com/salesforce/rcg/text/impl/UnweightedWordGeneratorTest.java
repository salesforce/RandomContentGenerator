package com.salesforce.rcg.text.impl;

import org.junit.Test;

import com.salesforce.rcg.util.CounterMap;

public class UnweightedWordGeneratorTest {
    /** Test an empty word generator (no items added to it).
     */
    @Test
    public void emptyTest() {
        WordGeneratorTestUtils.testEmpty(new WeightedWordGenerator("emptyTest-unweighted"), "emptyTest-unweighted");
    }
    
    @Test
    public void setRngTest() {
        WordGeneratorTestUtils.testSetRng(new UnweightedWordGenerator("rngTest"));
    }


    /** A test with various numbers of words in the list we can generate.
     * This will confirm that we get words with approximately-equal 
     * distributions regardless of whether we have a few words or many.
     */
    @Test
    public void rampingTest() {
        for (int size = 1; size < 10000; size *= 2) {
            // Build an unweighted word generator, and a CounterMap to contain
            // the expected frequencies.
            UnweightedWordGenerator generator = new UnweightedWordGenerator("generated");
            CounterMap<String> expected = new CounterMap<>(); 

            // Add the requested number of words to the generator, and also to the
            // expected frequency table.
            for (int i = 0; i < size; ++i) {
                String word = "word-" + i;
                generator.addWord(word);
                expected.add(word, 1);
            }
        
            WordGeneratorTestUtils.testWeightedGenerator(generator, expected);
        }
    }


}
