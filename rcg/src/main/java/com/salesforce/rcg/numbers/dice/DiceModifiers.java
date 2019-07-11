package com.salesforce.rcg.numbers.dice;

/** Various modifiers that can be applied when creating dice. */
public class DiceModifiers {
    /** "Loaded dice" - this gives the ability to request that dice generate values
     * with a non-uniform distribution of values. Note that these values merely 
     * specify that the die should be non-uniform; it does nothing do specify 
     * _how_ non-uniform.
     * 
     * @author mpreslermarshall
     */
    public enum Loading {
        /** Dice that are LOADED_HIGH are more likely to generate higher
         * values than 
         */
        LOADED_HIGH,
        UNLOADED,
        LOADED_LOW
    };

}
