package com.salesforce.rcg.numbers.dice.testutils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.salesforce.rcg.numbers.dice.DiceExpression;

/** Utilities for better automated testing of dice expressions.
 * 
 * @author mpreslermarshall
 *
 */
public class DiceTestUtils {
    
    /** A special string value that tells testRolls not to check the output
     * of toString() on a DiceExpression. This is meant to be used where we
     * want to verify that the generated dice produce the correct range, but
     * we don't know exactly what dice will be used.
     */
    public static final String ANY_STRING_FORM = "*any*";

    /** Test a dice expression to confirm that it generates the expected values.
     * This will test:
     * - Minimum value across many rolls, using expected.minValue
     * - Maximum value across many rolls, using expected.maxValue
     * - Average value across many rolls, using expected.average
     * - (optionally) the toString() value of the DiceExpression, using
     *   expected.stringValue.
     *   
     * @param testme The DiceExpression to test
     * @param source The source string that was used to create the DiceExpression
     *     <tt>testme</tt>. This is not actually needed for the test, but it allows
     *     for much more readable exceptions when something goes wrong.
     * @param expected Expected values for the expression
     */
    public static void testRolls(DiceExpression testme, 
            String source,
            ExpectedDiceResult expected) {
        // First, test that the string form matches what was expected.
        // If the string form is wrong, there's very little chance that the
        // generated values will be right - and this test shows the source 
        // string the expression was created from.
        if (!expected.stringValue.equals(ANY_STRING_FORM)) {
            assertEquals("The string form of the dice expression created from " + source + " should match", expected.stringValue, testme.toString());
        }
        testRolls(testme, 
            expected.minValue,
            expected.maxValue,
            expected.average);
    }

    /** Do a bunch of rolls on a given die, and validate that we get
     * the expected values. This will validate the minimum, maximum, 
     * and average values generated over many rolls.
     * 
     * @param testme
     * @param expectedMin
     * @param expectedMax
     * @param expectedAverage
     */
    public static void testRolls(DiceExpression testme,
            int expectedMin, 
            int expectedMax, 
            double expectedAverage) {
        // Track the actual minimum and maximum values generated
        int actualMin = Integer.MAX_VALUE;
        int actualMax = Integer.MIN_VALUE;
        
        // We'll track the sum so we can compute the average value too
        double sum = 0.0;
        final int NUM_ROLLS = 5000;
        for (int i = 0; i < NUM_ROLLS; ++i) {
            int value = testme.roll();
            if (value < actualMin) {
                actualMin = value;
            }
            if (value > actualMax) {
                actualMax = value;
            }
            sum += value;
        }
        double actualAverage = sum / NUM_ROLLS;
        assertTrue("Roll of " + testme + " should not be less than " + expectedMin,
            actualMin >= expectedMin);
        assertTrue("Roll of " + testme + " should not be greater than " + expectedMax,
                actualMax <= expectedMax);
        
        // Now let's check that the average is as expected
        if (expectedAverage != Double.NaN) {
            // For most dice expressions, we want the actual average to be within
            // 5% of the expected average. But if it's a numeric range that goes 
            // below 0, then the average might be quite small (even 0!). In that
            // case, we'll allow the range to be +/- 5% of the range.
        double fuzz;
        if (expectedMin >= 1) {
            fuzz = expectedAverage * 0.05;
        } else {
            fuzz = (expectedMax - expectedMin) * 0.05;
        }
        assertEquals("Average roll of " + testme,
            expectedAverage,
            actualAverage,
            fuzz); // The actual average should be within 5% of the expected
        }
    }

}
