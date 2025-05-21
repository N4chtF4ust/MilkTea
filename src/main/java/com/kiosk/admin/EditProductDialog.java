package com.kiosk.admin;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.kiosk.dao.ProductDAO;
import com.kiosk.model.ProductAdmin;
import com.kiosk.supabase.SupabaseConfig;

import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class EditProductDialog extends JDialog {

    private JTextField productNameField;
    private JTextField smallPriceField;
    private JTextField mediumPriceField;
    private JTextField largePriceField;
    private JCheckBox availabilityCheckbox;
    private JLabel imagePreviewLabel;
    private String selectedImagePath = null;
    private final int PREVIEW_WIDTH = 150;
    private final int PREVIEW_HEIGHT = 150;
    private boolean isProductUpdated = false;
    private ProductAdmin currentProduct;
    private int productId;

    public EditProductDialog(Frame owner, int productId, String productName, String status, ImageIcon icon) {
        super(owner, "Edit Product", true);
        this.productId = productId;
        this.currentProduct = ProductDAO.getProductById(productId);
        this.selectedImagePath = currentProduct.getImg();
        initializeUI(productName, status, icon);
    }

    private void initializeUI(String productName, String status, ImageIcon icon) {
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Edit Product");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(18, 52, 88));
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        // Product ID (disabled)
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product ID:"), gbc);
        gbc.gridx = 1;
        JTextField idField = new JTextField(String.valueOf(productId));
        idField.setEditable(false);
        formPanel.add(idField, gbc);

        // Product Name
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productNameField = new JTextField(20);
        productNameField.setText(productName);
        formPanel.add(productNameField, gbc);

        // Small Price
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Small Price (PHP):"), gbc);
        gbc.gridx = 1;
        smallPriceField = new JTextField(20);
        smallPriceField.setText(String.format("%.2f", currentProduct.getSmall()));
        formPanel.add(smallPriceField, gbc);

        // Medium Price
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Medium Price (PHP):"), gbc);
        gbc.gridx = 1;
        mediumPriceField = new JTextField(20);
        mediumPriceField.setText(String.format("%.2f", currentProduct.getMedium()));
        formPanel.add(mediumPriceField, gbc);

        // Large Price
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Large Price (PHP):"), gbc);
        gbc.gridx = 1;
        largePriceField = new JTextField(20);
        largePriceField.setText(String.format("%.2f", currentProduct.getLarge()));
        formPanel.add(largePriceField, gbc);

        // Availability
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Availability:"), gbc);
        gbc.gridx = 1;
        availabilityCheckbox = new JCheckBox("Available");
        availabilityCheckbox.setSelected(status.equals("Available"));
        formPanel.add(availabilityCheckbox, gbc);

        // Product Image
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Product Image:"), gbc);
        gbc.gridx = 1;
        JButton uploadButton = new JButton("Change Image");
        uploadButton.setBackground(new Color(18, 52, 88));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.addActionListener(e -> selectImage());
        formPanel.add(uploadButton, gbc);

        // Image Preview
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        imagePreviewLabel = new JLabel("", JLabel.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Show current image
        if (icon != null) {
            Image img = icon.getImage();
            Image resizedImage = img.getScaledInstance(PREVIEW_WIDTH, PREVIEW_HEIGHT, Image.SCALE_SMOOTH);
            imagePreviewLabel.setIcon(new ImageIcon(resizedImage));
        } else {
            imagePreviewLabel.setText("No image available");
        }
        
        formPanel.add(imagePreviewLabel, gbc);

        // Wrap formPanel in a scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> {
            isProductUpdated = false;
            dispose();
        });

        JButton saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(18, 52, 88));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveProduct());

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    protected void selectImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Product Image");
        FileNameExtensionFilter imageFilter = new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(imageFilter);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            selectedImagePath = selectedFile.getAbsolutePath();

            imagePreviewLabel.setText("Loading preview...");
            imagePreviewLabel.setIcon(null);

            new SwingWorker<ImageIcon, Void>() {
                @Override
                protected ImageIcon doInBackground() {
                    ImageIcon icon = new ImageIcon(selectedImagePath);
                    Image img = icon.getImage();
                    int newWidth = Math.max(PREVIEW_WIDTH, (int) (img.getWidth(null) * 0.05));
                    int newHeight = Math.max(PREVIEW_HEIGHT, (int) (img.getHeight(null) * 0.05));
                    Image resizedImage = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    return new ImageIcon(resizedImage);
                }

                @Override
                protected void done() {
                    try {
                        ImageIcon resizedIcon = get();
                        imagePreviewLabel.setIcon(resizedIcon);
                        imagePreviewLabel.setText("");
                        imagePreviewLabel.setPreferredSize(new Dimension(resizedIcon.getIconWidth(), resizedIcon.getIconHeight()));
                        imagePreviewLabel.revalidate();
                        imagePreviewLabel.repaint();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(EditProductDialog.this, "Failed to load image preview.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }.execute();
        }
    }
    
    private void saveProduct() {
        if (productNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a product name.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double smallPrice, mediumPrice, largePrice;
        try {
            smallPrice = Double.parseDouble(smallPriceField.getText().trim());
            mediumPrice = Double.parseDouble(mediumPriceField.getText().trim());
            largePrice = Double.parseDouble(largePriceField.getText().trim());

            if (smallPrice < 0 || mediumPrice < 0 || largePrice < 0) {
                throw new NumberFormatException("Negative price.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid, non-negative prices.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        saveProductInBackground(smallPrice, mediumPrice, largePrice);
    }

    private void saveProductInBackground(double small, double medium, double large) {
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        new SwingWorker<Boolean, Void>() {
            String imagePath = selectedImagePath;
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    // Only upload a new image if it's a local file path (not a Supabase path)
                    if (selectedImagePath != null && !selectedImagePath.startsWith("http") && !selectedImagePath.equals(currentProduct.getImg())) {
                        imagePath = uploadImageToSupabase(selectedImagePath);
                        if (imagePath == null) return false;
                    }

                    ProductAdmin updatedProduct = new ProductAdmin(
                            productId,
                            productNameField.getText().trim(),
                            small, medium, large,
                            imagePath,
                            availabilityCheckbox.isSelected()
                    );
                    return ProductDAO.updateProduct(updatedProduct);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    errorMsg = ex.getMessage();
                    return false;
                }
            }

            @Override
            protected void done() {
                setCursor(Cursor.getDefaultCursor());
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(EditProductDialog.this, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        isProductUpdated = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(EditProductDialog.this, "Failed to update product. " + (errorMsg != null ? errorMsg : ""), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(EditProductDialog.this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private String uploadImageToSupabase(String sourcePath) throws IOException {
        ProductAdmin product = ProductDAO.getProductById(productId);
        String oldImage = null;

        if (product != null) {
            oldImage = product.getImg();
            System.out.println("The old image is: " + oldImage);
        } else {
            System.err.println("Product with ID " + productId + " not found.");
        }

        // Generate a unique file name for the new image
        String fileExt = getFileExtension(sourcePath);
        String fileName = UUID.randomUUID() + fileExt;
        File file = new File(sourcePath);

        byte[] fileData = Files.readAllBytes(file.toPath());
        String mimeType = Files.probeContentType(file.toPath());

        RequestBody requestBody = RequestBody.create(fileData, okhttp3.MediaType.parse(mimeType));
        Request uploadRequest = new Request.Builder()
                .url(SupabaseConfig.SUPABASE_URL + "/storage/v1/object/" + SupabaseConfig.BUCKET_NAME + "/" + fileName)
                .header("apikey", SupabaseConfig.SUPABASE_API_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
                .header("x-upsert", "true")
                .put(requestBody)
                .build();

        try (Response response = SupabaseConfig.client.newCall(uploadRequest).execute()) {
            if (response.isSuccessful()) {
                // Delete old image after successful upload
                if (oldImage != null && !oldImage.isEmpty()) {
                    deleteOldImageFromSupabase(oldImage);
                }
                return fileName;
            } else {
                String err = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Upload failed: " + err);
            }
        }
    }
    
    protected static  boolean deleteOldImageFromSupabase(String fileName) throws IOException {
        // The correct URL format for deletion is using "object" not "delete"
        String deleteUrl = SupabaseConfig.SUPABASE_URL + "/storage/v1/object/" + SupabaseConfig.BUCKET_NAME;
        
        // Append the file path to the URL
        if (!fileName.startsWith("/")) {
            deleteUrl += "/";
        }
        deleteUrl += fileName;
        
        Request deleteRequest = new Request.Builder()
            .url(deleteUrl)
            .header("apikey", SupabaseConfig.SUPABASE_API_KEY)
            .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
            .delete() // Use DELETE method instead of POST
            .build();

        try (Response response = SupabaseConfig.client.newCall(deleteRequest).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Old image deleted: " + fileName);
                return true;
            } else {
                System.err.println("Failed to delete old image: " + fileName + 
                                 ", Status code: " + response.code() + 
                                 ", Response body: " + (response.body() != null ? response.body().string() : "null"));
                return false;
            }
        }
        
    
    }

    private static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        return (dotIndex == -1) ? "" : path.substring(dotIndex);
    }

    public static boolean showDialog(JFrame parent, int productId, String productName, String status, String status2, ImageIcon icon) {
        EditProductDialog dialog = new EditProductDialog(parent, productId, productName, status, icon);
        dialog.setVisible(true);
        return dialog.isProductUpdated;
    }
}