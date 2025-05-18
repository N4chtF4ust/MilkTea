package com.kiosk.admin;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

public class AdminProducts extends JFrame {
    public AdminProducts() {
        setTitle("Products - Flavors");
        setSize(1000, 600);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(217, 217, 217)); // #D9D9D9

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(40, 80, 10, 50));

        JLabel titleLabel = new JLabel("Products - Flavors");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        titleLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(titleLabel, BorderLayout.WEST);

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profileLabel.setForeground(new Color(18, 52, 88));
        topPanel.add(profileLabel, BorderLayout.EAST);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Search + Add Product
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        searchPanel.setOpaque(false);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 80, 10, 80));

        JTextField searchField = new JTextField(20);
        JButton addButton = new JButton("➕ ADD PRODUCT");
        addButton.setBackground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        searchPanel.add(searchField);
        searchPanel.add(addButton);
        mainPanel.add(searchPanel, BorderLayout.CENTER);

        // Table
        String[] columnNames = {"ID", "Product Name", "Price", "Size", "Status", "Image", "Action"};
        Object[][] data = {
            {"1", "Matcha", "PHP 49.00", "Small", "Available", "Image Link", ""},
            {"2", "Okinawa", "PHP 59.00", "Medium", "Available", "", ""},
            {"3", "Chocolate", "PHP 49.00", "Large", "Available", "", ""},
            {"4", "Wintermelon", "PHP 59.00", "Large", "Available", "", ""},
            {"5", "Taro", "PHP 59.00", "Medium", "Available", "", ""},
            {"6", "Hokkaido", "PHP 69.00", "Large", "Available", "", ""},
            {"7", "Classic", "PHP 49.00", "Small", "Available", "", ""},
            {"8", "Dark Chocolate", "PHP 69.00", "Large", "Unavailable", "", ""},
            {"9", "Salted Caramel", "PHP 69.00", "Large", "Unavailable", "", ""},
            {"10", "Matcha", "PHP 69.00", "Large", "Unavailable", "", ""}
        };

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        table.setRowHeight(30);
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(0, 80, 10, 80));
        mainPanel.add(tableScroll, BorderLayout.SOUTH);

        // Pagination
        JPanel pagination = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pagination.setOpaque(false);
        pagination.setBorder(BorderFactory.createEmptyBorder(10, 80, 20, 80));
        for (int i = 1; i <= 4; i++) {
            JButton pageBtn = new JButton(String.valueOf(i));
            pageBtn.setFocusPainted(false);
            pagination.add(pageBtn);
        }
        mainPanel.add(pagination, BorderLayout.PAGE_END);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(18,52,88)); // #123458
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("milkteassai");
        logo.setForeground(Color.WHITE);
        logo.setFont(new Font("SansSerif", Font.BOLD, 25));
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        sidebar.add(Box.createVerticalStrut(30));
        sidebar.add(logo);
        sidebar.add(Box.createVerticalStrut(30));

        String[] navItems = {"Dashboard", "Products", "Orders", "Settings", "Logout"};
        for (String item : navItems) {
            JButton btn = new JButton(item);
            btn.setMaximumSize(new Dimension(180, 40));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setFocusPainted(false);
            btn.setForeground(Color.WHITE);
            btn.setBackground(new Color(18,52,88));
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            btn.setHorizontalAlignment(SwingConstants.LEFT);
            sidebar.add(btn);
        }

        return sidebar;
    }

    public static void main(String[] args) {
        AdminProducts page = new AdminProducts();
        page.setVisible(true);
    }
}
