package com.salesforce.rcg.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

public class CounterMapTest {

    @Test
    public void emptyTest() {
        CounterMap<String> cm = new CounterMap<>();
        
        // No entries, so the total value is 0
        assertEquals(0, cm.getTotal());
        
        // And the returned value for any key will be 0.
        assertEquals(0, cm.get("one"));
        assertEquals(0, cm.get("fish"));
    }
    
    @Test
    public void simpleTest() {
        CounterMap<String> cm = new CounterMap<>();
        
        // Add some entries
        cm.add("the");
        cm.add("lorax", 4);
        cm.add("the");
        cm.add("butter", 9);
        cm.add("battle", 6);
        cm.add("red");
        cm.add("fish", -4);
        
        // The total should be right
        assertEquals(18, cm.getTotal());
        
        // Check the values
        assertEquals(9, cm.get("butter"));
        assertEquals(6, cm.get("battle"));
        assertEquals(4, cm.get("lorax"));
        assertEquals(2, cm.get("the"));
        assertEquals(1, cm.get("red"));
        assertEquals(-4, cm.get("fish"));
        
        // The sorted key list will be sorted by the values,
        // highest first
        assertEquals(
            Arrays.asList(new String[] {"butter", "battle", "lorax", "the", "red", "fish"}),
            cm.getSortedKeys());
    }
}
