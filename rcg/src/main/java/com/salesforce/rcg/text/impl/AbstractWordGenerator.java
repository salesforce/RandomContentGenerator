package com.salesforce.rcg.text.impl;

import com.salesforce.rcg.text.WordGenerator;

public abstract class AbstractWordGenerator implements WordGenerator {
    protected final String name;
    
    public AbstractWordGenerator(String name) {
        this.name = name;
    }
    
    public String getName() {
        return(name);
    }
    

}
