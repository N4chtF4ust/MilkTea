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

public class AddProductDialog extends JDialog {

    private JTextField productNameField;
    private JTextField smallPriceField;
    private JTextField mediumPriceField;
    private JTextField largePriceField;
    private JCheckBox availabilityCheckbox;
    private JLabel imagePreviewLabel;
    private String selectedImagePath = null;
    private final int PREVIEW_WIDTH = 150;
    private final int PREVIEW_HEIGHT = 150;
    private boolean isProductAdded = false;

    public AddProductDialog(Frame owner) {
        super(owner, "Add New Product", true);
        initializeUI();
    }

    private void initializeUI() {
        setSize(500, 600);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Add New Product");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(new Color(18, 52, 88));
        titlePanel.add(titleLabel);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Product Name:"), gbc);
        gbc.gridx = 1;
        productNameField = new JTextField(20);
        formPanel.add(productNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Small Price (PHP):"), gbc);
        gbc.gridx = 1;
        smallPriceField = new JTextField(20);
        formPanel.add(smallPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Medium Price (PHP):"), gbc);
        gbc.gridx = 1;
        mediumPriceField = new JTextField(20);
        formPanel.add(mediumPriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Large Price (PHP):"), gbc);
        gbc.gridx = 1;
        largePriceField = new JTextField(20);
        formPanel.add(largePriceField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Availability:"), gbc);
        gbc.gridx = 1;
        availabilityCheckbox = new JCheckBox("Available");
        availabilityCheckbox.setSelected(true);
        formPanel.add(availabilityCheckbox, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Product Image:"), gbc);
        gbc.gridx = 1;
        JButton uploadButton = new JButton("Upload Image");
        uploadButton.setBackground(new Color(18, 52, 88));
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.addActionListener(e -> selectImage());
        formPanel.add(uploadButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        imagePreviewLabel = new JLabel("No image selected", JLabel.CENTER);
        imagePreviewLabel.setPreferredSize(new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT));
        imagePreviewLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
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
            isProductAdded = false;
            dispose();
        });

        JButton saveButton = new JButton("Save Product");
        saveButton.setBackground(new Color(18, 52, 88));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveProduct());

        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);

        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Updated here
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
                        JOptionPane.showMessageDialog(AddProductDialog.this, "Failed to load image preview.", "Error", JOptionPane.ERROR_MESSAGE);
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

        if (selectedImagePath == null) {
            JOptionPane.showMessageDialog(this, "Please select an image for the product.", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
            String imagePath = null;
            String errorMsg = null;

            @Override
            protected Boolean doInBackground() {
                try {
                    imagePath = uploadImageToSupabase(selectedImagePath);
                    if (imagePath == null) return false;

                    ProductAdmin newProduct = new ProductAdmin(
                            0,
                            productNameField.getText().trim(),
                            small, medium, large,
                            imagePath,
                            availabilityCheckbox.isSelected()
                    );
                    return ProductDAO.addProduct(newProduct);
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
                        JOptionPane.showMessageDialog(AddProductDialog.this, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        isProductAdded = true;
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(AddProductDialog.this, "Failed to add product. " + (errorMsg != null ? errorMsg : ""), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(AddProductDialog.this, "Unexpected error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }


    private String uploadImageToSupabase(String sourcePath) throws IOException {
        String fileExt = getFileExtension(sourcePath);
        String fileName = UUID.randomUUID() + fileExt;
        File file = new File(sourcePath);

        byte[] fileData = Files.readAllBytes(file.toPath());
        String mimeType = Files.probeContentType(file.toPath());

        RequestBody requestBody = RequestBody.create(fileData, okhttp3.MediaType.parse(mimeType));
        Request request = new Request.Builder()
                .url(SupabaseConfig.SUPABASE_URL + "/storage/v1/object/" + SupabaseConfig.BUCKET_NAME + "/" + fileName)
                .header("apikey", SupabaseConfig.SUPABASE_API_KEY)
                .header("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
                .header("x-upsert", "true")
                .put(requestBody)
                .build();

        try (Response response = SupabaseConfig.client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return fileName;
            } else {
                String err = response.body() != null ? response.body().string() : "Unknown error";
                throw new IOException("Upload failed: " + err);
            }
        }
    }


    private static String getFileExtension(String path) {
        int dotIndex = path.lastIndexOf(".");
        return (dotIndex == -1) ? "" : path.substring(dotIndex);
    }

    public static boolean showDialog(JFrame parent) {
        AddProductDialog dialog = new AddProductDialog(parent);
        dialog.setVisible(true);
        return dialog.isProductAdded;
    }
}
