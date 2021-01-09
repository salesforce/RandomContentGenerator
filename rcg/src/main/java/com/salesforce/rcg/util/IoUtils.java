package com.salesforce.rcg.util;

import java.io.IOException;
import java.io.Reader;

public class IoUtils {

    /** Read everything from a Reader into a String. 
     * 
     * @param rd The Reader to read from
     * @return A string containing everything read from the Reader.
     * @throws IOException This will be thrown if there's a problem reading the input.
     */
    public static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

}
