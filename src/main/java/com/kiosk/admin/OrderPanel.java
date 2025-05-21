package com.kiosk.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import com.kiosk.dbConnection.dbCon;

public class OrderPanel extends JPanel {

    private static final int ORDERS_PER_PAGE = 10;
    private static final Color PRIMARY_COLOR = new Color(18, 52, 88);  // #123458
    private static final Color BACKGROUND_COLOR = new Color(217, 217, 217);  // #D9D9D9
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color SUCCESS_BG = new Color(198, 239, 206);
    private static final Color SUCCESS_FG = new Color(0, 97, 0);
    private static final Color ERROR_BG = new Color(255, 199, 206);
    private static final Color ERROR_FG = new Color(156, 0, 6);
    private static final Color WARNING_BG = new Color(255, 235, 156);
    private static final Color WARNING_FG = new Color(156, 101, 0);

    private static Object[][] allOrders;
    private static Object[][] filteredOrders;

    private static JTable table;
    private static DefaultTableModel model;
    private static JPanel paginationPanel;
    private static int currentPage = 1;

    public static JPanel OrderPanel(AdminDashboard adminDashboard)  {
        JPanel orderPanel = new JPanel(new BorderLayout());
        orderPanel.setBackground(BACKGROUND_COLOR);

        // Load orders data from database
        loadOrdersFromDatabase();
        filteredOrders = allOrders;

        // Top Panel setup (title, back button, profile label)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(30, 40, 10, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Order Management");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        JButton backBtn = new JButton("←");
        backBtn.setForeground(WHITE_COLOR);
        backBtn.setBackground(PRIMARY_COLOR);
        backBtn.setFocusable(false);
        backBtn.setName("backBtn");
        titlePanel.add(backBtn);

        backBtn.addActionListener(e -> adminDashboard.showPanel("Dashboard"));

        JLabel profileLabel = new JLabel("👤 Admin");
        profileLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        profileLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        profileLabel.setForeground(PRIMARY_COLOR);

        topPanel.add(titlePanel, BorderLayout.WEST);
        topPanel.add(profileLabel, BorderLayout.EAST);

        // Search Panel setup
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(BACKGROUND_COLOR);
        searchPanel.setBorder(new EmptyBorder(0, 40, 20, 40));
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));

        JTextField searchField = new JTextField("");
        searchField.setPreferredSize(new Dimension(300, 30));
        searchField.setMaximumSize(new Dimension(300, 30));
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalStrut(10));

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterOrders(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterOrders(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterOrders(searchField.getText()); }
        });

        // Add filter dropdown for status
        String[] statusOptions = {"All Statuses", "PENDING", "PREPARING", "COMPLETED", "CANCELLED"};
        JComboBox<String> statusFilter = new JComboBox<>(statusOptions);
        statusFilter.setBackground(WHITE_COLOR);
        statusFilter.setPreferredSize(new Dimension(150, 30));
        statusFilter.setMaximumSize(new Dimension(150, 30));
        statusFilter.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchPanel.add(statusFilter);
        searchPanel.add(Box.createHorizontalStrut(10));

        statusFilter.addActionListener(e -> {
            String selectedStatus = (String) statusFilter.getSelectedItem();
            if (selectedStatus.equals("All Statuses")) {
                filterOrders(searchField.getText());
            } else {
                filterOrdersByStatus(searchField.getText(), selectedStatus);
            }
        });

        JButton refreshButton = new JButton("↻ Refresh");
        refreshButton.setBackground(PRIMARY_COLOR);
        refreshButton.setForeground(WHITE_COLOR);
        refreshButton.setFocusPainted(false);
        searchPanel.add(refreshButton);
        
        refreshButton.addActionListener(e -> {
            // Refresh order data from database
            loadOrdersFromDatabase();
            filterOrders(searchField.getText());
        });

        // Table columns including Action
        String[] columnNames = {"ID", "Order Summary", "Total", "Paid", "Status", "Timestamp", "Action"};
        model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make Action column editable for buttons
                return column == 6;
            }
        };

        table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(WHITE_COLOR);
        
        // Center align all columns except Action
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount() - 1; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Status column color renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(String.valueOf(value));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                switch (String.valueOf(value)) {
                    case "COMPLETED":
                        label.setBackground(SUCCESS_BG);
                        label.setForeground(SUCCESS_FG);
                        break;
                    case "CANCELLED":
                        label.setBackground(ERROR_BG);
                        label.setForeground(ERROR_FG);
                        break;
                    default: // PROCESSING, PENDING, PREPARING
                        label.setBackground(new Color(191, 223, 255));
                        label.setForeground(new Color(0, 84, 159));
                        break;
                }
                return label;
            }
        });
        
        // Paid column color renderer
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(String.valueOf(value));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Yes".equals(value)) {
                    label.setBackground(SUCCESS_BG);
                    label.setForeground(SUCCESS_FG);
                } else {
                    label.setBackground(WARNING_BG);
                    label.setForeground(WARNING_FG);
                }
                return label;
            }
        });

        // Set custom renderer and editor for Action column
        table.getColumn("Action").setCellRenderer(new OrderButtonRenderer());
        table.getColumn("Action").setCellEditor(new OrderButtonEditor(new JCheckBox(), adminDashboard));

        // Adjust column widths for better responsiveness
        table.getColumnModel().getColumn(0).setPreferredWidth(80);    // Order ID
        table.getColumnModel().getColumn(1).setPreferredWidth(140);   // Order Summary
        table.getColumnModel().getColumn(2).setPreferredWidth(100);   // Total
        table.getColumnModel().getColumn(3).setPreferredWidth(80);    // Paid
        table.getColumnModel().getColumn(4).setPreferredWidth(100);   // Status
        table.getColumnModel().getColumn(5).setPreferredWidth(120);   // Date
        table.getColumnModel().getColumn(6).setPreferredWidth(150);   // Action

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        paginationPanel = new JPanel();
        paginationPanel.setBackground(BACKGROUND_COLOR);
        paginationPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        updateTable(1);
        createPaginationButtons();

        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.setBackground(BACKGROUND_COLOR);
        topContainerPanel.add(topPanel, BorderLayout.NORTH);
        topContainerPanel.add(searchPanel, BorderLayout.SOUTH);

        orderPanel.add(topContainerPanel, BorderLayout.NORTH);
        orderPanel.add(tableScroll, BorderLayout.CENTER);
        orderPanel.add(paginationPanel, BorderLayout.SOUTH);

        return orderPanel;
    }

    private static void updateTable(int page) {
        model.setRowCount(0);
        int start = (page - 1) * ORDERS_PER_PAGE;
        int end = Math.min(start + ORDERS_PER_PAGE, filteredOrders.length);
        for (int i = start; i < end; i++) {
            // Add dummy value for Action column (buttons will render here)
            Object[] row = new Object[7];
            System.arraycopy(filteredOrders[i], 0, row, 0, 6);
            row[6] = "buttons"; // placeholder for buttons column
            model.addRow(row);
        }
    }

    private static void filterOrders(String query) {
        query = query.trim().toLowerCase();
        if (query.isEmpty()) {
            filteredOrders = allOrders;
        } else {
            ArrayList<Object[]> matched = new ArrayList<>();
            for (Object[] order : allOrders) {
                if (order[0].toString().toLowerCase().contains(query) || 
                    order[1].toString().toLowerCase().contains(query) ||
                    order[2].toString().toLowerCase().contains(query)) {
                    matched.add(order);
                }
            }
            filteredOrders = matched.toArray(new Object[0][6]);
        }
        currentPage = 1;
        updateTable(currentPage);
        createPaginationButtons();
    }
    
    private static void filterOrdersByStatus(String query, String status) {
        query = query.trim().toLowerCase();
        ArrayList<Object[]> matched = new ArrayList<>();
        for (Object[] order : allOrders) {
            boolean statusMatch = order[4].toString().equals(status);
            boolean queryMatch = query.isEmpty() || 
                              order[0].toString().toLowerCase().contains(query) || 
                              order[1].toString().toLowerCase().contains(query) ||
                              order[2].toString().toLowerCase().contains(query);
            if (statusMatch && queryMatch) {
                matched.add(order);
            }
        }
        filteredOrders = matched.toArray(new Object[0][6]);
        currentPage = 1;
        updateTable(currentPage);
        createPaginationButtons();
    }

    private static void createPaginationButtons() {
        paginationPanel.removeAll();
        int totalPages = (int) Math.ceil(filteredOrders.length / (double) ORDERS_PER_PAGE);
        int pageWindow = 5;
        int startPage = ((currentPage - 1) / pageWindow) * pageWindow + 1;
        int endPage = Math.min(startPage + pageWindow - 1, totalPages);

        // Use FlowLayout with CENTER alignment for pagination
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

        if (startPage > 1) {
            JButton prevGroup = new JButton("<");
            prevGroup.setFocusable(false);
            prevGroup.setBackground(WHITE_COLOR);
            prevGroup.setForeground(PRIMARY_COLOR);
            prevGroup.addActionListener(e -> {
                currentPage = startPage - 1;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(prevGroup);
        }

        for (int i = startPage; i <= endPage; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setFocusable(false);
            pageButton.setBackground(i == currentPage ? PRIMARY_COLOR : WHITE_COLOR);
            pageButton.setForeground(i == currentPage ? WHITE_COLOR : PRIMARY_COLOR);
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
            nextGroup.setFocusable(false);
            nextGroup.setBackground(WHITE_COLOR);
            nextGroup.setForeground(PRIMARY_COLOR);
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

    // Load orders from database
    private static void loadOrdersFromDatabase() {
        ArrayList<Object[]> orders = new ArrayList<>();
        String query = "SELECT o.id, o.orderSummary, o.total, o.paid, " +
                      "o.status, o.timestamp FROM orders o ORDER BY o.timestamp DESC";
                      
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String orderDetails = rs.getString("orderSummary");
                double totalAmount = rs.getDouble("total");
                boolean isPaid = rs.getBoolean("paid");
                String status = rs.getString("status");
                String timestamp = rs.getTimestamp("timestamp").toString();
                
                String formattedTotal = String.format("PHP %.2f", totalAmount);
                
                Object[] order = {id, orderDetails, formattedTotal, isPaid ? "Yes" : "No", status, timestamp};
                orders.add(order);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        allOrders = orders.toArray(new Object[0][6]);
    }
    
    // Update order status in database
    public static boolean updateOrderStatus(int orderId, String newStatus) {
        String query = "UPDATE orders SET status = ? WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, newStatus);
            stmt.setInt(2, orderId);
            int result = stmt.executeUpdate();
            
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Update payment status in database
    public static boolean updatePaymentStatus(int orderId, boolean isPaid) {
        String query = "UPDATE orders SET paid = ? WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, isPaid);
            stmt.setInt(2, orderId);
            int result = stmt.executeUpdate();
            
            return result > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Database error: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Get order details by ID
    public static JSONObject getOrderDetails(int orderId) {
        String query = "SELECT orderSummary FROM orders WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, orderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String orderDetailsJson = rs.getString("orderSummary");
                JSONParser parser = new JSONParser();
                return (JSONObject) parser.parse(orderDetailsJson);
            }
            
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, 
                "Error retrieving order details: " + e.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return null;
    }
    
    // Methods for accessing from other classes
    public static JTable getTable() {
        return table;
    }
    
    public static Object[][] getFilteredOrders() {
        return filteredOrders;
    }
    
    public static Object[][] getAllOrders() {
        return allOrders;
    }
    
    public static void setFilteredOrders(Object[][] orders) {
        filteredOrders = orders;
    }
    
    public static void setAllOrders(Object[][] orders) {
        allOrders = orders;
    }
    
    // Access to colors for consistency
    public static Color getPrimaryColor() {
        return PRIMARY_COLOR;
    }
    
    public static Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }
    
    public static Color getWhiteColor() {
        return WHITE_COLOR;
    }
    
    public static Color getSuccessBg() {
        return SUCCESS_BG;
    }
    
    public static Color getSuccessFg() {
        return SUCCESS_FG;
    }
    
    public static Color getErrorBg() {
        return ERROR_BG;
    }
    
    public static Color getErrorFg() {
        return ERROR_FG;
    }
    
    public static Color getWarningBg() {
        return WARNING_BG;
    }
    
    public static Color getWarningFg() {
        return WARNING_FG;
    }
}