package com.kiosk.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class SignUp extends JPanel {

    public SignUp() {
        setPreferredSize(new Dimension(650, 600));
        setBackground(new Color(209, 213, 219));
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("CREATE AN ACCOUNT");
        titleLabel.setFont(new Font("Helvetica", Font.BOLD, 30));
        titleLabel.setForeground(new Color(15, 23, 42));
        add(titleLabel, gbc);

        // Full Name
        gbc.gridy++;
        gbc.anchor = GridBagConstraints.WEST;
        JLabel fullNameLabel = new JLabel("Full Name");
        add(fullNameLabel, gbc);

        gbc.gridy++;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField fullNameText = new JTextField(15);
        JLabel fullNameErrorLabel = new JLabel();
        fullNameErrorLabel.setForeground(Color.RED);
        add(fullNameText, gbc);

        gbc.gridy++;
        add(fullNameErrorLabel, gbc);

        fullNameText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validateFullName(fullNameText, fullNameErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validateFullName(fullNameText, fullNameErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validateFullName(fullNameText, fullNameErrorLabel);
            }
        });

        // Username
        gbc.gridy++;
        JLabel userLabel = new JLabel("Username");
        add(userLabel, gbc);

        gbc.gridy++;
        JTextField userText = new JTextField(15);
        JLabel userErrorLabel = new JLabel();
        userErrorLabel.setForeground(Color.RED);
        add(userText, gbc);

        gbc.gridy++;
        add(userErrorLabel, gbc);

        userText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validateUsername(userText, userErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validateUsername(userText, userErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validateUsername(userText, userErrorLabel);
            }
        });

        // Password
        gbc.gridy++;
        JLabel passLabel = new JLabel("Password");
        add(passLabel, gbc);

        gbc.gridy++;
        JPasswordField passText = new JPasswordField(15);
        JLabel passErrorLabel = new JLabel();
        passErrorLabel.setForeground(Color.RED);
        add(passText, gbc);

        gbc.gridy++;
        add(passErrorLabel, gbc);

        passText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validatePassword(passText, passErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validatePassword(passText, passErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validatePassword(passText, passErrorLabel);
            }
        });

        // Confirm Password
        gbc.gridy++;
        JLabel confirmPassLabel = new JLabel("Confirm Password");
        add(confirmPassLabel, gbc);

        gbc.gridy++;
        JPasswordField confirmPassText = new JPasswordField(15);
        JLabel confirmPassErrorLabel = new JLabel();
        confirmPassErrorLabel.setForeground(Color.RED);
        add(confirmPassText, gbc);

        gbc.gridy++;
        add(confirmPassErrorLabel, gbc);

        confirmPassText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validateConfirmPassword(passText, confirmPassText, confirmPassErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validateConfirmPassword(passText, confirmPassText, confirmPassErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validateConfirmPassword(passText, confirmPassText, confirmPassErrorLabel);
            }
        });

        // Contact
        gbc.gridy++;
        JLabel contactLabel = new JLabel("Contact Number");
        add(contactLabel, gbc);

        gbc.gridy++;
        JTextField contactText = new JTextField(15);
        JLabel contactErrorLabel = new JLabel();
        contactErrorLabel.setForeground(Color.RED);
        add(contactText, gbc);

        gbc.gridy++;
        add(contactErrorLabel, gbc);

        contactText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validateContact(contactText, contactErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validateContact(contactText, contactErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validateContact(contactText, contactErrorLabel);
            }
        });

        // Address
        gbc.gridy++;
        JLabel addressLabel = new JLabel("Address");
        add(addressLabel, gbc);

        gbc.gridy++;
        JTextArea addressText = new JTextArea(3, 15);
        JScrollPane addressScroll = new JScrollPane(addressText);
        JLabel addressErrorLabel = new JLabel();
        addressErrorLabel.setForeground(Color.RED);
        add(addressScroll, gbc);

        gbc.gridy++;
        add(addressErrorLabel, gbc);

        addressText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
			public void insertUpdate(DocumentEvent e) {
                validateAddress(addressText, addressErrorLabel);
            }

            @Override
			public void removeUpdate(DocumentEvent e) {
                validateAddress(addressText, addressErrorLabel);
            }

            @Override
			public void changedUpdate(DocumentEvent e) {
                validateAddress(addressText, addressErrorLabel);
            }
        });

        // Terms Link
        gbc.gridy++;
        JLabel termsLinkLabel = new JLabel("<HTML><U>Terms and Conditions</U></HTML>");
        termsLinkLabel.setForeground(Color.BLUE);
        add(termsLinkLabel, gbc);

        termsLinkLabel.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent e) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(SignUp.this);
                showTermsDialog(parentFrame);
            }
        });

        // Terms Checkbox
        gbc.gridy++;
        JCheckBox termsCheckBox = new JCheckBox("I agree to the terms and conditions.");
        termsCheckBox.setBackground(new Color(209, 213, 219));
        add(termsCheckBox, gbc);

        // Sign Up Button
        gbc.gridy++;
        JButton signUpButton = new JButton("SIGN UP");
        signUpButton.setBackground(new Color(31, 41, 55));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setFocusPainted(false);
        add(signUpButton, gbc);

        signUpButton.addActionListener(e -> {
            try {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(SignUp.this);

                String fullName = fullNameText.getText();
                if (fullName.isEmpty() || !fullName.matches("[a-zA-Z ]+")) {
                    throw new Exception("Full Name is invalid or empty.");
                }

                String username = userText.getText();
                if (username.isEmpty() || !username.matches("[a-zA-Z0-9]+")) {
                    throw new Exception("Username is invalid or empty.");
                }

                String password = new String(passText.getPassword());
                if (password.isEmpty() || password.length() < 8) {
                    throw new Exception("Password is invalid or too short.");
                }

                String confirmPassword = new String(confirmPassText.getPassword());
                if (!password.equals(confirmPassword)) {
                    throw new Exception("Passwords do not match.");
                }

                String contact = contactText.getText();
                if (contact.isEmpty() || !contact.matches("[0-9]+") || contact.length() != 11) {
                    throw new Exception("Contact number is invalid or empty.");
                }

                String address = addressText.getText();
                if (address.isEmpty() || !address.matches("[a-zA-Z0-9 ]+")) {
                    throw new Exception("Address is invalid or empty.");
                }

                if (!termsCheckBox.isSelected()) {
                    throw new Exception("You must agree to the terms and conditions.");
                }

                JOptionPane.showMessageDialog(parentFrame, "Sign up successful!");
            } catch (Exception ex) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(SignUp.this);
                JOptionPane.showMessageDialog(parentFrame, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Login Link
        gbc.gridy++;
        JLabel loginLabel = new JLabel("Already have an account?");
        loginLabel.setForeground(new Color(55, 65, 81));
        add(loginLabel, gbc);

        gbc.gridy++;
        JLabel loginLink = new JLabel("Login Here");
        loginLink.setForeground(new Color(37, 99, 235));
        add(loginLink, gbc);

        loginLink.addMouseListener(new MouseAdapter() {
            @Override
			public void mouseClicked(MouseEvent e) {
                // Open login screen
                JOptionPane.showMessageDialog(SignUp.this, "Login screen here.");
            }
        });
    }

    private void validateFullName(JTextField fullNameText, JLabel errorLabel) {
        String fullName = fullNameText.getText();
        if (!fullName.matches("[a-zA-Z ]+")) {
            errorLabel.setText("Full Name can only contain letters and spaces.");
        } else {
            errorLabel.setText("");
        }
    }

    private void validateUsername(JTextField userText, JLabel errorLabel) {
        String username = userText.getText();
        if (!username.matches("[a-zA-Z0-9]+")) {
            errorLabel.setText("Username can only contain letters and numbers.");
        } else {
            errorLabel.setText("");
        }
    }

    private void validatePassword(JPasswordField passText, JLabel errorLabel) {
        String password = new String(passText.getPassword());
        if (password.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters.");
        } else {
            errorLabel.setText("");
        }
    }

    private void validateConfirmPassword(JPasswordField passText, JPasswordField confirmPassText, JLabel errorLabel) {
        String password = new String(passText.getPassword());
        String confirmPassword = new String(confirmPassText.getPassword());
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
        } else {
            errorLabel.setText("");
        }
    }

    private void validateContact(JTextField contactText, JLabel errorLabel) {
        String contact = contactText.getText();
        if (!contact.matches("[0-9]+") || contact.length() != 11) {
            errorLabel.setText("Contact number must be 11 digits.");
        } else {
            errorLabel.setText("");
        }
    }

    private void validateAddress(JTextArea addressText, JLabel errorLabel) {
        String address = addressText.getText();
        if (!address.matches("[a-zA-Z0-9 ]+")) {
            errorLabel.setText("Address can only contain letters, numbers, and spaces.");
        } else {
            errorLabel.setText("");
        }
    }

    private void showTermsDialog(JFrame parent) {
        JDialog termsDialog = new JDialog(parent, "Terms and Conditions", true);
        termsDialog.setBackground(new Color(209, 213, 219));
        termsDialog.setSize(450, 350);
        JTextArea termsTextArea = new JTextArea(10, 30);
        termsTextArea.setBackground(new Color(209, 213, 219));
        termsTextArea.setFont(new Font("Helvetica", Font.BOLD, 12));
        termsTextArea.setEditable(false);
        termsTextArea.setText(TermsAndConditions());
        JScrollPane termsScrollPane = new JScrollPane(termsTextArea);
        termsDialog.add(termsScrollPane);
        termsDialog.setLocationRelativeTo(parent);
        termsDialog.setVisible(true);
    }

    private String TermsAndConditions() {
        return "Terms and Conditions:\n\n" +
                "1. Introduction\nThese Terms and Conditions govern your use of our services.\n\n" +
                "2. User Responsibilities\nYou agree to use our services only for lawful purposes.\n\n" +
                "3. Data Privacy\nWe respect your privacy and will not share your personal information without consent.\n\n" +
                "4. Termination\nWe may suspend or terminate your account if you violate these terms.\n\n" +
                "By using our services, you agree to these terms.";
    }



}
