package com.kiosk.dao;

import com.kiosk.dbConnection.dbCon;

import java.sql.*;
import java.util.ArrayList;

public class AddOnDAO {

    public static ArrayList<Object[]> fetchAllAddOns() {
        ArrayList<Object[]> list = new ArrayList<>();
        String query = "SELECT id, AddOnName, AddOnPrice, Availability FROM addOns";

        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[5];
                row[0] = rs.getInt("id");
                row[1] = rs.getString("AddOnName");
                row[2] = "PHP " + String.format("%.2f", rs.getDouble("AddOnPrice"));
                row[3] = rs.getBoolean("Availability") ? "Available" : "Unavailable";
                row[4] = "Action"; // placeholder
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    
    
    public static void deleteAddOnById(int id) {
        // Your JDBC logic to delete an add-on by ID
    	
        String query = "DELETE FROM addOns WHERE id = (?)";
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);

       
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAddOnById(int id, String name, double price, boolean status) {
        // Update your SQL query to update status (assuming `status` is a BOOLEAN column)
        String query = "UPDATE addOns SET AddOnName = ?, AddOnPrice = ?, Availability = ? WHERE id = ?";
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setBoolean(3, status);
            stmt.setInt(4, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    
    public static void insertAddOn(String name, double price, boolean status) {
        // Implement your SQL INSERT logic here
        // For example (pseudocode):
        // INSERT INTO addons (name, price, status) VALUES (?, ?, ?);
    	
    	
        String query = "INSERT INTO addOns (AddOnName, AddOnPrice, Availability) VALUES (?, ?, ?);";
        try (Connection conn = dbCon.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setBoolean(3, status);
       
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
