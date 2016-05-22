package com.slayerz.dfagame;

import java.awt.Color;
import java.awt.Graphics2D;

public class State {
    private boolean isAccept;

    public static final int RAD = (int) (Game.BOX_DIM / 8.0);

    public State(boolean accept) {
        isAccept = accept;
    }

    public boolean isAccept() {
        return isAccept;
    }

    public void toggleAccept() {
        isAccept = !isAccept;
    }

    public void draw(int r, int c, Graphics2D g2d) {
        g2d.setPaint(Color.BLACK);
        if (isAccept) {
            g2d.setPaint(Color.RED);
        }
        g2d.fillOval((int) (c * Game.BOX_DIM) - RAD, (int) (r * Game.BOX_DIM) - RAD, 2 * RAD, 2 * RAD);
    }
}