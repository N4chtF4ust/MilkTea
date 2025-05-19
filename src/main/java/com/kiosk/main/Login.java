package com.kiosk.main;

import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.*;

import org.mindrot.jbcrypt.BCrypt;

import com.kiosk.admin.AdminDashboard;
import com.kiosk.client.ClientSideCart;
import com.kiosk.dbConnection.dbCon;

public class Login extends JPanel {
    private final Color PRIMARY_DARK = new Color(18, 52, 88);    // Dark blue
    private final Color PRIMARY_LIGHT = new Color(217, 217, 217); // Light gray

    private boolean passwordVisible = false;
    private final String EYE_OPEN_ICON = "/icon/eye-fill.png";   // path in resources
    private final String EYE_CLOSED_ICON = "/icon/eye-close.png";

    public Login(JFrame parentFrame) {
        setOpaque(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Title Label
        JLabel titleLabel = new JLabel("Anjo MilkTea");
        titleLabel.setFont(new Font("Arial Rounded MT Bold", Font.BOLD, 32));
        titleLabel.setForeground(PRIMARY_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 0, 5, 0);
        add(titleLabel, gbc);

        // Subtitle
        JLabel subtitleLabel = new JLabel("Admin Dashboard");
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
        subtitleLabel.setForeground(new Color(PRIMARY_DARK.getRed(),
                PRIMARY_DARK.getGreen(),
                PRIMARY_DARK.getBlue(), 180));
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);
        add(subtitleLabel, gbc);

        JPanel loginPanel = new JPanel();
        loginPanel.setOpaque(false);
        loginPanel.setLayout(new GridBagLayout());

        GridBagConstraints innerGbc = new GridBagConstraints();
        innerGbc.fill = GridBagConstraints.HORIZONTAL;
        innerGbc.weightx = 1.0;

        JLabel userLabel = new JLabel("Username");
        userLabel.setForeground(PRIMARY_DARK);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        innerGbc.gridx = 0;
        innerGbc.gridy = 0;
        innerGbc.anchor = GridBagConstraints.WEST;
        innerGbc.insets = new Insets(15, 15, 5, 15);
        loginPanel.add(userLabel, innerGbc);

        JTextField userText = createStyledTextField();
        innerGbc.gridy = 1;
        innerGbc.insets = new Insets(0, 15, 15, 15);
        loginPanel.add(userText, innerGbc);

        JLabel passLabel = new JLabel("Password");
        passLabel.setForeground(PRIMARY_DARK);
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        innerGbc.gridy = 2;
        innerGbc.insets = new Insets(0, 15, 5, 15);
        loginPanel.add(passLabel, innerGbc);

        // Password field and eye icon
        JPasswordField passText = new JPasswordField(15);
        passText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passText.setEchoChar('•');
        passText.setBorder(null);

        ImageIcon closedEye = loadAndResizeIcon(EYE_CLOSED_ICON, 24, 24);
        ImageIcon openEye = loadAndResizeIcon(EYE_OPEN_ICON, 24, 24);

        JLabel eyeLabel = new JLabel(closedEye);
        eyeLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        eyeLabel.setPreferredSize(new Dimension(36, 36));
        eyeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel passPanel = new JPanel(new BorderLayout());
        passPanel.setBackground(Color.WHITE);
        passPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150), 2),
            BorderFactory.createEmptyBorder(0, 10, 0, 10)
        ));
        passPanel.add(passText, BorderLayout.CENTER);
        passPanel.add(eyeLabel, BorderLayout.EAST);

        // Focus effect
        passText.addFocusListener(new java.awt.event.FocusAdapter() {
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

        // Toggle password visibility
        eyeLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                passText.setEchoChar(passwordVisible ? (char) 0 : '•');
                eyeLabel.setIcon(passwordVisible ? openEye : closedEye);
            }
        });

        innerGbc.gridy = 3;
        innerGbc.insets = new Insets(0, 15, 15, 15);
        loginPanel.add(passPanel, innerGbc);

        // Login button
        JButton loginButton = new JButton("LOG IN");
        styleButton(loginButton);
        innerGbc.gridy = 4;
        innerGbc.insets = new Insets(10, 15, 15, 15);
        loginPanel.add(loginButton, innerGbc);

        // Message label
        JLabel messageLabel = new JLabel(" ");
        messageLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        messageLabel.setForeground(PRIMARY_DARK);
        innerGbc.gridy = 5;
        innerGbc.insets = new Insets(0, 15, 15, 15);
        loginPanel.add(messageLabel, innerGbc);

        // Back button
        JButton backButton = new JButton("Back");
        styleButton(backButton);
        innerGbc.gridy = 6;
        innerGbc.insets = new Insets(0, 15, 20, 15);
        loginPanel.add(backButton, innerGbc);

        backButton.addActionListener(e -> {
            System.out.println("Back button pressed");
     
             parentFrame.getContentPane().remove(this);
             parentFrame.getContentPane().add(Welcome.WelcomePanel );
             parentFrame.revalidate();
             parentFrame.repaint();
        });

        // Add loginPanel to main layout
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.fill = GridBagConstraints.BOTH;
        add(loginPanel, gbc);



        loginButton.addActionListener(e -> {
            String user = userText.getText().trim();
            String pass = new String(passText.getPassword());

		            try (Connection conn = dbCon.getConnection()) {
		                String sql = "SELECT password FROM users WHERE username = ?";
		                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
		                    stmt.setString(1, user);
		                    try (ResultSet rs = stmt.executeQuery()) {
		                        if (rs.next()) {
		                            String storedHash = rs.getString("password");
		                         
		                            if (BCrypt.checkpw(pass, storedHash)) {
		                                messageLabel.setForeground(new Color(46, 125, 50));
		                             
		                                
		                                
		                                Welcome.showLoadingScreen();
		                                
		                                SwingWorker<JPanel, Void> worker = new SwingWorker<>() {
		                                    @Override
		                                    protected JPanel doInBackground() throws Exception {
		                                        // Perform any background tasks here (e.g., data loading)
		                                        return new AdminDashboard(); // Return the new panel
		                                    }

		                                    @Override
		                                    protected void done() {
		                                        try {
		                                            JPanel newPanel = get(); // Get the result from doInBackground()

		                                            parentFrame.getContentPane().removeAll(); // Clear existing components
		                                            parentFrame.getContentPane().add(newPanel); // Add the new panel
		                                            parentFrame.revalidate();
		                                            parentFrame.repaint();
		                                        } catch (Exception ex) {
		                                            ex.printStackTrace();
		                                        }
		                                    }
		                                };
		                                worker.execute();

                

           
                     
                            } else {
               
                                messageLabel.setForeground(new Color(192, 57, 43));
                                messageLabel.setText("Invalid username or password.");
                                passText.setText("");
                            }
                        } else {
                            messageLabel.setForeground(new Color(192, 57, 43));
                            messageLabel.setText("Invalid username or password.");
                            passText.setText("");
                        }
                    }
                }
            } catch (Exception ex) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Database error: " + ex.getMessage());
                ex.printStackTrace();
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


    private JPasswordField createStyledPasswordField(int width) {
        JPasswordField field = new JPasswordField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setEchoChar('•');
        field.setPreferredSize(new Dimension(width, 40));
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

    private void styleButton(JButton button) {
        button.setBackground(PRIMARY_DARK);
        button.setForeground(PRIMARY_LIGHT);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent evt) {
                button.setBackground(new Color(28, 66, 107));
            }

            public void mouseExited(MouseEvent evt) {
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
        Image resizedImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImg);
    }


}
