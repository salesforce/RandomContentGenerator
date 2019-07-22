package com.salesforce.rcg.text.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingleWordGeneratorTest {
    @Test
    public void basicTest() {
        SingleWordGenerator example = new SingleWordGenerator("example's name", "my word!");
        assertEquals("example's name", example.getName());
        assertEquals("my word!", example.generateWord());
        assertTrue(example.toString().indexOf("example") >= 0);
    }
}
