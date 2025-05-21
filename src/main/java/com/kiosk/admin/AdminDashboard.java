package com.kiosk.admin;

import java.awt.*;
import java.net.URL;
import javax.swing.*;
import com.kiosk.main.Login;
import com.kiosk.main.Welcome;
import com.kiosk.dbConnection.dbCon;
import java.sql.*;
import java.beans.PropertyChangeListener;

public class AdminDashboard extends JPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JLabel[] valueLabels; // Moved to class scope for reuse in polling
    private Timer dashboardRefreshTimer;

    public AdminDashboard() {
        setLayout(new BorderLayout());
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(ProductsPanelFlavors.ProductsPanelFlavors(this), "Products");
        mainPanel.add(ProductsPanelAddOns.ProductsPanelAddOns(this), "AddOns");
        mainPanel.add(OrderPanel.OrderPanel(this), "Orders");
        mainPanel.add(new AdminSettings(), "Settings");

        add(mainPanel, BorderLayout.CENTER);

        // Start polling to refresh dashboard data every 3 seconds
        startDashboardPolling();

        // Listen for changes in the visibility of the panel to stop/start polling
        this.addHierarchyListener(e -> {
            if (isVisible()) {
                // Panel is now visible, start polling
                startDashboardPolling();
            } else {
                // Panel is not visible, stop polling
                stopDashboardPolling();
            }
        });
    }


    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18, 52, 88));
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("milkteassai");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 25));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        String[] navItems = {"Dashboard", "Products", "Orders", "Settings", "Logout"};
        String[] iconPaths = {
            "/icon/homeIcon.png",
            "/icon/productsIcon.png",
            "/icon/ordersIcon.png",
            "/icon/settingsIcon.png",
            "/icon/logoutIcon.png"
        };

        for (int i = 0; i < navItems.length; i++) {
            String item = navItems[i];
            ImageIcon icon = null;
            URL iconURL = getClass().getResource(iconPaths[i]);

            if (iconURL != null) {
                icon = new ImageIcon(iconURL);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            }

            JButton btn = new JButton(item, icon);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(18, 52, 88));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(10);

            btn.addActionListener(e -> {
                if (item.equals("Logout")) {
                    LogoutDialog.showLogoutDialog((JFrame) SwingUtilities.getWindowAncestor(this), () -> {
                        Welcome.WelcomeFrame.getContentPane().remove(this);
                        Welcome.WelcomeFrame.add(new Login(Welcome.WelcomeFrame));
                        Welcome.WelcomeFrame.repaint();
                        Welcome.WelcomeFrame.revalidate();
                        if (dashboardRefreshTimer != null) {
                            dashboardRefreshTimer.stop(); // Stop polling on logout
                        }
                    });
                } else {
                    cardLayout.show(mainPanel, item);
                }
            });

            sidebar.add(btn);
        }

        return sidebar;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(217, 217, 217));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 10, 50));

        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        titleLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profileLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(profileLabel, BorderLayout.EAST);

        dashboard.add(topPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 40, 40));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 80, 100));
        cardsPanel.setOpaque(false);

        String[] cardTitles = {"Total Sales", "Completed Orders", "Pending Orders", "Cancelled Orders"};
        valueLabels = new JLabel[4]; // Save to class field for polling

        for (int i = 0; i < cardTitles.length; i++) {
            JPanel card = new JPanel(new BorderLayout());
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));

            JLabel cardLabel = new JLabel(cardTitles[i], SwingConstants.CENTER);
            cardLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            cardLabel.setForeground(new Color(11, 56, 95));
            card.add(cardLabel, BorderLayout.NORTH);

            JLabel valueLabel = new JLabel("Loading...", SwingConstants.CENTER);
            valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
            valueLabel.setForeground(new Color(40, 40, 40));
            valueLabels[i] = valueLabel;

            // Center the text vertically and horizontally within the card
            JPanel centerPanel = new JPanel(new BorderLayout());
            centerPanel.setOpaque(false);

            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
            valueLabel.setVerticalAlignment(SwingConstants.CENTER);
            centerPanel.add(valueLabel, BorderLayout.CENTER);

            card.add(centerPanel, BorderLayout.CENTER);
            cardsPanel.add(card);
        }

        dashboard.add(cardsPanel, BorderLayout.CENTER);
        loadDashboardData(valueLabels);
        return dashboard;
    }

    private void loadDashboardData(JLabel[] labels) {
        new SwingWorker<Void, Void>() {
            private double totalSales;
            private int completedOrders;
            private int pendingOrders;
            private int cancelledOrders;

            @Override
            protected Void doInBackground() {
                totalSales = getTotalSales();
                completedOrders = getCompletedOrders();
                pendingOrders = getPendingOrders();
                cancelledOrders = getCancelledOrders();
                return null;
            }

            @Override
            protected void done() {
                labels[0].setText(String.format("PHP %.2f", totalSales));
                labels[1].setText(String.valueOf(completedOrders));
                labels[2].setText(String.valueOf(pendingOrders));
                labels[3].setText(String.valueOf(cancelledOrders));
            }
        }.execute();
    }

    private void startDashboardPolling() {
        dashboardRefreshTimer = new Timer(3000, e -> loadDashboardData(valueLabels));
        dashboardRefreshTimer.start();
    }

    private void stopDashboardPolling() {
        if (dashboardRefreshTimer != null) {
            dashboardRefreshTimer.stop();
        }
    }

    private double getTotalSales() {
        String query = "SELECT SUM(total) FROM orders WHERE status = 'COMPLETED'";
        try (Connection conn = dbCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private int getCompletedOrders() {
        String query = "SELECT COUNT(*) FROM orders WHERE status = 'COMPLETED'";
        try (Connection conn = dbCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getPendingOrders() {
        String query = "SELECT COUNT(*) FROM orders WHERE status = 'PROCESSING'";
        try (Connection conn = dbCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getCancelledOrders() {
        String query = "SELECT COUNT(*) FROM orders WHERE status = 'CANCELLED'";
        try (Connection conn = dbCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private JPanel createPlaceholderPanel(String label) {
        JPanel placeholder = new JPanel(new BorderLayout());
        placeholder.setBackground(Color.WHITE);
        JLabel message = new JLabel(label + " is under construction", SwingConstants.CENTER);
        message.setFont(new Font("SansSerif", Font.BOLD, 20));
        message.setForeground(new Color(100, 100, 100));
        placeholder.add(message, BorderLayout.CENTER);
        return placeholder;
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
}
