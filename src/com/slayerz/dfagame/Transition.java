package com.slayerz.dfagame;

public class Transition {

    private State start;
    private State end;
    private String chars;

    public Transition(State s, State e, String c) {
        start = s;
        end = e;
        chars = c;
    }

    public State getStart() {
        return start;
    }

    public State getEnd() {
        return end;
    }

    public boolean connectsTo(State s) {
        return start == s || end == s;
    }

    public String getChars() {
        return chars;
    }

    public boolean accepts(String l) {
        return chars.contains(l);
    }
    
    @Override
    public boolean equals(Object o){
    	if(o instanceof Transition){
    		Transition t = (Transition) o;
    		return start == t.getStart() && end == t.getEnd() && chars == t.getChars(); 
    	}
    	return false;
    }
}