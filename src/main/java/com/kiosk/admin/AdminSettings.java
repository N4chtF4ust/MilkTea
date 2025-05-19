package com.kiosk.admin;

import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField businessNameField;

    public AdminSettings() {
        setLayout(new BorderLayout());
        setBackground(new Color(217, 217, 217));

        // ⬆️ Move topPanel to this panel's NORTH
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 10, 50));

        JLabel titleLabel = new JLabel("Settings");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        titleLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(profileLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH); // 👈 Add here like Dashboard

        // Settings Card Panel
        JPanel settingsCard = new JPanel(new GridBagLayout());
        settingsCard.setBackground(Color.WHITE);
        settingsCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 220), 1),
                BorderFactory.createEmptyBorder(20, 30, 20, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 10, 20, 10);

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
        styleButton(changeUsernameBtn);
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
        styleButton(changePasswordBtn);
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
        styleButton(businessNameBtn);
        settingsCard.add(businessNameBtn, gbc);

        // Save Button
        gbc.gridx = 1;
        gbc.gridy = 3;
        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton);
        settingsCard.add(saveButton, gbc);

        // Listeners
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

        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setBackground(new Color(217, 217, 217)); // Same as main bg
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerWrapper.add(settingsCard, centerGbc);

        // Optional: Adjust the size of settingsCard
        settingsCard.setPreferredSize(new Dimension(900, 600)); // You can tweak these

        // Padding around the white card
        JPanel paddedPanel = new JPanel(new BorderLayout());
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(20, 80, 60, 80)); 
        paddedPanel.setOpaque(false); 
        paddedPanel.add(centerWrapper, BorderLayout.CENTER);

        add(paddedPanel, BorderLayout.CENTER);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(18, 52, 88));
        button.setForeground(Color.WHITE);
    }

    public static JPanel AdminSettings() {
        return new AdminSettings();
    }
}
