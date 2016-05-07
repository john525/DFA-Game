package com.slayerz.dfagame;

import java.awt.BasicStroke;
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
			g2d.setStroke(new BasicStroke(4));
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
	
	public void removeState(Coord loc) {
		if(loc.r==1 && loc.c==1) {
			return;
		}
		System.out.println(states.remove(loc));
	}
	
	public void addTransition(Coord loc, Coord locf, String str) {
		State q1 = states.get(loc);
		State q2 = states.get(locf);
		if(q1 == null || q2 == null) return;
		transitions.add(new Transition(q1, q2, str));
	}
	
	/**
	 * Finds the coordinates (in terms of rows and columns on the 5x5 grid) of the nearest grid space to the specified point.
	 * @param x_int Integer x coordinate
	 * @param y_int Integer y coordinate.
	 * @return 
	 */
	public Coord nearestGridSpace(int x_int, int y_int) {
		double x = x_int;
		double y = y_int;
		int r = (int)Math.round(y/Game.BOX_DIM);
		int c = (int)Math.round(x/Game.BOX_DIM);
		return new Coord(r, c);
	}
	
	/**
	 * Tests whether a point (x,y) is sufficiently close to a grid space (within DFA.CLICK_RAD) for the click to be registered as
	 * affecting that grid space.
	 * @param x the x coordinate.
	 * @param y the y coordinate
	 * @return true if (x,y) is within DFA.CLICK_RAD of the nearest grid space
	 */
	public boolean onStateSpace(int x, int y) {
		Coord loc = nearestGridSpace(x,y);
		double xGrid = Game.BOX_DIM*loc.c;
		double yGrid = Game.BOX_DIM*loc.r;
		double dx=Math.abs(x-xGrid);
		double dy=Math.abs(y-yGrid);
		double dist = Math.sqrt(dx*dx + dy*dy);
		return dist < CLICK_RAD;
	}
	
	/**
	 * checks whether the user clicked a state on the grid.
	 * @param x the x coordinate.
	 * @param y the y coordinate
	 * @return true if (x,y) is within DFA.CLICK_RAD of the nearest grid space AND there is a state at that grid space
	 */
	public boolean onState(int x, int y) {
		Coord loc = nearestGridSpace(x,y);
		return onStateSpace(x,y) && (states.get(loc) != null);
	}
	
	public void handleCtrlClick(int x, int y) {
		if(onState(x,y)) {
			Coord loc = nearestGridSpace(x,y);
			removeState(loc);
		}
	}

	public void handleAltClick(int x, int y) {
		if(onState(x,y)) {
			states.get(nearestGridSpace(x,y)).toggleAccept();
		}
	}

	public void handleDrag(int x, int y, int xf, int yf) {
		if(onStateSpace(x,y) && onStateSpace(xf,yf)) {
			addTransition(nearestGridSpace(x,y),nearestGridSpace(xf,yf),"0");
		}
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
