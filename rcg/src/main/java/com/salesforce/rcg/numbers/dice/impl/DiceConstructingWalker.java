package com.salesforce.rcg.numbers.dice.impl;

import java.util.List;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import com.salesforce.rcg.numbers.dice.DiceExpression;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.Add_or_subtractContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.BasicExpressionContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.ConstantExpressionContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.ExpressionContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.FrpExpressionContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.MinMaxExpressionContext;
import com.salesforce.rcg.numbers.dice.impl.DiceParser.MultiplierContext;

public class DiceConstructingWalker {
    public DiceConstructingWalker() {
    }
    
    public DiceExpression process(ExpressionContext expressionTree) {
        // Start at the top level and see what we have
        
        if (expressionTree.basicExpression() != null) {
            return(process(expressionTree.basicExpression()));
        } else {
            throw new IllegalStateException("Don't know how to process this expression tree!");
        }
    }
    
    protected DiceExpression process(BasicExpressionContext basicExpression) {
        // A basic expression can be one of several forms, all
        // with an optional multiplier. So first figure out which form
        // we have and construct a Dice object for that. Then
        // figure out what multiplier we have, and set that in the
        // die we constructed.
        SimpleDie constructed = null;
        if (basicExpression.frpExpression() != null) {
            constructed = processFrpExpression(basicExpression.frpExpression());
        } else if (basicExpression.minMaxExpression() != null) {
            constructed = processMinMaxExpression(basicExpression.minMaxExpression());
        } else if (basicExpression.constantExpression() != null) {
            constructed = processConstantExpression(basicExpression.constantExpression());
        } else {
            throw new IllegalStateException("Arrugh - how can we have a BasicExpression without one of these paths!");
        }
        
        
        // Now check for a multiplier
        int multiplier = getMultiplier(basicExpression);
        constructed.setMultiplier(multiplier);
        
        return(constructed);
    }
    
    protected SimpleDie processFrpExpression(FrpExpressionContext expression) {
        List<TerminalNode> numberList = expression.NUMBER();

        int numDice, sides;
        if (numberList.size() == 1) {
            // Only one number means the number of dice is implicitly 1 - the
            // only value given is the number of sides
            numDice = 1;
            sides = Integer.parseInt(numberList.get(0).getSymbol().getText());
        } else if (numberList.size() == 2) {
            // First field is the number of dice, second is the number of sides per die
            numDice = Integer.parseInt(numberList.get(0).getSymbol().getText());
            sides = Integer.parseInt(numberList.get(1).getSymbol().getText());
        } else {
            throw new IllegalStateException("Unrecognized count of numeric values in a FRP expression - should be 1 or 2 but found " + numberList.size());
        }
        
        // We can have a constant add/subtract after the number of dice
        // Check for that and process it if present.
        int adder = getAdder(expression);        
        
        // Build a die as specified
        SimpleDie result = new SimpleDie(numDice, sides, adder);
        return(result);
    }
    
    protected int getAdder(FrpExpressionContext expression) {
        // Did they specify a multiplier?
        Add_or_subtractContext asContext = expression.add_or_subtract();
        if (null == asContext) {
            // No add/subtract in the expression - use a default value of 0
            return(0);
        }
        
        int adder = Integer.parseInt(asContext.NUMBER().getSymbol().getText());
        int sign = 1;
        if (asContext.MINUS() != null) {
            sign = -1;
        }
        return(adder * sign);
    }
    
    protected SimpleDie processMinMaxExpression(MinMaxExpressionContext expression) {
        List<TerminalNode> numberList = expression.NUMBER();
        
        // The minimum and maximum values in the range the user requested.
        int min, max;
        
        if (numberList.size() == 2) {
            // First field is the number of dice, second is the number of sides per die
            min = Integer.parseInt(numberList.get(0).getSymbol().getText());
            max = Integer.parseInt(numberList.get(1).getSymbol().getText());
        } else {
            throw new IllegalStateException("Unrecognized count of numeric values in a min-max expression - should be 2 but found " + numberList.size());
        }
        
        if (expression.MINUS() == null) {
            // The expression was processed as NUMBER NUMBER without a dash
            // between the two terms. This comes from an input without any
            // spaces, such as "1-6". This means "a value between 1 and 6",
            // but the lexer didn't handle it this way. It gave us "1" and "-6".
            // So we invert the sign on the second number.
            max = -max;
        }
        SimpleDie result = SimpleDie.buildFromRange(min, max);
        
        return(result);
    }
    
    protected SimpleDie processConstantExpression(ConstantExpressionContext expression) {
        // A constant expression is just a number - extract and parse it
        int constantValue = Integer.parseInt(expression.NUMBER().getSymbol().getText());
        
        SimpleDie result = new SimpleDie();
        result.setAdder(constantValue);
        return(result);
    }
    
    protected int getMultiplier(BasicExpressionContext basicExpression) {
        // Did they specify a multiplier?
        MultiplierContext multiplierContext = basicExpression.multiplier();
        if (null == multiplierContext) {
            // No multiplier specified - the multiplier is 1
            return(1);
        }
        
        // This could be condensed to a chain of calls but I'm spelling it out
        // here for my own reference
        TerminalNode multiplierNumberNode = multiplierContext.NUMBER();
        Token multiplierNumberSymbol = multiplierNumberNode.getSymbol();
        String multiplierText = multiplierNumberSymbol.getText();
        return(Integer.parseInt(multiplierText));
    }
}
