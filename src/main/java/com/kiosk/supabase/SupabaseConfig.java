package com.kiosk.supabase;

import javax.swing.JOptionPane;

import io.github.cdimascio.dotenv.Dotenv;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SupabaseConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String SUPABASE_URL = dotenv.get("SUPABASE_URL");
    public static final String SUPABASE_API_KEY = dotenv.get("SUPABASE_API_KEY");
    public static final String BUCKET_NAME = dotenv.get("BUCKET_NAME");

    private static final OkHttpClient client = new OkHttpClient();

    static {
        checkSupabaseConnection();
    }

    private static void checkSupabaseConnection() {
        try {
            Request request = new Request.Builder()
                .url(SUPABASE_URL + "/rest/v1/") // base REST API
                .addHeader("apikey", SUPABASE_API_KEY)
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    showErrorAndExit("Failed to connect to Supabase. HTTP Code: " + response.code());
                } else {
                    System.out.println("Connected to Supabase successfully.");
                }
            }
        } catch (Exception e) {
            showErrorAndExit("Supabase connection error:\n" + e.getMessage());
        }
    }

    private static void showErrorAndExit(String message) {
        JOptionPane.showMessageDialog(
            null,
            message,
            "Connection Error",
            JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}
