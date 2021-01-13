package com.salesforce.rcg.util;

import java.util.Optional;

import org.json.JSONObject;

public class JSONUtils {

    public static Optional<Boolean> extractBoolean(JSONObject obj, String key) {
    	if (null == obj) {
    		return Optional.empty();
    	}
    	
        // If the key is not present, indicate that
        if (!obj.has(key)) {
            return(Optional.empty());
        } 
        Object value = obj.get(key);
        if (value instanceof Boolean) {
            Boolean booleanValue = (Boolean) value;
            return(Optional.of(booleanValue));
        } else if (value instanceof String) {
            return(StringUtils.parseBoolean((String) value));
        }
        
        // Not a boolean, not a parseable String - we give up.
        return(Optional.empty());
    }
    
    public static boolean extractBoolean(JSONObject obj, String key, boolean defaultValue) {
        Optional<Boolean> extracted = extractBoolean(obj, key);
        if (extracted.isPresent()) {
        	return extracted.get().booleanValue();
        } else {
        	return defaultValue;
        }
    }
    
    public static String extractString(JSONObject obj, String key, String defaultValue) {
        if (obj.has(key)) {
            return(obj.getString(key));
        }
        return(defaultValue);
    }
}
