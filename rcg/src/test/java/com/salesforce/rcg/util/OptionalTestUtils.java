package com.salesforce.rcg.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

/** Utility methods for testing with Optional return values.
 * 
 * @author Martin
 *
 */
public class OptionalTestUtils {

	/** Validate that the actual Optional value has a value, and that it is the
	 * expected value.
	 * 
	 * @param expected
	 * @param actual
	 */
	public static void is(boolean expected, Optional<Boolean> actual) {
		assertTrue(actual.isPresent());
		if (expected) {
    		assertTrue(actual.get().booleanValue());
		} else {
			assertFalse(actual.get().booleanValue());
		}
	}
}
