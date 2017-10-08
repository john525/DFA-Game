package com.slayerz.dfagame;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.builder.HashCodeBuilder; 

/**
 * Represents the transition function of a DFA.
 */
public class Delta {
    private List<Rule> transitionRules;

    public Delta() {
        transitionRules = new ArrayList<Rule>();
    }

    public void addRule(Transition transition) {
        char[] symbols = transition.getChars().toCharArray();
        for (char c : symbols) {
            transitionRules.add(new Rule(transition.getStart(), transition.getEnd(), c));
        }
    }

    public void removeRule(Transition transition) {
        char[] symbols = transition.getChars().toCharArray();
        
        for (char c : symbols) {
        	for (int i = 0; i < transitionRules.size(); i++) {
        		Rule r = transitionRules.get(i);
        		
                if (r.getStart().equals(transition.getStart()) &&
                        r.getEnd().equals(transition.getEnd()) &&
                        r.getSymbol() == c) {
                    transitionRules.remove(r);
                    i--;
                }
        	}
        }
    }

    /**
     * Given a state and a symbol within the alphabet of the DFA, returns the next state you'd navigate to.
     * delta(q, symbol in alphabet)
     *
     * @param currentState The state input of the described function.
     * @param symbol The symbol input of the described function.
     * @return The output of the described function.
     */
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

        public State getStart() {
            return start;
        }

        public State getEnd() {
            return end;
        }

        public char getSymbol() {
            return symbol;
        }
        
        @Override
        public boolean equals(Object o) {
        	if (o instanceof Rule){
        		Rule r = (Rule) o;
        		return start == r.getStart() && end == r.getEnd() && symbol == r.getSymbol();
        	}
        	return false;
        }
        
        @Override
        public int hashCode() {
        	return new HashCodeBuilder(17, 97).append(start).append(end).append(symbol).toHashCode();
        }
    }
}