package com.slayerz.dfagame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class DFA {
    private State start;
    private Map<Coord, State> states;
    private Set<Transition> transitions;

    public static final int CLICK_RAD = 3 * State.RAD;

    public DFA() {
        start = new State(false);
        states = new HashMap<Coord, State>();
        states.put(new Coord(1, 1), start);
        transitions = new HashSet<Transition>();
    }

    public boolean acceptsString(String s) {
        return false;
    }

    public Map<String, Boolean> testOnAll() {
        int pumpingLength = states.size() + 1;
        for (int len = 0; len <= pumpingLength; len++) {
            //generate all strings of length len and test using acceptsString()
        }
        return null;
    }

    public Coord locateState(State q) {
        for (Coord c : states.keySet()) {
            if (q == states.get(c)) {
                return c;
            }
        }
        return null;
    }

    public void draw(Graphics2D g2d) {
        //Draw arrow to start state;
        g2d.setPaint(Color.GREEN);
        g2d.setStroke(new BasicStroke(4));
        g2d.drawLine(0, 0, Game.BOX_DIM, Game.BOX_DIM);

        //Draw each state.
        for (Coord c : states.keySet()) {
            states.get(c).draw(c.getR(), c.getC(), g2d);
        }

        //Draw each transition.
        for (Transition t : transitions) {
            Coord start = locateState(t.getStart()), end = locateState(t.getEnd());

            if (start.r < end.r && start.c == end.c) { //Kludgy way of accounting for Math.atan returning from -PI/2 to PI/2
                Coord temp = new Coord(start.r, start.c);
                start = end;
                end = temp;
            }

            int fuzzingAmount = 1;
            int x = start.c * Game.BOX_DIM, y = start.r * Game.BOX_DIM, xf = end.c * Game.BOX_DIM, yf = end.r * Game.BOX_DIM;
            int xOffSet, yOffSet;

            int stateDeltaX = x - xf;
            int stateDeltaY = yf - y;

            double angleStartToTransition;

            if (stateDeltaX != 0) { //The tangent of the angle between the start state and the transition is defined.
                //Ternary operators are good for readability.
                angleStartToTransition = (end.c >= start.c) ? Math.atan(stateDeltaY / stateDeltaX) : (Math.PI + Math.atan(stateDeltaY / stateDeltaX));
                xOffSet = ((int) (State.RAD * Math.cos(angleStartToTransition))) + (Math.cos(angleStartToTransition) >= 0 ? fuzzingAmount : -fuzzingAmount);
                //Nested ternary operators are even better.
                yOffSet = (stateDeltaY != 0) ? (((int) (State.RAD * Math.sin(angleStartToTransition))) + ((Math.sin(angleStartToTransition) >= 0) ? fuzzingAmount : -fuzzingAmount)) : 0;
            } else { //Start and end states are in same column but different rows.
                angleStartToTransition = end.r <= start.r ? -Math.PI / 2 : Math.PI / 2;
                xOffSet = 0;
                yOffSet = State.RAD + fuzzingAmount;
            }

            double arrowSide = 0.15 * Game.BOX_DIM; //This is NOT the side length of an arrow, just an arbitrary scaling factor
            int[] arrowXCoordinates = {
                    (int) (xf - xOffSet * 0.9), //0.9 moves the arrow tip closer into the state
                    (int) (xf - xOffSet - arrowSide * Math.cos(-angleStartToTransition - Math.PI / 4)), //pi/4 changes width of the arrow
                    (int) (xf - xOffSet - arrowSide * Math.cos(-angleStartToTransition + Math.PI / 4))};

            int[] arrowYCoordinates = {
                    (int) (yf + yOffSet * 0.9),
                    (int) (yf + yOffSet - arrowSide * Math.sin(-angleStartToTransition - Math.PI / 4)),
                    (int) (yf + yOffSet - arrowSide * Math.sin(-angleStartToTransition + Math.PI / 4))};

            g2d.setPaint(Color.BLACK);
            g2d.drawLine(x + xOffSet, y - yOffSet, xf - xOffSet, yf + yOffSet);
            g2d.setPaint(Color.GREEN);
            g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);

            //TODO fix arrows, curve transition arrows so that they don't overlap if they go between the same states in opposite directions
        }
    }

    public void handleClick(double x, double y) {
        int r = (int) Math.round(y / Game.BOX_DIM);
        int c = (int) Math.round(x / Game.BOX_DIM);
        double xGrid = Game.BOX_DIM * c;
        double yGrid = Game.BOX_DIM * r;
        double dx = Math.abs(x - xGrid);
        double dy = Math.abs(y - yGrid);
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist < CLICK_RAD) {
            addState(r, c, new State(false));
        }
    }

    public boolean addState(int r, int c, State state) {
        //check if state is already at state.r, state.c
        if (states.containsKey(new Coord(r, c))) {
            return false;
        }
        states.put(new Coord(r, c), state);
        return true;
    }

    public void removeState(Coord loc) {
        if (loc.r == 1 && loc.c == 1) {
            return;
        }
        states.remove(loc);
    }

    public void addTransition(Coord loc, Coord locf, String str) {
        State q1 = states.get(loc);
        State q2 = states.get(locf);
        if (q1 == null || q2 == null) return;
        transitions.add(new Transition(q1, q2, str));
    }

    /**
     * Finds the coordinates (in terms of rows and columns on the 5x5 grid) of the nearest grid space to the specified point.
     *
     * @param x_int Integer x coordinate
     * @param y_int Integer y coordinate.
     * @return
     */
    public Coord nearestGridSpace(int x_int, int y_int) {
        double x = x_int;
        double y = y_int;
        int r = (int) Math.round(y / Game.BOX_DIM);
        int c = (int) Math.round(x / Game.BOX_DIM);
        return new Coord(r, c);
    }

    /**
     * Tests whether a point (x,y) is sufficiently close to a grid space (within DFA.CLICK_RAD) for the click to be registered as
     * affecting that grid space.
     *
     * @param x the x coordinate.
     * @param y the y coordinate
     * @return true if (x,y) is within DFA.CLICK_RAD of the nearest grid space
     */
    public boolean onStateSpace(int x, int y) {
        Coord loc = nearestGridSpace(x, y);
        double xGrid = Game.BOX_DIM * loc.c;
        double yGrid = Game.BOX_DIM * loc.r;
        double dx = Math.abs(x - xGrid);
        double dy = Math.abs(y - yGrid);
        double dist = Math.sqrt(dx * dx + dy * dy);
        return dist < CLICK_RAD;
    }

    /**
     * checks whether the user clicked a state on the grid.
     *
     * @param x the x coordinate.
     * @param y the y coordinate
     * @return true if (x,y) is within DFA.CLICK_RAD of the nearest grid space AND there is a state at that grid space
     */
    public boolean onState(int x, int y) {
        Coord loc = nearestGridSpace(x, y);
        return onStateSpace(x, y) && (states.get(loc) != null);
    }

    public void handleCtrlClick(int x, int y) {
        if (onState(x, y)) {
            Coord loc = nearestGridSpace(x, y);
            removeState(loc);
        }
    }

    public void handleAltClick(int x, int y) {
        if (onState(x, y)) {
            states.get(nearestGridSpace(x, y)).toggleAccept();
        }
    }

    public void handleDrag(int x, int y, int xf, int yf) {
        if (onStateSpace(x, y) && onStateSpace(xf, yf) && (!nearestGridSpace(x, y).equals(nearestGridSpace(xf, yf)))) {
            //dialog
            Object[] opts = {"0", "1", "0 or 1"};
            String s = (String) JOptionPane.showInputDialog(null, "Which of the following characters should lead from state q_1 to q_2?",
                    "Specify transition character", JOptionPane.PLAIN_MESSAGE, null, opts, "0");
            if (s.equals("0 or 1")) {
                s = "01";
            }
            addTransition(nearestGridSpace(x, y), nearestGridSpace(xf, yf), s);
        }
    }

    public void handleShiftDrag(int x, int y, int xf, int yf) {

    }

    private class Coord {
        private int r;
        private int c;

        public Coord(int r, int c) {
            this.r = r;
            this.c = c;
        }

        public int getR() {
            return r;
        }

        public int getC() {
            return c;
        }

        @Override
        public int hashCode() {
            return (r + c) % 7;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Coord) {
                Coord k = (Coord) o;
                return r == k.getR() && c == k.getC();
            }
            return false;
        }

        @Override
        public String toString() {
            return "(r=" + r + ",c=" + c + ")";
        }
    }
}
