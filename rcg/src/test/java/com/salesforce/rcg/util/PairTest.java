package com.salesforce.rcg.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class PairTest {

	@Test
	public void fieldAccessTest() {
		// Create a Pair and check its fields
		Pair<String, Long> pair1 = new Pair<>("Frog", Long.valueOf(412L));
		assertEquals("Frog", pair1.getFirst());
		assertEquals(Long.valueOf(412L), pair1.getSecond());
		
		// Do the same on a Pair with different types in it.
		List<String> sampleList = Arrays.asList(new String[] {"this", "is", "a", "list", "of", "strings"});
		Pair<Integer, List<String>> pair2 = new Pair<>(Integer.valueOf(17), sampleList);
		assertEquals(Integer.valueOf(17), pair2.getFirst());
		assertEquals(sampleList, pair2.getSecond());
	}
	
	@Test
	public void equalsTest() {
		String cat1 = "cat";
		StringBuilder sb = new StringBuilder("c");
		sb.append("at");
		String cat2 = sb.toString();
		
		Pair<String, Long> cat1And1 = new Pair<>(cat1, Long.valueOf(1L));
		Pair<String, Long> cat1And6 = new Pair<>(cat1, Long.valueOf(6L));
		Pair<String, Long> cat2And1 = new Pair<>(cat2, Long.valueOf(1L));
		Pair<String, Integer> dogAnd1 = new Pair<>("dog", Integer.valueOf(6));
		Pair<String, Long> cat1AndNull = new Pair<>(cat1, (Long) null);
		Pair<String, Long> nullAnd6 = new Pair<>((String) null, Long.valueOf(6L));
		
		// The Pairs that match
		assertTrue(cat1And1.equals(cat1And1));
		assertTrue(cat1And1.equals(cat2And1));

		// cat1And6 shouldn't match cat1And1 or cat2And1
		assertFalse(cat1And6.equals(cat1And1));
		assertFalse(cat1And6.equals(cat2And1));
		// Cats and dogs don't get along well.
		assertFalse(cat1And1.equals(dogAnd1));
		assertFalse(cat1And6.equals(dogAnd1));
		assertFalse(cat2And1.equals(dogAnd1));
		
		// Comparisons with the null values
		assertFalse(cat1And6.equals(cat1AndNull));
		assertFalse(cat1And6.equals(nullAnd6));
		// And the other way around
		assertFalse(cat1AndNull.equals(cat1And6));
		assertFalse(nullAnd6.equals(cat1And6));
		
		// Comparison to another object type
		assertFalse(cat1AndNull.equals(cat1));
	}
	
	@Test
	public void toStringTest() {
	    Pair<String, String> twoStrings = new Pair<>("frog", "elephant");
	    Pair<Integer, Long> twoNumbers = new Pair<>(Integer.valueOf(42), Long.valueOf(314L));
	    
	    String tss = twoStrings.toString();
	    assertTrue("String form of 'twoStrings' must contain 'frog': " + tss, tss.indexOf("frog") > -1);
        assertTrue("String form of 'twoStrings' must contain 'elephant': " + tss, tss.indexOf("elephant") > -1);
        
        String tns = twoNumbers.toString();
        assertTrue("String form of 'twoNumbers' must contain '42': " + tns, tns.indexOf("42") > -1);
        assertTrue("String form of 'twoNumbers' must contain '314': " + tns, tns.indexOf("314") > -1);
	}
}
