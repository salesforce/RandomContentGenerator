package com.salesforce.rcg.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNull;

import static com.salesforce.rcg.util.StringUtils.isBlank;
import static com.salesforce.rcg.util.StringUtils.isNumber;
import static com.salesforce.rcg.util.StringUtils.parseBoolean;

import org.junit.Test;

public class StringUtilsTest {
	
	@Test
	public void parseBoolean1Test() {
		// A null or empty string should not return a value
		assertFalse(parseBoolean(null).isPresent());
		assertFalse(parseBoolean("").isPresent());
		
		// Try the defined "true" values
		OptionalTestUtils.is(true, parseBoolean("true"));
		OptionalTestUtils.is(true, parseBoolean("yes"));
		OptionalTestUtils.is(true, parseBoolean("enabled"));
		OptionalTestUtils.is(true, parseBoolean("on"));
		OptionalTestUtils.is(true, parseBoolean("1"));
		
		// Try some mixed-case variants as well
		OptionalTestUtils.is(true, parseBoolean("True"));
		OptionalTestUtils.is(true, parseBoolean("YES"));
		OptionalTestUtils.is(true, parseBoolean("eNABled"));
		OptionalTestUtils.is(true, parseBoolean("oN"));
		
		// Now the false values
		OptionalTestUtils.is(false, parseBoolean("false"));
		OptionalTestUtils.is(false, parseBoolean("no"));
		OptionalTestUtils.is(false, parseBoolean("disabled"));
		OptionalTestUtils.is(false, parseBoolean("off"));
		OptionalTestUtils.is(false, parseBoolean("0"));

		// Mixed-case false values
		OptionalTestUtils.is(false, parseBoolean("falsE"));
		OptionalTestUtils.is(false, parseBoolean("NO"));
		OptionalTestUtils.is(false, parseBoolean("DisaBleD"));
		OptionalTestUtils.is(false, parseBoolean("ofF"));

		// Values which are not recognized should return a not-present result
		assertFalse(parseBoolean("frog").isPresent());
	}
	
	/** Tests for the 2-argument version of parseBoolean.
	 * 
	 */
	@Test
	public void parseBoolean2Test() {
		// Simple case - value and default are both true
		assertTrue(parseBoolean("true", true));
		// This is a recognized true string, so it'll return true, even though
		// the default is false.
		assertTrue(parseBoolean("true", false));
		// An unrecognized string should fall back to the default
		assertTrue(parseBoolean("walrus", true));
		
		// Repeating the above cases for 'false' values
		assertFalse(parseBoolean("false", false));
		assertFalse(parseBoolean("false", true));
		assertFalse(parseBoolean("elephant", false));
	}

    @Test
    public void isBlankTest() {
        assertTrue(isBlank(null));
        assertTrue(isBlank(""));
        assertTrue(isBlank(" "));
        assertTrue(isBlank("    "));
        assertTrue(isBlank(" \t"));
        assertTrue(isBlank(" \n\t \n"));        
    }

    /** Tests for the 1-arg isInteger method.
     * 
     */
    @Test
    public void isInteger1Test() {
    	// Null is not an integer, nor is the empty string.
    	assertFalse(StringUtils.isInteger(null));
    	assertFalse(StringUtils.isInteger(""));
    	
    	// Integers
    	assertTrue(StringUtils.isInteger("0"));
    	assertTrue(StringUtils.isInteger("42"));
    	
    	// Commas are not allowed
    	assertFalse(StringUtils.isInteger("1,042"));
    	
    	// Whitespace isn't allowed in the 1-arg version.
    	assertFalse(StringUtils.isInteger(" 142"));
    	assertFalse(StringUtils.isInteger("197   "));
    	
    	// Numbers with decimals are not integers
    	assertFalse(StringUtils.isInteger("0.4"));
    	
    	// Random gorp is also not an integer.
    	assertFalse(StringUtils.isInteger("To be, or not to be."));
    	assertFalse(StringUtils.isInteger("2 fish"));
    }

    /** Tests for the 2-arg version of isInteger.
     * 
     */
    @Test
    public void isInteger2Test() {
    	assertFalse(StringUtils.isInteger(" 142", false));
    	assertFalse(StringUtils.isInteger("197   ", false));
    	assertTrue(StringUtils.isInteger(" 142", true));
    	assertTrue(StringUtils.isInteger("197   ", true));
    }

    /** Tests for isNumber()
     * 
     */
    @Test
    public void isNumberTest() {
    	// Null is not a number, nor is the empty string.
    	assertFalse(isNumber(null, false));
    	assertFalse(isNumber("", false));
    	// Pure whitespace is not a number, even when trimmed
    	assertFalse(isNumber("    ", false));
    	assertFalse(isNumber("    ", true));
    	
    	// Integers
    	assertTrue(isNumber("0", false));
    	assertTrue(isNumber("42", false));
    	// With the trimmings
    	assertTrue(isNumber("   99", true));
    	assertTrue(isNumber("404 ", true));
    	assertTrue(isNumber(" 867   ", true));
    	// But if we don't tell it to trim, then whitespace means it's not a number
    	assertFalse(isNumber("   99", false));
    	assertFalse(isNumber("404 ", false));
    	assertFalse(isNumber(" 867   ", false));
    	
    	// Internal whitespace is not allowed even if the string gets trimmed.
    	assertFalse(isNumber("1 0 2", true));
    	
    	// Commas are not allowed 
    	assertFalse(isNumber("1,042", false));
    	
    	// A single decimal is OK. Multiple are not.
    	assertTrue(isNumber("0.4", false));
    	assertTrue(isNumber(".621", false));
    	assertTrue(isNumber("3.1415926535", false));
    	assertFalse(isNumber("4.12.1", false));
    	
    	// Random gorp is also not a number.
    	assertFalse(isNumber("Is this a dagger I see before me?", false));
    	assertFalse(isNumber("1 fish", false));

    	assertFalse(StringUtils.isInteger(" 142", false));
    	assertFalse(StringUtils.isInteger("197   ", false));
    	assertTrue(StringUtils.isInteger(" 142", true));
    	assertTrue(StringUtils.isInteger("197   ", true));
    }

    @Test
    public void capitalizeFirstTest() {
    	assertNull(StringUtils.capitalizeFirst(null));
    	
    	assertEquals("", StringUtils.capitalizeFirst(""));
    	assertEquals("A", StringUtils.capitalizeFirst("a"));
    	assertEquals("A", StringUtils.capitalizeFirst("A"));
    	
    	assertEquals("Ba", StringUtils.capitalizeFirst("ba"));
    	
    	// The examples from the Javadoc.
    	assertEquals("Seas", StringUtils.capitalizeFirst("seas"));
    	assertEquals("Seas", StringUtils.capitalizeFirst("Seas"));
    	assertEquals("Seas", StringUtils.capitalizeFirst("SEAS"));
    	
    	// We should get back the exact same String object in the event
    	// that the first letter is already capitalized.
    }   
}
