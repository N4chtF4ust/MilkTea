package com.kiosk.admin;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class AdminDashboard extends JPanel {
    public AdminDashboard() {
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(217, 217, 217)); // #D9D9D9

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

        mainPanel.add(topPanel, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 40, 40));
        cardsPanel.setBorder(BorderFactory.createEmptyBorder(10, 100, 80, 100));
        cardsPanel.setOpaque(false);

        String[] cardTitles = {"Total Sales", "Completed Orders", "Pending Orders", "Best Seller"};
        String[] cardValues = {"PHP 50,000", "500", "9", "Wintermelon"};

        for (int i = 0; i < cardTitles.length; i++) {
            JPanel card = new JPanel();
            card.setBackground(Color.WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
            ));
            card.setLayout(new BorderLayout());

            JPanel innerPanel = new JPanel();
            innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.Y_AXIS));
            innerPanel.setOpaque(false);

            JLabel cardLabel = new JLabel(cardTitles[i]);
            cardLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
            cardLabel.setForeground(new Color(11, 56, 95));

            JLabel valueLabel = new JLabel(cardValues[i]);
            valueLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
            valueLabel.setForeground(new Color(40, 40, 40));
            valueLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

            innerPanel.add(cardLabel);
            innerPanel.add(valueLabel);

            card.add(innerPanel, BorderLayout.NORTH);
            cardsPanel.add(card);
        }

        mainPanel.add(cardsPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18, 52, 88)); // #123458
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
            ImageIcon icon = null;
            URL iconURL = getClass().getResource(iconPaths[i]);

            if (iconURL != null) {
                icon = new ImageIcon(iconURL);
                Image img = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                icon = new ImageIcon(img);
            }

            JButton btn = new JButton(navItems[i], icon);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(18, 52, 88));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            btn.setIconTextGap(10);

            sidebar.add(btn);
        }

        return sidebar;
    }
}
