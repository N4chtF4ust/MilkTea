package com.kiosk.icons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Icons {


	//This code will create a trash icon
    public static ImageIcon createTrashIcon() {
        BufferedImage image = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Enable anti-aliasing for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set color to match the theme
        g2d.setColor(Color.WHITE);

        // Draw trash can outline
        g2d.drawRect(3, 2, 14, 2);  // Top of trash can
        g2d.drawRect(5, 4, 10, 14); // Body of trash can

        // Draw lines for the trash can details
        g2d.drawLine(8, 6, 8, 16);  // Left line inside trash
        g2d.drawLine(12, 6, 12, 16); // Right line inside trash

        g2d.dispose();
        return new ImageIcon(image);
    }

	//This code will create a cart icon
    public static ImageIcon createCartIcon() {
        // Create a panel

        BufferedImage image = new BufferedImage(40, 35, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

                // Enable anti-aliasing
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Set the cart color
                g2d.setColor(Color.decode("#123458"));

                // Draw cart body
                g2d.fillRoundRect(5, 5, 30, 20, 5, 5);

                // Draw cart handle
                g2d.fillRect(0, 0, 10, 3);

                // Draw wheels
                g2d.fillOval(8, 25, 5, 5);
                g2d.fillOval(25, 25, 5, 5);


        return new ImageIcon(image);
    }

}
