package com.salesforce.rcg.text;

import java.util.function.UnaryOperator;

import com.salesforce.rcg.util.StringUtils;

/** Text casings specify how a string should be converted, such as converting it
 * to uppercase or lowercase.
 * 
 * @author mpreslermarshall
 *
 */
public enum TextCasing implements UnaryOperator<String> {

    UNMODIFIED("unmodified", s -> s),
    LOWERCASE("lowercase", s -> s.toLowerCase()),
    UPPERCASE("uppercase", s -> s.toUpperCase()),
    CAPITALIZE_FIRST("capitalize-first", s -> StringUtils.capitalizeFirst(s));
    
    public static final TextCasing DEFAULT_CASING = UNMODIFIED;    
    
    /** The name of this string casing. */
    protected final String name;
    protected final UnaryOperator<String> implementation;
    
    private TextCasing(String name, UnaryOperator<String> op) {
        this.name = name;
        implementation = op;
    }
    
    public String apply(String source) {
        return(implementation.apply(source));
    }
    
    public static TextCasing from(String source) {
        if (null == source) {
            return(DEFAULT_CASING);
        }
        
        for (TextCasing c: TextCasing.values()) {
            if (c.name.equalsIgnoreCase(source)) {
                return(c);
            }
        }
        
        throw new IllegalArgumentException("Unrecognized text casing: " + source);
    }
}
