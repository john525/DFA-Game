package com.slayerz.dfagame;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFA {
	private State start;
	private Map<Coord, State> states;
	private Set<Transition> transitions;
	
	public static final int CLICK_RAD = 3*State.RAD;
	
	public DFA() {
		start = new State(false);
		states = new HashMap<Coord, State>();
		states.put(new Coord(1,1), start);
		transitions = new HashSet<Transition>();
	}
	
	public boolean acceptsString(String s) {
		return false;
	}
	
	public Map<String, Boolean> testOnAll() {
		int pumpingLength = states.size() + 1;
		for(int len=0; len<=pumpingLength; len++) {
			//generate all strings of length len and test using acceptsString()
		}
		return null;
	}
	
	public Coord locateState(State q) {
		for(Coord c : states.keySet()) {
			if(q == states.get(c)) {
				return c;
			}
		}
		return null;
	}
	
	public void draw(Graphics2D g2d) {
		//draw arrow to start state;
		
		for(Coord c : states.keySet()) {
			states.get(c).draw(c.getR(), c.getC(), g2d);
		}
		for(Transition t : transitions) {
			Coord start = locateState(t.getStart());
			Coord end = locateState(t.getEnd());
			int x = start.c*Game.BOX_DIM;
			int y = start.r*Game.BOX_DIM;
			int xf = end.c*Game.BOX_DIM;
			int yf = end.r*Game.BOX_DIM;
			g2d.setPaint(Color.BLACK);
			g2d.drawLine(x,y,xf,yf);
		}
	}
	
	public void handleClick(double x, double y) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		System.out.println(dist);
		if(dist < CLICK_RAD) {
			addState(r, c, new State(false));
		}
	}

	public boolean addState(int r, int c, State state) {
		//check if state is already at state.r, state.c
		if(states.containsKey(new Coord(r, c))) {
			return false;
		}
		states.put(new Coord(r, c), state);
		return true;
	}
	
	public void removeState(int r, int c) {
		if(r==1 && c==1) {
			return;
		}
		System.out.println(states.remove(new Coord(r,c)));
	}
	
	public void addTransition(int r, int c, int rf, int cf, String str) {
		State q1 = states.get(new Coord(r, c));
		State q2 = states.get(new Coord(rf, cf));
		if(q1 == null || q2 == null) return;
		transitions.add(new Transition(q1, q2, str));
	}
	
	public boolean onStateSpace(int x, int y) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		return dist < CLICK_RAD;
	}
	
	public boolean onState(int x, int y) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		return dist < CLICK_RAD && (states.get(new Coord(r, c)) != null);
	}
	
	public void handleCtrlClick(int x, int y) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		if(dist < CLICK_RAD) {
			removeState(r,c);
		}
	}

	public void handleAltClick(int x, int y) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		if(dist < CLICK_RAD) {
			states.get(new Coord(r,c)).toggleAccept();
		}
	}

	public void handleDrag(int x, int y, int xf, int yf) {
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		double xGrid = Game.BOX_DIM*c;
		double yGrid = Game.BOX_DIM*r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		if(dist > CLICK_RAD) {
			return;
		}
		int rf = (int)Math.round(yf/Game.BOX_DIM);
		int cf = (int)Math.round(xf/Game.BOX_DIM);
		xGrid = Game.BOX_DIM*cf;
		yGrid = Game.BOX_DIM*rf;
		dx=Math.abs(xf-xGrid);
		dy=Math.abs(yf-yGrid);
		dist = Math.sqrt(dx*dx + dy*dy);
		if(dist > CLICK_RAD) {
			return;
		}
		addTransition(r,c,rf,cf,"0");
	}
	
	public void handleShiftDrag(int x, int y, int xf, int yf) {
		
	}
	
	private class Coord {
		private int r;
		private int c;
		
		public Coord(int r, int c) {
			this.r=r;
			this.c=c;
		}
		
		public int getR() { return r; }
		public int getC() { return c; }
		
		@Override
		public int hashCode() {
			return (r+c) % 7;
		}
		
		@Override
		public boolean equals(Object o) {
			if(o instanceof Coord) {
				Coord k = (Coord) o;
				return r==k.getR() && c==k.getC();
			}
			return false;
		}
	}

}
