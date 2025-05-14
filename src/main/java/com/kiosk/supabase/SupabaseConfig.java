package com.kiosk.supabase;

import io.github.cdimascio.dotenv.Dotenv;

public class SupabaseConfig {
    private static final Dotenv dotenv = Dotenv.load();

    public static final String SUPABASE_URL = dotenv.get("SUPABASE_URL");
    public static final String SUPABASE_API_KEY = dotenv.get("SUPABASE_API_KEY");
    public static final String BUCKET_NAME = dotenv.get("BUCKET_NAME");
}
