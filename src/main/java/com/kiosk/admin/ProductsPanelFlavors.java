package com.kiosk.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.*;

import com.kiosk.cache_image.GetCachedImagePath;
import com.kiosk.dao.AddOnDAO;
import com.kiosk.dao.ProductDAO;
import com.kiosk.model.Product;
import com.kiosk.model.ProductAdmin;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;




public class ProductsPanelFlavors extends JPanel {
	
	

    public static final int PRODUCTS_PER_PAGE = 10;
    public static final int TOTAL_PRODUCTS = 20;
 

    public static Object[][] allProducts ;
    public static Object[][] filteredProducts ;

    public static JTable table;
    public static DefaultTableModel model;
    public static JPanel paginationPanel;
    public static int currentPage = 1;
    
    
    
    

    public static JPanel ProductsPanelFlavors(AdminDashboard adminDashboard) {
        JPanel productPanel = new JPanel(new BorderLayout());
        Color backgroundColor = new Color(217, 217, 217);
        productPanel.setBackground(backgroundColor);
        
        
        List<ProductAdmin> productList = ProductDAO.getAllProducts();
        allProducts = new Object[productList.size()][7];

        int index = 0;
        for (ProductAdmin p : productList) {
            // Format prices
            Map<String, String> priceMap = new HashMap<>();
            priceMap.put("Small", "PHP " + String.format("%.2f", p.getSmall()));
            priceMap.put("Medium", "PHP " + String.format("%.2f", p.getMedium()));
            priceMap.put("Large", "PHP " + String.format("%.2f", p.getLarge()));

            // Fill table row
            allProducts[index][0] = p.getId();
            allProducts[index][1] = p.getProductName();
            allProducts[index][2] = priceMap;
            allProducts[index][3] = "Small"; // Default size
            allProducts[index][4] = p.isAvailability() ? "Available" : "Unavailable";

            // Load product image
            ImageIcon icon;
            try {
                URL url = new URL(GetCachedImagePath.getCachedImagePath(p.getImg()));
                ImageIcon originalIcon = new ImageIcon(url);

                // Get original width and height
                int originalWidth = originalIcon.getIconWidth();
                int originalHeight = originalIcon.getIconHeight();

                // Calculate new dimensions (20% of original)
                int newWidth = (int)(originalWidth * 0.04);
                int newHeight = (int)(originalHeight * 0.04);

                // Resize image smoothly
                Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);

                ImageIcon resizedIcon = new ImageIcon(scaledImage);
                allProducts[index][5] = resizedIcon;

            } catch (MalformedURLException e) {
                e.printStackTrace();
                // fallback or error handling
            }


  

            index++;
        }

        // Copy to filtered list for filtering logic (if any)
        filteredProducts = allProducts;


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

        backBtn.addActionListener(e -> adminDashboard.showPanel("AddOns"));

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

        // 2. Add this code after the addButton declaration to handle the click event
        addButton.addActionListener(e -> {
            Window window = SwingUtilities.getWindowAncestor(productPanel);
            if (window instanceof JFrame) {
                boolean isAdded = AddProductDialog.showDialog((JFrame) window);
                if (isAdded) {
                    List<ProductAdmin> updatedProducts = ProductDAO.getAllProducts();
                    refreshProductTable(updatedProducts);
                }
            }
        });

        
        
        
        
        
        searchPanel.add(addButton);

     // Replace the section where the table and action buttons are set up with this code

