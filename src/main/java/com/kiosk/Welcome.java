// === Imports ===

package com.kiosk;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import com.kiosk.Model.AddOns;
import com.kiosk.Model.Product;
import com.kiosk.dbConnection.dbCon;
import com.kiosk.loading.loadingRotate;

public class Welcome {
    public static JFrame WelcomeFrame;
    private JPanel WelcomePanel;
    private JLabel WelcomeLabel;
    private JComboBox<String> WelcomeComboBox;
    private JButton WelcomeDoneButton;

    // === Constructor ===
    public Welcome() {
        // Frame setup
        WelcomeFrame = new JFrame("Welcome To Milktea Shop");
        WelcomeFrame.setSize(900, 600);
        WelcomeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WelcomeFrame.setLocationRelativeTo(null);

        // Panel with GridBagLayout
        WelcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        WelcomeLabel = new JLabel("WELCOME TO MILKTEA SHOP");
        WelcomeLabel.setFont(new Font("Rockwell", Font.BOLD, 25));
        WelcomeLabel.setForeground(new Color(11, 56, 95));

        // ComboBox
        String[] options = {"Admin", "Dashboard", "Client"};
        WelcomeComboBox = new JComboBox<>(options);
        WelcomeComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        WelcomeComboBox.setBackground(new Color(240, 248, 255));
        WelcomeComboBox.setForeground(new Color(11, 56, 95));
        WelcomeComboBox.setBorder(BorderFactory.createLineBorder(new Color(11, 56, 95), 2));
        WelcomeComboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        WelcomeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setBackground(isSelected ? new Color(11, 56, 95) : Color.WHITE);
                label.setForeground(isSelected ? Color.WHITE : new Color(11, 56, 95));
                return label;
            }
        });

        // Done Button
        WelcomeDoneButton = new JButton("Done");
        WelcomeDoneButton.setForeground(Color.WHITE);
        WelcomeDoneButton.setBackground(new Color(11, 56, 95));
        WelcomeDoneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedOption = (String) WelcomeComboBox.getSelectedItem();
                showLoadingScreen();

                SwingWorker<Void, Void> worker = new SwingWorker<>() {
                    @Override
                    protected Void doInBackground() throws Exception {
                        Thread.sleep(1000); // Simulated delay, replace with actual work in production
                        return null;
                    }

                    @Override
                    protected void done() {
                        WelcomeFrame.getContentPane().removeAll();
                        if ("Client".equals(selectedOption)) {
                            WelcomeFrame.getContentPane().add(new clientSideCart());
                        } else if ("Admin".equals(selectedOption)) {
                            WelcomeFrame.getContentPane().add(new Login(WelcomeFrame));
                        }
                        WelcomeFrame.revalidate();
                        WelcomeFrame.repaint();
                    }
                };
                worker.execute();
            }
        });

        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        WelcomePanel.add(WelcomeLabel, gbc);

        gbc.gridy++;
        WelcomePanel.add(WelcomeComboBox, gbc);

        gbc.gridy++;
        WelcomePanel.add(WelcomeDoneButton, gbc);

        // Final frame setup
        WelcomeFrame.add(WelcomePanel, BorderLayout.CENTER);
        WelcomeFrame.setVisible(true);
    }

    // === Helper Method ===
    private void showLoadingScreen() {
        JPanel loadingPanel = new loadingRotate();
        loadingPanel.setPreferredSize(new Dimension(300, 150));

        WelcomeFrame.getContentPane().removeAll();
        WelcomeFrame.getContentPane().add(loadingPanel, BorderLayout.CENTER);

        WelcomeFrame.revalidate();
        WelcomeFrame.repaint();
    }

    // === Getter for WelcomeFrame ===
    public static JFrame getWelcomeFrame() {
        return WelcomeFrame;
    }



    // === Database Loading Methods ===
    private static void loadProducts() {
        try (Connection conn = dbCon.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product");

            while (rs.next()) {
                int id = rs.getInt("id");
                String productName = rs.getString("productName");
                double small = rs.getDouble("small");
                double medium = rs.getDouble("medium");
                double large = rs.getDouble("large");
                String img = rs.getString("img");
                boolean availability = rs.getBoolean("availability");

                clientSideCart.products.add(new Product(id, productName, small, medium, large, img, availability));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void loadAddOns() {
        try (Connection conn = dbCon.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM addOns");

            while (rs.next()) {
                int id = rs.getInt("id");
                String addOnName = rs.getString("AddOnName");
                double addOnPrice = rs.getDouble("AddOnPrice");
                boolean availability = rs.getBoolean("Availability");

                clientSideCart.addOns.add(new AddOns(id, addOnName, addOnPrice, availability));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === Main Method ===
    public static void main(String[] args) {
        loadProducts();
        loadAddOns();
        new Welcome();
    }
}
