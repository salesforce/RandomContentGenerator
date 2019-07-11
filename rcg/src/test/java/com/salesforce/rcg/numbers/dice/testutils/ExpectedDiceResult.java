package com.salesforce.rcg.numbers.dice.testutils;

/** A container for the values expected from constructing a dice
 * expression.
 * 
 * @author mpreslermarshall
 *
 */
public class ExpectedDiceResult {
    /** Expected minimum value the expression can produce. */
    public final int minValue;

    /** Expected maximum value the expression can produce. */
    public final int maxValue;
    
    /** The expected average value that the expression will produce. */
    public final double average;
    
    /** The expected string that should be produced by calling 
     * toString() on the expression. This is a way of confirming
     * that the parsed expression has the expected number of dice,
     * sides, and so on.
     */
    public final String stringValue;
       
    public ExpectedDiceResult(int minValue,
        int maxValue,
        double average,
        String stringValue) {
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.average = average;
        this.stringValue = stringValue;
    }
    
    public ExpectedDiceResult(String source) {
        String components[] = source.split(",");
        minValue = Integer.parseInt(components[0]);
        maxValue = Integer.parseInt(components[1]);
        average = Double.parseDouble(components[2]);
        stringValue = components[3].trim();
    }
}
