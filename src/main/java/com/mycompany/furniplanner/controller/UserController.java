package com.mycompany.furniplanner.controller;

import com.mycompany.furniplanner.database.DatabaseManager;
import com.mycompany.furniplanner.model.User;

public class UserController {
    private static UserController instance;
    private User currentUser;
    private DatabaseManager dbManager;
    
    private UserController() {
        dbManager = DatabaseManager.getInstance();
    }
    
    public static synchronized UserController getInstance() {
        if (instance == null) {
            instance = new UserController();
        }
        return instance;
    }
    
    public boolean registerUser(String username, String password, String email) {
        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        return dbManager.registerUser(username, password, email);
    }
    
    public boolean loginUser(String username, String password) {
        // Validate input
        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        boolean authenticated = dbManager.authenticateUser(username, password);
        
        if (authenticated) {
            int userId = dbManager.getUserId(username);
            currentUser = new User(userId, username, ""); // Email is not retrieved for security
        }
        
        return authenticated;
    }
    
    public void logoutUser() {
        currentUser = null;
    }
    
    public User getCurrentUser() {
        return currentUser;
    }
    
    public boolean isLoggedIn() {
        return currentUser != null;
    }
}