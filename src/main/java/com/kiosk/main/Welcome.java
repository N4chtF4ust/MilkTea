package com.kiosk.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import com.kiosk.Model.AddOns;
import com.kiosk.Model.Product;
import com.kiosk.cache_image.GetCachedImagePath;
import com.kiosk.client.ClientSideCart;
import com.kiosk.dbConnection.dbCon;
import com.kiosk.loading.LoadingRotate;

public class Welcome {
    public static JFrame WelcomeFrame= new JFrame("Milkteassai");
    private JPanel WelcomePanel;
    private JLabel WelcomeLabel;
    private JComboBox<String> WelcomeComboBox;
    private JButton WelcomeDoneButton;

    // Color and Font palette
    private final Color primaryColor = new Color(11, 56, 95);
    private final Color lightPrimary = new Color(240, 248, 255);
    private final Font headerFont = new Font("Rockwell", Font.BOLD, 28);
    private final Font comboFont = new Font("Segoe UI", Font.PLAIN, 18);

    public Welcome() {


        URL iconURL = getClass().getResource("/icon/favicon.png");
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            WelcomeFrame.setIconImage(icon.getImage());
        } else {
            System.err.println("Icon not found: " + iconURL);
        }





        WelcomeFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
        //WelcomeFrame.setUndecorated(true); // optional: removes borders and title bar
        WelcomeFrame.setMinimumSize(new Dimension(900, 600));
        WelcomeFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        WelcomeFrame.setLocationRelativeTo(null);

        // Panel setup
        WelcomePanel = new JPanel();
        WelcomePanel.setLayout(new BoxLayout(WelcomePanel, BoxLayout.Y_AXIS));
        WelcomePanel.setBackground(Color.WHITE);
        WelcomePanel.setBorder(BorderFactory.createEmptyBorder(60, 80, 60, 80)); // Padding

        // Welcome label
        WelcomeLabel = new JLabel("WELCOME TO MILKTEASSAI", SwingConstants.CENTER);
        WelcomeLabel.setFont(headerFont);
        WelcomeLabel.setForeground(primaryColor);
        WelcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // ComboBox
        String[] options = {"Admin", "Dashboard", "Client"};
        WelcomeComboBox = new JComboBox<>(options);
        WelcomeComboBox.setFont(comboFont);
        WelcomeComboBox.setBackground(lightPrimary);
        WelcomeComboBox.setForeground(primaryColor);
        WelcomeComboBox.setMaximumSize(new Dimension(300, 40));
        WelcomeComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        WelcomeComboBox.setBorder(BorderFactory.createLineBorder(primaryColor, 2));
        WelcomeComboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        WelcomeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected,
                                                          boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(comboFont);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                label.setBackground(isSelected ? primaryColor : Color.WHITE);
                label.setForeground(isSelected ? Color.WHITE : primaryColor);
                return label;
            }
        });

        // Done Button
        WelcomeDoneButton = new JButton("Continue");
        WelcomeDoneButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        WelcomeDoneButton.setForeground(Color.WHITE);
        WelcomeDoneButton.setBackground(primaryColor);
        WelcomeDoneButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        WelcomeDoneButton.setFocusPainted(false);
        WelcomeDoneButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        WelcomeDoneButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        WelcomeDoneButton.addActionListener(e -> {
            String selectedOption = (String) WelcomeComboBox.getSelectedItem();
            showLoadingScreen();

            SwingWorker<JPanel, Void> worker = new SwingWorker<>() {
                @Override
                protected JPanel doInBackground() throws Exception {
                    // Load products and add-ons BEFORE creating the clientSideCart panel
                    loadProducts();
                    loadAddOns();

                    Thread.sleep(1000); // Simulated delay

                    if ("Client".equals(selectedOption)) {
                        return new ClientSideCart();
                    } else if ("Admin".equals(selectedOption)) {
                        return new Login(WelcomeFrame);
                    }
                    return new JPanel(); // fallback
                }

                @Override
                protected void done() {
                    try {
                        JPanel nextPanel = get();
                        WelcomeFrame.getContentPane().removeAll();
                        WelcomeFrame.getContentPane().add(nextPanel);
                        WelcomeFrame.revalidate();
                        WelcomeFrame.repaint();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

            };
            worker.execute();
        });

        // Spacers and layout
        WelcomePanel.add(Box.createVerticalGlue());
        WelcomePanel.add(WelcomeLabel);
        WelcomePanel.add(Box.createVerticalStrut(30));
        WelcomePanel.add(WelcomeComboBox);
        WelcomePanel.add(Box.createVerticalStrut(30));
        WelcomePanel.add(WelcomeDoneButton);
        WelcomePanel.add(Box.createVerticalGlue());

        // Add to frame
        WelcomeFrame.add(WelcomePanel, BorderLayout.CENTER);
        WelcomeFrame.setVisible(true);
    }

    private void showLoadingScreen() {
        JPanel loadingPanel = new LoadingRotate();
        loadingPanel.setPreferredSize(new Dimension(300, 150));

        WelcomeFrame.getContentPane().removeAll();
        WelcomeFrame.getContentPane().add(loadingPanel, BorderLayout.CENTER);
        WelcomeFrame.revalidate();
        WelcomeFrame.repaint();
    }

    public static synchronized  void loadProducts() {
        try (Connection conn = dbCon.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Product where availability = 1");

            ClientSideCart.products.clear();  // clear before loading fresh data

            while (rs.next()) {
                ClientSideCart.products.add(new Product(
                        rs.getInt("id"),
                        rs.getString("productName"),
                        rs.getDouble("small"),
                        rs.getDouble("medium"),
                        rs.getDouble("large"),
                        rs.getString("img"),
                        rs.getBoolean("availability")
                ));
                GetCachedImagePath.cachedFileNames.add(rs.getString("img"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static synchronized  void loadAddOns() {
        try (Connection conn = dbCon.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM addOns where Availability = 1");

            ClientSideCart.addOns.clear();  // clear before loading fresh data

            while (rs.next()) {
                ClientSideCart.addOns.add(new AddOns(
                        rs.getInt("id"),
                        rs.getString("AddOnName"),
                        rs.getDouble("AddOnPrice"),
                        rs.getBoolean("Availability")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {


        new Welcome();

        // Keep the main thread alive indefinitely
        try {
            Thread.sleep(Long.MAX_VALUE);
        } catch (InterruptedException e) {
            // Restore interrupted status and print stack trace for debugging
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }



    }



    public static JFrame getWelcomeFrame() {
        return WelcomeFrame;
    }
}
