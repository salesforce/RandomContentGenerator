package com.salesforce.rcg.numbers.dice;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import com.salesforce.rcg.numbers.dice.impl.DiceConstructingWalker;
import com.salesforce.rcg.numbers.dice.impl.DiceLexer;
import com.salesforce.rcg.numbers.dice.impl.DiceParser;

public class DiceFactory {
    public DiceFactory() {
    }
    
    public DiceExpression create(String source) {
        //System.out.println("Parsing dice expression '" + source + "'.");

        // Parse the expression into an Antlr parse tree
        DiceLexer lexer = new DiceLexer(CharStreams.fromString(source));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        DiceParser parser = new DiceParser(tokens);
        // MPM BUG: How can I force this to process the entire token stream, and not
        // just give up once we've consumed all we can.
        DiceParser.ExpressionContext expressionTree = parser.expression();
        
        //System.out.println("Expression tree created: " + expressionTree.toStringTree());
        
        DiceConstructingWalker walker = new DiceConstructingWalker();
        DiceExpression result = walker.process(expressionTree);        

        return(result);
    }

}
