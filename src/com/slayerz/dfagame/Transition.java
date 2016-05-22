package com.slayerz.dfagame;

public class Transition {

    private State start;
    private State end;
    private String chars;
    private Alphabet symbol;

    public Transition(State s, State e, String c, Alphabet transition) {
        start = s;
        end = e;
        chars = c;
        symbol = transition;
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

    public Alphabet getSymbol() {
        return symbol;
    }

    public boolean accepts(String l) {
        return chars.contains(l);
    }
}