package com.kiosk.client;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.kiosk.loading.LoadingRotate;

import com.kiosk.main.Welcome;

public class ThankYouScreen extends JPanel {
    private static final int COUNTDOWN_TIME = 20;
    private int counter = COUNTDOWN_TIME;
    private JLabel countdownLabel;
    private Timer countdownTimer;
    private CountdownCircle circle;
    private boolean hasLoadedCart = false;

    public ThankYouScreen(int orderId) {
        setBackground(new Color(245, 248, 250)); // Light background for freshness
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(15, 0, 15, 0);

        // Thank you label - bigger font, modern look
        JLabel thankYouLabel = new JLabel("<html><div style='text-align: center;'>Thank you for purchasing<br>please come again</div></html>", SwingConstants.CENTER);
        thankYouLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        thankYouLabel.setForeground(new Color(30, 50, 90));
        thankYouLabel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        gbc.gridy = 0;
        add(thankYouLabel, gbc);

        // Countdown circle with arc animation
        circle = new CountdownCircle();
        countdownLabel = circle.getCountdownLabel();
        gbc.gridy = 1;
        add(circle, gbc);

        // Order ID label - subtle and smaller
        JLabel orderLabel = new JLabel("Your order ID: " + orderId);
        orderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        orderLabel.setForeground(new Color(80, 80, 120));
        gbc.gridy = 2;
        add(orderLabel, gbc);

        // New Order button - modern flat style
        JButton newOrderButton = new JButton("New Order");
        newOrderButton.setName("newOrderButton");
        newOrderButton.setBackground(new Color(30, 50, 90));
        newOrderButton.setForeground(Color.WHITE);
        newOrderButton.setFocusPainted(false);
        newOrderButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        newOrderButton.setPreferredSize(new Dimension(140, 40));
        newOrderButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        newOrderButton.addActionListener(e -> {
            if (countdownTimer != null && countdownTimer.isRunning()) {
                countdownTimer.stop();
            }
            loadClientSideCart();
        });
        gbc.gridy = 3;
        add(newOrderButton, gbc);

        startCountdown();
    }

    private void loadClientSideCart() {
    	  if (hasLoadedCart) {
  			return;
  		}
          hasLoadedCart = true;

          // Custom panel for the loading animation
          JPanel loadingPanel = new LoadingRotate();

          loadingPanel.setPreferredSize(new Dimension(300, 150));


       // Setting up the frame and adding the loading panel
          JFrame frame = Welcome.getWelcomeFrame();
          Container contentPane = frame.getContentPane();

          contentPane.removeAll();
          contentPane.add(loadingPanel, BorderLayout.CENTER);
          frame.revalidate();
          frame.repaint();

          // Background loading task
          SwingWorker<JPanel, Void> worker = new SwingWorker<>() {
              @Override
              protected JPanel doInBackground() throws Exception {
                  Thread.sleep(1000); // Simulate work

                  // Clear static/shared resources if needed
                  ClientSideCart.cartPanel.removeAll();
                  ClientSideCart.cartPanelCenter.removeAll();
                  ClientSideCart.productPanel.removeAll();
                  ClientSideCart.productPanelCenter.removeAll();
                  ClientSideCart.clientOrders.clear();

                  // Build the new panel (off the EDT)
                  return new ClientSideCart();
              }

              @Override
              protected void done() {
                  try {
                      JPanel cartPanel = get(); // Retrieve built panel

                      SwingUtilities.invokeLater(() -> {
                          contentPane.removeAll();
                          contentPane.add(cartPanel, BorderLayout.CENTER);
                   
                          contentPane.revalidate();
         
                      });
                      
                      contentPane.repaint();

                  } catch (Exception ex) {
                      ex.printStackTrace(); // Optional: show error panel instead
                  }
              }
          };

          worker.execute(); // Start worker
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (counter > 0) {
                    counter--;
                    countdownLabel.setText(String.valueOf(counter));
                    circle.setProgress((double)counter / COUNTDOWN_TIME);
                    circle.repaint();
                } else {
                    countdownTimer.stop();
                    loadClientSideCart();
                }
            }
        });
        countdownTimer.start();
    }

    // Inner class: Circle with countdown and animated progress arc
    private class CountdownCircle extends JPanel {
        private final JLabel countdownLabel;
        private double progress = 1.0; // from 1 to 0

        public CountdownCircle() {
            setPreferredSize(new Dimension(140, 140));
            setOpaque(false);
            setLayout(new GridBagLayout());

            countdownLabel = new JLabel(String.valueOf(counter));
            countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
            countdownLabel.setForeground(new Color(30, 50, 90));
            countdownLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(countdownLabel);
        }

        public JLabel getCountdownLabel() {
            return countdownLabel;
        }

        public void setProgress(double progress) {
            this.progress = progress;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int size = Math.min(getWidth(), getHeight()) - 20;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;

            // Draw base circle (light gray)
            g2.setColor(new Color(220, 220, 220));
            g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawOval(x, y, size, size);

            // Draw progress arc
            g2.setColor(new Color(30, 50, 90));
            g2.setStroke(new BasicStroke(10f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            // Arc angle from 90 degrees clockwise (top center)
            int angle = (int) (360 * progress);
            g2.drawArc(x, y, size, size, 90, -angle);

            g2.dispose();
        }
    }
}
