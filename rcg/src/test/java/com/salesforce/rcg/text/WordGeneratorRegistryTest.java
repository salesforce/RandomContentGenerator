package com.salesforce.rcg.text;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONObject;
import org.junit.Test;

import com.salesforce.rcg.text.impl.WordGeneratorTestUtils;
import com.salesforce.rcg.util.CounterMap;
import com.salesforce.rcg.util.Pair;

public class WordGeneratorRegistryTest {

    public static final String SOURCE_MULTI_WG = "{\n" + 
            "    \"description\": \"Test source for several word generators\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"roadnames\",\n" + 
            "            \"weights\": \"defined-weights\",\n" + 
            "            \"wordlist\": [\n" + 
            "                {\"avenue\": 60},\n" + 
            "                {\"boulevard\": 10},\n" + 
            "                {\"court\": 10},\n" + 
            "                {\"drive\": 80},\n" + 
            "                {\"road\": 75},\n" + 
            "                {\"route\": 5},\n" + 
            "                {\"street\": 100},\n" + 
            "                {\"terrace\": 5},\n" + 
            "                {\"trail\": 8},\n" + 
            "                {\"way\": 4}\n" + 
            "            ]\n" + 
            "        },\n" + 
            "        {\n" + 
            "            \"name\": \"furniture\",\n" + 
            "            \"enabled\": false,\n" + 
            "            \"weights\": \"unweighted\",\n" + 
            "            \"word-filename\": \"furniture.csv\"\n" + 
            "        },\n" + 
            "        {\n" + 
            "            \"name\": \"single\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"The Word\"\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;

