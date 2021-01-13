package com.salesforce.rcg.util;

import java.util.Optional;

public class StringUtils {
    public static final String TRUE_ALIASES[] = {
            "true",
            "yes",
            "enabled",
            "on", 
            "1"
    };
    
    public static final String FALSE_ALIASES[] = {
            "false",
            "no",
            "disabled",
            "off",
            "0"
    };

    /** Parse a String as a boolean value. This is case-insensitive and has several aliases for
     * both "true" and "false". See TRUE_ALIASES for supported <tt>true</tt> values, and
     * FALSE_ALIASES for supported <tt>false</tt> values. Strings which match none of the above,
     * or null/empty strings, will return a not-present value.
     * 
     * @param text The string to parse
     * @return An Optional containing the parsed value.
     */
    public static Optional<Boolean> parseBoolean(String text) {
        if (null == text) {
            return(Optional.empty());
        }
        for (String s: TRUE_ALIASES) {
            if (s.equalsIgnoreCase(text)) {
                return(Optional.of(Boolean.TRUE));
            }
        }
        for (String s: FALSE_ALIASES) {
            if (s.equalsIgnoreCase(text)) {
                return(Optional.of(Boolean.FALSE));
            }
        }
        return(Optional.empty());
    }

    /** Parse a boolean value, with a default value to use if the supplied string
     * is not a valid boolean value.
     * 
     * @param text The text to parse. This will be parsed using the 1-argument
     *     version of <tt>parseBoolean</tt>
     * @param defaultValue Default value to use if the supplied String is not a 
     *     recognized boolean value.
     * @return
     */
    public static boolean parseBoolean(String text, boolean defaultValue) {
        Optional<Boolean> parsed = parseBoolean(text);
        if (parsed.isPresent()) {
        	return parsed.get().booleanValue();
        } else {
        	return defaultValue;
        }
    }
    
    public static boolean isBlank(String s) {
        if (null == s) {
            return true;
        }
        
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!Character.isWhitespace(c)) {
                return(false);
            }
        }
        
        return true;
    }

    public static boolean isInteger(String s) {
    	return isInteger(s, false);
    }
    
    public static boolean isInteger(String s, boolean trim) {
        if ((null == s) || (s.length() == 0)) {
            return(false);
        }
        
        if (trim) {
        	s = s.trim();
        }
        
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (!Character.isDigit(c)) {
                return(false);
            }
        }
        
        return(true);
    }
    
    /** Does the supplied string represent a number? This allows integers and decimal
     * numbers (only 1 decimal point allowed), but commas are not allowed in numbers.
     * 
     * @param s The string to test
     * @param trim If true, whitespace will be trimmed from both ends of the supplied
     *     string
     * @return true if the supplied string represents a number, and false otherwise.
     */
    public static boolean isNumber(String s, boolean trim) {
    	if (null == s) {
            return(false);
        }
        
        if (trim) {
        	s = s.trim();
        }
        
        if (s.length() == 0) {
        	return false;
        }
        
        int numDecimals = 0;
        
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                // This is OK
            } else if (c == '.') {
            	if (++numDecimals > 1) {
            		// Nope, can't have more than one decimal. These are numbers, not semvers.
            		return false;
            	}
            } else {
            	return false;
            }
        }
        
        return(true);
    }

    /** Capitalize the first letter of the given string, and convert the rest
     * to lower case.
     * 
     * @param s The string to capitalize
     * @return null if the supplied string was null, or the string with its first
     *     letter capitalized and the rest in lowercase. 
     */
    public static String capitalizeFirst(String s) {
        if (s == null) {
            return(null);
        } else if (s.length() <= 1) {
            return(s.toUpperCase());
        } else {
            return Character.toUpperCase(s.charAt(0)) +
                    s.substring(1).toLowerCase();
        }
    }
}
