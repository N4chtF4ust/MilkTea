package com.kiosk.dao;

import com.kiosk.dbConnection.dbCon;
import com.kiosk.model.ProductAdmin;
import com.kiosk.supabase.SupabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
	
	
	
    public static List<ProductAdmin> getAllProducts() {
        List<ProductAdmin> productList = new ArrayList<>();
        String sql = "SELECT * FROM Product";

        try (Connection conn = dbCon.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ProductAdmin p = new ProductAdmin(
                    rs.getLong("id"),
                    rs.getString("productName"),
                    rs.getDouble("small"),
                    rs.getDouble("medium"),
                    rs.getDouble("large"),
                    rs.getString("img"),
                    rs.getBoolean("availability")
                );
                productList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productList;
    }

    public static boolean addProduct(ProductAdmin product) {
        String sql = "INSERT INTO Product (productName, small, medium, large, img, availability) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setDouble(2, product.getSmall());
            pstmt.setDouble(3, product.getMedium());
            pstmt.setDouble(4, product.getLarge());
            pstmt.setString(5, product.getImg());
            pstmt.setBoolean(6, product.isAvailability());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Deletes a product by its ID
     * 
     * @param id The ID of the product to delete
     * @return true if deletion was successful, false otherwise
     */
    /**
     * Delete a product by its ID
     * 
     * @param productId the ID of the product to delete
     * @return true if deletion was successful, false otherwise
     */
    public static boolean deleteProduct(int productId) {
        String sql = "DELETE FROM Product WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Get a product by its ID
     * 
     * @param productId the ID of the product to retrieve
     * @return the ProductAdmin object or null if not found
     */
    public static ProductAdmin getProductById(int productId) {
        String sql = "SELECT * FROM Product WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, productId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new ProductAdmin(
                        rs.getInt("id"),
                        rs.getString("productName"),
                        rs.getDouble("small"),
                        rs.getDouble("medium"),
                        rs.getDouble("large"),
                        rs.getString("img"),
                        rs.getBoolean("availability")
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     * Update an existing product
     * 
     * @param product the product with updated values
     * @return true if update was successful, false otherwise
     */
    public static boolean updateProduct(ProductAdmin product) {
        String sql = "UPDATE Product SET productName = ?, small = ?, medium = ?, large = ?, " +
                     "img = ?, availability = ? WHERE id = ?";
        
        try (Connection conn = dbCon.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, product.getProductName());
            pstmt.setDouble(2, product.getSmall());
            pstmt.setDouble(3, product.getMedium());
            pstmt.setDouble(4, product.getLarge());
            pstmt.setString(5, product.getImg());
            pstmt.setBoolean(6, product.isAvailability());
            pstmt.setInt(7, (int) product.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
