package com.mycompany.furniplanner.database;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseManager {
    private static final String DB_URL = "jdbc:sqlite:furniplanner.db";
    private static DatabaseManager instance;
    private Connection connection;
    
    private DatabaseManager() {
        try {
            // Load the SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");
            
            // Create a connection to the database
            connection = DriverManager.getConnection(DB_URL);
            
            // Create tables if they don't exist
            createTables();
            
            System.out.println("Database connection established successfully.");
        } catch (ClassNotFoundException | SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "Database initialization failed", e);
        }
    }
    
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "email TEXT," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createUsersTable);
            
            // Create projects table
            String createProjectsTable = "CREATE TABLE IF NOT EXISTS projects (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "name TEXT NOT NULL," +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "data TEXT," +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")";
            stmt.execute(createProjectsTable);
            
        } catch (SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "Failed to create tables", e);
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "Failed to close database connection", e);
        }
    }
    
    // User authentication methods
    public boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, hashPassword(password)); // In a real app, use proper password hashing
            pstmt.setString(3, email);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "User registration failed", e);
            return false;
        }
    }
    
    public boolean authenticateUser(String username, String password) {
        String sql = "SELECT password FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(hashPassword(password)); // In a real app, use proper password verification
            }
            return false;
        } catch (SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "User authentication failed", e);
            return false;
        }
    }
    
    public int getUserId(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
            return -1;
        } catch (SQLException e) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, "Failed to get user ID", e);
            return -1;
        }
    }
    
    // Simple password hashing (for demonstration only)
    // In a real application, use a proper password hashing library like BCrypt
    private String hashPassword(String password) {
        // This is a very basic hash for demonstration purposes only
        // DO NOT use this in a production environment
        return String.valueOf(password.hashCode());
    }
}