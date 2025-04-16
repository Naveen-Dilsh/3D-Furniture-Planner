package com.mycompany.furniplanner;

import com.mycompany.furniplanner.database.DatabaseManager;
import com.mycompany.furniplanner.ui.LoginForm;

import javax.swing.*;

public class Furniplanner {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize database
        DatabaseManager.getInstance();
        
        // Start with login form
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
            }
        });
    }
}