    public static final String SOURCE_SINGLE_WG = "{\n" + 
            "    \"description\": \"Test source for a SingleWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"single\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"The Word\"\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    
    @Test
    public void createSourceSingleWgTest() throws Exception {
        // Create a new registry and load it from the SOURCE_SINGLE_WG input, 
        // which should contain a one SingleWordGenerator.
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_SINGLE_WG);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "single", and it's a single-word generator
        WordGenerator wg = registry.getGeneratorByName("single");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.SINGLE_WORD, wg.getType());
        
        // Let's verify the distribution of values from the word generator.
        // This might seem redundant, as each word generator implementation is
        // responsible for its own tests. But putting the test here verifies
        // that the code in WordGeneratorRegistry has properly parsed+processed
        // the weights as defined in the JSON input.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("The Word", 1);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }
    
    public static final String SOURCE_MULTIPLE_SWG = "{\n" + 
            "    \"description\": \"Test source with multiple SingleWordGenerators\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"first\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"First Word\"\n" + 
            "        },\n" + 
            "        {\n" + 
            "            \"name\": \"uncounted\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"Not me!\",\n" +
            "            \"enabled\": \"false\"\n" +
            "        },\n" + 
            "        {\n" + 
            "            \"name\": \"second\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"Second Word\"\n" + 
            "        },\n" + 
            "        {\n" + 
            "            \"name\": \"third\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"Third Word\"\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    
    @Test
    public void createSourceMultipleSwgTest() throws Exception {
        // Create a new registry and load it from the SOURCE_MULTIPLE_SWG input, 
        // which should contain a three SingleWordGenerators.
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_MULTIPLE_SWG);

        // This registry contains three word generators
        assertEquals(3, registry.getNumGenerators());
        
        // Confirm that the three generators we expect are there
        Map<String, String> nameToWordMap = new TreeMap<>();
        
        // Verify that the word generators we expect are found in the
        // registry. This is a pretty simple verification, because all the
        // generators are single-word generators. We'll build a map that
        // contains (name of generator) -> (word that generator should generate).
        // Then we can iterate through that map, retrieving the word generator
        // by name and verifying its output.
        //
        // First, build the map
        nameToWordMap.put("first", "First Word");
        nameToWordMap.put("second", "Second Word");
        nameToWordMap.put("third", "Third Word");
        
        // Confirm that the generator contains what we expect.
        for(Map.Entry<String, String> entry: nameToWordMap.entrySet()) {
            String generatorName = entry.getKey();
            String expectedWord = entry.getValue();
            
            WordGenerator generator = registry.getGeneratorByName(generatorName);
            // Verify that the generator is a single-word generator
            assertEquals(WordGeneratorType.SINGLE_WORD, generator.getType());
            
            // Verify its distribution
            CounterMap<String> expectedWeights = new CounterMap<>();
            expectedWeights.add(expectedWord, 1);
            WordGeneratorTestUtils.testWeightedGenerator(generator, expectedWeights); 
        }        
    }

    public static final String SOURCE_WEIGHTED_WG = "{\n" + 
            "    \"description\": \"Test source for a WeightedWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"roadnames\",\n" + 
            "            \"weights\": \"defined-weights\",\n" + 
            "            \"wordlist\": [\n" + 
            "                {\"avenue\": 60},\n" + 
            "                {\"boulevard\": 10},\n" + 
            "                {\"court\": 10},\n" + 
            "                {\"drive\": 80},\n" + 
            "                {\"road\": 75},\n" + 
            "                {\"route\": 5},\n" + 
            "                {\"street\": 100},\n" + 
            "                {\"terrace\": 5},\n" + 
            "                {\"trail\": 8},\n" + 
            "                {\"way\": 4}\n" + 
            "            ]\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    @Test
    public void createSourceWeightedWgTest() throws Exception {
        // Create a new registry and load it from the SOURCE_SINGLE_WG input, 
        // which should contain a one SingleWordGenerator.
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_WEIGHTED_WG);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "roadnames", and it's a weighted word generator
        WordGenerator wg = registry.getGeneratorByName("roadnames");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.WEIGHTED, wg.getType());

        // Now, verify the value distribution.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("avenue", 60);
        expectedWeights.add("boulevard", 10);
        expectedWeights.add("court", 10);
        expectedWeights.add("drive", 80);
        expectedWeights.add("road", 75);
        expectedWeights.add("route", 5);
        expectedWeights.add("street", 100);
        expectedWeights.add("terrace", 5);
        expectedWeights.add("trail", 8);
        expectedWeights.add("way", 4);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }
    
    public static final String SOURCE_UNWEIGHTED_WG = "{\n" + 
            "    \"description\": \"Test source for an UnweightedWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"ebs\",\n" + 
            "            \"weights\": \"unweighted\",\n" + 
            "            \"enabled\": \"true\",\n" +
            "            \"wordlist\": [\n" + 
            " \"this\", \"is\", \"a\", \"test\", \"of\", \"an\",\n" +
            " \"unweighted\", \"word\", \"generator\"\n" +
            "            ]\n" +
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    @Test
    public void createSourceUnweightedWgTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry(true);
        
        registry.loadGeneratorsFromJson(SOURCE_UNWEIGHTED_WG);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "ebs", and it's an unweighted word generator
        WordGenerator wg = registry.getGeneratorByName("ebs");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.UNWEIGHTED, wg.getType());

        // Now, verify the value distribution.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("this", 1);
        expectedWeights.add("is", 1);
        expectedWeights.add("a", 1);
        expectedWeights.add("test", 1);
        expectedWeights.add("of", 1);
        expectedWeights.add("an", 1);
        expectedWeights.add("unweighted", 1);
        expectedWeights.add("word", 1);
        expectedWeights.add("generator", 1);
        System.out.println("Testing unweighted word generator " + wg + ", expected total weight=" + expectedWeights.getTotal());
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }
    
    public static final String SOURCE_DISABLED = "{\n" + 
            "    \"description\": \"Test source with a disabled generator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"missing\",\n" + 
            "            \"weights\": \"single-word\",\n" + 
            "            \"word\": \"Can't find me!\",\n" + 
            "            \"disabled\": \"true\",\n" + 
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    
    @Test
    public void createSourceDisabledTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();        
        registry.loadGeneratorsFromJson(SOURCE_DISABLED);

        // The only registry defined by that input is disabled, so
        // there shouldn't be any in the registry.
        assertEquals(0, registry.getNumGenerators());

        // Since the only generator was disabled, there's nothing else to test.
    }
    
    @Test
    public void loadUnweightedWordGeneratorFromResourceTest() throws IOException {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        // Load the "Wordlist-Unweighted" generator several times with different
        // settings.
        registry.loadUnweightedWordGeneratorFromResource("trimmed",
        		"data/wordlists/Wordlist-Unweighted.csv",
        		null, // default file processing mode
        		true, // trim lines
        		null); // default text casing mode
        registry.loadUnweightedWordGeneratorFromResource("capitalize-first",
        		"data/wordlists/Wordlist-Unweighted.csv",
        		"csv", // force CSV processing mode
        		true,  // trim lines
        		"capitalize-first"); // force capitalize-first
        registry.loadUnweightedWordGeneratorFromResource("untrimmed",
        		"data/wordlists/Wordlist-Unweighted.csv",
        		null, // default file processing mode
        	    false,  // don't trim lines
        		"uppercase"); // force uppercase

        // Even though they were all loaded from a single resource file, there will be
        // three word generators in this registry.
        assertEquals(3, registry.getNumGenerators());
        
        // Test the generators we created
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("trimmed"), 
        		TextCasing.DEFAULT_CASING,
        		true, 
        		UNWEIGHTED_RESOURCE_STRINGS);
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("capitalize-first"), 
                TextCasing.CAPITALIZE_FIRST,
                true, 
                UNWEIGHTED_RESOURCE_STRINGS);
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("untrimmed"), 
                TextCasing.UPPERCASE,
                false, 
                UNWEIGHTED_RESOURCE_STRINGS);
        
    }
    
    public static final String SOURCE_UNWEIGHTED_RESOURCE_WG = "{\n" + 
            "    \"description\": \"Test source for a WeightedWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"unweighted1\",\n" + 
            "            \"weights\": \"unweighted\",\n" + 
            "            \"resourcename\": \"data/wordlists/Wordlist-Unweighted.csv\",\n" +
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    public static final String[] UNWEIGHTED_RESOURCE_STRINGS = {
    		"line1",
    		"line2",
    		"this is getting boring so let's do something else",
    		"    leading spaces on this line",
    		"trailing spaces on this line    ",
    		"blank line before this line"
    };
    @Test
    public void createSourceUnweightedResourceWgTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_UNWEIGHTED_RESOURCE_WG);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "unweighted1", and it's an unweighted word generator
        WordGenerator wg = registry.getGeneratorByName("unweighted1");
        WordGeneratorTestUtils.testUnweightedGenerator(wg, 
        		TextCasing.UNMODIFIED,
        		true, 
        		UNWEIGHTED_RESOURCE_STRINGS);
    }

    public static final String SOURCE_WEIGHTED_RESOURCE_WG = "{\n" + 
            "    \"description\": \"Test source for a WeightedWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"weighted1\",\n" + 
            "            \"weights\": \"defined-weights\",\n" + 
            "            \"resourcename\": \"data/wordlists/Wordlist-Unweighted.csv\",\n" +
            "            \"trim-words\": \"false\",\n" +
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    @Test
    public void createSourceWeightedResourceWgTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_WEIGHTED_RESOURCE_WG);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "weighted1", and it's a weighted word generator
        WordGenerator wg = registry.getGeneratorByName("weighted1");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.WEIGHTED, wg.getType());

        // Now, verify the value distribution.
        CounterMap<String> expectedWeights = new CounterMap<>();
        // We specifically asked for the lines not to be trimmed of spaces
        expectedWeights.add("line1", 1); 
        expectedWeights.add("line2", 1); 
        expectedWeights.add("this is getting boring so let's do something else", 1); 
        expectedWeights.add("    leading spaces on this line", 1); 
        expectedWeights.add("trailing spaces on this line    ", 1); 
        expectedWeights.add("blank line before this line", 1);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }
    
    public static final String SOURCE_WEIGHTED_RESOURCE_WG2 = "{\n" + 
            "    \"description\": \"Test source for a WeightedWordGenerator\",\n" + 
            "    \"generators\": [\n" + 
            "        {\n" + 
            "            \"name\": \"weighted2\",\n" + 
            "            \"weights\": \"defined-weights\",\n" + 
            "            \"resourcename\": \"data/wordlists/Wordlist-Weighted.csv\",\n" +
            "            \"trim-words\": \"true\",\n" +
            "            \"file-processing-mode\": \"csv\"\n" +
            "        }\n" + 
            "    ]\n" + 
            "}\n" ;
    
    public static final List<Pair<String, Double>> WEIGHTED_RESOURCE_ENTRIES = new ArrayList<>();
    static {
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("trivial", 10.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("simple", 50.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("also simple", 50.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("fairly simple   ", 80.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("    not exactly simple", 120.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("fancy!", 375.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("extra-fancy", 600.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("easy again", 60.0));
        WEIGHTED_RESOURCE_ENTRIES.add(new Pair<>("still pretty easy", 71.0));
    }
    
    @Test
    public void createSourceWeightedResourceWg2Test() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadGeneratorsFromJson(SOURCE_WEIGHTED_RESOURCE_WG2);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "weighted2", and it's a weighted word generator
        WordGenerator wg = registry.getGeneratorByName("weighted2");
        
        WordGeneratorTestUtils.testWeightedGenerator(wg, 
                TextCasing.UNMODIFIED,
                true, 
                WEIGHTED_RESOURCE_ENTRIES);
    }

    @Test
    public void loadWeightedWordGeneratorFromResourceTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        // Load the "Wordlist-Weighted" generator several times with different
        // settings.
        registry.loadWeightedWordGeneratorFromResource("trimmed",
                "data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                true, // trim lines
                null); // default text casing mode
        registry.loadWeightedWordGeneratorFromResource("capitalize-first",
                "data/wordlists/Wordlist-Weighted.csv",
                "csv", // force CSV processing mode
                true,  // trim lines
                "capitalize-first"); // force capitalize-first
        registry.loadWeightedWordGeneratorFromResource("untrimmed",
                "data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                false,  // don't trim lines
                "uppercase"); // force uppercase
        registry.loadWeightedWordGeneratorFromResource("untrimmed-lower",
                "data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                false,  // don't trim lines
                "lowercase"); // force uppercase
        // This registry contains four generators
        assertEquals(4, registry.getNumGenerators());
        
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("trimmed"), 
                TextCasing.DEFAULT_CASING,
                true, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("capitalize-first"), 
                TextCasing.CAPITALIZE_FIRST,
                true, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("untrimmed"), 
                TextCasing.UPPERCASE,
                false, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("untrimmed-lower"), 
                TextCasing.LOWERCASE,
                false, 
                WEIGHTED_RESOURCE_ENTRIES);
        
    }
    
    
    @Test
    public void loadSingleWordGeneratorTest() {
        // Create a new registry and load it by creating a single-word generator. 
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadSingleWordGenerator("My Generator", "Test Output");

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "My Generator", and it's a single-word generator
        WordGenerator wg = registry.getGeneratorByName("My Generator");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.SINGLE_WORD, wg.getType());
        
        // Let's verify the distribution of values from the word generator.
        // As with the JSON-based tests, this verifies that what we loaded
        // is as expected.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("Test Output", 1);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }

    @Test
    public void loadUnweightedWordGeneratorTest() {
        // Create a new registry and load it by creating an unweighted word generator. 
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        registry.loadUnweightedWordGeneratorFromList("Unweighted Generator", 
            Arrays.asList(new String[] {
            		"first",
            		"second", 
            		"another",
            		"fourth",
            		"a Fifth of Beethoven"            		
            }));

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "Unweighted Generator", and it's an unweighted generator
        WordGenerator wg = registry.getGeneratorByName("Unweighted Generator");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.UNWEIGHTED, wg.getType());
        
        // Let's verify the distribution of values from the word generator.
        // As with the JSON-based tests, this verifies that what we loaded
        // is as expected.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("first", 1);
        expectedWeights.add("second", 1);
        expectedWeights.add("another", 1);
        expectedWeights.add("fourth", 1);
        expectedWeights.add("a Fifth of Beethoven", 1);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }

    @Test
    public void loadWeightedWordGeneratorTest() {
        // Create a new registry and load it by creating an weighted word generator. 
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        List<Pair<String, Double>> items = new ArrayList<>();
        items.add(new Pair<>("to", 3.0));
        items.add(new Pair<>("be", 2.0));
        items.add(new Pair<>("or", 2.0));
        items.add(new Pair<>("not", 1.0));
        items.add(new Pair<>("question", 10.0));
        registry.loadWeightedWordGeneratorFromList("Weighted Generator", items);

        // This registry contains only one word generator
        assertEquals(1, registry.getNumGenerators());
        
        // That generator is named "Weighted Generator", and it's a weighted generator
        WordGenerator wg = registry.getGeneratorByName("Weighted Generator");
        assertNotNull(wg);
        assertEquals(WordGeneratorType.WEIGHTED, wg.getType());
        
        // Let's verify the distribution of values from the word generator.
        // As with the JSON-based tests, this verifies that what we loaded
        // is as expected.
        CounterMap<String> expectedWeights = new CounterMap<>();
        expectedWeights.add("to", 3);
        expectedWeights.add("be", 2);
        expectedWeights.add("or", 2);
        expectedWeights.add("not", 1);
        expectedWeights.add("question", 10);
        WordGeneratorTestUtils.testWeightedGenerator(wg, expectedWeights); 
    }
    
    @Test
    public void loadUnweightedWordGeneratorFromFileTest() throws IOException {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        // Load the "Wordlist-Unweighted" generator several times with different
        // settings.
        registry.loadUnweightedWordGeneratorFromFile("trimmed",
                "src/test/resources/data/wordlists/Wordlist-Unweighted.csv",
                null, // default file processing mode
                true, // trim lines
                null); // default text casing mode
        registry.loadUnweightedWordGeneratorFromFile("capitalize-first",
                "src/test/resources/data/wordlists/Wordlist-Unweighted.csv",
                "csv", // force CSV processing mode
                true,  // trim lines
                "capitalize-first"); // force capitalize-first
        registry.loadUnweightedWordGeneratorFromFile("untrimmed",
                "src/test/resources/data/wordlists/Wordlist-Unweighted.csv",
                null, // default file processing mode
                false,  // don't trim lines
                "uppercase"); // force uppercase

        // Even though they were all loaded from a single resource file, there will be
        // three word generators in this registry.
        assertEquals(3, registry.getNumGenerators());
        
        // Test the generators we created
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("trimmed"), 
                TextCasing.DEFAULT_CASING,
                true, 
                UNWEIGHTED_RESOURCE_STRINGS);
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("capitalize-first"), 
                TextCasing.CAPITALIZE_FIRST,
                true, 
                UNWEIGHTED_RESOURCE_STRINGS);
        WordGeneratorTestUtils.testUnweightedGenerator(registry.getGeneratorByName("untrimmed"), 
                TextCasing.UPPERCASE,
                false, 
                UNWEIGHTED_RESOURCE_STRINGS);
    }

    @Test(expected=java.io.FileNotFoundException.class)
    public void loadUnweightedWordGeneratorFromMissingFileTest() throws IOException {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        // Try to load a word generator that doesn't exist, so we can check that we
        // get a reasonable exception.
        registry.loadUnweightedWordGeneratorFromFile("trimmed",
                "this/file/does/not/exist.csv",
                null, // default file processing mode
                true, // trim lines
                null); // default text casing mode
    }
    
    @Test
    public void loadWeightedWordGeneratorFromFileTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        // Load the "Wordlist-Weighted" generator several times with different
        // settings.
        registry.loadWeightedWordGeneratorFromFile("trimmed",
                "src/test/resources/data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                true, // trim lines
                null); // default text casing mode
        registry.loadWeightedWordGeneratorFromFile("capitalize-first",
                "src/test/resources/data/wordlists/Wordlist-Weighted.csv",
                "csv", // force CSV processing mode
                true,  // trim lines
                "capitalize-first"); // force capitalize-first
        registry.loadWeightedWordGeneratorFromFile("untrimmed",
                "src/test/resources/data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                false,  // don't trim lines
                "uppercase"); // force uppercase
        registry.loadWeightedWordGeneratorFromFile("untrimmed-lower",
                "src/test/resources/data/wordlists/Wordlist-Weighted.csv",
                null, // default file processing mode
                false,  // don't trim lines
                "lowercase"); // force uppercase
        // This registry contains four generators
        assertEquals(4, registry.getNumGenerators());
        
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("trimmed"), 
                TextCasing.DEFAULT_CASING,
                true, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("capitalize-first"), 
                TextCasing.CAPITALIZE_FIRST,
                true, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("untrimmed"), 
                TextCasing.UPPERCASE,
                false, 
                WEIGHTED_RESOURCE_ENTRIES);
        WordGeneratorTestUtils.testWeightedGenerator(registry.getGeneratorByName("untrimmed-lower"), 
                TextCasing.LOWERCASE,
                false, 
                WEIGHTED_RESOURCE_ENTRIES);
    }
    

    public static final String SOURCE_NO_GENERATORS = "{\n" + 
            "    \"description\": \"Test source without the 'generators' element\"\n" + 
            "}\n" ;
    @Test(expected=java.lang.IllegalArgumentException.class)
    public void createSourceNoGeneratorsTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();        
        registry.loadGeneratorsFromJson(SOURCE_NO_GENERATORS);

        // The only registry defined by that input is disabled, so
        // there shouldn't be any in the registry.
        assertEquals(0, registry.getNumGenerators());

        // Since the only generator was disabled, there's nothing else to test.
    }

    public static final String SOURCE_EMPTY_GENERATORS = "{\n" + 
            "    \"description\": \"Test source with an empty 'generators' element\"\n" +
            "    \"generators\": []\n" +
            "}\n" ;
    @Test()
    public void createSourceEmptyGeneratorsTest() throws Exception {
        WordGeneratorRegistry registry = new WordGeneratorRegistry();        
        registry.loadGeneratorsFromJson(SOURCE_DISABLED);

        // There are no generators in the input, so there should be no
        // generators in the registry
        assertEquals(0, registry.getNumGenerators());

        // That's all.
    }
    
    // TBD: A test that does several calls to load generators into a single registry.
    
    @Test
    public void multipleLoadsTest() throws Exception {
        // Create the registry
        WordGeneratorRegistry registry = new WordGeneratorRegistry();
        
        // Load it with several of our inputs
        registry.loadGeneratorsFromJson(SOURCE_DISABLED);     // 0 word generators
        registry.loadGeneratorsFromJson(SOURCE_SINGLE_WG);    // 1 word generator
        registry.loadGeneratorsFromJson(SOURCE_MULTIPLE_SWG); // 3 word generators
        registry.loadGeneratorsFromJson(SOURCE_UNWEIGHTED_RESOURCE_WG); // 1 word generator
        
        // There should be 

    }

    @Test
    public void isDisabledTest1() {
    	JSONObject empty = new JSONObject();
    	
    	// The empty JSON object says nothing about enabled/disabled, so it should default to false
    	assertFalse(WordGeneratorRegistry.isDisabled(empty));
    }
    
    @Test
    public void isDisabledTest2() {    	
    	// Try JSON objects that have 'enabled' set to 'true'
    	JSONObject enabledTrue = new JSONObject();
    	enabledTrue.put("enabled", "true");
    	assertFalse(WordGeneratorRegistry.isDisabled(enabledTrue));
    	JSONObject enabledTrueB = new JSONObject();
    	enabledTrueB.put("enabled", true);
    	assertFalse(WordGeneratorRegistry.isDisabled(enabledTrueB));
    	
    	// Now JSON objects that have 'enabled' set to 'false'.
        JSONObject enabledFalse = new JSONObject();
        enabledFalse.put("enabled", "false");
        assertTrue(WordGeneratorRegistry.isDisabled(enabledFalse));
        JSONObject enabledFalseB = new JSONObject();
        enabledFalseB.put("enabled", false);
        assertTrue(WordGeneratorRegistry.isDisabled(enabledFalseB));
    }

    @Test
    public void isDisabledTest3() {   
    	// Test the "disabled" settings.
    	JSONObject disabledTrue = new JSONObject();
    	disabledTrue.put("disabled", "true");
    	assertTrue(WordGeneratorRegistry.isDisabled(disabledTrue));
    	JSONObject disabledTrueB = new JSONObject();
    	disabledTrueB.put("disabled", true);
    	assertTrue(WordGeneratorRegistry.isDisabled(disabledTrueB));
    	
    	// Now JSON objects that have 'disabled' set to 'false'.
        JSONObject disabledFalse = new JSONObject();
        disabledFalse.put("disabled", "false");
        assertFalse(WordGeneratorRegistry.isDisabled(disabledFalse));
        JSONObject disabledFalseB = new JSONObject();
        disabledFalseB.put("disabled", false);
        assertFalse(WordGeneratorRegistry.isDisabled(disabledFalseB));
    }

}
