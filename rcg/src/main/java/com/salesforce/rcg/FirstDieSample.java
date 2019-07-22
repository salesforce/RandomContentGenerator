package com.salesforce.rcg;

import com.salesforce.rcg.numbers.dice.DiceFactory;
import com.salesforce.rcg.numbers.dice.DiceExpression;

/**
 * A command-line sample that takes a dice expression as arguments,
 * parses it, and shows the generated dice expression.
 * 
 * @author mpreslermarshall
 *
 */
public class FirstDieSample {

    public static String expression = "3d6";
    
    public static void main(String[] args) {
        if (args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for(String arg: args) {
                sb.append(arg);
                sb.append(' ');
            }
            expression = sb.toString();
        }
        
        DiceFactory factory = new DiceFactory();
        
        
        DiceExpression result = factory.create(expression);

        System.out.println("Resulting die: " + result);

    }

}
