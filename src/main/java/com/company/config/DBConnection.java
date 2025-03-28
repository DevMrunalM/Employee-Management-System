package com.company.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBConnection {

    private static Connection conn = null;
    private static String url;
    private static String username;
    private static String password;

 // Method to get the database connection
    public static Connection getConnection() {
        if (conn == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
                conn = DriverManager.getConnection(url, username, password);
            } catch (ClassNotFoundException | SQLException e) {
                System.err.println("Database connection failed: " + e.getMessage());
                return null;
            }
        }
        return conn;
    }
    
    //  properties from application.properties
    static {
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            Properties properties = new Properties();
            if (input != null) {
                properties.load(input);
                url = properties.getProperty("db.url");
                username = properties.getProperty("db.username");
                password = properties.getProperty("db.password");
            } else {
                throw new RuntimeException("application.properties file not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database properties: " + e.getMessage());
        }
    }   
    
    
}
