package com.salesforce.rcg.numbers.dice.impl;

import java.text.DecimalFormat;
import java.util.Random;

import com.salesforce.rcg.numbers.dice.DiceExpression;

public class SimpleDie implements DiceExpression {
    protected int numDice = 1;
    protected int sides;
    protected int adder; 
    protected int multiplier = 1;
    protected double chance = 1.0;
    protected DecimalFormat df = null;
    /* package-private */ Random rng;
    
    public SimpleDie(int sides) {
        this();
        this.sides = sides;
    }
    
    public SimpleDie(int sides, Random rng) {
        this.sides = sides;
        this.rng = rng;
    }
    
    public SimpleDie(int numDice, int sides) {
        this();
        this.numDice = numDice;
        this.sides = sides;
    }
    
    public SimpleDie(int numDice, int sides, int adder) {
        this();
        this.numDice = numDice;
        this.sides = sides;
        this.adder = adder;
    }
    
    public SimpleDie() {
        rng = new Random();
    }
    
    public void setRng(Random rng) {
        this.rng = rng;
    }
    
    public Random getRng() {
        return(rng);
    }

    @Override
    public int roll() {
        if (chance < 1.0) {
            double rolledChance = rng.nextDouble();
            if (rolledChance >= chance) {
                return 0;
            }
        }
        int sum = adder;
        if (sides != 0) {
            for (int i = 0; i < numDice; ++i) {
                sum += (rng.nextInt(sides) + 1);
            }
        }
        return(sum * multiplier);
    }
    
    @Override
    public synchronized String toString() {
        StringBuilder result = new StringBuilder();
        result.append(numDice);
        result.append("d");
        result.append(sides);
        if (adder < 0) {
            result.append(" - ");
            result.append(-adder);
        } else if (adder > 0) {
            result.append(" + ");
            result.append(adder);
        }

        if (multiplier != 1) {
            result.append(" * ");
            result.append(multiplier);
        }
        
        if (chance < 1.0) {
            if (df == null) {
                df = new DecimalFormat("0%");
            }
            result.append(" with a " + df.format(chance) + " chance of generating a non-zero result");
        }
        
        return result.toString();
    }
    
    public int getNumSides() {
        return(sides);
    }

    public int getNumDice() {
        return numDice;
    }

    public void setNumDice(int numDice) {
        this.numDice = numDice;
    }

    public void setNumSides(int sides) {
        this.sides = sides;
    }

    public int getAdder() {
        return adder;
    }

    public void setAdder(int adder) {
        this.adder = adder;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }
    
    /** Create a SimpleDie, picking a reasonable number of dice, sides, and adder, to 
     * produce the specified range.
     * 
     * @param min Desired minimum value of the range
     * @param max Desired maximum value of the range
     * @return
     */
    public static SimpleDie buildFromRange(int min, int max) {
        // First, check if they accidentally flipped the values. The max must be higher
        if (max < min) {
            int temp = max;
            max = min;
            min = temp;
        }
        
        // Now check if the min/max values are the same. If so, this is easy: 
        // no dice, just an adder.
        if (min == max) {
            return(new SimpleDie(0, 0, min));
        }
        
        // The algorithm below only works for ranges where the lower number is at least 1.
        // If the supplied lower range is less than one, then you get a flat range (single die)
        // in the specified range.
        if (min < 1) {
            int range = max - min + 1;
            int adder = min - 1;
            return(new SimpleDie(1, range, adder));
        }
        
        // OK, we have to find a combination that works.
        int numDice = min;
        while (numDice >= 1) {
            // Compute the adder and number of sides
            int adder = (min - numDice);
            int adjustedMax = (max - adder);
            int numSides = adjustedMax / numDice;
            
            // Does this combination work?
            int actualMax = (numDice * numSides) + adder;
            if (actualMax == max) {
                // It's a hit!
                return(new SimpleDie(numDice, numSides, adder));
            }
            
            // Try for fewer dice.
            --numDice;
        }
        
        // We should never get here, because at worst, using 1 die and the appropriate
        // adder will cover every possible range.
        throw new IllegalStateException("Can't find a die combination that works for the range " + min + "-" + max + "!");
    }

    public double getChance() {
        return chance;
    }

    public void setChance(double chance) {
        this.chance = chance;
    }

}