        String[] columnNames = {"ID", "Product Name", "Size", "Price", "Status", "Image", "Action"};
        model = new DefaultTableModel(null, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make the Size column and Action column editable
                return column == 2 || column == 6;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 5) {
                    return ImageIcon.class;
                }
                return Object.class;
            }
        };

        table = new JTable(model);
        table.setRowHeight(50);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center Renderer
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            if (i != 5 && i != 6) {
                table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        // Status Renderer
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(String.valueOf(value), SwingConstants.CENTER);
                label.setOpaque(true);
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

        // Size Dropdown
        String[] sizes = {"Small", "Medium", "Large"};
        TableColumn sizeColumn = table.getColumnModel().getColumn(2);
        sizeColumn.setCellEditor(new DefaultCellEditor(new JComboBox<>(sizes)));

        table.getModel().addTableModelListener(e -> {
            if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                String selectedSize = (String) table.getValueAt(row, 2);
                int dataIndex = (currentPage - 1) * PRODUCTS_PER_PAGE + row;
                @SuppressWarnings("unchecked")
                Map<String, String> priceMap = (Map<String, String>) filteredProducts[dataIndex][2];
                table.setValueAt(priceMap.get(selectedSize), row, 3);
                filteredProducts[dataIndex][3] = selectedSize;
            }
        });

        // Image Renderer
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setHorizontalAlignment(SwingConstants.CENTER);
                if (value instanceof ImageIcon) {
                    label.setIcon((ImageIcon) value);
                } else {
                    label.setText("No Image");
                }
                return label;
            }
        });

        // Action Buttons - Replace with new renderer and editor
        table.getColumnModel().getColumn(6).setCellRenderer(new ButtonRendererFlavors());
        table.getColumnModel().getColumn(6).setCellEditor(new ButtonEditorFlavors(new JCheckBox(), adminDashboard, table));

        // Set preferred width for the action column
     //   table.getColumnModel().getColumn(6).setPreferredWidth(150);

        // Make sure table handles clicks properly - this is important for button editors
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = table.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / table.getRowHeight();
                
                if (row < table.getRowCount() && row >= 0 && column < table.getColumnCount() && column >= 0) {
                    // Ensure we're in a valid cell
                    if (column == 6) {
                        // If in the Action column, manually start cell editing
                        table.editCellAt(row, column);
                        Component comp = table.getEditorComponent();
                        if (comp != null) {
                            comp.requestFocusInWindow();
                        }
                    }
                }
            }
        });
        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 40));

        paginationPanel = new JPanel();
        paginationPanel.setBackground(backgroundColor);
        paginationPanel.setBorder(new EmptyBorder(10, 40, 30, 40));

        updateTable(1);
        createPaginationButtons();

        JPanel topContainerPanel = new JPanel(new BorderLayout());
        topContainerPanel.add(topPanel, BorderLayout.NORTH);
        topContainerPanel.add(searchPanel, BorderLayout.SOUTH);

        productPanel.add(topContainerPanel, BorderLayout.NORTH);
        productPanel.add(scroll, BorderLayout.CENTER);
        productPanel.add(paginationPanel, BorderLayout.SOUTH);

        return productPanel;
    }
    
    
    
 // 3. Add this helper method at the end of the ProductsPanelFlavors class
    public static void refreshProductTable(List<ProductAdmin> productList) {
        new SwingWorker<Object[][], Void>() {
            @Override
            protected Object[][] doInBackground() throws Exception {
                Object[][] tempProducts = new Object[productList.size()][7];
                int index = 0;
                for (ProductAdmin p : productList) {
                    // Format prices
                    Map<String, String> priceMap = new HashMap<>();
                    priceMap.put("Small", "PHP " + String.format("%.2f", p.getSmall()));
                    priceMap.put("Medium", "PHP " + String.format("%.2f", p.getMedium()));
                    priceMap.put("Large", "PHP " + String.format("%.2f", p.getLarge()));

                    // Fill table row
                    tempProducts[index][0] = p.getId();
                    tempProducts[index][1] = p.getProductName();
                    tempProducts[index][2] = priceMap;
                    tempProducts[index][3] = "Small"; // Default size
                    tempProducts[index][4] = p.isAvailability() ? "Available" : "Unavailable";

                    // Load product image
                    try {
                        URL url = new URL(GetCachedImagePath.getCachedImagePath(p.getImg()));
                        ImageIcon originalIcon = new ImageIcon(url);

                        int originalWidth = originalIcon.getIconWidth();
                        int originalHeight = originalIcon.getIconHeight();

                        int newWidth = (int) (originalWidth * 0.04);
                        int newHeight = (int) (originalHeight * 0.04);

                        Image scaledImage = originalIcon.getImage().getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                        ImageIcon resizedIcon = new ImageIcon(scaledImage);
                        tempProducts[index][5] = resizedIcon;
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                        tempProducts[index][5] = null; // or some placeholder icon
                    }

                    tempProducts[index][6] = "Action"; // Placeholder for action buttons
                    index++;
                }
                return tempProducts;
            }

            @Override
            protected void done() {
                try {
                    allProducts = get();  // get() fetches the result from doInBackground
                    filteredProducts = allProducts;
                    currentPage = 1;
                    updateTable(currentPage);
                    createPaginationButtons();
                } catch (Exception e) {
                    e.printStackTrace();
                    // Handle errors during async processing
                }
            }
        }.execute();
    }

    
    
    static JButton createStyledButton(String text, Color bg, Color fg) {
        JButton button = new JButton(text);
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("SansSerif", Font.PLAIN, 12));
        button.setOpaque(true);
        button.setBorderPainted(false);

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });

        return button;
    }


    private static void updateTable(int page) {
        model.setRowCount(0);
        int start = (page - 1) * PRODUCTS_PER_PAGE;
        int end = Math.min(start + PRODUCTS_PER_PAGE, filteredProducts.length);

        for (int i = start; i < end; i++) {
            @SuppressWarnings("unchecked")
            Map<String, String> priceMap = (Map<String, String>) filteredProducts[i][2];
            String selectedSize = (String) filteredProducts[i][3];
            String price = priceMap.get(selectedSize);

            model.addRow(new Object[]{
                    filteredProducts[i][0],
                    filteredProducts[i][1],
                    selectedSize,
                    price,
                    filteredProducts[i][4],
                    filteredProducts[i][5],
                    filteredProducts[i][6]
            });
        }

        adjustRowHeights();
    }

    private static void adjustRowHeights() {
        for (int row = 0; row < table.getRowCount(); row++) {
            Object value = table.getValueAt(row, 5);  // column 5 = Image column
            int rowHeight = table.getRowHeight();    // fallback default row height

            if (value instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) value;
                int iconHeight = icon.getIconHeight();
                // Add some padding for better appearance
                rowHeight = Math.max(rowHeight, iconHeight + 10);
            }

            table.setRowHeight(row, rowHeight);
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
            JButton prev = new JButton("<");
            prev.addActionListener(e -> {
                currentPage = startPage - 1;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(prev);
        }

        for (int i = startPage; i <= endPage; i++) {
            JButton pageBtn = new JButton(String.valueOf(i));
            pageBtn.setForeground(new Color(18, 52, 88));
            pageBtn.setBackground(Color.WHITE);
            if (i == currentPage) {
                pageBtn.setBackground(new Color(18, 52, 88));
                pageBtn.setForeground(Color.WHITE);
            }
            int pageNum = i;
            pageBtn.addActionListener(e -> {
                currentPage = pageNum;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(pageBtn);
        }

        if (endPage < totalPages) {
            JButton next = new JButton(">");
            next.addActionListener(e -> {
                currentPage = endPage + 1;
                updateTable(currentPage);
                createPaginationButtons();
            });
            paginationPanel.add(next);
        }

        paginationPanel.revalidate();
        paginationPanel.repaint();
    }
    
    
  
    
    
}

class ButtonEditorFlavors extends DefaultCellEditor {
    private static final Color PRIMARY_COLOR = new Color(18, 52, 88);
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color ERROR_FG = new Color(156, 0, 6);

    protected JButton editBtn;
    protected JButton deleteBtn;
    protected String label;
    protected boolean isPushed;
    protected int row;
    protected JTable table;
    protected AdminDashboard dashboard;

    public ButtonEditorFlavors(JCheckBox checkBox, AdminDashboard dashboard, JTable table) {
        super(checkBox);
        this.dashboard = dashboard;
        this.table = table;
        
        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");
        
        styleButton(editBtn, PRIMARY_COLOR, WHITE_COLOR);
        styleButton(deleteBtn, ERROR_FG, WHITE_COLOR);
        
        editBtn.setActionCommand("Edit");
        deleteBtn.setActionCommand("Delete");
        
        // Add action listeners
        editBtn.addActionListener(e -> {
            fireEditingStopped();
            handleEditAction();
        });
        
        deleteBtn.addActionListener(e -> {
            fireEditingStopped();
            handleDeleteAction();
        });
    }
    
    private void handleEditAction() {
        try {
            // Get the product ID from the table model
        	int productId = ((Number) table.getValueAt(row, 0)).intValue();

            String productName = (String) table.getValueAt(row, 1);
            String size = (String) table.getValueAt(row, 2);
            String status = (String) table.getValueAt(row, 4);
            ImageIcon icon = (ImageIcon) table.getValueAt(row, 5);
            
            // Open the edit dialog
            Window window = SwingUtilities.getWindowAncestor(table);
            if (window instanceof JFrame) {
                boolean isUpdated = EditProductDialog.showDialog((JFrame) window, productId, productName, size, status, icon);
                if (isUpdated) {
                    // Refresh the table with updated data
                    List<ProductAdmin> updatedProducts = ProductDAO.getAllProducts();
                    ProductsPanelFlavors.refreshProductTable(updatedProducts);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing edit action: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    private void handleDeleteAction() {
        try {
            // Get the product ID from the table model
        	int productId = ((Number) table.getValueAt(row, 0)).intValue();

            String productName = (String) table.getValueAt(row, 1);
            
            // Confirm delete
            int option = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(table),
                "Are you sure you want to delete " + productName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (option == JOptionPane.YES_OPTION) {
                // Delete the product
            	
                ProductAdmin product = ProductDAO.getProductById(productId);
                String imgpath = null;

                if (product != null) {
                	imgpath = product.getImg();
                    System.out.println("The  image  is: " + imgpath);
                } else {
                    System.err.println("Product with ID " + productId + " not found.");
                }

                boolean isDeleted = ProductDAO.deleteProduct(productId);
                try {
					boolean imgDeleted = EditProductDialog.deleteOldImageFromSupabase(imgpath.toString());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                if (isDeleted) {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(table),
                        "Product deleted successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                    // Refresh the table with updated data
                    List<ProductAdmin> updatedProducts = ProductDAO.getAllProducts();
                    ProductsPanelFlavors.refreshProductTable(updatedProducts);
                } else {
                    JOptionPane.showMessageDialog(
                        SwingUtilities.getWindowAncestor(table),
                        "Failed to delete product. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error processing delete action: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        }
        
        
  
    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bg);
            }
        });
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
                                               boolean isSelected, int row, int column) {
        this.row = row;
        this.label = (value == null) ? "" : value.toString();
        
        JPanel panel = new JPanel();

        
        
       
            panel = new JPanel(new GridBagLayout()); // Centers content both horizontally and vertically
            panel.setOpaque(true);

  
    
            // Use GridBagConstraints to place buttons side-by-side
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 5, 0, 5); // Add spacing between buttons
            panel.add(editBtn, gbc);

            gbc.gridx = 1;
            panel.add(deleteBtn, gbc);
      

        
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return label;
    }
}


class ButtonRendererFlavors implements TableCellRenderer {
    private static final Color PRIMARY_COLOR = new Color(18, 52, 88);  // #123458
    private static final Color WHITE_COLOR = Color.WHITE;
    private static final Color ERROR_FG = new Color(156, 0, 6);
    
    private final JButton editBtn;
    private final JButton deleteBtn;
    private final JPanel panel;

    public ButtonRendererFlavors() {
        panel = new JPanel(new GridBagLayout()); // Centers content both horizontally and vertically
        panel.setOpaque(true);

        editBtn = new JButton("Edit");
        deleteBtn = new JButton("Delete");

        styleButton(editBtn, PRIMARY_COLOR, WHITE_COLOR);
        styleButton(deleteBtn, ERROR_FG, WHITE_COLOR);

        // Use GridBagConstraints to place buttons side-by-side
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 5, 0, 5); // Add spacing between buttons
        panel.add(editBtn, gbc);

        gbc.gridx = 1;
        panel.add(deleteBtn, gbc);
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                 boolean isSelected, boolean hasFocus,
                                                 int row, int column) {
        return panel;
    }

    private void styleButton(JButton button, Color bg, Color fg) {
        button.setBackground(bg);
        button.setForeground(fg);
        button.setFocusable(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
     //   button.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
        button.setOpaque(true);
        button.setBorderPainted(false);
    }
}






