package com.kiosk.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class ProductsPanelFlavors extends JPanel {

    private static final int PRODUCTS_PER_PAGE = 10;
    private static final int TOTAL_PRODUCTS = 200;
    private static Object[][] allProducts = new Object[TOTAL_PRODUCTS][7];
    private static Object[][] filteredProducts = allProducts;

    private static JTable table;
    private static DefaultTableModel model;
    private static JPanel paginationPanel;
    private static int currentPage = 1;

    public static JPanel ProductsPanelFlavors(AdminDashboard adminDashboard) {
        JPanel productPanel = new JPanel(new BorderLayout());
        Color backgroundColor = new Color(217, 217, 217);
        productPanel.setBackground(backgroundColor);

        for (int i = 0; i < TOTAL_PRODUCTS; i++) {
            allProducts[i][0] = i + 1;
            allProducts[i][1] = "Product " + (i + 1);
            allProducts[i][2] = "Size " + (i + 1);
            allProducts[i][3] = "PHP " + (10 + (i % 5) * 5) + ".00";
            allProducts[i][4] = (i % 3 == 0) ? "Unavailable" : "Available";
            allProducts[i][5] = "image link";
            allProducts[i][6] = "Action";
        }

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor);
        topPanel.setBorder(new EmptyBorder(30, 40, 10, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(backgroundColor);

        JLabel titleLabel = new JLabel("Products - Flavors");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(new Color(18, 52, 88));
        titlePanel.add(titleLabel);

        JButton backBtn = new JButton("→");
        backBtn.setForeground(Color.WHITE);
        backBtn.setBackground(new Color(18, 52, 88));
        backBtn.setFocusable(false);
        titlePanel.add(backBtn);

        backBtn.addActionListener(e -> {
            adminDashboard.showPanel("AddOns");  // Navigate to AddOns panel
        });

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profileLabel.setForeground(new Color(18, 52, 88));

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(profileLabel, BorderLayout.EAST);

        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(backgroundColor);
        searchPanel.setBorder(new EmptyBorder(0, 40, 20, 40));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JTextField searchField = new JTextField("");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
        });

        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setMaximumSize(new Dimension(300, 30));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));

        JButton addButton = new JButton("+ ADD PRODUCT");
        addButton.setBackground(new Color(18, 52, 88));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        searchPanel.add(addButton);

        String[] columnNames = {"ID", "Product Name", "Sizes", "Price", "Status", "Image", "Action"};
        model = new DefaultTableModel(null, columnNames) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);

        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(String.valueOf(value));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Available".equals(value)) {
                    label.setBackground(new Color(198, 239, 206));
                    label.setForeground(new Color(0, 97, 0));
                } else {
                    label.setBackground(new Color(255, 199, 206));
                    label.setForeground(new Color(156, 0, 6));
                }
                return label;
            }
        });

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        paginationPanel = new JPanel();
        paginationPanel.setBackground(backgroundColor);
        paginationPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        updateTable(1);
        createPaginationButtons();

        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.add(topPanel, BorderLayout.NORTH);
        topContainerPanel.add(searchPanel, BorderLayout.SOUTH);

        productPanel.add(topContainerPanel, BorderLayout.NORTH);
        productPanel.add(tableScroll, BorderLayout.CENTER);
        productPanel.add(paginationPanel, BorderLayout.SOUTH);

        return productPanel;
    }

    private static void updateTable(int page) {
        model.setRowCount(0);
        int start = (page - 1) * PRODUCTS_PER_PAGE;
        int end = Math.min(start + PRODUCTS_PER_PAGE, filteredProducts.length);

        for (int i = start; i < end; i++) {
            model.addRow(filteredProducts[i]);
        }
    }

    private static void filterProducts(String query) {
        query = query.trim().toLowerCase();

        if (query.isEmpty()) {
            filteredProducts = allProducts;
        } else {
            java.util.List<Object[]> matched = new java.util.ArrayList<>();
            for (Object[] product : allProducts) {
                if (product[1].toString().toLowerCase().contains(query)) {
                    matched.add(product);
                }
            }
            filteredProducts = matched.toArray(new Object[0][7]);
        }

        currentPage = 1;
        updateTable(currentPage);
        createPaginationButtons();
    }

    private static void createPaginationButtons() {
        paginationPanel.removeAll();

        int totalPages = (int) Math.ceil(filteredProducts.length / (double) PRODUCTS_PER_PAGE);
        int pageWindow = 5;
        int startPage = ((currentPage - 1) / pageWindow) * pageWindow + 1;
        int endPage = Math.min(startPage + pageWindow - 1, totalPages);

        if (startPage > 1) {
            JButton prevGroup = new JButton("<");
            prevGroup.addActionListener(e -> {
                currentPage = startPage - 1;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(prevGroup);
        }

        for (int i = startPage; i <= endPage; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            if (i == currentPage) {
                pageButton.setBackground(new Color(18, 52, 88));
                pageButton.setForeground(Color.WHITE);
            }
            final int pageNum = i;
            pageButton.addActionListener(e -> {
                currentPage = pageNum;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(pageButton);
        }

        if (endPage < totalPages) {
            JButton nextGroup = new JButton(">");
            nextGroup.addActionListener(e -> {
                currentPage = endPage + 1;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(nextGroup);
        }

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }
}
