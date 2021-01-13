package com.salesforce.rcg;

/**
 * An index of the demo applications provided with the random content generator.
 *
 */
public class Demos {
    public static void main(String[] args) {
        System.out.println("Random content generator version " + Constants.VERSION);
        
        System.out.println();
        
        System.out.println("List of content generator demo programs:");
        System.out.println("    com.salesforce.rcg.demos.WordGeneratorRegistryDemo");
        
        System.out.println();
        
        System.out.println("To run any of these demo programs, run them from the command line.");
        System.out.println("For example, to run the WordGeneratorRegistryDemo, do:");
        System.out.println("    java -jar rcg-" + Constants.VERSION + ".jar com.salesforce.rcg.demos.WordGeneratorRegistryDemo");
    }
}
