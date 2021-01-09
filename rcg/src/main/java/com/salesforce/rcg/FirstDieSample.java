package com.salesforce.rcg;

import com.salesforce.rcg.numbers.dice.DiceExpression;
import com.salesforce.rcg.numbers.dice.DiceFactory;

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
        System.out.println("Die-roller sample program, using Random Content Generator version " + Constants.VERSION + ".");
        if (args.length > 0) {
            StringBuilder sb = new StringBuilder();
            for(String arg: args) {
                sb.append(arg);
                sb.append(' ');
            }
            expression = sb.toString();
        }
        System.out.println("Expression to parse: '" + expression + "'.");
        
        DiceFactory factory = new DiceFactory();        
        
        DiceExpression result = factory.create(expression);

        System.out.println("Resulting die: " + result);

    }

}
