package com.salesforce.rcg.text.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WeightedItemTest {
    
    /** Test a simple WeightedItem that contains a String. 
     * This will set no weights, so we verify that they're all zero.
     */
    @Test
    public void stringTest() {
        WeightedItem<String> item = new WeightedItem<>("Oh hi");
        assertEquals("Oh hi", item.getItem());
        
        // We assigned no weights, so they should all be 0's
        assertEquals(0.0, item.getWeight(), 0.01);
        assertEquals(0.0, item.getCumulativeWeightLow(), 0.01);
        assertEquals(0.0, item.getCumulativeWeightHigh(), 0.01);
    }

    /** Test a simple WeightedItem containing an Integer.
     * We will assign weights on the constructor, and verify those.
     */
    @Test
    public void integerTest() {
        WeightedItem<Integer> item = new WeightedItem<>(Integer.valueOf(42), 3.1, 6.8, 9.9);
        assertEquals(Integer.valueOf(42), item.getItem());
        
        // Let's check the weights too
        assertEquals(3.1, item.getWeight(), 0.01);
        assertEquals(6.8, item.getCumulativeWeightLow(), 0.01);
        assertEquals(9.9, item.getCumulativeWeightHigh(), 0.01);
    }

    /** Test that the setters for setting the weight behave as expected.
     * 
     */
    @Test
    public void setWeightTest() {
        WeightedItem<Long> item = new WeightedItem<>(Long.valueOf(314159));
        // Use setters to set up the weights
        item.setWeight(3.6);
        
        // Setting the weight makes no change to the lower cumulative weight,
        // but it does update the upper.
        assertEquals(0.0, item.getCumulativeWeightLow(), 0.01);
        assertEquals(3.6, item.getCumulativeWeightHigh(), 0.01);
        
        // Now set the lower cumulative weight, and poof! the upper cumulative
        // weight follows immediately.
        item.setCumulativeWeightLow(2.9);
        //item.setCumulativeWeightHigh(6.5);

        // Now let's check the weights
        assertEquals(3.6, item.getWeight(), 0.01);
        assertEquals(2.9, item.getCumulativeWeightLow(), 0.01);
        assertEquals(6.5, item.getCumulativeWeightHigh(), 0.01);
    }
}
