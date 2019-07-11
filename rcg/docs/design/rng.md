# Random Number Generation

## Requirements

The basic idea here is that the user should be able to specify a numeric range, and the system will create an object that’ll generate random values within that range. Basically, we’re creating a highly specialized language – one that’s optimized for random-number generation.

We can make that more concrete with a Dungeons and Dragons (D&D) example. D&D uses multiple kinds of dice to generate different numeric ranges: 4-sided, 6-sided, 8-sided, and so on. And since dice are used so frequently in the game, there’s shorthand to refer to them. A 4-sided die is a “d4”, a 6-sided die is a “d6”, and so on. And you can say that you should roll several of them, and add up the result, by putting a number in front: 3d8 means roll three 8-sided dice and add up the numbers from each.

That’s the beginnings of a language for specifying random ranges. We should be able to give the string “3d8” to our system, and get back an object that will do the electronic equivalent of rolling three 8-sided dice and adding up their results.

But that’s just the beginning. Here are the capabilities the system should have – the user should be able to specify the following, and get back an object that’ll generate those values.

## Specific Requirements

1. Specify a number of sides, and (optionally) the number of dice to roll. If the number of dice is omitted, we’ll assume it’s a single die of the specified number of sides.
  1. This should not be restricted to real-world geometric solids. It’s not possible to produce a 7-sided die in the real world where each side has an equal chance of coming up – or at least if it is possible, that’s geometry I’m not aware of. But our system will have no such geometric restrictions. 7-sided dice, 13-sided dice, 294-sided dice - any of these should be possible.
  1. That having been said, the number of sides must be an integer. No 3.1-sided dice in this world (what would that mean, anyway?). And the output of rolling a die, or any more-complex expression we come up with, should be an integer.
1. The capabilities of #1, but also adding a constant to the result (or subtracting a constant from it), so there should be some syntax to indicate “roll two 6-sided dice, add up the results, then subtract three”.
1. Specify a numeric range, and the system will pick some dice and (if needed) a constant factor to add to it that will generate this range.
  1. This opens up some interesting possibilities, as many numeric ranges have more than one combination of that’ll produce that range. For example, the range 3-9 can be produced by rolling two 4-sided dice, adding up the results, and adding 1. Or it can be produced by rolling a single 7-sided die and adding 2 to the value shown on the die. Both produce the same range, both have the same average, but they will have different distributions. 
  We’ll probably want at least two variants of this syntax: one that has the same frequency for each value in the range (roll a 7-sided die and add 2, from the example above) and a second that will allow multiple dice (roll two 4-sided dice and add 1, from the example above).
1. Any of the capabilities above, but using loaded dice – that is, dice that don’t have an equal chance of producing each value. In real life, dice can be loaded to prefer any value the cheater desires. For the purposes of this library, I think two kinds of loading are probably good enough: loaded-low and loaded-high. Loaded-low dice have a higher-than-normal chance of rolling a 1 and a lower chance of rolling higher values. Loaded-high dice are the exact opposite: they have a higher chance of producing their highest value, and a lower chance of producing lower values.
The exact degree of loading is an implementation detail – we can pick whatever algorithm works for loading the dice.
1. Ranges specified as plus-or-minus: normally one thinks of a range based on its minimum and maximum values. But another perfectly reasonable way to specify ranges is to give the midpoint of the range and the maximum possible distance from the midpoint. For example, “10 plus or minus 4” means a range of 6 – 14.
1. Giving multipliers for the dice. For example, say you want to generate numbers in the range 10-60 – but the only possible values you want are 10, 20, 30, 40, 50, and 60. In real life, you’d roll a (6-sided) die and multiply the result by 10. We should support a multiplier for any of the expressions above.
1. The ability to chain multiple dice expressions together. For example, say you want to roll a 10-sided die and then add to its result the result of a 4-sided die. That should be possible. Or rolling an 8-sided die and then subtracting from it the result of rolling a 12-sided die.
1. Degenerate expressions should also be supported. For example, a constant value really means “roll no dice – and I don’t care how many sides are on the dice we aren’t rolling – then add the constant”. A range where the minimum and maximum are equal is saying the same thing; we should support this as well.

Note that this does not specifying syntax for any of this yet – that comes later.

## Other Requirements

- We should be able to create at least some die roller variants through API calls, without using a string syntax. At the very least, we should support the basic case of “X dice with Y sides each, plus the constant Z”.
- The default behavior should be that each new die roller object creates a new underlying random number generator (using the java.util.Random class), but it should be possible to replace this with a subclass of Random if desired. There should also be some way to tell the die-rolling objects to use their current thread’s ThreadLocalRandom.
- Given a die-rolling object, it should be able to produce a text string that explains exactly what it does. So, if the die-rolling object simulates rolling three 12-sided dice and subtracting 5 from the result, it should produce a string that says something to that effect. In practical terms, this means the toString() method should give a description of what the object does.
This does not mean that the text description we produce should be able to be parsed to be turned back into a die-rolling object. That is probably just more complexity than we need.
- Unit tests: we must have unit tests. We need to be able to verify that the parser produces die-rolling objects of the expected type. And we should confirm that if a die-roller says it’ll produce values in a given range, that it does so.

## Detailed Syntax


## Design Notes

This is some notes on what the code ought to look like, rather than a clear, complete,
and coherent design document.

The basic code structure should include:
- An interface for the die roller class, and at least one implementation of that interface.
- A factory class for creating new die roller instances. This class contains all the logic for parsing die language expressions, plus methods for creating die rollers directly. This class could either be a singleton or else its methods could be static.
- The class(es) that do the combination of numbers should be separate from the classes that roll a single die. That way we can provide loaded dice by swapping out the “roll a single die” implementation.

## Open Questions
- Are there other interfaces we might implement? 
  - We might choose to implement Iterable<Integer> - but the only logical interpretation I can come up for this would be to iterate forever, generating random numbers from the range the die roller generates.
  - It would be nice to have the die roller execute something N times, where N is the value produced by generating a random value from the range. We could potentially use the Callable interface here. However, call() returns a value – what are we doing with that value? We could produce a List of the results, perhaps. Think about this some more.


