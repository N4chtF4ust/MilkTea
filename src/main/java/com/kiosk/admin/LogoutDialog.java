package com.kiosk.admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LogoutDialog extends JDialog {
    private Point mouseDownCompCoords = null;

    public LogoutDialog(JFrame parent, Runnable onLogout) {
        super(parent, "Confirm Logout", true);
        setUndecorated(true);
        setSize(400, 200);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(new Color(0, 0, 0, 100));

        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));

        // Title Bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(Color.WHITE);
        titleBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));

        JLabel titleLabel = new JLabel("CONFIRM LOGOUT");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(new Color(18, 52, 88));

        // Custom 2D X Icon
        JPanel closeButton = new JPanel() {
            private boolean hover = false;

            {
                setPreferredSize(new Dimension(20, 20));
                setOpaque(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        dispose();
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        hover = true;
                        repaint();
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        hover = false;
                        repaint();
                    }
                });
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(2));
                g2.setColor(hover ? Color.RED : Color.GRAY);
                g2.drawLine(4, 4, getWidth() - 4, getHeight() - 4);
                g2.drawLine(getWidth() - 4, 4, 4, getHeight() - 4);
            }
        };

        // Drag with constraints
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseDownCompCoords = e.getPoint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                Point screenPoint = e.getLocationOnScreen();
                int newX = screenPoint.x - mouseDownCompCoords.x;
                int newY = screenPoint.y - mouseDownCompCoords.y;

                // Constrain to parent frame bounds
                Rectangle bounds = parent.getBounds();
                int maxX = bounds.x + bounds.width - getWidth();
                int maxY = bounds.y + bounds.height - getHeight();
                int minX = bounds.x;
                int minY = bounds.y;

                newX = Math.max(minX, Math.min(newX, maxX));
                newY = Math.max(minY, Math.min(newY, maxY));

                setLocation(newX, newY);
            }
        };

        titleBar.addMouseListener(dragListener);
        titleBar.addMouseMotionListener(dragListener);
        titleLabel.addMouseListener(dragListener);
        titleLabel.addMouseMotionListener(dragListener);

        titleBar.add(titleLabel, BorderLayout.WEST);
        titleBar.add(closeButton, BorderLayout.EAST);

        JLabel messageLabel = new JLabel("<html><div style='text-align: center;'>Are you sure you want to sign out?</div></html>");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        messageLabel.setForeground(new Color(18, 52, 88));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        messageLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JButton yesButton = new JButton("YES");
        yesButton.setBackground(new Color(18, 52, 88));
        yesButton.setForeground(Color.WHITE);
        yesButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        yesButton.setPreferredSize(new Dimension(120, 40));
        yesButton.addActionListener(e -> {
            dispose();
            onLogout.run();
        });

        JButton cancelButton = new JButton("CANCEL");
        cancelButton.setBackground(Color.DARK_GRAY);
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(yesButton);
        buttonPanel.add(Box.createHorizontalStrut(20));
        buttonPanel.add(cancelButton);

        container.add(titleBar, BorderLayout.NORTH);
        container.add(messageLabel, BorderLayout.CENTER);
        container.add(buttonPanel, BorderLayout.SOUTH);

        add(container);
    }

    public static void showLogoutDialog(JFrame parent, Runnable onLogout) {
        LogoutDialog dialog = new LogoutDialog(parent, onLogout);
        dialog.setVisible(true);
    }
}
