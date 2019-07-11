package com.salesforce.rcg.numbers.dice;

/** The Die interface represents objects that act as do dice in the real world -
 * they can be rolled to produce a random integer based on the number of sides the
 * die has.
 *  
 * @author mpreslermarshall
 *
 */
public interface DiceExpression {
    /** Roll the die and return the result.
     *  
     * @return A pseudo-random value within the range of values this die will generate.
     */
    public int roll();

    /** Get the number of sides this die has. This must be an integer (a die 
     * with 3.14 sides makes no sense, unless Bloody Stupid Johnson designed it).
     * However, there need not be an equal chance of each value in that range
     * being generated when calling roll().
     * 
     * @return the number of sides in this simulated die.
     */
    public int getNumSides();
}
