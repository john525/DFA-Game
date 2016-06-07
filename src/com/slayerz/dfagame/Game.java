package com.slayerz.dfagame;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.Timer;

public class Game {

    private JFrame frame;
    private GamePanel gamePanel;
    private JLabel text;
    private Timer timer;
    private JButton testButton;

    private DFA dfa;
    private String regex = "01";

    //user input stuff
    private boolean alt, shift, ctrl;
    private int startX, startY;
    private int currentX, currentY;
    private boolean drawingTransition;

    public static final int DIM = 800, LINES = 5;
    public static final int BOX_DIM = (int) (((double) DIM) / ((double) (LINES)));

    public static void main(String[] args) {
        new Game().go();
    }

    public Game() {
        alt = shift = ctrl = false;
        startX = startY = -10;
        currentX = currentY = 0;
        drawingTransition = false;

        dfa = new DFA();
    }

    public void go() {
        frame = new JFrame("Slayyy");
        frame.setSize(DIM, DIM + 150);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setResizable(false);

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(DIM, DIM));
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        StateInputListener s = new StateInputListener();

        gamePanel.addMouseListener(s);
        gamePanel.addMouseMotionListener(s);
        gamePanel.addKeyListener(s);

        testButton = new JButton("Click here to test your DFA!");
        testButton.addActionListener(new GameStatusChecker());
        frame.add(testButton, BorderLayout.PAGE_END);

        frame.add(gamePanel, BorderLayout.CENTER);

        text = new JLabel();
        text.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 22));
        text.setText("Regex: " + regex);
        text.setVerticalAlignment(SwingConstants.CENTER);
        text.setHorizontalAlignment(SwingConstants.CENTER);

        frame.add(text, BorderLayout.SOUTH);
        frame.setVisible(true);

        timer = new Timer(1000 / 30, new Animator());
        timer.setInitialDelay(0);
        timer.start();
    }

    private class GameStatusChecker implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if(dfa.isValid()) {
                if (dfa.MatchesRegex(regex)) {
                    JOptionPane.showMessageDialog(gamePanel, "Congratulations!");
                }
                else {
                    JOptionPane.showMessageDialog(gamePanel, "Your DFA is properly constructed but incorrect.");
                }
            } else {
                JOptionPane.showMessageDialog(gamePanel, "Your DFA is invalid.");
            }
        }
    }

    private class Animator implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            gamePanel.repaint();
        }
    }

    private class GamePanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(Color.decode("#EEEEEE"));
            g2d.fillRect(0, 0, DIM, DIM);
            g2d.setPaint(Color.GRAY);
            for (int i = 1; i <= LINES; i++) {
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine((int) (i * BOX_DIM), 0, (int) (i * BOX_DIM), DIM);
                g2d.drawLine(0, (int) (i * BOX_DIM), DIM, (int) (i * BOX_DIM));
            }
            dfa.draw(g2d);

            if (drawingTransition) {
                g2d.setStroke(new BasicStroke(4));
                g2d.setPaint(Color.GRAY);
                g2d.drawLine(startX, startY, currentX, currentY);
            }
        }
    }

    private class StateInputListener implements MouseListener, MouseMotionListener, KeyListener {

        public StateInputListener() {

        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getClickCount() == 2) {
                dfa.handleDoubleClick(e.getX(), e.getY());
            } else if (alt) {
                dfa.handleAltClick(e.getX(), e.getY());
            } else if (ctrl) {
                dfa.handleCtrlClick(e.getX(), e.getY());
            } else if (shift) {

            } else {
                dfa.handleClick(e.getX(), e.getY());
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            startX = e.getX();
            startY = e.getY();

            if (dfa.onState(e.getX(), e.getY())) {
                drawingTransition = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();
            if (shift) {
                dfa.handleShiftDrag(startX, startY, endX, endY);
            } else {

                dfa.handleDrag(startX, startY, endX, endY);
            }
            startX = startY = -10;
            drawingTransition = false;
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                shift = true;
            } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                ctrl = true;
                System.out.println("ct");
            } else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                alt = true;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                shift = false;
            } else if (e.getKeyCode() == KeyEvent.VK_CONTROL) {
                ctrl = false;
            } else if (e.getKeyCode() == KeyEvent.VK_ALT) {
                alt = false;
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            currentX = e.getX();
            currentY = e.getY();
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            currentX = e.getX();
            currentY = e.getY();
        }

    }
}