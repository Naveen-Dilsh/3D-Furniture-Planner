package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.CameraController;
import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.controller.RoomController;
import com.mycompany.furniplanner.model.Room;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {
    private Canvas3D canvas3D;
    private ToolPanel toolPanel;
    private FurniturePanel furniturePanel;
    private RoomController roomController;
    private FurnitureController furnitureController;
    private CameraController cameraController;

    public MainFrame() {
        setTitle("3D Furniture Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        
        // Initialize controllers
        Room room = new Room(500, 300, 250); // Default room size
        roomController = new RoomController(room);
        furnitureController = new FurnitureController(room);
        cameraController = new CameraController();
        
        // Initialize UI components
        initComponents();
        
        // Set menu bar
        setJMenuBar(new MenuBar(this, roomController, furnitureController));
    }
    
    private void initComponents() {
        // Main layout
        setLayout(new BorderLayout());
        
        // 3D Canvas (center)
        canvas3D = new Canvas3D(roomController, furnitureController, cameraController);
        add(canvas3D, BorderLayout.CENTER);
        
        // Tool panel (top)
        toolPanel = new ToolPanel(roomController, furnitureController, cameraController, canvas3D);
        add(toolPanel, BorderLayout.NORTH);
        
        // Furniture panel (right)
        furniturePanel = new FurniturePanel(furnitureController, canvas3D);
        JScrollPane scrollPane = new JScrollPane(furniturePanel);
        scrollPane.setPreferredSize(new Dimension(200, getHeight()));
        add(scrollPane, BorderLayout.EAST);
    }
    
    public Canvas3D getCanvas3D() {
        return canvas3D;
    }
    
    public void refreshView() {
        canvas3D.repaint();
    }
}