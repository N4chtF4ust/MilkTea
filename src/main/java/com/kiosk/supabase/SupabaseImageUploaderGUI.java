package com.kiosk.supabase;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SupabaseImageUploaderGUI extends JFrame {
    private JLabel imageLabel;
    private File selectedFile;
    private JPanel imagePanel;

    public static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    public SupabaseImageUploaderGUI() {
        setTitle("Supabase Image Uploader & Gallery");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        imageLabel = new JLabel("No image selected", SwingConstants.CENTER);
        imageLabel.setPreferredSize(new Dimension(300, 300));
        add(imageLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton selectButton = new JButton("Select Image");
        JButton uploadButton = new JButton("Upload Image");
        JButton refreshButton = new JButton("Refresh Gallery");
        buttonPanel.add(selectButton);
        buttonPanel.add(uploadButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);

        imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        add(scrollPane, BorderLayout.CENTER);

        selectButton.addActionListener(e -> chooseImage());
        uploadButton.addActionListener(e -> {
            if (selectedFile != null) {
                uploadToSupabase(selectedFile);
            } else {
                JOptionPane.showMessageDialog(this, "Please select an image first.");
            }
        });
        refreshButton.addActionListener(e -> fetchAndDisplayImages());

        SwingUtilities.invokeLater(this::fetchAndDisplayImages);
    }

    private void chooseImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select an Image");
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(
            new javax.swing.filechooser.FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "webp", "gif")
        );

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            selectedFile = fileChooser.getSelectedFile();

            // Validate file extension
            String name = selectedFile.getName().toLowerCase();
            if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") ||
                  name.endsWith(".webp") || name.endsWith(".gif"))) {
                JOptionPane.showMessageDialog(this, "Only image files are allowed.", "Invalid File", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate file size (max 25 MB)
            long fileSizeInBytes = selectedFile.length();
            long maxSizeInBytes = 25L * 1024 * 1024; // 25 MB

            if (fileSizeInBytes > maxSizeInBytes) {
                JOptionPane.showMessageDialog(this, "File size must be less than 25 MB.", "File Too Large", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                BufferedImage originalImage = ImageIO.read(selectedFile);
                if (originalImage != null) {
                    int labelWidth = imageLabel.getWidth();
                    int labelHeight = imageLabel.getHeight();
                    Image scaledImage = originalImage.getScaledInstance(labelWidth, labelHeight, Image.SCALE_SMOOTH);

                    imageLabel.setIcon(new ImageIcon(scaledImage));
                    imageLabel.setText(null);
                    imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                    imageLabel.setVerticalAlignment(SwingConstants.CENTER);
                    imageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 2));
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid image format.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error loading image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void uploadToSupabase(File file) {
        new Thread(() -> {
            try {
                String fileName = file.getName();
                byte[] fileBytes = Files.readAllBytes(file.toPath());

                String contentType = Files.probeContentType(file.toPath());
                if (contentType == null) {
                    contentType = "image/jpeg";
                }

                RequestBody requestBody = RequestBody.create(MediaType.parse(contentType), fileBytes);

                Request request = new Request.Builder()
                        .url(SupabaseConfig.SUPABASE_URL + "/storage/v1/object/" + SupabaseConfig.BUCKET_NAME + "/" + fileName)
                        .addHeader("apikey", SupabaseConfig.SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
                        .addHeader("Content-Type", contentType)
                        .put(requestBody)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    String message = response.isSuccessful() ? "Upload successful!" : "Upload failed: " + response.message();
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, message);
                        fetchAndDisplayImages();
                    });
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Upload error: " + e.getMessage()));
            }
        }).start();
    }

    private void fetchAndDisplayImages() {
        imagePanel.removeAll();
        imagePanel.add(new JLabel("Loading...", SwingConstants.CENTER));
        imagePanel.revalidate();
        imagePanel.repaint();

        new Thread(() -> {
            try {
                // Prepare JSON request payload
                JSONObject requestJson = new JSONObject();
                requestJson.put("prefix", "");
                requestJson.put("limit", 100);
                requestJson.put("offset", 0);
                requestJson.put("sortBy", new JSONObject().put("column", "name").put("order", "asc"));

                RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    requestJson.toString()
                );

                Request request = new Request.Builder()
                        .url(SupabaseConfig.SUPABASE_URL + "/storage/v1/object/list/" + SupabaseConfig.BUCKET_NAME)
                        .addHeader("apikey", SupabaseConfig.SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
                        .addHeader("Content-Type", "application/json")
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("HTTP " + response.code() + ": " + response.message());
                    }

                    ResponseBody responseBody = response.body();
                    if (responseBody == null) {
                        throw new IOException("Empty response body");
                    }

                    String responseText = responseBody.string();
                    JSONArray array = new JSONArray(responseText);
                    List<String> urls = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject fileObj = array.getJSONObject(i);
                        if (fileObj.has("name")) {
                            String fileName = fileObj.getString("name");

                            // Skip placeholder or empty filenames early
                            if (fileName == null || fileName.trim().isEmpty() || fileName.equals(".emptyFolderPlaceholder")) {
                                continue;
                            }

                            String signedUrl = generateSignedUrl(fileName);

                            // Skip if URL is invalid or empty
                            if (signedUrl != null && !signedUrl.trim().isEmpty()) {
                                urls.add(signedUrl);
                            }
                        }
                    }


                    SwingUtilities.invokeLater(() -> {
                        imagePanel.removeAll();
                        displayImages(urls);
                        imagePanel.revalidate();
                        imagePanel.repaint();
                    });
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                        imagePanel,
                        "Failed to fetch images: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                    imagePanel.removeAll();
                    imagePanel.add(new JLabel("Error loading gallery.", SwingConstants.CENTER));
                    imagePanel.revalidate();
                    imagePanel.repaint();
                });
            }
        }).start();
    }


    private String generateSignedUrl(String fileName) {
        // Check for null, empty, or placeholder filename
        if (fileName == null || fileName.trim().isEmpty() || fileName.equals(".emptyFolderPlaceholder")) {
            System.err.println("Invalid or placeholder filename provided: " + fileName);
            return ""; // or return a default placeholder image URL if you have one
        }

        try {
            // Set a long expiration time (e.g., one year)
            JSONObject json = new JSONObject();
            json.put("expiresIn", 60 * 60 * 24 * 365); // 1 year in seconds

            RequestBody body = RequestBody.create(
                    MediaType.parse("application/json"),
                    json.toString()
            );

            Request request = new Request.Builder()
                    .url(SupabaseConfig.SUPABASE_URL + "/storage/v1/object/sign/" + SupabaseConfig.BUCKET_NAME + "/" + fileName)
                    .addHeader("apikey", SupabaseConfig.SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SupabaseConfig.SUPABASE_API_KEY)
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    JSONObject responseJson = new JSONObject(responseString);

                    if (responseJson.has("signedURL")) {
                        return SupabaseConfig.SUPABASE_URL + "/storage/v1" + responseJson.getString("signedURL");
                    } else {
                        System.err.println("Signed URL not present in response: " + responseString);
                    }
                } else {
                    System.err.println("Failed to generate signed URL. HTTP Code: " + response.code());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while generating signed URL: " + e.getMessage());
            e.printStackTrace();
        }

        // Fallback to public URL
        return SupabaseConfig.SUPABASE_URL + "/storage/v1/object/public/" + SupabaseConfig.BUCKET_NAME + "/" + fileName;
    }



    private void displayImages(List<String> urls) {
        for (String url : urls) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setPreferredSize(new Dimension(200, 200));
            panel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            JLabel label = new JLabel("Loading...", SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);
            imagePanel.add(panel);

            new Thread(() -> loadSingleImage(url, panel)).start();
        }
    }

    private void loadSingleImage(String url, JPanel panel) {
        new Thread(() -> {
            try {
                System.out.println("Loading image from: " + url); // 👈 log it

                BufferedImage image = ImageIO.read(new URL(url));

                if (image != null) {
                    Image scaled = image.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaled);
                    BufferedImage finalImg = image;

                    SwingUtilities.invokeLater(() -> {
                        panel.removeAll();
                        JLabel imgLabel = new JLabel(icon);
                        panel.add(imgLabel, BorderLayout.CENTER);
                        JButton viewButton = new JButton("View");
                        viewButton.addActionListener(e -> showFullSizeImage(finalImg));
                        panel.add(viewButton, BorderLayout.SOUTH);
                        panel.revalidate();
                        panel.repaint();
                    });
                } else {
                    throw new IOException("Could not decode image.");
                }
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    panel.removeAll();
                    JLabel errorLabel = new JLabel("Failed to load", SwingConstants.CENTER);
                    panel.add(errorLabel, BorderLayout.CENTER);
                    JButton retry = new JButton("Retry");
                    retry.addActionListener(ev -> loadSingleImage(url, panel));
                    panel.add(retry, BorderLayout.SOUTH);
                    panel.revalidate();
                    panel.repaint();
                });
                e.printStackTrace(); // 👈 Add for debug
            }
        }).start();
    }



    private void showFullSizeImage(BufferedImage image) {
        JFrame frame = new JFrame("Full Size Image");
        frame.setSize(600, 600);
        JLabel label = new JLabel(new ImageIcon(image));
        JScrollPane scrollPane = new JScrollPane(label);
        frame.add(scrollPane);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SupabaseImageUploaderGUI app = new SupabaseImageUploaderGUI();
            app.setVisible(true);
        });
    }
}
