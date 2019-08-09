package com.salesforce.rcg.text.impl;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Test;

import com.salesforce.rcg.text.ExtensibleWordGenerator;

/** Performance tests for word generators. These tests are experimental and will 
 * not cause unit test failures.
 * 
 * @author mpreslermarshall
 *
 */
public class PerformanceTest {
    public static final double LONG_TEST_MS = 6_000.0;
    public static final int NUM_WORDS = 20_000;

    /** Performance test: weighted generator vs. unweighted generator (why do I
     * feel like I'm writing the Computer Science version of "Shark vs Train"?)
     * 
     * The entire reason the UnweightedWordGenerator exists is that it professes to
     * give better performance than a WeightedWordGenerator where every field has a 
     * weight of 1. This test lets me see if that assertion is generally true.
     * 
     * Note that this is *not* actually being checked using some kind of JUnit-level
     * assertion. This is intentional. We have no control over what's happening on
     * the host system when this build is happening - so the results might be badly 
     * skewed by that other activity. Instead we print results to stdout, and
     * let the developer see, when they choose to check, what the difference is. 
     * 
     */
    @Test
    public void unweightedWordGeneratorPerfTest() {
        Map<String, Double> durations;
        long iterations = 1000L;
        
        do {
            Map<String, ExtensibleWordGenerator> generators = new TreeMap<>();
            durations = new TreeMap<>();
            generators.put("weighted", new WeightedWordGenerator());
            generators.put("unweighted", new UnweightedWordGenerator());
            
            runTest(iterations, generators, durations);
            
            iterations *= 4L;
        } while (isShortTest(iterations, durations));
        
        double durationWeighted = durations.get("weighted");
        double durationUnweighted = durations.get("unweighted");
        
        DecimalFormat df0 = new DecimalFormat("#,##0");
        DecimalFormat df1 = new DecimalFormat("#,##0.0");
        DecimalFormat dfp = new DecimalFormat("0.0%");
        
        System.out.println("*** Unweighted word generator performance test: " + df0.format(iterations) + " iterations:");
        System.out.println("    Weighted generator time:   " + df1.format(durationWeighted) + " ms.");
        System.out.println("    Unweighted generator time: " + df1.format(durationUnweighted) + " ms.");
        double unweightedImprovement = (durationWeighted - durationUnweighted) / durationWeighted;
        System.out.println("    Unweighted improvement:    " + dfp.format(unweightedImprovement));
        
    }
    
    private void runTest(long iterations, 
            Map<String, ExtensibleWordGenerator> generators,
            Map<String, Double> durations) {
        List<String> generatorTypes = new ArrayList<>(generators.size());
        
        // Create the generators and populate them. Also generate a single word from
        // each one to give it the chance to initialize itself if needed.
        for (Map.Entry<String, ExtensibleWordGenerator> entry: generators.entrySet()) {
            // Store the type for later use
            generatorTypes.add(entry.getKey());
            
            // Retrieve the generator
            ExtensibleWordGenerator generator = entry.getValue();
            
            // Fill it up
            for (int i = 0; i < NUM_WORDS; ++i) {
                String word = "word-" + i;
                generator.addWord(word);
            }
            
            // Prime the generator by generating a single word
            generator.generateWord();                
        }
        
        for (String type: generatorTypes) {
            ExtensibleWordGenerator generator = generators.get(type);
            
            long start = System.nanoTime();
            for (long i = 0; i < iterations; ++i) {
                generator.generateWord();
            }
            long end = System.nanoTime();
            
            double duration = (end - start) / 1_000_000.0;
            durations.put(type, duration);
        }
    }
    
    private boolean isShortTest(long iterations, Map<String, Double> durations) {
        double longestDuration = 0.0;

        // Find whichever test took longest.
        for (Map.Entry<String, Double> entry: durations.entrySet()) {
            Double duration = entry.getValue();
            if (duration.doubleValue() > longestDuration) {
                longestDuration = duration.doubleValue();
            }
        }
        
        System.out.println("For " + iterations + " iterations, the longest test took " 
            + longestDuration + " ms.");
        
        if (longestDuration >= LONG_TEST_MS) {
            return(false);
        } else {
            return(true);
        }
    }
}
