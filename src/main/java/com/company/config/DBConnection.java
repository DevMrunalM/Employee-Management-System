package com.company.config;

import java.sql.Connection;
import java.sql.DriverManager;
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
	
	
	
}
