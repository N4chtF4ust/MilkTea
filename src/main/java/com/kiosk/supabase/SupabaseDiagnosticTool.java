package com.kiosk.supabase;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SupabaseDiagnosticTool extends JFrame {
    // Supabase credentials from your original code


	Dotenv dotenv = Dotenv.load();

	private  final String SUPABASE_URL = dotenv.get("SUPABASE_URL");
	private  final String SUPABASE_API_KEY = dotenv.get("SUPABASE_API_KEY");
	private  final String BUCKET_NAME = dotenv.get("SUPABASE_BUCKET_NAME");

    // Configure OkHttpClient with longer timeouts
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    private JTextArea logArea;
    private JTextField bucketNameField;
    private JButton testConnectionButton;
    private JButton listBucketsButton;
    private JButton testBucketButton;
    private JButton createBucketButton;

    public SupabaseDiagnosticTool() {
        setTitle("Supabase Storage Diagnostic Tool");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Main layout
        setLayout(new BorderLayout());

        // Top panel for connection and bucket inputs
        JPanel topPanel = new JPanel(new BorderLayout());

        // Connection info panel
        JPanel connectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        connectionPanel.add(new JLabel("Supabase URL:"));
        JTextField urlField = new JTextField(SUPABASE_URL, 30);
        urlField.setEditable(false);
        connectionPanel.add(urlField);

        testConnectionButton = new JButton("Test Connection");
        connectionPanel.add(testConnectionButton);
        topPanel.add(connectionPanel, BorderLayout.NORTH);

        // Bucket panel
        JPanel bucketPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bucketPanel.add(new JLabel("Bucket Name:"));
        bucketNameField = new JTextField(BUCKET_NAME, 20);
        bucketPanel.add(bucketNameField);

        testBucketButton = new JButton("Test Bucket Access");
        bucketPanel.add(testBucketButton);

        listBucketsButton = new JButton("List All Buckets");
        bucketPanel.add(listBucketsButton);

        createBucketButton = new JButton("Create Bucket");
        bucketPanel.add(createBucketButton);

        topPanel.add(bucketPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // Log area
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(logArea);
        add(scrollPane, BorderLayout.CENTER);

        // Setup button actions
        setupActions();

        // Initial log message
        log("Diagnostic tool started. API Key (first 10 chars): " +
            SUPABASE_API_KEY.substring(0, 10) + "...");
    }

    private void setupActions() {
        testConnectionButton.addActionListener(this::testConnection);
        listBucketsButton.addActionListener(this::listBuckets);
        testBucketButton.addActionListener(this::testBucket);
        createBucketButton.addActionListener(this::createBucket);
    }

    private void testConnection(ActionEvent e) {
        log("\n--- Testing Connection to Supabase ---");

        new Thread(() -> {
            try {
                // Test basic connectivity
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/storage/v1/bucket")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    log("Connection Response: " + response.code() + " " + response.message());

                    if (response.isSuccessful()) {
                        log("✓ Connection successful!");
                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "No response body";
                        log("⚠ Connection failed. Response body:");
                        log(responseBody);

                        // Check for common error types
                        if (response.code() == 401 || response.code() == 403) {
                            log("⚠ AUTHENTICATION ERROR: Your API key may be invalid or expired.");
                        } else if (response.code() >= 500) {
                            log("⚠ SERVER ERROR: The Supabase server is experiencing issues.");
                        }
                    }
                }
            } catch (IOException ex) {
                log("⚠ ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }

    private void listBuckets(ActionEvent e) {
        log("\n--- Listing All Buckets ---");

        new Thread(() -> {
            try {
                Request request = new Request.Builder()
                        .url(SUPABASE_URL + "/storage/v1/bucket")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get()
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    log("Response: " + response.code() + " " + response.message());

                    if (response.isSuccessful() && response.body() != null) {
                        String responseBody = response.body().string();
                        log("Available buckets:");

                        JSONArray buckets = new JSONArray(responseBody);
                        if (buckets.length() == 0) {
                            log("No buckets found in this project.");
                        }

                        for (int i = 0; i < buckets.length(); i++) {
                            JSONObject bucket = buckets.getJSONObject(i);
                            String publicStatus;

                            // Check the type of "public" and handle accordingly
                            if (bucket.get("public") instanceof Boolean) {
                                publicStatus = Boolean.toString(bucket.getBoolean("public"));
                            } else {
                                publicStatus = bucket.getString("public");  // Fallback to string if it's already a string
                            }

                            log(" - " + bucket.getString("name") +
                                " (ID: " + bucket.getString("id") + ", " +
                                "Public: " + publicStatus + ")");
                        }
                    } else {
                        String responseBody = response.body() != null ? response.body().string() : "No response body";
                        log("⚠ Failed to list buckets. Response body:");
                        log(responseBody);
                    }
                }
            } catch (IOException ex) {
                log("⚠ ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }


    private void testBucket(ActionEvent e) {
        String bucketName = bucketNameField.getText().trim();
        log("\n--- Testing Access to Bucket: " + bucketName + " ---");

        new Thread(() -> {
            try {
                // First test if the bucket exists
                Request bucketRequest = new Request.Builder()
                        .url(SUPABASE_URL + "/storage/v1/bucket/" + bucketName)
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .get()
                        .build();

                try (Response bucketResponse = client.newCall(bucketRequest).execute()) {
                    log("Bucket Info Response: " + bucketResponse.code() + " " + bucketResponse.message());

                    if (bucketResponse.isSuccessful()) {
                        // Handle successful response
                        if (bucketResponse.body() != null) {
                            String responseBody = bucketResponse.body().string();
                            log("Bucket details: " + responseBody);
                        }
                        log("✓ Bucket exists!");

                        // Now test listing objects within the bucket
                        testBucketObjects(bucketName);
                    } else {
                        // Handle errors
                        String responseBody = bucketResponse.body() != null ? bucketResponse.body().string() : "No response body";
                        log("⚠ Bucket test failed. Response body:");
                        log(responseBody);

                        // Handle specific error codes
                        if (bucketResponse.code() == 404) {
                            log("⚠ BUCKET NOT FOUND: The bucket '" + bucketName + "' does not exist.");
                            log("   You need to create this bucket first in the Supabase dashboard or using the Create Bucket button.");
                        } else if (bucketResponse.code() == 401 || bucketResponse.code() == 403) {
                            log("⚠ AUTHORIZATION ERROR: Check your API key and permissions.");
                        } else if (bucketResponse.code() >= 500) {
                            log("⚠ SERVER ERROR: The Supabase server is experiencing issues.");
                        } else {
                            log("⚠ Unexpected error occurred with status code: " + bucketResponse.code());
                        }
                    }
                }
            } catch (IOException ex) {
                log("⚠ ERROR: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }

    private void testBucketObjects(String bucketName) {
        log("\n--- Listing Objects in Bucket: " + bucketName + " ---");

        try {
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
                    .url(SUPABASE_URL + "/storage/v1/object/list/" + bucketName)
                    .addHeader("apikey", SUPABASE_API_KEY)
                    .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                log("Objects List Response: " + response.code() + " " + response.message());

                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    log("Objects in " + bucketName + ": " + responseBody);
                } else {
                    String responseBody = response.body() != null ? response.body().string() : "No response body";
                    log("⚠ Failed to list objects. Response body:");
                    log(responseBody);
                }
            }
        } catch (IOException e) {
            log("⚠ ERROR while listing objects: " + e.getMessage());
            e.printStackTrace();
        }
    }






    private void createBucket(ActionEvent e) {
        String bucketName = bucketNameField.getText().trim();
        log("\n--- Creating Bucket: " + bucketName + " ---");

        new Thread(() -> {
            try {
                RequestBody body = RequestBody.create(
                        MediaType.parse("application/json"),
                        new JSONObject()
                                .put("name", bucketName)
                                .put("public", false)  // Set the bucket to private initially
                                .toString()
                );

                Request createRequest = new Request.Builder()
                        .url(SUPABASE_URL + "/storage/v1/bucket")
                        .addHeader("apikey", SUPABASE_API_KEY)
                        .addHeader("Authorization", "Bearer " + SUPABASE_API_KEY)
                        .post(body)
                        .build();

                try (Response createResponse = client.newCall(createRequest).execute()) {
                    log("Create Bucket Response: " + createResponse.code() + " " + createResponse.message());

                    if (createResponse.isSuccessful()) {
                        log("✓ Bucket created successfully.");
                    } else {
                        String responseBody = createResponse.body() != null ? createResponse.body().string() : "No response body";
                        log("⚠ Failed to create bucket. Response body:");
                        log(responseBody);
                    }
                }
            } catch (IOException ex) {
                log("⚠ ERROR while creating bucket: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> logArea.append(message + "\n"));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SupabaseDiagnosticTool tool = new SupabaseDiagnosticTool();
            tool.setVisible(true);
        });
    }
}
