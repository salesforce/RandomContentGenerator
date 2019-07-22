package com.salesforce.rcg.text.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WeightedItemTest {
    
    @Test
    public void stringTest() {
        WeightedItem<String> item = new WeightedItem<>("Oh hi");
        assertEquals("Oh hi", item.getItem());
        
        // We assigned no weights, so they should all be 0's
        assertEquals(0.0, item.getWeight(), 0.01);
        assertEquals(0.0, item.getCumulativeWeightLow(), 0.01);
        assertEquals(0.0, item.getCumulativeWeightHigh(), 0.01);
    }
    
    @Test
    public void integerTest() {
        WeightedItem<Integer> item = new WeightedItem<>(Integer.valueOf(42), 3.1, 6.8, 9.9);
        assertEquals(Integer.valueOf(42), item.getItem());
        
        // Let's check the weights too
        assertEquals(3.1, item.getWeight(), 0.01);
        assertEquals(6.8, item.getCumulativeWeightLow(), 0.01);
        assertEquals(9.9, item.getCumulativeWeightHigh(), 0.01);
    }

}
