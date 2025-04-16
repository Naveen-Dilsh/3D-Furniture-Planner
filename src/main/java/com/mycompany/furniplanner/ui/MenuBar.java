package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.controller.RoomController;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;

public class MenuBar extends JMenuBar {
    private MainFrame mainFrame;
    private RoomController roomController;
    private FurnitureController furnitureController;
    
    public MenuBar(MainFrame mainFrame, RoomController roomController, FurnitureController furnitureController) {
        this.mainFrame = mainFrame;
        this.roomController = roomController;
        this.furnitureController = furnitureController;
        
        initComponents();
    }
    
    private void initComponents() {
        // File menu
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        
        JMenuItem newItem = new JMenuItem("New Project", KeyEvent.VK_N);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        newItem.addActionListener(e -> newProject());
        
        JMenuItem saveItem = new JMenuItem("Save Project", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.addActionListener(e -> saveProject());
        
        JMenuItem loadItem = new JMenuItem("Load Project", KeyEvent.VK_L);
        loadItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        loadItem.addActionListener(e -> loadProject());
        
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic(KeyEvent.VK_V);
        
        JMenuItem resetViewItem = new JMenuItem("Reset View", KeyEvent.VK_R);
        resetViewItem.addActionListener(e -> resetView());
        
        JMenuItem topViewItem = new JMenuItem("Top View", KeyEvent.VK_T);
        topViewItem.addActionListener(e -> topView());
        
        JMenuItem frontViewItem = new JMenuItem("Front View", KeyEvent.VK_F);
        frontViewItem.addActionListener(e -> frontView());
        
        JMenuItem sideViewItem = new JMenuItem("Side View", KeyEvent.VK_S);
        sideViewItem.addActionListener(e -> sideView());
        
        viewMenu.add(resetViewItem);
        viewMenu.add(topViewItem);
        viewMenu.add(frontViewItem);
        viewMenu.add(sideViewItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        
        JMenuItem aboutItem = new JMenuItem("About", KeyEvent.VK_A);
        aboutItem.addActionListener(e -> showAboutDialog());
        
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        add(fileMenu);
        add(viewMenu);
        add(helpMenu);
    }
    
    private void newProject() {
        int option = JOptionPane.showConfirmDialog(
            mainFrame,
            "Do you want to create a new project? Any unsaved changes will be lost.",
            "New Project",
            JOptionPane.YES_NO_OPTION
        );
        
        if (option == JOptionPane.YES_OPTION) {
            roomController.createRoom(500, 300, 250); // Default room size
            furnitureController.clearFurniture();
            mainFrame.refreshView();
        }
    }
    
    private void saveProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Project");
        
        int userSelection = fileChooser.showSaveDialog(mainFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            // Implement saving logic here
            JOptionPane.showMessageDialog(
                mainFrame,
                "Project saved to " + fileToSave.getAbsolutePath(),
                "Save Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
        }
    }
    
    private void loadProject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Project");
        
        int userSelection = fileChooser.showOpenDialog(mainFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToLoad = fileChooser.getSelectedFile();
            // Implement loading logic here
            JOptionPane.showMessageDialog(
                mainFrame,
                "Project loaded from " + fileToLoad.getAbsolutePath(),
                "Load Successful",
                JOptionPane.INFORMATION_MESSAGE
            );
            mainFrame.refreshView();
        }
    }
    
    private void resetView() {
        // Reset camera to default position
        // Implement in CameraController
        mainFrame.refreshView();
    }
    
    private void topView() {
        // Set camera to top view
        // Implement in CameraController
        mainFrame.refreshView();
    }
    
    private void frontView() {
        // Set camera to front view
        // Implement in CameraController
        mainFrame.refreshView();
    }
    
    private void sideView() {
        // Set camera to side view
        // Implement in CameraController
        mainFrame.refreshView();
    }
    
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
            mainFrame,
            "3D Furniture Planner\nVersion 1.0\n\nA Java Swing application for planning furniture layouts in 3D.",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}