// Define a grammar for random-number expressions
grammar Dice;

// Dice language to-do's:
// - Chaining together multiple dice, in the form 1d6 & 2d10
// - Dice expressions with chances, such as 10%:1d6

@header {

// No package declaration - the Antlr4 Maven plugin should deal with that.	

// Put import statements here as needed...but at the moment none are needed.

// End of @header
}

//
// The top of the parse tree: various kinds of random-number expressions
// This will be represented by the RandomNumericExpression interface
// By adding EOF to the end of the syntax, that tells Antlr that I expect
// to parse the entire input. Now I get errors output to the console when
// there is extraneous input, but I don't know how to turn those into
// runtime exceptions.
expression
    : singleDie (AND singleDie)* EOF
    //| singleDie AND expression 
    ;

singleDie 
    : dicePrefix? basicExpression 
    ;

dicePrefix
    : CHANCE COLON INTEGER PERCENT COMMA
    ;

// The concrete types of random number expressions. These are implementations of the RandomNumericExpression interface
basicExpression
    // FrpExpression
    : frpExpression multiplier?
    // MinMaxExpression
    | minMaxExpression multiplier?
    // ConstantExpression
    | constantExpression multiplier?
    ;

multiplier 
    : TIMES INTEGER ;

//
// Fantasy Role-Playing Game Expressions
frpExpression 
    // Basic FRP expressions are xDy+z
    //     x = number of dice to roll. This part is optional. If not present,
    //         it's assumed to be 1
    //     y = number of sides on each die
    //     z = constant to add or subtract. This part is also optional. If not
    //         present, it's assumed to be 0.
    : INTEGER? die INTEGER add_or_subtract?
    ;
    
// A random die is represented by the Die interface. At some point I need a way to say whether you want standard dice
// (the SimpleDie implementation) or various loaded dice.
die : SIMPLE_DIE ;

//
// Utility constructs
//

// Amount added to or subtracted from a dice expression. For purposes of the
// examples below, assume we've already parsed a basic FRP expression of "3d6"
add_or_subtract :
    // This is to support adding a constant to the expression.
    // For example, you may want to add 2 to the result with "3d6 + 2" 
    PLUS INTEGER
    // Alternatively, you may with to subtract a constant from the
    // expression, such as "3d6 - 2"
  | MINUS INTEGER
    // This is a funny one to deal with ambiguity. The expression "3d6 - 2" is simple
    // to parse, because it's 
    // NUMBER (3) SIMPLE_DIE ('d') NUMBER (6) MINUS ('-') NUMBER (2)
    // But "3d6-2" is trickier. The "-2" part could be MINUS NUMBER or just NUMBER,
    // since numbers can start with a unary minus. And antlr picks the longest matching
    // rule, so it becomes just NUMBER (-2). This form is to deal with that lexigraphic 
    // ambiguity. 
  | INTEGER
  ;

//
// Min/max expressions: this is a lower and upper range, separated by a dash.
// For example, "4-10" means we want a random number between 4 and 10, inclusive
minMaxExpression: 
      INTEGER MINUS INTEGER
    | INTEGER INTEGER
    ; 

// A constant expression is just a number, like "6". As you might expect, this means
// to always generate the specified value.
constantExpression: INTEGER ;

// Keywords
CHANCE: ('c' | 'C') ('h' | 'H') ('a' | 'A') ('n' | 'N') ('c' | 'C') ('e' | 'E') ;
AND
    : ('a' | 'A') ('n' | 'N') ('d' | 'D') 
    | '&'
    ;
    
//
// Lexical analysis
PLUS : '+' ;
MINUS : '-' ;
TIMES : '*' ;
COLON : ':' ;
PERCENT : '%' ;
PERIOD : '.' ;
COMMA : ',' ;

// Numbers
//DECIMAL_NUMBER 
//    : INTEGER
//    | INTEGER PERIOD DIGITS
//    ;
    
INTEGER 
    : ZERO 
    | MINUS? NZDIGIT DIGIT* ;

// Individual digits
DIGIT : ZERO | NZDIGIT ;
ZERO : '0' ;
NZDIGIT : [1-9] ;

// 'd' represents dice to roll.
// I need to think about this one carefully. I've got this defined as a lexical expression,
// which may not be the right way to go about doing this if I have other tokens that begin
// with 'd'. 
SIMPLE_DIE : D;

//
// Fragments for characters
//
fragment D : [dD] ;

// Whitespace is ignored
WS : [ \t\r\n]+ -> skip ; 
