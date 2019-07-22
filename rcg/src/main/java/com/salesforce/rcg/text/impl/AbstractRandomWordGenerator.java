package com.salesforce.rcg.text.impl;

import java.util.Random;

public abstract class AbstractRandomWordGenerator extends AbstractWordGenerator {
    protected Random rng;
    
    public AbstractRandomWordGenerator(String name) {
        super(name);
        rng = new Random();
    }
    
    public AbstractRandomWordGenerator(String name, Random rng) {
        super(name);
        this.rng = rng;
    }
    
    public Random getRng() {
        return(rng);
    }
    
    public AbstractRandomWordGenerator setRng(Random rng) {
        this.rng = rng;
        return(this);
    }


}
