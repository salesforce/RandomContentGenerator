package com.salesforce.rcg.util;

/** A wrapper for an integer value. Unlike java.lang.Integer, this value
 * is mutable - it can change over time.
 * 
 * @author mpreslermarshall
 *
 */
public class MutableInteger extends Number {
    private static final long serialVersionUID = 1L;
    
    protected int current = 0;
    
    public MutableInteger() {
    }
    
    public MutableInteger(int start) {
        current = start;
    }

    @Override
    public int intValue() {
        return(current);
    }

    @Override
    public long longValue() {
        return((long) current);
    }

    @Override
    public float floatValue() {
        return((float) current);
    }

    @Override
    public double doubleValue() {
        return((double) current);
    }
    
    public void increment() {
        ++current;
    }
    
    public void increment(int amount) {
        current += amount;
    }
    
    public void decrement() {
        --current;
    }
    
    public void decrement(int amount) {
        current -= amount;
    }

}
