package com.slayerz.dfagame;

import java.awt.Color;
import java.awt.Graphics2D;

public class State {
    private boolean isAccept;

    public final int BOX_DIM;
    public final int RAD;

    public State(boolean accept, int boxDIM, int rad) {
    	BOX_DIM = boxDIM;
    	RAD = rad;
        isAccept = accept;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void toggleAccept() {
        isAccept = !isAccept;
    }

    public void draw(int r, int c, Graphics2D g2d) {
        g2d.setPaint(isAccept ? Color.RED : Color.BLACK);

        g2d.fillOval((int) (c * BOX_DIM) - RAD, (int) (r * BOX_DIM) - RAD, 2 * RAD, 2 * RAD);
    }
}