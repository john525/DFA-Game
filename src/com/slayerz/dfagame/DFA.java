/**
 * @author John Lhota, Thomas Reber, Douglas Wong
 * <p>
 * Represents and contains all information pertaining to the displayed DFA.
 * Handles creation of states and transitions and interactions with them.
 */

package com.slayerz.dfagame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.*;
import java.util.regex.Pattern;

import javax.swing.*;

public class DFA {
    /**
     * The start state.
     */
    private State start;

    /**
     * The set of states of the DFA mapped by their coordinates on the game grid.
     */
    private Map<Coord, State> states;

    /**
     * Set of transitions of this DFA. Roughly corresponds to delta.
     */
    private Set<Transition> transitions;

    /**
     * Transition function of the DFA.
     */
    private Delta transitionFunction;

    /**
     * The radius around a state in which a click will be treated as corresponding to that state.
     */
    public static final int CLICK_RAD = 3 * State.RAD;

    /**
     * Create a new simple DFA.\
     * Places a start state in the upper left corner of the game board.
     */
    public DFA() {
        start = new State(false);
        transitionFunction = new Delta();
        states = new HashMap<Coord, State>();
        states.put(new Coord(1, 1), start);
        transitions = new HashSet<Transition>();
    }

    /**
     * Determines if the DFA is valid. A valid DFA has ONE legitimate transition leaving every state for '0' and '1'
     *
     * @return True if the DFA is valid (as defined), false otherwise.
     */
    public boolean isValid() {
        for (State s : states.values()) {
            if (transitionFunction.GetNextState(s, '0') == null || transitionFunction.GetNextState(s, '1') == null) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determine whether the DFA accepts the strings it should.
     *
     * @param regex The regex to check strings against.
     * @return True if the DFA only accepts the strings it should and rejects the strings it should. False otherwise.
     */
    public boolean MatchesRegex(String regex) {
        Pattern compiledRegex = Pattern.compile(regex);
        Map<String, Boolean> dfaResults = testOnAll();

        for (String s : dfaResults.keySet()) {
            if (compiledRegex.matcher(s).matches() != dfaResults.get(s)) {
//            if (Pattern.compile(regex).matcher(s).matches() != dfaResults.get(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Determines if the DFA will accept a given string.
     *
     * @param s The string to check.
     * @return Returns true if the string is in the language of the DFA, false otherwise.
     */
    public boolean acceptsString(String s) {
        State currentState = start;
        for (char c : s.toCharArray()) {
            currentState = transitionFunction.GetNextState(currentState, c);
        }

        return currentState.isAccept();
    }

    /**
     * Test all strings as long as the DFAs pumping length and tests them for acceptance by the DFA.
     *
     * @return A map of strings of the pumping length mapped to a boolean representing whether or not they are accepted by the DFA.
     */
    public Map<String, Boolean> testOnAll() {
        Map<String, Boolean> testResults = new HashMap<String, Boolean>();
        int pumpingLength = Math.max(2 * states.size() + 1, 13);

        List<String> binaryStrings = GenerateBinaryStrings(pumpingLength);

        for (String binaryString : binaryStrings) {
            testResults.put(binaryString, acceptsString(binaryString));
        }

        return testResults;
    }

    /**
     * Generates all string representations of binary numbers up to a certain length.
     *
     * @param length The maximum length of binary string to produced.
     * @return List of all binary strings of length up to and including length in ascending value order.
     */
    public List<String> GenerateBinaryStrings(int length) {
        List<String> allBinaryNums = new ArrayList<>();
        for (int i = 1; i <= length; i++) {
            for (String s : getPartialStrings(i)) {
                allBinaryNums.add(s);
            }
        }

        return allBinaryNums;
    }

    /**
     * Generates all string representations of binary numbers.
     * All the strings are a certain length so shorter numbers are zero padded.
     *
     * @param n The length of each string.
     * @return List of strings.
     */
    public List<String> getPartialStrings(int n) {
        List<String> partialStrings = new ArrayList<>();

        for (int i = 0; i < Math.pow(2, n); i++) {
            String bin = Integer.toBinaryString(i);
            while (bin.length() < n) {
                bin = "0" + bin;
            }
            partialStrings.add(bin);
        }

        return partialStrings;
    }

    /**
     * Given a state returns the Coordinate where that state is located.
     *
     * @param q The state in question.
     * @return The coord representing the onscreen location of the state.
     */
    public Coord locateState(State q) {
        for (Coord c : states.keySet()) {
            if (q == states.get(c)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Handles drawing off all states and transitions.
     *
     * @param g2d The graphics object which handles all the relevant work.
     */
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

            final int bufferFactor = 25; //How far arrow rises above horizontal/vertical
            final double arrowSide = 0.15 * Game.BOX_DIM; //This is NOT the side length of an arrow, just an arbitrary scaling factor
            final int radiusOffset = (int) (0.75 * State.RAD); //To cover up the transition arc correctly with the arrow
            final double recipSQRT2 = 0.71;

            if (!start.equals(end)) {

                int xi = start.c * Game.BOX_DIM, yi = start.r * Game.BOX_DIM, xf = end.c * Game.BOX_DIM, yf = end.r * Game.BOX_DIM;

                g2d.setPaint(Color.BLACK);

                if (yf < yi && xf != xi) {

                    g2d.drawArc(xf < xi ? xf - Math.abs(xf - xi) : xi, yf, 2 * Math.abs(xf - xi), 2 * Math.abs(yf - yi), xf < xi ? 0 : 90, 90);
                    g2d.drawString(t.getChars(), xf > xi ? (xf + xi) / 2 - Math.abs(xf - xi) / 8 : (xf + xi) / 2 + Math.abs(xf - xi) / 8, yf + Math.abs(yf - yi) / 12);

                    int[] arrowXCoordinates = {
                            (int) (xf > xi ? xf - radiusOffset : xf + radiusOffset),
                            (int) (xf > xi ? xf - radiusOffset - arrowSide : xf + radiusOffset + arrowSide),
                            (int) (xf > xi ? xf - radiusOffset - arrowSide : xf + radiusOffset + arrowSide)
                    };
                    int[] arrowYCoordinates = {yf, yf + (int) arrowSide, yf - (int) arrowSide / 2};
                    g2d.setPaint(Color.ORANGE);
                    g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);

                } else if (yf > yi && xf != xi) {

                    g2d.drawArc(xf < xi ? xf - Math.abs(xf - xi) : xi, yi - Math.abs(yf - yi), 2 * Math.abs(xf - xi), 2 * Math.abs(yf - yi), xf < xi ? 270 : 180, 90);
                    g2d.drawString(t.getChars(), xf > xi ? (xf + xi) / 2 - Math.abs(xf - xi) / 8 : (xf + xi) / 2 + Math.abs(xf - xi) / 8, yf - Math.abs(yf - yi) / 12);

                    int[] arrowXCoordinates = {
                            (int) (xf > xi ? xf - radiusOffset : xf + radiusOffset),
                            (int) (xf > xi ? xf - radiusOffset - arrowSide : xf + radiusOffset + arrowSide),
                            (int) (xf > xi ? xf - radiusOffset - arrowSide : xf + radiusOffset + arrowSide)
                    };
                    int[] arrowYCoordinates = {yf, yf + (int) arrowSide / 2, yf - (int) arrowSide};
                    g2d.setPaint(Color.ORANGE);
                    g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);

                } else if (yf == yi && xf != xi) {

                    g2d.drawArc(xf < xi ? xf : xi, yi - bufferFactor, Math.abs(xf - xi), 2 * bufferFactor, xf > xi ? 0 : 180, 180);
                    g2d.drawString(t.getChars(), (xi + xf) / 2, xi < xf ? yi - bufferFactor - 10 : yi + bufferFactor + 20);

                    int[] arrowXCoordinates = {
                            (int) (xf > xi ? xf - recipSQRT2 * radiusOffset : xf + recipSQRT2 * radiusOffset),
                            (int) (xf > xi ? xf - recipSQRT2 * (radiusOffset + arrowSide) : xf + recipSQRT2 * (radiusOffset + arrowSide)),
                            (int) (xf > xi ? xf - recipSQRT2 * (radiusOffset + 2 * arrowSide) : xf + recipSQRT2 * (radiusOffset + 2 * arrowSide))
                    };
                    int[] arrowYCoordinates = {
                            (int) (xf > xi ? yf - recipSQRT2 * radiusOffset : yf + recipSQRT2 * radiusOffset),
                            (int) (xf > xi ? yf - recipSQRT2 * (radiusOffset + 1.5 * arrowSide) : yf + recipSQRT2 * (radiusOffset + 1.5 * arrowSide)),
                            (int) (xf > xi ? yf - recipSQRT2 * (radiusOffset - arrowSide / 4) : yf + recipSQRT2 * (radiusOffset - arrowSide / 4))
                    };
                    //Magic numbers experimentally determined
                    g2d.setPaint(Color.ORANGE);
                    g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);

                } else /* (xf == xi) */ {

                    g2d.drawArc(xi - bufferFactor, yf < yi ? yf : yi, 2 * bufferFactor, Math.abs(yf - yi), yf < yi ? 90 : 270, 180);
                    g2d.drawString(t.getChars(), yi > yf ? xi - bufferFactor - 18 : xi + bufferFactor + 10, (yi + yf) / 2);
                    //No one even knows why the pixel shift factor for different sides has to be different... -Douglas

                    int[] arrowXCoordinates = {
                            (int) (yf > yi ? xf + recipSQRT2 * radiusOffset : xf - recipSQRT2 * radiusOffset),
                            (int) (yf > yi ? xf + recipSQRT2 * (radiusOffset + 1.5 * arrowSide) : xf - recipSQRT2 * (radiusOffset + 1.5 * arrowSide)),
                            (int) (yf > yi ? xf + recipSQRT2 * (radiusOffset - 0.7 * arrowSide) : xf - recipSQRT2 * (radiusOffset - 0.7 * arrowSide))
                    };
                    int[] arrowYCoordinates = {
                            (int) (yf > yi ? yf - recipSQRT2 * radiusOffset : yf + recipSQRT2 * radiusOffset),
                            (int) (yf > yi ? yf - recipSQRT2 * (radiusOffset + 1.5 * arrowSide) : yf + recipSQRT2 * (radiusOffset + 1.5 * arrowSide)),
                            (int) (yf > yi ? yf - recipSQRT2 * (radiusOffset + 2 * arrowSide) : yf + recipSQRT2 * (radiusOffset + 2 * arrowSide))
                    };
                    //Magic numbers experimentally determined
                    g2d.setPaint(Color.ORANGE);
                    g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);
                }

            } else { //Start and end states are the same.

                g2d.setPaint(Color.BLACK);
                int x = start.c * Game.BOX_DIM, y = start.r * Game.BOX_DIM;

                int xLabelOffset = t.getChars().equals("01") ? 7 : 3; //Guesswork.
                g2d.drawString(t.getChars(), x - xLabelOffset, y - (int) (4 * State.RAD));

                g2d.drawOval(x - (int) (State.RAD), y - 4 * State.RAD + 5, State.RAD * 2, State.RAD * 3);

                int[] arrowXCoordinates = {
                        (int) (x + 0.6 * radiusOffset),
                        (int) (x + 0.6 * (radiusOffset - 0.6 * arrowSide)),
                        (int) (x + 0.6 * (radiusOffset + 1.2 * arrowSide))
                };
                int[] arrowYCoordinates = {
                        (int) (y - radiusOffset),
                        (int) (y - radiusOffset - arrowSide),
                        (int) (y - radiusOffset - 0.6 * arrowSide)
                };

                g2d.setPaint(Color.ORANGE);
                g2d.fillPolygon(arrowXCoordinates, arrowYCoordinates, 3);
            }
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
        State s = states.get(loc);
        Iterator<Transition> it = transitions.iterator();
        while (it.hasNext()) {
            Transition t = it.next();
            if (t.connectsTo(s)) {
                transitionFunction.RemoveRule(t);
                it.remove();
            }
        }
        states.remove(loc);
    }

    public void addTransition(Coord loc, Coord locf, String str) {
        State q1 = states.get(loc);
        State q2 = states.get(locf);
        //Check if transitions already exists
        if (transitions.contains(new Transition(q1, q2, str)) || transitions.contains(new Transition(q1, q2, "01"))) {
            JOptionPane.showMessageDialog(null, "Transition Already Exists", "Error", JOptionPane.ERROR_MESSAGE);
            return; //TODO Fix this weird shit
        }
        if (q1 == null || q2 == null) return;
        Transition t = new Transition(q1, q2, str);
        transitions.add(t);
        transitionFunction.AddRule(t);
    }

    /**
     * Finds the coordinates (in terms of rows and columns on the 5x5 grid) of the nearest grid space to the specified point.
     *
     * @param x_int Integer x coordinate
     * @param y_int Integer y coordinate.
     * @return A coordinate object representing the grid space nearest a set of coordinates,
     */
    public Coord nearestGridSpace(int x_int, int y_int) {
        int r = (int) Math.round((double) y_int / Game.BOX_DIM);
        int c = (int) Math.round((double) x_int / Game.BOX_DIM);
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

    /**
     * Removes a state.
     *
     * @param x The x value of the onscreen location of the state to be removed.
     * @param y The y value of the onscreen location of the state to be removed.
     */
    public void handleCtrlClick(int x, int y) {
        if (onState(x, y)) {
            Coord loc = nearestGridSpace(x, y);
            removeState(loc);
        }
    }

    /**
     * Toggles a state's accept status.
     *
     * @param x The x value of the onscreen location of the state in question.
     * @param y The y value of the onscreen location of the state in question.
     */
    public void handleAltClick(int x, int y) {
        if (onState(x, y)) {
            states.get(nearestGridSpace(x, y)).toggleAccept();
        }
    }

    public String displayTransitionPrompt() {
        Object[] opts = {"0", "1", "0 or 1"};

        String s = (String) JOptionPane.showInputDialog(null, "Which of the following characters should lead from state q_1 to q_2?",
                "Specify transition character", JOptionPane.PLAIN_MESSAGE, null, opts, "0");

        if (s.equals("0 or 1")) {
            s = "01";
        }

        return s;
    }

    public void handleDrag(int x, int y, int xf, int yf) {
        if (onState(xf, yf) && onStateSpace(x, y) && onStateSpace(xf, yf) && (!nearestGridSpace(x, y).equals(nearestGridSpace(xf, yf)))) {
            String s = displayTransitionPrompt();

            addTransition(nearestGridSpace(x, y), nearestGridSpace(xf, yf), s);
        }
    }

    /**
     * Creates a transition from a state to itself.
     *
     * @param x The x value of the onsreen location of the state to receive such a transition.
     * @param y The y value of the onsreen location of the state to receive such a transition.
     */
    public void handleDoubleClick(int x, int y) {
        if (onState(x, y) && onStateSpace(x, y)) {
            String s = displayTransitionPrompt();

            addTransition(nearestGridSpace(x, y), nearestGridSpace(x, y), s);
        }
    }

    public void handleShiftDrag(int x, int y, int xf, int yf) {

    }


    /**
     * Represetns a location in terms of row and column on our game grid.
     */
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