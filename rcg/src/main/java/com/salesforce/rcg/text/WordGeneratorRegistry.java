package com.salesforce.rcg.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import com.salesforce.rcg.text.impl.SingleWordGenerator;
import com.salesforce.rcg.text.impl.UnweightedWordGenerator;
import com.salesforce.rcg.text.impl.WeightedWordGenerator;
import com.salesforce.rcg.util.IoUtils;
import com.salesforce.rcg.util.JSONUtils;
import com.salesforce.rcg.util.Pair;
import com.salesforce.rcg.util.StringUtils;

/**
 * A WordGeneratorRegistry provides registration, lookup, and loading functions for 
 * {@link <tt>WordGenerator</tt>s}. 
 * 
 * This class is threadsafe. For most purposes, it's expected that an application
 * will only need a single instance of this class.
 * 
 * The standard way to load a WordGeneratorRegistry is to load it from a JSON string
 * that defines the registry and the word generator(s) it contains. The JSON syntax
 * is:
 * TODO: Proper syntax definition.
 * 
 * @author mpreslermarshall
 *
 */
public class WordGeneratorRegistry {
    public static WordGeneratorRegistry STANDARD = new WordGeneratorRegistry().setupStandard();
    
    protected Map<String, WordGenerator> generators = new HashMap<>();
    
    protected boolean debug = false;
    
    /** Create a new, empty word generator registry.
     * 
     */
    public WordGeneratorRegistry() {
    	this(false);
    }
    
    /* package-private */ WordGeneratorRegistry(boolean debug) {
    	this.debug = debug;
    }
    
    /**
     * Setup the word generator registry with the standard set of word generators.
     * 
     * @return
     */
    public WordGeneratorRegistry setupStandard() {
        return(this);
    }
    
    //
    // JSON APIs
    //
    
