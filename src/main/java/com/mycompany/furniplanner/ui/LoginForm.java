package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserController userController;
    
    public LoginForm() {
        userController = UserController.getInstance();
        
        setTitle("FurniPlanner - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("FurniPlanner Login");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        
        loginButton = new JButton("Login");
        registerButton = new JButton("Register");
        
        // Add components to panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(usernameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(usernameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(passwordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(passwordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);
        
        gbc.gridx = 1;
        panel.add(registerButton, gbc);
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (userController.loginUser(username, password)) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Login successful! Welcome, " + username + "!", 
                            "Login Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Open the main application window
                    openMainApplication();
                    
                    // Close the login form
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                            "Invalid username or password. Please try again.", 
                            "Login Failed", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Open registration form
                RegisterForm registerForm = new RegisterForm();
                registerForm.setVisible(true);
                dispose();
            }
        });
        
        add(panel);
    }
    
    private void openMainApplication() {
        // This method should initialize and open your main application window
        // You'll need to modify this to fit your application structure
        MainApplication mainApp = new MainApplication();
        mainApp.setVisible(true);
    }
}