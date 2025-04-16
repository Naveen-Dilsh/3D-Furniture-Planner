package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.UserController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JButton registerButton;
    private JButton backButton;
    private UserController userController;
    
    public RegisterForm() {
        userController = UserController.getInstance();
        
        setTitle("FurniPlanner - Register");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create components
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        JLabel titleLabel = new JLabel("FurniPlanner Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        
        JLabel usernameLabel = new JLabel("Username:");
        JLabel passwordLabel = new JLabel("Password:");
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JLabel emailLabel = new JLabel("Email:");
        
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);
        emailField = new JTextField(20);
        
        registerButton = new JButton("Register");
        backButton = new JButton("Back to Login");
        
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
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(confirmPasswordLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(confirmPasswordField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.EAST;
        panel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(emailField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);
        
        gbc.gridx = 1;
        panel.add(backButton, gbc);
        
        // Add action listeners
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String email = emailField.getText();
                
                // Validate input
                if (username.trim().isEmpty() || password.trim().isEmpty() || 
                    confirmPassword.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                            "All fields are required.", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                            "Passwords do not match.", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Register user
                if (userController.registerUser(username, password, email)) {
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                            "Registration successful! You can now login.", 
                            "Registration Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Go back to login form
                    LoginForm loginForm = new LoginForm();
                    loginForm.setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(RegisterForm.this, 
                            "Registration failed. Username may already exist.", 
                            "Registration Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Go back to login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                dispose();
            }
        });
        
        add(panel);
    }
}