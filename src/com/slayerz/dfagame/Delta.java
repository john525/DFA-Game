package com.slayerz.dfagame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the transition function of a DFA.
 */
public class Delta {
    private List<Rule> transitionRules;

    public Delta() {
        transitionRules = new ArrayList<Rule>();
    }

    public void AddRule(Transition transition) {
        char[] symbols = transition.getChars().toCharArray();
        for (char c : symbols) {
            transitionRules.add(new Rule(transition.getStart(), transition.getEnd(), c));
        }
    }

    public void RemoveRule(Transition transition) {
        Iterator<Rule> i = transitionRules.iterator();

        char[] symbols = transition.getChars().toCharArray();
        for (char c : symbols) {
            while (i.hasNext()) {
                Rule r = i.next();

                if (r.getStart().equals(transition.getStart()) &&
                        r.getEnd().equals(transition.getEnd()) &&
                        r.getSymbol() == c) {
                    transitionRules.remove(r);
                }
            }
        }
    }

    public State GetNextState(State currentState, char symbol) {
        for (Rule r : transitionRules) {
            if (r.getStart().equals(currentState) && r.getSymbol() == symbol) {
                return r.getEnd();
            }
        }

        return null;
    }


    private class Rule {
        public State start;
        public State end;
        public char symbol;

        public Rule(State start, State end, char symbol) {
            this.start = start;
            this.end = end;
            this.symbol = symbol;
        }

        public State getStart() { return start; }

        public State getEnd() { return end; }

        public char getSymbol() { return symbol; }
    }
}