package com.kiosk.admin;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OrderButtonEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {
    private final JPanel panel;
    private final JButton viewButton;
    private final JButton updateButton;
    private int selectedRow;
    private JTable table;
    private final AdminDashboard adminDashboard;

    public OrderButtonEditor(JCheckBox checkBox, AdminDashboard adminDashboard) {
        this.adminDashboard = adminDashboard;

        panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        viewButton = new JButton("View");
        viewButton.setBackground(OrderPanel.getPrimaryColor());
        viewButton.setForeground(Color.WHITE);
        viewButton.setFocusPainted(false);
        viewButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        viewButton.addActionListener(this);
        viewButton.setActionCommand("view");

        updateButton = new JButton("Update");
        updateButton.setBackground(new Color(0, 102, 153));
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        updateButton.addActionListener(this);
        updateButton.setActionCommand("update");

        panel.add(viewButton);
        panel.add(updateButton);
    }

    @Override
    public Object getCellEditorValue() {
        return "buttons";
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.selectedRow = row;
        this.table = table;
        return panel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("view".equals(e.getActionCommand())) {
            displayOrderDetails();
        } else if ("update".equals(e.getActionCommand())) {
            showUpdateOrderDialog();
        }
        fireEditingStopped();
    }

    private void displayOrderDetails() {
        int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());

        new SwingWorker<JSONObject, Void>() {
            @Override
            protected JSONObject doInBackground() {
                return OrderPanel.getOrderDetails(orderId);
            }

            @Override
            protected void done() {
                try {
                    JSONObject orderDetails = get();
                    if (orderDetails == null) {
                        JOptionPane.showMessageDialog(null,
                            "Could not retrieve order details.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    showOrderDetailsDialog(orderDetails);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                        "Error retrieving order details: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void showOrderDetailsDialog(JSONObject orderDetails) {
        int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String total = table.getValueAt(selectedRow, 2).toString();
        String isPaid = table.getValueAt(selectedRow, 3).toString();
        String status = table.getValueAt(selectedRow, 4).toString();
        String timestamp = table.getValueAt(selectedRow, 5).toString();

        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Order Details #" + orderId);
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(null);
        detailsDialog.setModal(true);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new GridLayout(4, 2, 10, 5));
        headerPanel.add(new JLabel("Order ID:"));
        headerPanel.add(new JLabel("#" + orderId));
        headerPanel.add(new JLabel("Total:"));
        headerPanel.add(new JLabel(total));
        headerPanel.add(new JLabel("Status:"));

        JLabel statusLabel = new JLabel(status);
        statusLabel.setOpaque(true);
        switch (status) {
            case "COMPLETED":
                statusLabel.setBackground(OrderPanel.getSuccessBg());
                statusLabel.setForeground(OrderPanel.getSuccessFg());
                break;
            case "CANCELLED":
                statusLabel.setBackground(OrderPanel.getErrorBg());
                statusLabel.setForeground(OrderPanel.getErrorFg());
                break;
            default:
                statusLabel.setBackground(new Color(191, 223, 255));
                statusLabel.setForeground(new Color(0, 84, 159));
                break;
        }
        headerPanel.add(statusLabel);

        headerPanel.add(new JLabel("Payment:"));
        JLabel paidLabel = new JLabel(isPaid);
        paidLabel.setOpaque(true);
        if ("Yes".equals(isPaid)) {
            paidLabel.setBackground(OrderPanel.getSuccessBg());
            paidLabel.setForeground(OrderPanel.getSuccessFg());
        } else {
            paidLabel.setBackground(OrderPanel.getWarningBg());
            paidLabel.setForeground(OrderPanel.getWarningFg());
        }
        headerPanel.add(paidLabel);

        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Order Items"));

        try {
            JSONArray ordersArray = (JSONArray) orderDetails.get("orders");

            for (Object item : ordersArray) {
                JSONObject orderItem = (JSONObject) item;
                String productName = (String) orderItem.get("productName");
                long quantity = (Long) orderItem.get("quantity");
                double price = ((Number) orderItem.get("price")).doubleValue();
                String size = (String) orderItem.get("size");
                String addons = (String) orderItem.get("addons");

                JPanel itemPanel = new JPanel();
                itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
                itemPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JLabel nameLabel = new JLabel(productName + " (" + size + ") x" + quantity);
                nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

                JLabel priceLabel = new JLabel("Price: PHP " + String.format("%.2f", price) + " each");
                JLabel addonsLabel = new JLabel("Add-ons: " + addons);
                JLabel subtotalLabel = new JLabel("Subtotal: $" + String.format("%.2f", price * quantity));

                itemPanel.add(nameLabel);
                itemPanel.add(priceLabel);
                itemPanel.add(addonsLabel);
                itemPanel.add(subtotalLabel);

                itemsPanel.add(itemPanel);
                itemsPanel.add(Box.createVerticalStrut(10));
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error parsing order details: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }

        JLabel timestampLabel = new JLabel("Ordered on: " + timestamp);
        timestampLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));

        contentPanel.add(headerPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(timestampLabel, BorderLayout.SOUTH);

        detailsDialog.add(contentPanel);
        detailsDialog.setVisible(true);
    }

    private void showUpdateOrderDialog() {
        int orderId = Integer.parseInt(table.getValueAt(selectedRow, 0).toString());
        String currentStatus = table.getValueAt(selectedRow, 4).toString();
        String currentPaid = table.getValueAt(selectedRow, 3).toString();
        boolean isPaid = "Yes".equals(currentPaid);

        JDialog updateDialog = new JDialog();
        updateDialog.setTitle("Update Order #" + orderId);
        updateDialog.setSize(350, 250);
        updateDialog.setLocationRelativeTo(null);
        updateDialog.setModal(true);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.add(new JLabel("Order ID:"));
        JTextField idField = new JTextField("#" + orderId);
        idField.setEditable(false);
        formPanel.add(idField);

        formPanel.add(new JLabel("Status:"));
        String[] statusOptions = {"PENDING", "PREPARING", "COMPLETED", "CANCELLED"};
        JComboBox<String> statusCombo = new JComboBox<>(statusOptions);
        statusCombo.setSelectedItem(currentStatus);
        formPanel.add(statusCombo);

        formPanel.add(new JLabel("Paid:"));
        JCheckBox paidCheckbox = new JCheckBox("", isPaid);
        formPanel.add(paidCheckbox);

        JPanel buttonPanel = new JPanel(new GridBagLayout());
        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(OrderPanel.getPrimaryColor());
        saveButton.setForeground(Color.WHITE);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        saveButton.addActionListener(e -> {
            String newStatus = (String) statusCombo.getSelectedItem();
            boolean newPaid = paidCheckbox.isSelected();

            saveButton.setEnabled(false);

            new SwingWorker<Boolean, Void>() {
                boolean statusUpdated = false;
                boolean paymentUpdated = false;

                @Override
                protected Boolean doInBackground() {
                    statusUpdated = OrderPanel.updateOrderStatus(orderId, newStatus);
                    paymentUpdated = OrderPanel.updatePaymentStatus(orderId, newPaid);
                    return statusUpdated && paymentUpdated;
                }

                @Override
                protected void done() {
                    saveButton.setEnabled(true);
                    try {
                        if (get()) {
                            table.setValueAt(newStatus, selectedRow, 4);
                            table.setValueAt(newPaid ? "Yes" : "No", selectedRow, 3);

                            JOptionPane.showMessageDialog(null,
                                "Order updated successfully!",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                            updateDialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(null,
                                "Failed to update order. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                            "Unexpected error: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        });

        cancelButton.addActionListener(e -> updateDialog.dispose());

        contentPanel.add(formPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        updateDialog.add(contentPanel);
        updateDialog.setVisible(true);
    }
}
