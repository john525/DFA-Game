package com.slayerz.dfagame;

import org.apache.commons.lang3.builder.HashCodeBuilder; 

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
    
    public void setChars(String l) {
    	chars = l;
    }

    public boolean accepts(String l) {
        return chars.contains(l);
    }

    @Override
    // THIS JUST REQUIRES TWO TRANSITIONS TO START AND END FROM THE SAME STATES
    public boolean equals(Object o) {
        if (o instanceof Transition) {
            Transition t = (Transition) o;
            return start == t.getStart() && end == t.getEnd();
        }
        return false;
    }

    // THIS REQUIRES TWO TRANSITIONS TO GO BETWEEN THE SAME STATES AND HAVE SAME CHARACTERS
    public boolean fullyEquals(Transition t) {
    	return start == t.getStart() && end == t.getEnd() && chars.equals(t.getChars());
    }
    
    @Override
    // NOTE THIS IS NOT WHAT YOU THINK IT IS
    // HASH CODES HAVE THE SAME HASH CODE WHEN THEY GO BETWEEN SAME STATES
    // COMPARE WITH fullyEquals ABOVE
    public int hashCode() {
    	return new HashCodeBuilder(13, 59).append(start).append(end).toHashCode();
    }
}