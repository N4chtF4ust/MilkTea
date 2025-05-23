package com.kiosk.main;

import com.kiosk.dbConnection.dbCon;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.sql.*;

public class DashBoard extends JPanel {
    private JTable pendingTable;
    private JTable toReceiveTable;
    private Timer pollTimer;

    public DashBoard(JFrame parentFrame) {
        parentFrame.getContentPane().setBackground(new Color(15, 23, 42));
        setPreferredSize(new Dimension(500, 450));
        setBackground(new Color(209, 213, 219));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(new Color(55, 65, 81), 2));

        // Header Panel
        JPanel headerPanel = new JPanel(new GridLayout(1, 2));
        headerPanel.setPreferredSize(new Dimension(450, 100));
        headerPanel.setBackground(new Color(190, 212, 233));
        headerPanel.setBorder(BorderFactory.createLineBorder(new Color(18, 52, 88), 2, true));

        JPanel leftHeaderPanel = new JPanel(new GridBagLayout());
        leftHeaderPanel.setOpaque(false);
        JLabel pendingOrderLabel = new JLabel("Pending Order");
        pendingOrderLabel.setForeground(new Color(11, 56, 95));
        pendingOrderLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        leftHeaderPanel.add(pendingOrderLabel);

        JPanel rightHeaderPanel = new JPanel(new GridBagLayout());
        rightHeaderPanel.setOpaque(false);
        JLabel toReceiveLabel = new JLabel("Preparing");
        toReceiveLabel.setForeground(new Color(11, 56, 95));
        toReceiveLabel.setFont(new Font("Rockwell", Font.BOLD, 20));
        rightHeaderPanel.add(toReceiveLabel);

        headerPanel.add(leftHeaderPanel);
        headerPanel.add(rightHeaderPanel);
        add(headerPanel, BorderLayout.NORTH);

        // Wrapper panel for outer spacing
        JPanel mainWrapperPanel = new JPanel(new BorderLayout());
        mainWrapperPanel.setBackground(new Color(209, 213, 219));
        mainWrapperPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Main container with space in between panels
        JPanel mainContainerPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        mainContainerPanel.setBackground(Color.WHITE);
        mainContainerPanel.setBorder(BorderFactory.createLineBorder(new Color(18, 52, 88), 3, true));

        // Custom cell renderer for bold, colored text
        DefaultTableCellRenderer boldRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(getFont().deriveFont(Font.BOLD));
                setForeground(new Color(18, 52, 88));
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        // Custom header renderer for bold, black text
        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setFont(getFont().deriveFont(Font.BOLD));
                setForeground(Color.BLACK);
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        };

        // Left Panel (Pending Orders)
        pendingTable = new JTable(new DefaultTableModel(new Object[]{"Order ID", "Date", "Total"}, 0));
        pendingTable.setRowHeight(35); // set to 35 pixels
        
        // Apply custom renderer to all columns
        for (int i = 0; i < pendingTable.getColumnCount(); i++) {
            pendingTable.getColumnModel().getColumn(i).setCellRenderer(boldRenderer);
        }
        
        // Apply custom header renderer
        for (int i = 0; i < pendingTable.getColumnCount(); i++) {
            pendingTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        JScrollPane pendingScrollPane = new JScrollPane(pendingTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel leftContentPanel = new JPanel(new BorderLayout());
        leftContentPanel.setOpaque(false);
        leftContentPanel.add(pendingScrollPane, BorderLayout.CENTER);

        // Right Panel (Preparing Orders)
        toReceiveTable = new JTable(new DefaultTableModel(new Object[]{"Order ID", "Date", "Total"}, 0));
        toReceiveTable.setRowHeight(35);
        
        // Apply custom renderer to all columns
        for (int i = 0; i < toReceiveTable.getColumnCount(); i++) {
            toReceiveTable.getColumnModel().getColumn(i).setCellRenderer(boldRenderer);
        }
        
        // Apply custom header renderer
        for (int i = 0; i < toReceiveTable.getColumnCount(); i++) {
            toReceiveTable.getColumnModel().getColumn(i).setHeaderRenderer(headerRenderer);
        }
        
        JScrollPane toReceiveScrollPane = new JScrollPane(toReceiveTable,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        JPanel rightContentPanel = new JPanel(new BorderLayout());
        rightContentPanel.setOpaque(false);
        rightContentPanel.add(toReceiveScrollPane, BorderLayout.CENTER);

        mainContainerPanel.add(leftContentPanel);
        mainContainerPanel.add(rightContentPanel);

        mainWrapperPanel.add(mainContainerPanel, BorderLayout.CENTER);
        add(mainWrapperPanel, BorderLayout.CENTER);

        // Setup polling timer (starts immediately)
        pollTimer = new Timer(5000, e -> {
            loadOrdersByStatus("pending", pendingTable);
            loadOrdersByStatus("preparing", toReceiveTable);
        });
        pollTimer.setRepeats(true);
        pollTimer.start(); // Start polling immediately

        // Initial load
        loadOrdersByStatus("pending", pendingTable);
        loadOrdersByStatus("preparing", toReceiveTable);
    }

    // Public methods to control polling
    public void startPolling() {
        if (pollTimer != null && !pollTimer.isRunning()) {
            pollTimer.start();
        }
    }

    public void stopPolling() {
        if (pollTimer != null && pollTimer.isRunning()) {
            pollTimer.stop();
        }
    }

    // Method to refresh data manually
    public void refreshData() {
        loadOrdersByStatus("pending", pendingTable);
        loadOrdersByStatus("preparing", toReceiveTable);
    }

    private void loadOrdersByStatus(String status, JTable table) {
        String query = "SELECT id, timestamp, total FROM orders WHERE status = ? ORDER BY timestamp DESC";

        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();

            DefaultTableModel model = (DefaultTableModel) table.getModel();
            model.setRowCount(0);

            while (rs.next()) {
                int id = rs.getInt("id");
                Timestamp date = rs.getTimestamp("timestamp");
                double total = rs.getDouble("total");

                model.addRow(new Object[]{id, date, total});
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load " + status + " orders: " + e.getMessage());
        }
    }
}