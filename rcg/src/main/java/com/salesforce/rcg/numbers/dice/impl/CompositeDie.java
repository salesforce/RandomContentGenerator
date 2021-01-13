package com.salesforce.rcg.numbers.dice.impl;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import com.salesforce.rcg.numbers.dice.DiceExpression;

/** A <tt>CompositeDie</tt> contains zero or more DiceExpressions. Rolling a composite
 * die consists of rolling each of the underlying dice expressions, and adding
 * up the total.
 * 
 * <tt>CompositeDie</tt> objects can have all their underlying dice added at construction
 * time, or they can be added incrementally with the {@link #addDie <tt>addDie</tt>} method.
 * 
 * CompositeDie objects are threadsafe, assuming that the underlying dice that make it
 * up are also threadsafe.  
 * 
 * So why, you may ask, does this exist, when a {@link SimpleDie <tt>SimpleDie</tt>}
 * can already represent multiple dice? The answer is that a composite die allows 
 * combining multiple dice with different distributions. For example, 4d8 (4 8-sided dice)
 * will produce values in the range 4-32. 2d6 + 2d10 will <em>also</em> produce values
 * in that same range, but with a different frequency distribution. The CompositeDie
 * allows building these different frequency distributions.
 * 
 * @author Martin Presler-Marshall
 *
 */
public class CompositeDie implements DiceExpression {
    protected final List<DiceExpression> dice = new Vector<>();
    
    public CompositeDie() {
    }
    
    public CompositeDie(DiceExpression... startingDice) {
        for (DiceExpression die: startingDice) {
            addDie(die);
        }
    }
    
    public CompositeDie(Collection<? extends DiceExpression> startingDice) {
        for (DiceExpression die: startingDice) {
            addDie(die);
        }
    }
    
    public CompositeDie addDie(DiceExpression die) {
        if (null == die) {
            throw new IllegalArgumentException("Can't add null dice to a composite die!");
        }
        dice.add(die);
        
        return this;
    }
    
    public int size() {
        return dice.size();
    }
    
    @Override
    public int roll() {
        int sum = 0;
        
        for (DiceExpression die: dice) {
            sum += die.roll();
        }
        
        return sum;
    }
    
    @Override
    public synchronized String toString() {
        if (size() == 0) {
            return "An empty CompositeDie";
        } else {
            StringBuilder result = new StringBuilder();
            
            for (int i = 0; i < dice.size(); ++i) {
                result.append(dice.get(i).toString());
                if (i < dice.size() - 1) {
                    result.append(" and ");
                }
            }
            
            return result.toString();
        }
    }
}
