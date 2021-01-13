package com.salesforce.rcg.util;

public class Pair<F, S> {
	private F first;
	private S second;
	
	public Pair(F f, S s) {
		first = f;
		second = s;
	}
	
	public F getFirst() {
		return first;
	}
	
	public S getSecond() {
		return second;
	}
	
	public boolean equals(Object o) {
		if (o instanceof Pair<?, ?>) {
			Pair<?, ?> other = (Pair<?, ?>) o;
			if (first == null) {
				if (other.first != null) {
					return false;
				}
			} else {
				if (!first.equals(other.first)) {
					return false;
				}
			}
			
			if (second == null) {
				if (other.second != null) {
					return false;
				}
			} else {
				if (!second.equals(other.second)) {
					return false;
				}
			}
			
			return true;
			
		} else {
			return false;
		}
	}
	
	public String toString() {
	    return '[' + first.toString() + ',' + second.toString() + ']';
	}
}
