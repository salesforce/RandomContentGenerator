package com.salesforce.rcg.text.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

import java.util.Random;

import org.junit.Test;

import com.salesforce.rcg.util.CounterMap;

public class WeightedWordGeneratorTest {
    /** A WeightedWordGenerator that has had no items added
     * should return null when generating a word.
     */
    @Test
    public void emptyTest() {
        WordGeneratorTestUtils.testEmpty(new WeightedWordGenerator("emptyTest"), "emptyTest");
    }
    
    @Test
    public void setRngTest() {
        WordGeneratorTestUtils.testSetRng(new WeightedWordGenerator("rngTest"));
    }
    
     
    @Test
    public void rngConstructorTest() {
        Random r2 = new Random();
        WeightedWordGenerator testMeToo = new WeightedWordGenerator("testMeToo", r2);
        assertEquals(r2, testMeToo.getRng());
    }
    
    /** If the total weight of items in the generator is zero, generateWord 
     * should return null.
     */
    @Test
    public void weightZeroTest() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("weightZeroTest");
        testMe.addWord("zero", 0.0);
        testMe.addWord("zip", 0.0);
        testMe.addWord("nuffing", 0.0);
        assertEquals("weightZeroTest", testMe.getName());
        assertNull(testMe.generateWord());
    }
    
    /** Negative weights are a no-no.
     * 
     */
    @Test(expected=java.lang.IllegalArgumentException.class)
    public void negativeWeightsBad() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("negativeWeightsBad");
        testMe.addWord("impossible", -1.0);
        assertFalse(true);
    }
    
    /** If there's a single item, we ought to generate just that. 
     * 
     */
    @Test
    public void singleItemTest() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("singleItemTest");
        testMe.addWord("positive");
        for (int i = 0; i < 100; ++i) {
            assertEquals("positive", testMe.generateWord());
        }
    }
    
    /** Test a word generator with three items with different 
     * weights on each
     */
    @Test
    public void threeItemTest() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("threeItemTest");
        testMe.addWord("low", 5.0);
        testMe.addWord("medium", 25.0);
        testMe.addWord("high", 70.0);   
        
        CounterMap<String> expectedRelativeWeights = new CounterMap<>();
        expectedRelativeWeights.add("low", 5);
        expectedRelativeWeights.add("medium", 25);
        expectedRelativeWeights.add("high", 70);
        
        WordGeneratorTestUtils.testWeightedGenerator(testMe, expectedRelativeWeights);
    }
    
    /** A larger word generator test that has a broader distribution of weights.
     * One item will be generated only 0.1% of the time and another will be
     * generated 50% of the time.
     */
    @Test
    public void sixItemTest() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("sixItemTest");
        testMe.addWord("zero", 500.0);
        testMe.addWord("one", 1.0);
        testMe.addWord("two", 9.0);
        testMe.addWord("three", 90.0);
        testMe.addWord("four", 200.0);
        testMe.addWord("five", 200.0);
        
        CounterMap<String> expected = new CounterMap<>();
        expected.add("one", 1);
        expected.add("two", 9);
        expected.add("three", 90);
        expected.add("four", 200);
        expected.add("five", 200);
        expected.add("zero", 500);
        
        WordGeneratorTestUtils.testWeightedGenerator(testMe, expected);
    }

    /** Test a word generator that has multiple entries that each have zero
     * weights. The items which are in the generator all have equal weights.
     */
    @Test
    public void withZeroWeightTest() {
        WeightedWordGenerator testMe = new WeightedWordGenerator("withZeroWeight");
        testMe.addWord("nope", 0.0);
        testMe.addWord("yes", 27.0); // Arbitrary, but all synonyms of "yes" have the same weight
        testMe.addWord("yep", 27.0);
        testMe.addWord("ja", 27.0);
        testMe.addWord("oui", 27.0);
        testMe.addWord("si", 27.0);
        testMe.addWord("no", 0.0);
        testMe.addWord("nein", 0.0);
        
        CounterMap<String> expected = new CounterMap<>();
        // Only add the words we expect to show up.
        expected.add("yes", 1);
        expected.add("yep", 1);
        expected.add("ja", 1);
        expected.add("oui", 1);
        expected.add("si", 1);
        
        WordGeneratorTestUtils.testWeightedGenerator(testMe, expected);
    }
    
    @Test
    public void generatedTest() {
        int weight = 5;
        
        WeightedWordGenerator generator = new WeightedWordGenerator("generated");
        CounterMap<String> expected = new CounterMap<>(); 
        
        for (int i = 0; i < 100; ++i) {
            String word = "word-" + i;
            generator.addWord(word, weight);
            expected.add(word, weight);
            
            ++weight;
        }
        
        WordGeneratorTestUtils.testWeightedGenerator(generator, expected);
    }
    
}
