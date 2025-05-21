package com.kiosk.admin;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;

/**
 * Custom renderer for action buttons in the order table
 */
public class OrderButtonRenderer extends JPanel implements TableCellRenderer {
    private JButton viewBtn, updateBtn;
    
    public OrderButtonRenderer() {
        setLayout(new GridBagLayout());
        setBackground(OrderPanel.getWhiteColor());
        
        viewBtn = new JButton("View");
        updateBtn = new JButton("Update");
        
        // Style the buttons
        viewBtn.setBackground(OrderPanel.getPrimaryColor());
        viewBtn.setForeground(OrderPanel.getWhiteColor());
        viewBtn.setFocusable(false);
        viewBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        updateBtn.setBackground(new Color(0, 128, 128)); // Teal
        updateBtn.setForeground(OrderPanel.getWhiteColor());
        updateBtn.setFocusable(false);
        updateBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        add(viewBtn);
        add(updateBtn);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        return this;
    }
}