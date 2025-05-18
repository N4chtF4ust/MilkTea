package com.kiosk.loading;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class LoadingRotate extends JPanel {
    private int angle = 0;

    public LoadingRotate() {
        // Timer to trigger repaint at regular intervals (30ms)
        Timer timer = new Timer(5, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angle += 5;
                if (angle >= 360) {
                    angle = 0;
                }
                repaint();
            }
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Cast Graphics to Graphics2D for better control
        Graphics2D g2d = (Graphics2D) g;

        // Anti-aliasing for smooth drawing
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set the color for the loading circle
        g2d.setColor(Color.decode("#123458"));

        // Rotate the graphics
        g2d.rotate(Math.toRadians(angle), getWidth() / 2, getHeight() / 2);

        // Draw the outer circle
        g2d.setStroke(new BasicStroke(5));
        g2d.drawArc(getWidth() / 2 - 50, getHeight() / 2 - 50, 100, 100, 0, 90);
    }


}

