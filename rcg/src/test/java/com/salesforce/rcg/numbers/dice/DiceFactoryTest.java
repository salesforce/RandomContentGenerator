package com.salesforce.rcg.numbers.dice;

import org.junit.Test;

import com.salesforce.rcg.numbers.dice.testutils.DiceTestUtils;
import com.salesforce.rcg.numbers.dice.testutils.ExpectedDiceResult;

/**
 * A table-driven test for the DiceFactory. 
 * 
 * This contains a table of strings to pass in to the DiceFactory, along with 
 * a representation of the expected values that should result from parsing each
 * string. See <tt>TEST_VALUES</tt> for more details on how this works. 
 * 
 * @author Martin Presler-Marshall
 *
 */
public class DiceFactoryTest {
    /**
     * Our table of test values. The syntax is:
     * expression-to-parse "/" expected-values
     *   -  expression-to-parse is the exact string that'll be passed in to the
     *      <tt>create</tt> method on a <tt>DiceFactory</tt> instance.
     *   - expected-values is a string representing the values we expect to get.
     *     This will contain the expected minimum, maximum, and average values from
     *     generating values from the range, plus the expected string representation
     *     of the dice expression. See <tt>ExpectedDiceResult</tt> for the syntax
     *     of this part.
     */
    public static final String[] GOOD_TEST_VALUES = {
        // Simple FRP expressions
        "d8/1,8,4.5,1d8",
        "3d6/3,18,10.5,3d6",
        
        // Whitespace
        " 3 d 4/3,12,7.5,3d4",
        "3d 4/3,12,7.5,3d4",
        
        // Add/subtract values
        "3d6 + 2/5,20,12.5,3d6 + 2",
        "3d6 - 2/1,16,8.5,3d6 - 2",
        "3d6 -2/1,16,8.5,3d6 - 2",
        "3d6-2/1,16,8.5,3d6 - 2",
        "2d8+0/2,16,9,2d8",
        
        // Multipliers
        "d12 * 4/4,48,26,1d12 * 4",
        "2d10 * 4/8,80,44,2d10 * 4",
        "1d5+2*3/9,21,15,1d5 + 2 * 3",
        "3d2*0/0,0,0,3d2 * 0",
        
        // Constant values
        "0/0,0,0,1d0",
        "5/5,5,5,1d0 + 5",
        "-3/-3,-3,-3,1d0 - 3",
        "2*4/8,8,8,1d0 + 2 * 4",
        
        // Min-max ranges 
        "1-6/1,6,3.5,1d6",
        "1- 6/1,6,3.5,1d6",
        "1 -6/1,6,3.5,1d6",
        "1 - 6/1,6,3.5,1d6",
        "2-12/2,12,7," + DiceTestUtils.ANY_STRING_FORM,
        "0 - 9/0,9,4.5," + DiceTestUtils.ANY_STRING_FORM,
        "2-9/2,9,5.5," + DiceTestUtils.ANY_STRING_FORM,
        "1-10*4/4,40,22,1d10 * 4",
        
        // A range where min=max should produce a constant expression
        "4-4/4,4,4," + DiceTestUtils.ANY_STRING_FORM,
        
        // If the range values are swapped (max first), they should get
        // automatically un-swapped
        "8-1/1,8,4.5,1d8",
        
        // Min-max ranges with negative numbers
        "-3 - 7/-3,7,2," + DiceTestUtils.ANY_STRING_FORM,
        "-4 - 4/-4,4,0," + DiceTestUtils.ANY_STRING_FORM,  
        
        // Leading and trailing whitespace are OK
        " 2d12/2,24,13,2d12",
        "6d6   /6,36,21,6d6",
        
        // The 'd' in FRP dice expressions is case-insensitive.
        "1D5/1,5,3,1d5",
        "D8/1,8,4.5,1d8",

        // The "chance of generating a result" prefix
        "chance: 25%, 1d8/0,8,1.125,1d8 with a 25% chance of generating a non-zero result",
        "chance: 100%,2d3/2,6,4,2d3", // 100% chance is just the normal behavior
        "chance: 150%,3d4/3,12,7.5,3d4", // More than a 100% chance is just a 100% chance
        "chance: 0%,1d12/0,0,0,1d12 with a 0% chance of generating a non-zero result",
        
        // Composite dice
        "1d6 & 1d8/2,14,8.0,1d6 and 1d8",
        "2d4 and 1d12/3,20,11.5,2d4 and 1d12",
        "6d6+1 AND 1d3/8,40,24.0,6d6 + 1 and 1d3",
        // More than two dice is OK.
        "1d4 & 1d6 and 1d8/3,18,10.5,1d4 and 1d6 and 1d8",
        
        // Composite dice can use prefixes as well
        "chance: 25%, 1d8 & 1d10/1,18,6.625,1d8 with a 25% chance of generating a non-zero result and 1d10",
        "chance:100%, 2d9 & chance:75%, 3d7/2,39,19.0,2d9 and 3d7 with a 75% chance of generating a non-zero result",
    };
    
    /** Input string which will not parse correctly. Each of these should result in
     * some sort of an exception.
     */
    
    /**
     * Execute the table-driven test for the parser, testing that each
     * value produces a DiceExpression that has the expected characteristics.
     */
    @Test
    public void tableDrivenTest() throws Exception {
        for (String testValue: GOOD_TEST_VALUES) {
            if (testValue.indexOf('/') == -1) {
                throw new IllegalArgumentException("Syntax error in test value '" 
                        + testValue + "' - no '/' separating the input string from the expected values.");
            }
            String components[] = testValue.split("/");
            String source = components[0];
            try {
                ExpectedDiceResult expected = new ExpectedDiceResult(components[1]);
            
                DiceFactory factory = new DiceFactory();
                DiceExpression expression = factory.create(source);
            
                DiceTestUtils.testRolls(expression, source, expected);
            } catch (Throwable t) {
                throw new Exception("Error while processing dice expression '" + source + "'.", t);
            }
                        
        }
        System.out.println("Table-driven dice factory test ran " + GOOD_TEST_VALUES.length + " values successfully.");
    }

}
