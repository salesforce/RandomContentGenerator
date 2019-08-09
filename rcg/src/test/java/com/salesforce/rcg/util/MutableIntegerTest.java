package com.salesforce.rcg.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class MutableIntegerTest {
    @Test
    public void constructorTest0() {
        MutableInteger mi = new MutableInteger();
        assertEquals(0, mi.intValue());
    }
    
    @Test
    public void constructorTest1() {
        MutableInteger mi1 = new MutableInteger(1);
        MutableInteger mi5 = new MutableInteger(5); // Yes, this is where Bond works
        MutableInteger mim7 = new MutableInteger(-7);
        
        assertEquals(1, mi1.intValue());
        assertEquals(5, mi5.intValue());
        assertEquals(-7, mim7.intValue());
    }
    
    @Test
    public void mutationTest() {
        MutableInteger mi = new MutableInteger(42);
        assertEquals(42, mi.intValue());
        
        mi.increment();
        assertEquals(43, mi.intValue());
        
        mi.increment(5);
        assertEquals(48, mi.intValue());
        
        mi.decrement();
        assertEquals(47, mi.intValue());
    }
        
    /** Execute random operations on a MutableInteger, tracking the expected
     * value with a real integer.
     * 
     */
    @Test
    public void randomTest() {
        Random r = new Random();
        
        MutableInteger m = new MutableInteger();
        int expected = 0;
        
        for (int i = 0; i < 100000; ++i) {
            int randValue = r.nextInt(1000);
            
            switch(r.nextInt(4)) {
            case 0:
                m.increment();
                ++expected;
                break;
                
            case 1:    
                m.increment(randValue);
                expected += randValue;
                break;
                
            case 2:
                m.decrement();
                --expected;
                break;
                
            case 3:
                m.decrement(randValue);
                expected -= randValue;
                break;
                
            default: 
                break;
            }
            assertEquals(expected, m.intValue());
        }
    }
    
    @Test
    public void transformationTest() {
        MutableInteger mi = new MutableInteger(12);
        
        assertEquals(12, mi.intValue());
        assertEquals(12L, mi.longValue());
        assertEquals(12.0f, mi.floatValue(), 0.01);
        assertEquals(12.0, mi.doubleValue(), 0.01);
    }

}
