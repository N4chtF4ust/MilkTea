package com.kiosk.dbConnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import io.github.cdimascio.dotenv.Dotenv;

public class dbCon {
    private static final Dotenv dotenv = Dotenv.configure()
        .filename(".env") // Optional if file is named `.env`
        .load();

    private static final String URL = dotenv.get("DB_URL");
    private static final String USER = dotenv.get("DB_USER");
    private static final String PASSWORD = dotenv.get("DB_PASSWORD");

    static {
        // Ensure that required environment variables are set
        if (Objects.isNull(URL) || Objects.isNull(USER) || Objects.isNull(PASSWORD)) {
            throw new IllegalStateException("Missing environment variables for database connection");
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
