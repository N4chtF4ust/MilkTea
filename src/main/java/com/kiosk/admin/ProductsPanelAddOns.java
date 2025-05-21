package com.kiosk.admin;

import java.awt.*;
import com.kiosk.dao.AddOnDAO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.util.ArrayList;

public class ProductsPanelAddOns extends JPanel {

    private static final int PRODUCTS_PER_PAGE = 10;
    private static final Color PRIMARY_COLOR = new Color(18, 52, 88);  // #123458
    private static final Color BACKGROUND_COLOR = new Color(217, 217, 217);  // #D9D9D9
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color SUCCESS_BG = new Color(198, 239, 206);
    private static final Color SUCCESS_FG = new Color(0, 97, 0);
    private static final Color ERROR_BG = new Color(255, 199, 206);
    private static final Color ERROR_FG = new Color(156, 0, 6);

    private static Object[][] allProducts;
    private static Object[][] filteredProducts;

    private static JTable table;
    private static DefaultTableModel model;
    private static JPanel paginationPanel;
    private static int currentPage = 1;

    public static JPanel ProductsPanelAddOns(AdminDashboard adminDashboard)  {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBackground(BACKGROUND_COLOR);

        // Load products from DAO
        ArrayList<Object[]> productList = AddOnDAO.fetchAllAddOns();
        allProducts = productList.toArray(new Object[0][5]);
        filteredProducts = allProducts;

        // Top Panel setup (title, back button, profile label)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(BACKGROUND_COLOR);
        topPanel.setBorder(new EmptyBorder(30, 40, 10, 40));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Products - AddOns");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titlePanel.add(titleLabel);

        JButton backBtn = new JButton("←");
        backBtn.setForeground(WHITE_COLOR);
        backBtn.setBackground(PRIMARY_COLOR);
        backBtn.setFocusable(false);
        backBtn.setName("backBtn");
        titlePanel.add(backBtn);

        backBtn.addActionListener(e -> adminDashboard.showPanel("Products"));

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
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterProducts(searchField.getText()); }
        });

        JButton addButton = new JButton("+ ADD ADDONS");
        addButton.setBackground(PRIMARY_COLOR);
        addButton.setForeground(WHITE_COLOR);
        addButton.setFocusPainted(false);
        searchPanel.add(addButton);
        
        addButton.addActionListener(e -> {
            // Create a custom dialog with styled components
            JDialog dialog = new JDialog();
            dialog.setTitle("Add AddOn");
            dialog.setModal(true);
            dialog.setSize(400, 300);
            dialog.setResizable(false);

            dialog.setLocationRelativeTo(null);
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);
            
            JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
            form.setBorder(new EmptyBorder(20, 20, 20, 20));
            form.setBackground(BACKGROUND_COLOR);
            
            JTextField nameField = new JTextField(20);
            nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            JTextField priceField = new JTextField(10);
            priceField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            String[] statusOptions = {"Available", "Unavailable"};
            JComboBox<String> statusDropdown = new JComboBox<>(statusOptions);
            statusDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
            statusDropdown.setBackground(WHITE_COLOR);
            
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(PRIMARY_COLOR);
            
            JLabel priceLabel = new JLabel("Price:");
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            priceLabel.setForeground(PRIMARY_COLOR);
            
            JLabel statusLabel = new JLabel("Status:");
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusLabel.setForeground(PRIMARY_COLOR);
            
            form.add(nameLabel);
            form.add(nameField);
            form.add(priceLabel);
            form.add(priceField);
            form.add(statusLabel);
            form.add(statusDropdown);
            
            JPanel buttonPanel = new JPanel();

            buttonPanel.setBackground(BACKGROUND_COLOR);
            
            JButton saveButton = new JButton("Save");
            saveButton.setBackground(PRIMARY_COLOR);
            saveButton.setForeground(WHITE_COLOR);
            saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            saveButton.setFocusPainted(false);
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(WHITE_COLOR);
            cancelButton.setForeground(PRIMARY_COLOR);
            cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            cancelButton.setFocusPainted(false);
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            form.add(Box.createHorizontalStrut(5));
            form.add(buttonPanel);
            
            dialog.add(form);
            
            cancelButton.addActionListener(evt -> dialog.dispose());
            
            saveButton.addActionListener(evt -> {
                String newName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                boolean newStatus = statusDropdown.getSelectedItem().equals("Available");

                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double newPrice = Double.parseDouble(priceText);
                    AddOnDAO.insertAddOn(newName, newPrice, newStatus);
                    // Refresh the table
                    ArrayList<Object[]> updatedList = AddOnDAO.fetchAllAddOns();
                    allProducts = updatedList.toArray(new Object[0][5]);
                    filterProducts("");
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid price value", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            dialog.setVisible(true);
        });

        // Table columns including Action
        String[] columnNames = {"ID", "Product Name", "Price", "Status", "Action"};
        model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make Action column editable for buttons
                return column == 4;
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
        table.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(String.valueOf(value));
                label.setOpaque(true);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if ("Available".equals(value)) {
                    label.setBackground(SUCCESS_BG);
                    label.setForeground(SUCCESS_FG);
                } else {
                    label.setBackground(ERROR_BG);
                    label.setForeground(ERROR_FG);
                }
                return label;
            }
        });

        // Set custom renderer and editor for Action column
        table.getColumn("Action").setCellRenderer(new ButtonRenderer());
        table.getColumn("Action").setCellEditor(new ButtonEditor(new JCheckBox(), adminDashboard));

        // Adjust column widths for better responsiveness
        table.getColumnModel().getColumn(0).setPreferredWidth(50);    // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(200);   // Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80);    // Price
        table.getColumnModel().getColumn(3).setPreferredWidth(100);   // Status
        table.getColumnModel().getColumn(4).setPreferredWidth(150);   // Action

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
            // Add dummy value for Action column (buttons will render here)
            Object[] row = new Object[5];
            System.arraycopy(filteredProducts[i], 0, row, 0, 4);
            row[4] = "buttons"; // placeholder for buttons column
            model.addRow(row);
        }
    }

    private static void filterProducts(String query) {
        query = query.trim().toLowerCase();
        if (query.isEmpty()) {
            filteredProducts = allProducts;
        } else {
            ArrayList<Object[]> matched = new ArrayList<>();
            for (Object[] product : allProducts) {
                if (product[1].toString().toLowerCase().contains(query)) {
                    matched.add(product);
                }
            }
            filteredProducts = matched.toArray(new Object[0][5]);
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

    // Renderer for buttons in Action column - centered with improved styling
    static class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editBtn, deleteBtn;
        
        public ButtonRenderer() {
            setLayout(new FlowLayout());
            setBackground(WHITE_COLOR);
            
            editBtn = new JButton("Edit");
            deleteBtn = new JButton("Delete");
            
            // Style the buttons
            editBtn.setBackground(PRIMARY_COLOR);
            editBtn.setForeground(WHITE_COLOR);
            editBtn.setFocusable(false);
            editBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            deleteBtn.setBackground(ERROR_FG);
            deleteBtn.setForeground(WHITE_COLOR);
            deleteBtn.setFocusable(false);
            deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            add(editBtn);
            add(deleteBtn);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            return this;
        }
    }

    // Editor with buttons and actions
    static class ButtonEditor extends DefaultCellEditor {
        protected JPanel panel;
        private JButton editBtn, deleteBtn;
        private AdminDashboard dashboard;
        private int selectedRow;

        public ButtonEditor(JCheckBox checkBox, AdminDashboard dashboard) {
            super(checkBox);
            this.dashboard = dashboard;
            panel = new JPanel(new FlowLayout());
            panel.setBackground(WHITE_COLOR);
            
            editBtn = new JButton("Edit");
            deleteBtn = new JButton("Delete");

            // Style the buttons to match the renderer
            editBtn.setBackground(PRIMARY_COLOR);
            editBtn.setForeground(WHITE_COLOR);
            editBtn.setFocusable(false);
            editBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
            
            deleteBtn.setBackground(ERROR_FG);
            deleteBtn.setForeground(WHITE_COLOR);
            deleteBtn.setFocusable(false);
            deleteBtn.setFont(new Font("SansSerif", Font.BOLD, 12));

            panel.add(editBtn);
            panel.add(deleteBtn);

            editBtn.addActionListener(e -> SwingUtilities.invokeLater(() -> {
                fireEditingStopped(); // stop editing first
                Object id = table.getValueAt(selectedRow, 0);
                Object name = table.getValueAt(selectedRow, 1);
                Object price = table.getValueAt(selectedRow, 2);
                Object status = table.getValueAt(selectedRow, 3);
                showEditDialog(id, name, price, status);
            }));

            deleteBtn.addActionListener(e -> {
                fireEditingStopped();
                // Create a styled confirmation dialog
                JPanel confirmPanel = new JPanel(new BorderLayout(10, 10));
                confirmPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
                confirmPanel.setBackground(WHITE_COLOR);
                
                JLabel confirmMessage = new JLabel("Are you sure you want to delete this item?");
                confirmMessage.setFont(new Font("SansSerif", Font.BOLD, 14));
                confirmMessage.setForeground(PRIMARY_COLOR);
                confirmPanel.add(confirmMessage, BorderLayout.CENTER);
                
                int confirm = JOptionPane.showConfirmDialog(
                    null, 
                    confirmPanel, 
                    "Confirm Delete", 
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    Object id = table.getValueAt(selectedRow, 0);
                    AddOnDAO.deleteAddOnById((int) id);
                    // Refresh table data
                    ArrayList<Object[]> updatedList = AddOnDAO.fetchAllAddOns();
                    allProducts = updatedList.toArray(new Object[0][5]);
                    filterProducts("");
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            selectedRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }

        private void showEditDialog(Object id, Object name, Object price, Object status) {
            // Create a custom dialog with styled components matching the Add dialog
            JDialog dialog = new JDialog();
            dialog.setTitle("Edit AddOn");
            dialog.setModal(true);
            dialog.setSize(400, 350);
            dialog.setResizable(false);

            dialog.setLocationRelativeTo(null);
            dialog.getContentPane().setBackground(BACKGROUND_COLOR);
            
            JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
            form.setBorder(new EmptyBorder(20, 20, 20, 20));
            form.setBackground(BACKGROUND_COLOR);
            
            // Prepare the price value
            String numericOnly = price.toString().replaceAll("[^0-9.]", "").trim();
            double convertedPrice = Double.parseDouble(numericOnly);
            
            JTextField nameField = new JTextField(name.toString(), 20);
            nameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            JTextField priceField = new JTextField(String.format("%.2f", convertedPrice), 10);
            priceField.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            String[] statusOptions = {"Available", "Unavailable"};
            JComboBox<String> statusDropdown = new JComboBox<>(statusOptions);
            statusDropdown.setFont(new Font("SansSerif", Font.PLAIN, 14));
            statusDropdown.setBackground(WHITE_COLOR);
            
            // Set the current status
            boolean isAvailable = "Available".equals(status.toString());
            statusDropdown.setSelectedItem(isAvailable ? "Available" : "Unavailable");
            
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            nameLabel.setForeground(PRIMARY_COLOR);
            
            JLabel priceLabel = new JLabel("Price:");
            priceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            priceLabel.setForeground(PRIMARY_COLOR);
            
            JLabel statusLabel = new JLabel("Status:");
            statusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            statusLabel.setForeground(PRIMARY_COLOR);
            
            form.add(nameLabel);
            form.add(nameField);
            form.add(priceLabel);
            form.add(priceField);
            form.add(statusLabel);
            form.add(statusDropdown);
            
            JPanel buttonPanel = new JPanel();
            buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

            buttonPanel.setBackground(BACKGROUND_COLOR);
            
            JButton saveButton = new JButton("Update");
            saveButton.setBackground(PRIMARY_COLOR);
            saveButton.setForeground(WHITE_COLOR);
            saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            saveButton.setFocusPainted(false);
   
            
            JButton cancelButton = new JButton("Cancel");
            cancelButton.setBackground(WHITE_COLOR);
            cancelButton.setForeground(PRIMARY_COLOR);
            cancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            cancelButton.setFocusPainted(false);
            
            buttonPanel.add(saveButton);
            buttonPanel.add(Box.createHorizontalStrut(10)); // 10px gap
            buttonPanel.add(cancelButton);
            
            form.add(Box.createHorizontalStrut(5));
            form.add(buttonPanel);
            
            dialog.add(form);
            
            cancelButton.addActionListener(e -> dialog.dispose());
            
            saveButton.addActionListener(e -> {
                String newName = nameField.getText().trim();
                String priceText = priceField.getText().trim();
                boolean newStatus = statusDropdown.getSelectedItem().equals("Available");

                if (newName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                try {
                    double newPrice = Double.parseDouble(priceText);
                    AddOnDAO.updateAddOnById((int) id, newName, newPrice, newStatus);
                    // Refresh
                    ArrayList<Object[]> updatedList = AddOnDAO.fetchAllAddOns();
                    allProducts = updatedList.toArray(new Object[0][5]);
                    filterProducts("");
                    dialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid price value", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            dialog.setVisible(true);
        }
    }
}