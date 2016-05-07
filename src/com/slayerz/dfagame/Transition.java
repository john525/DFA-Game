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
	
	public String getChars() {
		return chars;
	}
	
	public boolean accepts(String l) {
		return chars.contains(l);
	}
}
