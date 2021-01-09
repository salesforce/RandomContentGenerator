package com.salesforce.rcg.text;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WordGeneratorTypeTest {

    /** Validate that every known type of word generator will create a 
     * valid word generator.
     */
    @Test
    public void knownTypesTest() {
        for (WordGeneratorType type: WordGeneratorType.values()) {
            WordGenerator result = WordGeneratorType.create(type, "Word generator for " + type);
            assertNotNull(result);
            assertEquals(type, result.getType());
        }
    }
    
    /** We can create word generators with no name - one should be automatically 
     * supplied.
     */
    @Test
    public void noNameIsOkTest() {
        WordGenerator result = WordGeneratorType.create(WordGeneratorType.WEIGHTED, null);
        // We should have been able to create the generator even with no name
        assertNotNull(result);
        assertEquals(WordGeneratorType.WEIGHTED, result.getType());        

        // A non-null name should have been filled in for us.
        assertNotNull(result.getName());
    }
    
    @SuppressWarnings("unused")
	@Test(expected=java.lang.Exception.class) 
    public void nullTypeTest1() {
        WordGenerator result = WordGeneratorType.create(null, "A null type is meaningless");
    }

    @SuppressWarnings("unused")
    @Test(expected=java.lang.Exception.class) 
    public void nullTypeTest2() {
        WordGeneratorType result = WordGeneratorType.from(null);
    }

    @SuppressWarnings("unused")
    @Test(expected=java.lang.Exception.class) 
    public void bogusTypeTest() {
        WordGeneratorType result = WordGeneratorType.from("bogus");
    }

}
