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
 * @author mpreslermarshall
 *
 */
public class DiceFactoryTest {
    /**
     * Our table of test values. The syntax is:
     * expression-to-parse "|" expected-values
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
        
        // Min-max ranges 
        "1-6/1,6,3.5,1d6",
        "1- 6/1,6,3.5,1d6",
        "1 -6/1,6,3.5,1d6",
        "1 - 6/1,6,3.5,1d6",
        "2-12/2,12,7," + DiceTestUtils.ANY_STRING_FORM,
        "0 - 9/0,9,4.5," + DiceTestUtils.ANY_STRING_FORM,
        "2-9/2,9,5.5," + DiceTestUtils.ANY_STRING_FORM,
        
        // A range where min=max should produce a constant expression
        "4-4/4,4,4," + DiceTestUtils.ANY_STRING_FORM,
        
        // If the range values are swapped (max first), they should get
        // automatically un-swapped
        "8-1/1,8,4.5,1d8",
       
        
        // Min-max ranges with negative numbers
        "-3 - 7/-3,7,2," + DiceTestUtils.ANY_STRING_FORM,
        "-4 - 4/-4,4,0," + DiceTestUtils.ANY_STRING_FORM,  
    };
    
    /** Input string which will not parse correctly. Each of these should result in
     * some sort of an exception.
     */
    
    /**
     * Execute the table-driven test for the parser, testing that each
     * value produces a DiceExpression that has the expected characteristics.
     */
    @Test
    public void tableDrivenTest() {
        for (String testValue: GOOD_TEST_VALUES) {
            String components[] = testValue.split("/");
            String source = components[0];
            ExpectedDiceResult expected = new ExpectedDiceResult(components[1]);
            
            DiceFactory factory = new DiceFactory();
            DiceExpression expression = factory.create(source);
            
            DiceTestUtils.testRolls(expression, source, expected);
        }
        System.out.println("Table-driven dice factory test ran " + GOOD_TEST_VALUES.length + " values.");
    }

}
