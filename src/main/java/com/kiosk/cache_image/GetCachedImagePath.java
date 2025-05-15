
package com.kiosk.cache_image;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.kiosk.supabase.SupabaseConfig;
import com.kiosk.supabase.SupabaseImageUploaderGUI;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.util.HashSet;
import java.util.Set;

public class GetCachedImagePath {
	
    public static Set<String> cachedFileNames  = new HashSet<>();
    

    public static String getCachedImagePath(String fileName) {
        // 1. Validate input
        if (fileName == null || fileName.trim().isEmpty() || fileName.equals(".emptyFolderPlaceholder")) {
            System.err.println("Invalid or placeholder filename provided: " + fileName);
            return ""; // Or path to default placeholder image
        }



        // 2. Define cache directory
        File cacheDir = new File("image_cache");
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }

        // 3. Define the requested file path
        File cachedFile = new File(cacheDir, fileName);
        System.out.println("Requested File Path: " + cachedFile.getAbsolutePath());

        // 4. Delete all other files in the cache directory except those in stringList
        List<String> deletedFiles = new ArrayList<>();
        File[] files = cacheDir.listFiles();
        if (files != null) {
            for (File file : files) {
                System.out.println("Checking file: " + file.getAbsolutePath());
                if (!cachedFileNames.contains(file.getName())) {
                    deletedFiles.add(file.getName());
                    if (file.delete()) {
                        System.out.println("Deleted file: " + file.getName());
                    } else {
                        System.err.println("Failed to delete: " + file.getAbsolutePath());
                    }
                }
            }
        }


        // Print deleted file names
        if (!deletedFiles.isEmpty()) {
            System.out.println("Deleted files: " + String.join(", ", deletedFiles));
        } else {
            System.out.println("No files were deleted.");
        }

        // 5. If the requested file now exists, return its URI as a file:// URL
        if (cachedFile.exists()) {
            System.out.println("The img cache is used file path: " + cachedFile.getAbsolutePath());
            try {
                // Convert absolute path to file:// URL format
                String fileUrl = cachedFile.toURI().toString();
                System.out.println("Using file URL: " + fileUrl);
                return fileUrl;
            } catch (Exception e) {
                System.err.println("Error converting file path to URL: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // 6. Otherwise, generate signed URL and download the image
        try {
            JSONObject json = new JSONObject();
            json.put("expiresIn", 60 * 60 * 24 * 365); // 1 year

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

            try (Response response = SupabaseImageUploaderGUI.client.newCall(request).execute()) {
                if (response.isSuccessful() && response.body() != null) {
                    String responseString = response.body().string();
                    JSONObject responseJson = new JSONObject(responseString);

                    if (responseJson.has("signedURL")) {
                        String signedUrl = SupabaseConfig.SUPABASE_URL + "/storage/v1" + responseJson.getString("signedURL");

                        // Download the image to local cache
                        Request imageRequest = new Request.Builder().url(signedUrl).build();
                        try (Response imageResponse = SupabaseImageUploaderGUI.client.newCall(imageRequest).execute()) {
                            if (imageResponse.isSuccessful() && imageResponse.body() != null) {
                                byte[] imageBytes = imageResponse.body().bytes();

                                // Save image to file
                                try (FileOutputStream fos = new FileOutputStream(cachedFile)) {
                                    fos.write(imageBytes);
                                }

                                // Return file as URL
                                return cachedFile.toURI().toString();
                            } else {
                                System.err.println("Failed to download image. HTTP Code: " + imageResponse.code());
                            }
                        }
                    } else {
                        System.err.println("Signed URL not present in response: " + responseString);
                    }
                } else {
                    System.err.println("Failed to generate signed URL. HTTP Code: " + response.code());
                }
            }
        } catch (Exception e) {
            System.err.println("Exception while caching image: " + e.getMessage());
            e.printStackTrace();
        }

        return ""; // Return empty string or fallback path if something goes wrong
    }
}