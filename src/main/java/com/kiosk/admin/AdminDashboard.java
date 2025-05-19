
package com.kiosk.admin;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import javax.swing.*;

import com.kiosk.main.Login;
import com.kiosk.main.Welcome;

public class AdminDashboard extends JPanel {
    private JPanel mainPanel;
    private CardLayout cardLayout;

    public AdminDashboard() {
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createDashboardPanel(), "Dashboard");
        mainPanel.add(ProductsPanelFlavors.ProductsPanelFlavors(this), "Products");
        mainPanel.add(ProductsPanelAddOns.ProductsPanelAddOns(this), "AddOns"); // Ensure this exists
        mainPanel.add(createPlaceholderPanel("Orders Panel"), "Orders");
        mainPanel.add(AdminSettings.AdminSettings(), "Settings");

        add(mainPanel, BorderLayout.CENTER);
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

        String[] cardTitles = {"Total Sales", "Completed Orders", "Pending Orders", "Stocks"};
        String[] cardValues = {"PHP 50,000", "500", "9", "7"};

        for (int i = 0; i < cardTitles.length; i++) {
            JPanel card = new JPanel();
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            card.setLayout(new BorderLayout());

            // Title at the top
            JLabel cardLabel = new JLabel(cardTitles[i], SwingConstants.CENTER);
            cardLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            cardLabel.setForeground(new Color(11, 56, 95));
            cardLabel.setHorizontalAlignment(SwingConstants.CENTER);
            card.add(cardLabel, BorderLayout.NORTH);

            // Center value using Box and vertical glue
            JLabel valueLabel = new JLabel(cardValues[i], SwingConstants.CENTER);
            valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
            valueLabel.setForeground(new Color(40, 40, 40));
            valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel centerPanel = new JPanel();
            centerPanel.setOpaque(false);
            centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
            centerPanel.add(Box.createVerticalGlue());
            centerPanel.add(valueLabel);
            centerPanel.add(Box.createVerticalGlue());

            card.add(centerPanel, BorderLayout.CENTER);

            cardsPanel.add(card);
        }


        dashboard.add(cardsPanel, BorderLayout.CENTER);
        return dashboard;
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


