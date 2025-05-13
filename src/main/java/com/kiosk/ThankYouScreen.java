package com.kiosk;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import com.kiosk.loading.loadingRotate;

public class ThankYouScreen extends JPanel {
    private int counter = 10;
    private JLabel countdownLabel;
    private boolean hasLoadedCart = false;
    private Timer countdownTimer;

    public ThankYouScreen(int orderId) {

        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 0, 10, 0);

        // Thank you label
        JLabel thankYouLabel = new JLabel("<html><div style='text-align: center;'>Thank you for purchasing<br>please come again</div></html>", SwingConstants.CENTER);
        thankYouLabel.setFont(new Font("Arial", Font.BOLD, 18));
        thankYouLabel.setForeground(new Color(0, 38, 84));
        gbc.gridy = 0;
        add(thankYouLabel, gbc);

        // Circle with countdown
        CountdownCircle circle = new CountdownCircle();
        countdownLabel = circle.getCountdownLabel();
        gbc.gridy = 1;
        add(circle, gbc);

        // Order ID
        JLabel orderLabel = new JLabel("Your order id: " + orderId);
        orderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        orderLabel.setForeground(new Color(0, 38, 84));
        gbc.gridy = 2;
        add(orderLabel, gbc);

        // New Order button
        JButton newOrderButton = new JButton("New Order");
        newOrderButton.setName("newOrderButton");
        newOrderButton.setBackground(new Color(0, 38, 84));
        newOrderButton.setForeground(Color.WHITE);
        newOrderButton.setFocusPainted(false);
        newOrderButton.setPreferredSize(new Dimension(100, 30));
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
        JPanel loadingPanel = new loadingRotate();

        loadingPanel.setPreferredSize(new Dimension(300, 150));


        // Setting up the frame and adding the loading panel
        JFrame frame = Welcome.getWelcomeFrame();
        Container contentPane = frame.getContentPane();
        contentPane.removeAll();
        contentPane.add(loadingPanel, BorderLayout.CENTER);

        frame.revalidate();
        frame.repaint();

        // Background loading task (simulate loading)
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(1000); // Simulate loading
                return null;
            }

            @Override
            protected void done() {
                SwingUtilities.invokeLater(() -> {
                    contentPane.removeAll();
                    clientSideCart.cartPanel.removeAll();
                    clientSideCart.cartPanelCenter.removeAll();
                    clientSideCart.productPanel.removeAll();
                    clientSideCart.productPanelCenter.removeAll();

                    contentPane.add(new clientSideCart());
                    contentPane.revalidate();
                    contentPane.repaint();
                });
            }
        };
        worker.execute();
    }


    private void startCountdown() {
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                if (counter > 0) {
                    counter--;
                    countdownLabel.setText(String.valueOf(counter));
                } else {
                    countdownTimer.stop();
                    loadClientSideCart();
                }
            }
        });
        countdownTimer.start();
    }

    // Inner class to draw the countdown circle
    private class CountdownCircle extends JPanel {
        private final JLabel countdownLabel;

        public CountdownCircle() {
            setPreferredSize(new Dimension(120, 120));
            setOpaque(false);
            setLayout(new GridBagLayout());

            countdownLabel = new JLabel(String.valueOf(counter));
            countdownLabel.setFont(new Font("Arial", Font.BOLD, 24));
            countdownLabel.setForeground(new Color(0, 38, 84));
            add(countdownLabel);
        }

        public JLabel getCountdownLabel() {
            return countdownLabel;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(new Color(0, 38, 84));
            g2.setStroke(new BasicStroke(2));
            int diameter = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - diameter) / 2;
            int y = (getHeight() - diameter) / 2;
            g2.drawOval(x, y, diameter, diameter);
        }
    }
}
