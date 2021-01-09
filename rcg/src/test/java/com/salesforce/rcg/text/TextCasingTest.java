package com.salesforce.rcg.text;

import static com.salesforce.rcg.text.TextCasing.CAPITALIZE_FIRST;
import static com.salesforce.rcg.text.TextCasing.DEFAULT_CASING;
import static com.salesforce.rcg.text.TextCasing.LOWERCASE;
import static com.salesforce.rcg.text.TextCasing.UNMODIFIED;
import static com.salesforce.rcg.text.TextCasing.UPPERCASE;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/** Tests for the TextCasing enum.
 * 
 * @author Martin
 *
 */
public class TextCasingTest {

	@Test
	public void unmodifiedTest() {
		assertEquals("dog", UNMODIFIED.apply("dog"));
		assertEquals("Fish", UNMODIFIED.apply("Fish"));
		assertEquals("WORM", UNMODIFIED.apply("WORM"));		
	}

	@Test
	public void lowercaseTest() {
		assertEquals("dog", LOWERCASE.apply("dog"));
		assertEquals("fish", LOWERCASE.apply("Fish"));
		assertEquals("worm", LOWERCASE.apply("WORM"));		
	}

	@Test
	public void uppercaseTest() {
		assertEquals("DOG", UPPERCASE.apply("dog"));
		assertEquals("FISH", UPPERCASE.apply("Fish"));
		assertEquals("WORM", UPPERCASE.apply("WORM"));		
	}

	@Test
	public void capitalizeFirstTest() {
		assertEquals("Dog", CAPITALIZE_FIRST.apply("dog"));
		assertEquals("Fish", CAPITALIZE_FIRST.apply("Fish"));
		assertEquals("Worm", CAPITALIZE_FIRST.apply("WORM"));		
	}

	@Test
	public void fromTest() {
		assertEquals(UNMODIFIED, TextCasing.from("unmodified"));
		assertEquals(LOWERCASE, TextCasing.from("Lowercase"));
		assertEquals(UPPERCASE, TextCasing.from("UpperCase"));
		assertEquals(CAPITALIZE_FIRST, TextCasing.from("CAPITALIZE-FIRST"));
		
		assertEquals(DEFAULT_CASING, TextCasing.from(null));
	}
}
