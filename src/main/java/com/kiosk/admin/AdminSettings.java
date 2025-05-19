package com.kiosk.admin;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class AdminSettings extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField businessNameField;

    public AdminSettings() {
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(217, 217, 217)); // #D9D9D9

        // Top Bar
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 30));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        titleLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(profileLabel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Settings Card Panel
        JPanel settingsCard = new JPanel();
        settingsCard.setLayout(new GridBagLayout());
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        //username
        gbc.gridx = 0;
        gbc.gridy = 0;
        settingsCard.add(new JLabel("Username:"), gbc);

        gbc.gridx = 1;
        usernameField = new JTextField("admin");
        usernameField.setEditable(false);
        settingsCard.add(usernameField, gbc);

        gbc.gridx = 2;
        JButton changeUsernameBtn = new JButton("Change Username");
        changeUsernameBtn.setBackground(new Color(18, 52, 88));
        changeUsernameBtn.setForeground(Color.WHITE);
        settingsCard.add(changeUsernameBtn, gbc);

        //password
        gbc.gridx = 0;
        gbc.gridy = 1;
        settingsCard.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField("********");
        passwordField.setEditable(false);
        settingsCard.add(passwordField, gbc);

        gbc.gridx = 2;
        JButton changePasswordBtn = new JButton("Change Password");
        changePasswordBtn.setBackground(new Color(18, 52, 88));
        changePasswordBtn.setForeground(Color.WHITE);
        settingsCard.add(changePasswordBtn, gbc);

        //business name
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsCard.add(new JLabel("Business Name:"), gbc);

        gbc.gridx = 1;
        businessNameField = new JTextField();
        businessNameField.setEditable(false);
        settingsCard.add(businessNameField, gbc);

        gbc.gridx = 2;
        JButton businessNameBtn;
        if (businessNameField.getText().isEmpty()) {
            businessNameBtn = new JButton("Add Business Name");
        } else {
            businessNameBtn = new JButton("Change Business Name");
        }
        businessNameBtn.setBackground(new Color(18, 52, 88));
        businessNameBtn.setForeground(Color.WHITE);
        settingsCard.add(businessNameBtn, gbc);

        // Save Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(18, 52, 88));
        saveButton.setForeground(Color.WHITE);
        settingsCard.add(saveButton, gbc);

        // Action Listeners to enable editing on button click:
        changeUsernameBtn.addActionListener(e -> usernameField.setEditable(true));
        changePasswordBtn.addActionListener(e -> passwordField.setEditable(true));
        businessNameBtn.addActionListener(e -> businessNameField.setEditable(true));

        // Save button action: just keep the panel, no popup
        saveButton.addActionListener(e -> {
            // Optionally disable editing again after save:
            usernameField.setEditable(false);
            passwordField.setEditable(false);
            businessNameField.setEditable(false);

            // Update businessNameBtn text if business name changed
            if (businessNameField.getText().isEmpty()) {
                businessNameBtn.setText("Add Business Name");
            } else {
                businessNameBtn.setText("Change Business Name");
            }
        });

        // Padding Wrapper for Settings Card
        JPanel paddedMainPanel = new JPanel(new BorderLayout());
        paddedMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        paddedMainPanel.setBackground(new Color(217, 217, 217));
        paddedMainPanel.add(settingsCard, BorderLayout.CENTER);

        mainPanel.add(paddedMainPanel, BorderLayout.CENTER);

        // Wrap mainPanel in a centered wrapper with fixed size
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(217, 217, 217));
        mainPanel.setPreferredSize(new Dimension(750, 520));  // Smaller main panel size
        wrapper.add(mainPanel);

        add(wrapper, BorderLayout.CENTER);
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
