package com.slayerz.dfagame;

import java.util.ArrayList;

/**
 * Represents the transition function of a DFA.
 */
public class Delta {
    private ArrayList<Rule> transitionRules;

    public Delta() {
        transitionRules = new ArrayList<>();
    }

    public void AddRule(Transition transition) {
        transitionRules.add(new Rule(transition.getStart(), transition.getEnd(), transition.getChars()));
    }

    public State GetNextState(State currentState, String symbol) {
        for (Rule r : transitionRules) {
            if (r.getStart().equals(currentState) && r.getSymbol().equals(symbol)) {
                return r.getEnd();
            }
        }

        return null;
    }


    private class Rule {
        public State start;
        public State end;
        public String symbol;

        public Rule(State start, State end, String symbol) {
            this.start = start;
            this.end = end;
            this.symbol = symbol;
        }

        public State getStart() { return start; }

        public State getEnd() { return end; }

        public String getSymbol() { return symbol; }
    }
}
