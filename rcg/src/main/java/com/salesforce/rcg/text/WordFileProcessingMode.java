package com.salesforce.rcg.text;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public enum WordFileProcessingMode {
    CSV("csv", null),
    UNPARSED("unparsed", null),
    US_CENSUS_FIRSTNAMES("us-census-first", null),
    US_CENSUS_LASTNAMES("us-census-last", Arrays.asList(new String[] {"ALL OTHER NAMES"}));
    
    /** The default mode to use when reading wordlist files. */
    public static final WordFileProcessingMode DEFAULT_FILE_PROCESSING_MODE = UNPARSED;

    protected final String name;
    protected final Set<String> skipTokens;
    
    private WordFileProcessingMode(String name, Collection<String> skipTokenInput) {
        this.name = name;
        if (skipTokenInput == null) {
            skipTokens = null;
        } else {
            skipTokens = new HashSet<>(skipTokenInput.size() * 2 + 1);
            for (String st: skipTokenInput) {
                skipTokens.add(st.toLowerCase());
            }
        }
    }
    
    public String getName() {
        return(name);
    }
    
    public boolean shouldSkipToken(String token) {
        if (skipTokens != null) {
            return(skipTokens.contains(token.toLowerCase()));
        }
        return(false);
    }

    public static WordFileProcessingMode from(String modeString) {
        if (null == modeString) {
            return DEFAULT_FILE_PROCESSING_MODE;
        }
        
        for (WordFileProcessingMode mode: WordFileProcessingMode.values()) {
            if (modeString.equalsIgnoreCase(mode.getName())) {
                return(mode);
            }
        }
        
        throw new IllegalArgumentException("Unrecognized word file processing mode: " + modeString);
    }
}
