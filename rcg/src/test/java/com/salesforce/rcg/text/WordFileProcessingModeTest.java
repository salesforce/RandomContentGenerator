package com.salesforce.rcg.text;

import static com.salesforce.rcg.text.WordFileProcessingMode.CSV;
import static com.salesforce.rcg.text.WordFileProcessingMode.UNPARSED;
import static com.salesforce.rcg.text.WordFileProcessingMode.US_CENSUS_FIRSTNAMES;
import static com.salesforce.rcg.text.WordFileProcessingMode.US_CENSUS_LASTNAMES;
import static com.salesforce.rcg.text.WordFileProcessingMode.DEFAULT_FILE_PROCESSING_MODE;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class WordFileProcessingModeTest {
    private static final String NON_SKIPPABLE_TOKENS[] = { "cat", "mouse", "rabbit"};
    		
	@Test
	public void fromTest() {
		assertEquals(CSV, WordFileProcessingMode.from("csv"));
		assertEquals(UNPARSED, WordFileProcessingMode.from("Unparsed"));
		assertEquals(US_CENSUS_FIRSTNAMES, WordFileProcessingMode.from("US-census-first"));
		assertEquals(US_CENSUS_LASTNAMES, WordFileProcessingMode.from("US-CENSUS-LAST"));
		
		// And the default mode
		assertEquals(DEFAULT_FILE_PROCESSING_MODE, WordFileProcessingMode.from(null));
	}
	
	@Test(expected=java.lang.IllegalArgumentException.class)
	public void fromTestUnrecognized() {
		assertNotNull(WordFileProcessingMode.from("rattlesnake"));
	}
	
	/** Test for shouldSkipToken, using tokens that none of the file modes should skip.
	 * 
	 */
	@Test
	public void nonSkipTokensTest() {
		for (WordFileProcessingMode mode: WordFileProcessingMode.values()) {
			for (String token: NON_SKIPPABLE_TOKENS) {
				assertFalse("Mode " + mode.getName() + " should skip token " + token + ".",
						mode.shouldSkipToken(token));
			}
		}
	}
	
	@Test
	public void skipTokenTest() {
		// Recognized, skippable tokens.
		assertTrue(US_CENSUS_LASTNAMES.shouldSkipToken("ALL OTHER NAMES"));
		
		// Skippable tokens are NOT case-sensitive
		assertTrue(US_CENSUS_LASTNAMES.shouldSkipToken("All Other Names"));		
	}
}
