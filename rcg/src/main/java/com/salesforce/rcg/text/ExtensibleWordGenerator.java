package com.salesforce.rcg.text;

public interface ExtensibleWordGenerator extends WordGenerator {
    public void addWord(String text);
 
    public void addWord(String text, double weight);
}
