package com.company.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBConnection {

    private static Connection conn = null;
    private static final String url = "jdbc:mysql://localhost:3306/employee_db";
    private static final String username = "root";
    private static final String password = "root";
	
	 // Constructor to initialize connection
    public DBConnection() {
        
    }

    // Method to get the database connection
    public static Connection getConnection() {
    	if(conn == null)
    	{
    		try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
                conn = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException e) {
                System.err.println("JDBC Driver not found: " + e.getMessage());
                return null;
            } catch (SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                return null;
            }
    	}
    	
        return conn;
    }
    public static boolean employeeExists(int id) {
        String query = "SELECT COUNT(*) FROM employees WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;  // Returns true if ID exists
            }
        } catch (SQLException e) {
            System.err.println("Error checking employee existence: " + e.getMessage());
        }
        return false;
    }
    }
	
	