    /** 
     * Load a resource which contains JSON describing some word generators.
     * 
     * @param resourceName
     * @throws Exception
     */
    public void loadResource(String resourceName) throws Exception {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)))) {
            loadGeneratorsFromJson(reader, resourceName);
        }
    }
    
    /** 
     * Load the word generators specified by the JSON given.
     * 
     * @param jsonSource A JSON string that describes a WordGeneratorRegistry to load.
     * @throws IOException
     */
    public void loadGeneratorsFromJson(String jsonSource)
            throws IOException {
        Reader reader = new StringReader(jsonSource);
        
        loadGeneratorsFromJson(reader, "Input string");
    }
    
    /** Load the word generators specified by a Reader. The Reader is assumed to be reading
     * a JSON string that'll specify word generators to load.
     * 
     * @param input Reader accessing a JSON string that describes the word generators to load.
     * @param name The name of this word generator registry.
     * @throws IOException
     */
    public void loadGeneratorsFromJson(Reader input, String name) 
            throws IOException {
        String sourceText = IoUtils.readAll(input);
        JSONObject json = new JSONObject(sourceText);

        loadGeneratorsFromJson(json, name);
    }

    /** Load word generators specified by the JSON object given.
     * 
     * @param json
     * @param name
     * @throws IOException
     */
    public void loadGeneratorsFromJson(JSONObject json, String name) throws IOException {
        if (!json.has("generators")) {
            throw new IllegalArgumentException(
                    "Can't process the generator " + name + ", as it has no 'generators' entry.");
        }

        JSONArray generators = json.getJSONArray("generators");
        for (int i = 0; i < generators.length(); ++i) {
            JSONObject generator = generators.getJSONObject(i);
            loadGeneratorFromJson(generator, name);
        }
    }

    public void loadGeneratorFromJson(JSONObject generatorJson, String name) throws IOException {
        assert generatorJson != null;

        if (isDisabled(generatorJson)) {
            // This generator is disabled - nothing to do here.
            return;
        }

        // What kind of generator is requested?
        String generatorTypeString = getGeneratorType(generatorJson, name);
        if (debug)
            System.out.println("Type string: " + generatorTypeString);
        WordGeneratorType type = WordGeneratorType.from(generatorTypeString);

        // We'll need the name of the generator in order to store it in our lookup
        // table.
        String generatorName = generatorJson.getString("name");

        // Create the word generator
        WordGenerator generator = createGenerator(type, generatorName);

        // Now we have to load words into the generator. How we do this depends on the
        // type of generator and what info the source JSON gives us.
        switch (type) {
        case SINGLE_WORD: 
            {
                String singleWord = possiblyGetString(generatorJson, "word");
                SingleWordGenerator swg = (SingleWordGenerator) generator;

                if (singleWord == null) {
                    throw new IllegalArgumentException(
                            "For a single-word generator, the 'singleWord' parameter must not be null.");
                }
                swg.setWord(singleWord);
            }
            break;

        case UNWEIGHTED:
        case WEIGHTED: 
            {
                ExtensibleWordGenerator ewg = (ExtensibleWordGenerator) generator;

                List<Pair<String, Double>> wordList = possiblyGetList(generatorJson, "wordlist");

                if (wordList != null) {
                    loadWordGeneratorFromWordlist(ewg, wordList);
                } else {
                    String fileProcessingMode = generatorJson.has("file-processing-mode") ? generatorJson.getString("file-processing-mode") : null;
                    boolean trimWords = JSONUtils.extractBoolean(generatorJson, "trim-words").orElse(Boolean.TRUE).booleanValue();
                    String textCasing = JSONUtils.extractString(generatorJson, "casing", null);
                    if (generatorJson.has("resourcename")) {
                        loadWordGeneratorFromResource(ewg, 
                                generatorJson.getString("resourcename"), 
                                fileProcessingMode,
                                trimWords, 
                                textCasing);
                    } else if (generatorJson.has("filename")) {
                        loadWordGeneratorFromFile(ewg, 
                                generatorJson.getString("filename"), 
                                fileProcessingMode,
                                trimWords, 
                                textCasing);
                    }
                }
            }
            break;
        }
    }
    

    private void loadWordGeneratorFromWordlist(ExtensibleWordGenerator generator,
    		List<Pair<String, Double>> words) {
    	for (Pair<String, Double> entry: words) {
    		generator.addWord(entry.getFirst(), entry.getSecond());
    	}
    }

    //
    // Direct APIs: these allow loading a word generator of a specific type,
    //     giving all the data required to create such a word generator.
    //
    
    /** Load a single-word generator.
     * 
     * @param internalName
     * @param singleWord
     */
    public void loadSingleWordGenerator(String internalName,
    		String singleWord) {
    	if (singleWord == null) {
        	throw new IllegalArgumentException("For a single-word generator, the 'singleWord' parameter must not be null.");
        }
    	
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.SINGLE_WORD, internalName);

        // It ought to be a SingleWordGenerator, since that's what we asked for.
        assert generator instanceof SingleWordGenerator;        
        SingleWordGenerator swg = (SingleWordGenerator) generator;

        // Set its one word
        swg.setWord(singleWord);
    }
    
    /** Load an unweighted word generator.
     * 
     * @param internalName
     * @param singleWord
     */
    public void loadUnweightedWordGeneratorFromList(String internalName,
    		List<String> words) {
    	if (words == null) {
        	throw new IllegalArgumentException("To load a word generator from a list of words, the word list parameter must not be null.");
        }
    	
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.UNWEIGHTED, internalName);

        // It ought to be a UnweightedWordGenerator, since that's what we asked for.
        assert generator instanceof UnweightedWordGenerator;        
        UnweightedWordGenerator uwg = (UnweightedWordGenerator) generator;

        // Load the words
        for (String word: words) {
        	uwg.addWord(word);
        }
    }
    
    /** Load a weighted word generator using a list of words and weights.
     * 
     * @param internalName
     * @param singleWord
     */
    public void loadWeightedWordGeneratorFromList(String internalName,
    		List<Pair<String, Double>> words) {
    	if (words == null) {
        	throw new IllegalArgumentException("To load a word generator from a list of words, the word list parameter must not be null.");
        }
    	
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.WEIGHTED, internalName);

        // It ought to be a WeightedWordGenerator, since that's what we asked for.
        assert generator instanceof WeightedWordGenerator;        
        WeightedWordGenerator wwg = (WeightedWordGenerator) generator;

        // Load the words
        for (Pair<String, Double> pair: words) {
        	wwg.addWord(pair.getFirst(), pair.getSecond());
        }
    }

    //
    // Resource APIs: Load word generators from resources
    //
    
    /** Read a resource that contains a word list, and load it into the
     * given word generator.
     */
    public void loadUnweightedWordGeneratorFromResource(String name,
    		String resourceName,
    		String fileProcessingMode,
    		boolean trimLines,
    		String textCasingMode) throws IOException {
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.UNWEIGHTED, name);

        // It ought to be a UnweightedWordGenerator, since that's what we asked for.
        assert generator instanceof UnweightedWordGenerator;        
        UnweightedWordGenerator uwg = (UnweightedWordGenerator) generator;    	
    	
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)))) {
            loadWordGeneratorFromReader(uwg, reader, resourceName, fileProcessingMode, trimLines, textCasingMode);
        }
    }

    /** Read a resource that contains a word list, and load it into the
     * given word generator.
     */
    public void loadWeightedWordGeneratorFromResource(String name,
            String resourceName,
            String fileProcessingMode,
            boolean trimLines,
            String textCasingMode) throws IOException {
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.WEIGHTED, name);

        // It ought to be a UnweightedWordGenerator, since that's what we asked for.
        assert generator instanceof WeightedWordGenerator;
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)))) {
            loadWordGeneratorFromReader((ExtensibleWordGenerator) generator, reader, resourceName, fileProcessingMode, trimLines, textCasingMode);
        }
    }
    
    // 
    // File APIs: Load word generators from files
    //
    
    /** Read a file that contains a word list, and load it as an unweighted
     * word generator
     */
    public void loadUnweightedWordGeneratorFromFile(String name,
            String fileName,
            String fileProcessingMode,
            boolean trimLines,
            String textCasingMode) throws IOException {
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.UNWEIGHTED, name);

        // It ought to be a UnweightedWordGenerator, since that's what we asked for.
        assert generator instanceof UnweightedWordGenerator;        
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            loadWordGeneratorFromReader((ExtensibleWordGenerator) generator, reader, fileName, fileProcessingMode, trimLines, textCasingMode);
        }       
    }

    /** Read a file that contains a word list, and load it as an weighted
     * word generator
     */
    public void loadWeightedWordGeneratorFromFile(String name,
            String fileName,
            String fileProcessingMode,
            boolean trimLines,
            String textCasingMode) throws IOException {
        // Create the word generator
        WordGenerator generator = createGenerator(WordGeneratorType.WEIGHTED, name);

        // It ought to be a WeightedWordGenerator, since that's what we asked for.
        assert generator instanceof WeightedWordGenerator;        
        
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            loadWordGeneratorFromReader((ExtensibleWordGenerator) generator, reader, fileName, fileProcessingMode, trimLines, textCasingMode);
        }
    }

    /** Read a resource that contains a word list, and load it into the
     * given word generator.
     */
    private void loadWordGeneratorFromResource(ExtensibleWordGenerator generator,
    		String resourceName,
    		String fileProcessingMode,
    		boolean trimLines,
    		String textCasingMode) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(resourceName)))) {
            loadWordGeneratorFromReader(generator, reader, resourceName, fileProcessingMode, trimLines, textCasingMode);
        }
    }

    
    

    private void loadWordGeneratorFromFile(ExtensibleWordGenerator generator,
    		String fileName,
    		String fileProcessingMode,
    		boolean trimLines,
    		String textCasingMode) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            loadWordGeneratorFromReader(generator, reader, fileName, fileProcessingMode, trimLines, textCasingMode);            
        }
    }


    private void loadWordGeneratorFromReader(ExtensibleWordGenerator generator,
            BufferedReader reader,
            String sourceName,
            String fileProcessingMode,
            boolean trimLines,
            String textCasingMode) throws IOException {
        // Figure out how we're going to process this input.
        WordFileProcessingMode parsedProcessingMode;
        if ((fileProcessingMode == null) && (sourceName.endsWith(".csv"))) {
            parsedProcessingMode = WordFileProcessingMode.CSV;
        } else {
            parsedProcessingMode = WordFileProcessingMode.from(fileProcessingMode);
        }
        if (debug) System.out.println("Processing mode: " + parsedProcessingMode);
        TextCasing textCasing = TextCasing.from(textCasingMode);
        if (debug) System.out.println("Text casing: " + textCasing);
        
        // Read the content and load it into the generator.
        String line;
        while ((line = reader.readLine()) != null) {
            if (!StringUtils.isBlank(line)) {
                // OK, we have a line of input. Process it based on the reading mode.
                switch(parsedProcessingMode) {
                case UNPARSED:
                    if (trimLines) {
                        line = line.trim();
                    }
                    line = textCasing.apply(line);
                    generator.addWord(line);
                    break;
                case CSV:
                    processCsvLine(generator, line, 1, trimLines, parsedProcessingMode, textCasing);
                    break;
                case US_CENSUS_FIRSTNAMES:
                    processCsvLine(generator, line, 2, trimLines, parsedProcessingMode, textCasing);
                    break;
                case US_CENSUS_LASTNAMES:
                    processCsvLine(generator, line, 3, trimLines, parsedProcessingMode, textCasing);
                    break;
                }
            }
        }
    }
    
    private void processCsvLine(ExtensibleWordGenerator generator, 
            String line, 
            int weightIndex, 
            boolean trimLines,
            WordFileProcessingMode processingMode,
            UnaryOperator<String> tokenProcessor) {
        // A hack of a CSV implementation: just split based on commas.
        // At some point, fix my Maven config and then replace this with
        // Apache Commons CSV.
        String components[] = line.split(",");

        String text = components[0];
        if (trimLines) {
            text = text.trim();
        }
        text = tokenProcessor.apply(text);
        if (debug) System.out.println("Found text '" + text + "'.");

        // Now decide what to do with the components
        if (generator.getType() == WordGeneratorType.UNWEIGHTED) {
            // Simple - just add the text and ignore everything else.
        	if (debug) System.out.println("    Unweighted word - adding it directly.");
            generator.addWord(text);
        } else if (components.length <= weightIndex) {
            // We wanted a weight but there aren't enough components on the line
            // to provide one. We'll treat this as a weight of 1.
        	if (debug) System.out.println("    Not enough tokens to get a weight.");
            generator.addWord(text);
        } else if (!StringUtils.isNumber(components[weightIndex], true)) {
            // The weight field isn't a number. Skip this line.
        	if (debug) System.out.println("    The weight (" + components[weightIndex] + ") isn't a number - skipping!"); 
        } else if (processingMode.shouldSkipToken(components[0])) {
            // This is a line we should skip. Do so.
        } else {
            double weight = Double.parseDouble(components[weightIndex].trim());
            generator.addWord(text, weight);
        }
    }
    
    //
    // Utilities for extracting values from JSON objects
    //
    
    private String possiblyGetString(JSONObject source, String key) {
    	if (source.has(key)) {
    		return source.getString(key);
    	} else {
    		return null;
    	}
    }
    
    private List<Pair<String, Double>> possiblyGetList(JSONObject source, String key) {
    	if (source.has(key)) {
    		if (debug) System.out.println("Parsing word list under key " + key);
            JSONArray array = source.getJSONArray(key);
            int total = array.length();
            List<Pair<String, Double>> result = new ArrayList<>(total);
            
            for (int i = 0; i < total; ++i) {
                Object o = array.get(i);
                if ((o instanceof String) || (o instanceof JSONString)) {
                	if (debug) System.out.println("    Found '" + o.toString() + "'.");
                    result.add(new Pair<String, Double>(o.toString(), 1.0));
                } else if (o instanceof JSONObject) {
                	if (debug) System.out.println("    Found a JSON object.");
                    JSONObject entry = (JSONObject) o;
                    String keys[] = JSONObject.getNames(entry);
                    
                    for (String subKey: keys) {                    	
                        double weight = entry.getDouble(subKey);
                        result.add(new Pair<String, Double>(subKey, weight));
                    }                                
                } else {
                    throw new IllegalArgumentException("Don't know how to handle an object of type " + o.getClass().getName() + " in a wordlist array!");
                }
            }
            return result;
    	} else {
    		return null;
    	}
    }

    
    //
    // Field parsers
    //
    

    
    /** Get the type of word generator that this JSON object is describing.
     * This may be explicitly stated by the JSON, or we may infer it from the
     * other properties it has.
     * 
     * @param generatorJson
     * @param name
     * @return
     */
    private String getGeneratorType(JSONObject generatorJson, String name) {
        // If the generator explicitly says its type, use that.
        if (generatorJson.has("generator-type")) {
            return(generatorJson.getString("generator-type"));
        } else if (generatorJson.has("weights")) {
        	return generatorJson.getString("weights");
        }
        
        // If it has a "word" property, assume it's a single-word generator
        if (generatorJson.has("word")) {
            return("single-word");
        }
        
        // Otherwise assume it's an unweighted generator.
        return("unweighted");
    }

    
    /** Does the source JSON for this word generator indicate that it's disabled?
     * 
     * @param generatorJson The generator JSON to process
     * @param name The name of the generator being processed
     * @return True if the JSON contains "enabled": "false". False otherwise.
     */
    /* package-private */ static boolean isDisabled(JSONObject generatorJson) {
        
        Optional<Boolean> result = JSONUtils.extractBoolean(generatorJson, "enabled");
        if (result.isPresent()) {
        	return !(result.get().booleanValue());
        }
        
        result = JSONUtils.extractBoolean(generatorJson, "disabled");
        if (result.isPresent()) {
        	return result.get().booleanValue();
        }
        return(false);
    }
    
    /** Create a word generator with the specified name and type, then store it in
     * our lookup table. This does not load any content into the generator.
     * 
     * @param type Type of word generator to create
     * @param name Name of the generator to use in our lookup table of word generators.
     * @return The newly-created word generator.
     */
    private WordGenerator createGenerator(WordGeneratorType type, String name) {
    	if (name == null) {
    		throw new IllegalArgumentException("Word generators must not have a null name.");
    	}
    	
        // Create the word generator
        WordGenerator generator = WordGeneratorType.create(type, name);
        // Store it in the lookup table
        generators.put(name, generator);
        
        return generator;
    }    	
    
    
    /** Get the number of word generators contained in this registry.
     * 
     * @return The number of generators
     */
    public int getNumGenerators() {
        return(generators.size());
    }

    /** Retrieve a word generator based on the name it was defined with.
     * If there's no such generator, you'll get null.
     * 
     * @param name The name of the generator to be retrieved
     * @return The generator that was defined with that name, or null if
     *     there isn't one.
     */
    public WordGenerator getGeneratorByName(String name) {
        return(generators.get(name));
    }
    
    public Set<String> getGeneratorNames() {
        return(Collections.unmodifiableSet(generators.keySet()));
    }


}
