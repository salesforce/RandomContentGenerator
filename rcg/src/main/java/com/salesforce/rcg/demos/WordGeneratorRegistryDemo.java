package com.salesforce.rcg.demos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.salesforce.rcg.text.WordGenerator;
import com.salesforce.rcg.text.WordGeneratorRegistry;

/**
 * An index of the demo applications provided with the random content generator.
 *
 */
public class WordGeneratorRegistryDemo {
    private WordGeneratorRegistryDemo() {
    }
    
    public static void main(String[] args) throws IOException {
        WordGeneratorRegistryDemo instance = new WordGeneratorRegistryDemo();
        instance.run(args);
    }
    
    public static String DEFAULT_RESOURCES[] = {
        "data/registries/Registry-Sample.json"
    };
    
    protected void run(String args[]) throws IOException {
        System.out.println("Word generator registry demo");
        
        List<String> registryFiles = new ArrayList<>();
        if (args.length == 0) {
            registryFiles.addAll(Arrays.asList(DEFAULT_RESOURCES));
        } else {
            // At some point, put real command-line parsing here.
            // MPM BUG: This is a file, not a resource
            registryFiles.addAll(Arrays.asList(args));
        }
        

        WordGeneratorRegistry registry = new WordGeneratorRegistry();

        System.out.println("Loading resources...");
        for (String resource: registryFiles) {
            try {
                registry.loadResource(resource);
            } catch (Exception e) {
                System.out.println("Problem loading resource '" + resource + "': " + e);
                e.printStackTrace(System.out);
                System.exit(1);
            }
        }
        System.out.println("...done loading resources; loaded " + registry.getNumGenerators() + " generators.");
        System.out.println();
        
        System.out.println("List of generators by name:");
        for (String generatorName: registry.getGeneratorNames()) {
            WordGenerator generator = registry.getGeneratorByName(generatorName);
            System.out.println("    " + generatorName + ": " + generator);
            System.out.println("        sample words: " + generator.generateWord() 
                + ", " + generator.generateWord() + ", " + generator.generateWord());
        }

    }
}
