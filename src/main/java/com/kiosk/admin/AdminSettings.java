package com.kiosk.admin;

import com.kiosk.dbConnection.dbCon;
import org.mindrot.jbcrypt.BCrypt;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AdminSettings extends JPanel {

    private final Color PRIMARY_DARK = new Color(18, 52, 88); // Dark blue
    private final Color PRIMARY_LIGHT = new Color(217, 217, 217); // Light gray

    private boolean currentPassVisible = false;
    private boolean newPassVisible = false;

    private final String EYE_OPEN_ICON = "/icon/eye-fill.png";
    private final String EYE_CLOSED_ICON = "/icon/eye-close.png";

    public AdminSettings() {
        setLayout(new BorderLayout());
        setBackground(PRIMARY_LIGHT);

        // ========== Top Panel ==========
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 10, 50));

        JLabel headerLabel = new JLabel("Settings");
        headerLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        headerLabel.setForeground(PRIMARY_DARK);
        topPanel.add(headerLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profileLabel.setForeground(PRIMARY_DARK);
        topPanel.add(profileLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // ========== Main Card Container ==========
     // Wrapper to center the card panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        add(centerPanel, BorderLayout.CENTER);

        // White card container (800x700)
        JPanel cardPanel = new JPanel(new GridBagLayout());
        cardPanel.setPreferredSize(new Dimension(800, 700));
        cardPanel.setBackground(Color.WHITE);
        cardPanel.setBorder(BorderFactory.createLineBorder(new Color(18, 52, 88), 5, true));

        // Add cardPanel to centerPanel and center it
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.gridx = 0;
        centerGbc.gridy = 0;
        centerPanel.add(cardPanel, centerGbc);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int y = 0;

        JLabel titleLabel = new JLabel("Change Admin Credentials");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 26));
        titleLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(0, 10, 30, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        cardPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 5, 20);
        JLabel currentUsernameLabel = new JLabel("Current Username");
        currentUsernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentUsernameLabel.setForeground(PRIMARY_DARK);
        cardPanel.add(currentUsernameLabel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JTextField currentUsernameField = createStyledTextField();
        cardPanel.add(currentUsernameField, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 5, 20);
        JLabel currentPasswordLabel = new JLabel("Current Password");
        currentPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        currentPasswordLabel.setForeground(PRIMARY_DARK);
        cardPanel.add(currentPasswordLabel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JPanel currentPassPanel = createPasswordPanel(currentPassVisible);
        JPasswordField currentPasswordField = (JPasswordField) currentPassPanel.getClientProperty("field");
        JLabel currentEye = (JLabel) currentPassPanel.getClientProperty("eye");
        currentEye.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                currentPassVisible = !currentPassVisible;
                currentPasswordField.setEchoChar(currentPassVisible ? (char) 0 : '•');
                currentEye.setIcon(loadAndResizeIcon(currentPassVisible ? EYE_OPEN_ICON : EYE_CLOSED_ICON, 24, 24));
            }
        });
        cardPanel.add(currentPassPanel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 5, 20);
        JLabel newUsernameLabel = new JLabel("New Username");
        newUsernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newUsernameLabel.setForeground(PRIMARY_DARK);
        cardPanel.add(newUsernameLabel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JTextField newUsernameField = createStyledTextField();
        cardPanel.add(newUsernameField, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 5, 20);
        JLabel newPasswordLabel = new JLabel("New Password");
        newPasswordLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        newPasswordLabel.setForeground(PRIMARY_DARK);
        cardPanel.add(newPasswordLabel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(0, 20, 15, 20);
        JPanel newPassPanel = createPasswordPanel(newPassVisible);
        JPasswordField newPasswordField = (JPasswordField) newPassPanel.getClientProperty("field");
        JLabel newEye = (JLabel) newPassPanel.getClientProperty("eye");
        newEye.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                newPassVisible = !newPassVisible;
                newPasswordField.setEchoChar(newPassVisible ? (char) 0 : '•');
                newEye.setIcon(loadAndResizeIcon(newPassVisible ? EYE_OPEN_ICON : EYE_CLOSED_ICON, 24, 24));
            }
        });
        cardPanel.add(newPassPanel, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(20, 20, 10, 20);
        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton);
        cardPanel.add(saveButton, gbc);

        gbc.gridy = y++;
        gbc.insets = new Insets(5, 20, 20, 20);
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        messageLabel.setForeground(Color.RED);
        cardPanel.add(messageLabel, gbc);

        saveButton.addActionListener(e -> {
            String currUser = currentUsernameField.getText().trim();
            String currPass = new String(currentPasswordField.getPassword());
            String newUser = newUsernameField.getText().trim();
            String newPass = new String(newPasswordField.getPassword());

            if (currUser.isEmpty() || currPass.isEmpty() || newUser.isEmpty() || newPass.isEmpty()) {
                messageLabel.setText("All fields are required.");
                return;
            }

            try (Connection conn = dbCon.getConnection()) {
                String sql = "SELECT password FROM users WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, currUser);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String storedHash = rs.getString("password");
                            if (BCrypt.checkpw(currPass, storedHash)) {
                                String updateSql = "UPDATE users SET username = ?, password = ? WHERE username = ?";
                                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                    updateStmt.setString(1, newUser);
                                    updateStmt.setString(2, BCrypt.hashpw(newPass, BCrypt.gensalt()));
                                    updateStmt.setString(3, currUser);
                                    updateStmt.executeUpdate();
                                    messageLabel.setForeground(new Color(46, 125, 50));
                                    messageLabel.setText("Credentials updated successfully.");
                                }
                            } else {
                                messageLabel.setForeground(Color.RED);
                                messageLabel.setText("Incorrect current password.");
                            }
                        } else {
                            messageLabel.setForeground(Color.RED);
                            messageLabel.setText("Current username not found.");
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Error: " + ex.getMessage());
            }
        });
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(66, 133, 244), 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }

            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                        BorderFactory.createEmptyBorder(8, 10, 8, 10)
                ));
            }
        });

        return field;
    }

    private JPanel createPasswordPanel(boolean isVisible) {
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setEchoChar(isVisible ? (char) 0 : '•');
        passwordField.setBorder(null);

        JLabel eyeIcon = new JLabel(loadAndResizeIcon(isVisible ? EYE_OPEN_ICON : EYE_CLOSED_ICON, 24, 24));
        eyeIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeIcon.setPreferredSize(new Dimension(36, 36));

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.add(eyeIcon, BorderLayout.EAST);

        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                passPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(66, 133, 244), 2),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }

            public void focusLost(FocusEvent e) {
                passPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
                        BorderFactory.createEmptyBorder(0, 10, 0, 10)
                ));
            }
        });

        passPanel.putClientProperty("field", passwordField);
        passPanel.putClientProperty("eye", eyeIcon);
        return passPanel;
    }

    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_DARK);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(28, 66, 107));
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(PRIMARY_DARK);
            }
        });
    }

    private ImageIcon loadAndResizeIcon(String path, int width, int height) {
        java.net.URL imgURL = getClass().getResource(path);
        if (imgURL == null) {
            System.err.println("Icon not found: " + path);
            return null;
        }
        ImageIcon icon = new ImageIcon(imgURL);
        Image img = icon.getImage();
        Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resized);
    }
}
