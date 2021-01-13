package com.salesforce.rcg.numbers.dice.impl;

import static com.salesforce.rcg.numbers.dice.testutils.DiceTestUtils.testRolls;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class CompositeDieTest {
    
    /** A ComositeDie that is created without any underlying dice should be empty!
     * 
     */
    @Test
    public void emptyTest() {
        CompositeDie cd = new CompositeDie();
        
        // It should have no dice in it.
        assertEquals(0, cd.size());
        
        // No matter how many times we roll it, it should return 0.
        testRolls(cd, 0, 0, 0.0);
    }

    @Test
    public void singleDieTest() {
        // Create several composite dice, each of which contains a single underlying die
        CompositeDie cd1 = new CompositeDie(new SimpleDie(6));
        CompositeDie cd2 = new CompositeDie();
        cd2.addDie(new SimpleDie(6));
        
        // Each die contains one underlying die.
        assertEquals(1, cd1.size());
        assertEquals(1, cd2.size());
        
        // Each of these is effectively 1d6, which has an average value of 3.5.
        testRolls(cd1, 1, 6, 3.5);
        testRolls(cd2, 1, 6, 3.5);
    }
    
    @Test
    public void multiDieTest() {
        // Create a bunch of dice
        SimpleDie d6 = new SimpleDie(6);
        SimpleDie d8 = new SimpleDie(8);
        SimpleDie twoD12 = new SimpleDie(2, 12);
        SimpleDie threeD4Plus1 = new SimpleDie(3, 4, 1);
                
        // Let's make sure these dice do what we think they do.
        testRolls(d6, 1, 6, 3.5);
        testRolls(d8, 1, 8, 4.5);
        testRolls(twoD12, 2, 24, 13.0);
        testRolls(threeD4Plus1, 4, 13, 8.5);
        
        // Now build some composites
        CompositeDie cd1 = new CompositeDie();
        cd1.addDie(d6);
        cd1.addDie(d8);
        // We can chain together adding dice!
        cd1.addDie(twoD12).addDie(threeD4Plus1);
        List<SimpleDie> allTheDice = Arrays.asList(new SimpleDie[] {d6, d8, twoD12, threeD4Plus1});
        CompositeDie cd2 = new CompositeDie(allTheDice);
        CompositeDie cd3 = new CompositeDie(d6, d8, twoD12, threeD4Plus1);
        
        // Verify that the dice contain what we expect
        testRolls(cd1, (1+1+2+4), (6+8+24+13), (3.5+4.5+13.0+8.5));
        testRolls(cd2, (1+1+2+4), (6+8+24+13), (3.5+4.5+13.0+8.5));
        testRolls(cd3, (1+1+2+4), (6+8+24+13), (3.5+4.5+13.0+8.5));
    }

    @Test
    public void addingToCompositeTest() {
        SimpleDie d10 = new SimpleDie(10);
        SimpleDie d12 = new SimpleDie(12);
        SimpleDie d20 = new SimpleDie(20);
        CompositeDie cd = new CompositeDie();
        
        // There are no dice in the composite die now.
        testRolls(cd, 0, 0, 0.0);
        
        // Add in the D10.
        cd.addDie(d10);
        testRolls(cd, 1, 10, 5.5);
        
        // Now the D12 as well
        cd.addDie(d12);
        testRolls(cd, 2, 22, 12.0);
        
        // We can add the D10 twice, and that gets added to the result.
        cd.addDie(d10);
        testRolls(cd, 3, 32, 17.5);
        
        // And then the D20 as well.
        cd.addDie(d20);
        testRolls(cd, 4, 52, 28.0);
    }
    
    @Test
    public void toStringEmptyTest() {
        CompositeDie cd = new CompositeDie();
        String stringForm = cd.toString();
        
        assertNotNull(stringForm);
    }

    @Test
    public void toStringTest() {
        SimpleDie d10 = new SimpleDie(10);
        SimpleDie d12 = new SimpleDie(12);
        SimpleDie twoD8 = new SimpleDie(2, 8);
        
        CompositeDie cd = new CompositeDie(d10, d12, twoD8);
        String stringForm = cd.toString();
        
        // The stringified form should contain each of the underlying dice.
        assertNotNull(stringForm);
        assertNotEquals(-1, stringForm.indexOf("1d10"));
        assertNotEquals(-1, stringForm.indexOf("1d12"));
        assertNotEquals(-1, stringForm.indexOf("2d8"));
    }

}
