package com.salesforce.rcg.numbers.dice.impl;

import static com.salesforce.rcg.numbers.dice.testutils.DiceTestUtils.testRolls;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Random;

import org.junit.Test;


public class SimpleDieTest {

    /** Test the constructors for the SimpleDie class. */
    @Test
    public void constructorTestSidesOnly() {
        SimpleDie instance = new SimpleDie(17);
        assertEquals(17, instance.getNumSides());
        assertNotNull("The die should have a random number generator", instance.getRng());
        assertEquals("1d17", instance.toString());
        assertEquals("Adder should default to zero", 0, instance.getAdder());
        assertEquals("Multiplier should default to one", 1, instance.getMultiplier());

        testRolls(instance, 1, 17, (1.0 + 17.0) / 2.0);
    }
    
    @Test
    public void constructorTestSidesAndRandom() {
        Random rng = new Random();
        SimpleDie instance2 = new SimpleDie(12, rng);
        assertEquals(12, instance2.getNumSides());
        assertEquals(rng, instance2.rng);
        assertEquals("1d12", instance2.toString());
        testRolls(instance2, 1, 12, (1.0 + 12.0) / 2.0);
    }
    
    @Test
    public void constructorTestDiceAndSides() {
        SimpleDie instance = new SimpleDie(4, 7);
        assertEquals("Number of dice is first argument", 4, instance.getNumDice());
        assertEquals("Number of sides is second argument", 7, instance.getNumSides());
        assertEquals("Adder should default to zero", 0, instance.getAdder());
        assertEquals("Multiplier should default to one", 1, instance.getMultiplier());
        assertNotNull("The die should have a random number generator", instance.getRng());
        assertEquals("4d7", instance.toString());
        // A 7-sided die produces an average roll of 4, and we have 4 of 'em,
        // thus we expect an average roll of 16.
        testRolls(instance, 4, 4*7, (4.0 * 4.0)); 
    }

    @Test
    public void constructorTestDiceSidesAdder() {
        SimpleDie instance = new SimpleDie(3, 8, -2);
        assertEquals("Number of dice is first argument", 3, instance.getNumDice());
        assertEquals("Number of sides is second argument", 8, instance.getNumSides());
        assertEquals("Adder is third argument", -2, instance.getAdder());
        assertNotNull("The die should have a random number generator", instance.getRng());
        assertEquals("3d8 - 2", instance.toString());
        testRolls(instance, 1, 22, (4.5 * 3) - 2.0);
        
        SimpleDie instance2 = new SimpleDie(5, 12, 6);
        assertEquals("Number of dice is first argument", 5, instance2.getNumDice());
        assertEquals("Number of sides is second argument", 12, instance2.getNumSides());
        assertEquals("Adder is third argument", 6, instance2.getAdder());
        assertNotNull("The die should have a random number generator", instance2.getRng());
        assertEquals("5d12 + 6", instance2.toString());     
        testRolls(instance2, 11, 66, (6.5 * 5.0) + 6.0);
    }
    
    @Test
    public void constructorTestNoArgs() {
        SimpleDie instance = new SimpleDie();
        // Number of dice, number of sides, and adder should all be 0.
        assertEquals("No-arg constructor should set number of dice to one", 1, instance.getNumDice());
        assertEquals("No-arg constructor should set number of sides to zero", 0, instance.getNumSides());
        assertEquals("No-arg constructor should set adder to zero", 0, instance.getAdder());
        assertNotNull("The die should have a random number generator", instance.getRng());
        testRolls(instance, 0, 0, 0.0);
    }

    @Test
    public void multiplierTest() {
        SimpleDie instance = new SimpleDie(2, 5, 4);
        instance.setMultiplier(3);
        assertEquals("Number of dice is first argument", 2, instance.getNumDice());
        assertEquals("Number of sides is second argument", 5, instance.getNumSides());
        assertEquals("Adder is third argument", 4, instance.getAdder());
        assertNotNull("The die should have a random number generator", instance.getRng());
        assertEquals("2d5 + 4 * 3", instance.toString());
        testRolls(instance, 18, 42, 30.0);
    }
    
    @Test
    public void setterTest() {
        SimpleDie instance = new SimpleDie();
        Random rng = new Random();
        instance.setNumDice(2);
        instance.setNumSides(10);
        instance.setAdder(3);
        instance.setMultiplier(5);
        instance.setRng(rng);
        assertEquals("2d10 + 3 * 5", instance.toString());
        assertEquals(rng, instance.getRng());
        testRolls(instance, 25, 115, 70.0);
    }
    
}
