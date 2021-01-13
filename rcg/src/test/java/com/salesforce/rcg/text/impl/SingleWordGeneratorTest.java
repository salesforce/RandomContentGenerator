package com.salesforce.rcg.text.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SingleWordGeneratorTest {
    @Test
    public void basicTest() {
        SingleWordGenerator example = new SingleWordGenerator("example's name", "my word!");
        assertEquals("example's name", example.getName());
        assertEquals("my word!", example.generateWord());
        assertTrue(example.toString().indexOf("example") >= 0);
    }

    /** It's illegal to set the word on a word generator that already has a defined
     * word. Let's confirm that doing so causes an exception.
     */
    @Test(expected=java.lang.Exception.class)
    public void cantSetWord() {
        // Create the generator with a word it can generate
        SingleWordGenerator example = new SingleWordGenerator("cantSetWord", "Oh hi");
        
        // We have a word, we can generate it.
        assertEquals("Oh hi", example.generateWord());
        
        // Trying to set the word will fail with some kind of exception
        example.setWord("Something else");
    }

    /** It's illegal to set the word on a word generator once it has a defined
     * word. Let's confirm that doing so causes an exception.
     */
    @Test(expected=java.lang.Exception.class)
    public void cantSetWord2() {
        // Create the generator with no word
        SingleWordGenerator example = new SingleWordGenerator("cantSetWord2");
        
        // Set the word
        example.setWord("Mumble");
        
        // We have a word, we can generate it.
        assertEquals("Mumble", example.generateWord());
        
        // Trying to set the word will fail with some kind of exception
        example.setWord("Speak clearly");
    }
}
