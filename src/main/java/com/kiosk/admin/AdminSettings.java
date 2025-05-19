package com.kiosk.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField businessNameField;

    // Constructor builds the UI
    public AdminSettings() {
        setLayout(new BorderLayout());
        setBackground(new Color(217, 217, 217));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(217, 217, 217));

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
        JPanel settingsCard = new JPanel(new GridBagLayout());
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Username
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

        // Password
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

        // Business Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        settingsCard.add(new JLabel("Business Name:"), gbc);

        gbc.gridx = 1;
        businessNameField = new JTextField();
        businessNameField.setEditable(false);
        settingsCard.add(businessNameField, gbc);

        gbc.gridx = 2;
        JButton businessNameBtn = new JButton(
            businessNameField.getText().isEmpty() ? "Add Business Name" : "Change Business Name"
        );
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

        // Enable editing on button clicks
        changeUsernameBtn.addActionListener(e -> usernameField.setEditable(true));
        changePasswordBtn.addActionListener(e -> passwordField.setEditable(true));
        businessNameBtn.addActionListener(e -> businessNameField.setEditable(true));

        saveButton.addActionListener(e -> {
            usernameField.setEditable(false);
            passwordField.setEditable(false);
            businessNameField.setEditable(false);

            businessNameBtn.setText(
                businessNameField.getText().isEmpty() ? "Add Business Name" : "Change Business Name"
            );
        });

        // Padding wrapper
        JPanel paddedMainPanel = new JPanel(new BorderLayout());
        paddedMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 30, 30));
        paddedMainPanel.setBackground(new Color(217, 217, 217));
        paddedMainPanel.add(settingsCard, BorderLayout.CENTER);

        mainPanel.add(paddedMainPanel, BorderLayout.CENTER);

        // Center wrapper panel with fixed size
        JPanel wrapper = new JPanel(new GridBagLayout());
        wrapper.setBackground(new Color(217, 217, 217));
        mainPanel.setPreferredSize(new Dimension(750, 520));
        wrapper.add(mainPanel);

        add(wrapper, BorderLayout.CENTER);
    }

    // Static method to be called as ProductsPanelAddOns.AdminSettings()
    public static JPanel AdminSettings() {
        return new AdminSettings();
    }
}
