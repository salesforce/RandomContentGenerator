package com.salesforce.rcg.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CounterMap<K> {
    
    protected Map<K, MutableInteger> counts;
    protected int total;
    
    public CounterMap() {
        counts = new HashMap<K, MutableInteger>();
    }
    
    public void add(K key) {
        add(key, 1);
    }
    
    public void add(K key, int amount) {
        MutableInteger current = counts.get(key);
        if (null == current) {
            current = new MutableInteger();
            counts.put(key, current);
        }
        current.increment(amount);
        total += amount;
    }
    
    public int getTotal() {
        return(total);
    }
    
    public int get(K key) {
        Number currentValue = counts.get(key);
        if (null == currentValue) {
            return(0);
        }
        return currentValue.intValue();
    }
    
    /** Get the keys in the map, sorted by the weights that have been assigned 
     * to them. The highest-weighted item will be first in the list. 
     * @return
     */
    public List<K> getSortedKeys() {
        return getSortedKeys(new InternalSorter());
    }
    
    /** Get the keys in the map, sorted according to the provided Comparator.
     * If the provided comparator is null, then the keys will be placed in
     * the list in an arbitrary order.
     * 
     * @param sorter
     * @return
     */
    public List<K> getSortedKeys(Comparator<K> sorter) {
        // Collect all the keys
        List<K> keys = new ArrayList<>(counts.size());        
        keys.addAll(counts.keySet());
        
        // Sort them if requested
        if (sorter != null) {
            Collections.sort(keys, sorter);
        }
        
        return(keys);
    }
    
    protected class InternalSorter implements Comparator<K> {

        @Override
        public int compare(K o1, K o2) {
            int count1 = get(o1);
            int count2 = get(o2);
            
            return count2 - count1;
        }
    }

}
