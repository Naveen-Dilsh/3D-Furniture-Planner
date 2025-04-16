package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.CameraController;
import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.controller.RoomController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ToolPanel extends JPanel {
    private RoomController roomController;
    private FurnitureController furnitureController;
    private CameraController cameraController;
    private Canvas3D canvas3D;
    
    private JButton createRoomBtn;
    private JButton rotateViewBtn;
    private JButton moveItemBtn;
    private JButton deleteItemBtn;
    private JToggleButton topViewBtn;
    private JToggleButton frontViewBtn;
    private JToggleButton sideViewBtn;
    private JToggleButton freeViewBtn;
    private JToggleButton boundaryToggleBtn;
    private JButton wallColorBtn;
    private JButton floorColorBtn;
    
    public ToolPanel(RoomController roomController, FurnitureController furnitureController, 
                    CameraController cameraController, Canvas3D canvas3D) {
        this.roomController = roomController;
        this.furnitureController = furnitureController;
        this.cameraController = cameraController;
        this.canvas3D = canvas3D;
        
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        initComponents();
    }
    
    private void initComponents() {
        // Create panels for organization
        JPanel roomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Room"));
        
        JPanel viewPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        viewPanel.setBorder(BorderFactory.createTitledBorder("View"));
        
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setBorder(BorderFactory.createTitledBorder("Items"));
        
        // Room controls
        createRoomBtn = new JButton("Create Room");
        createRoomBtn.addActionListener(e -> showRoomDialog());
        roomPanel.add(createRoomBtn);
        
        wallColorBtn = new JButton("Wall Color");
        wallColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Wall Color", new Color(120, 80, 60));
            if (newColor != null) {
                // Update wall color in Canvas3D
                if (canvas3D instanceof Canvas3D) {
                    ((Canvas3D) canvas3D).setWallColor(newColor);
                    canvas3D.repaint();
                }
            }
        });
        roomPanel.add(wallColorBtn);
        
        floorColorBtn = new JButton("Floor Color");
        floorColorBtn.addActionListener(e -> {
            Color newColor = JColorChooser.showDialog(this, "Choose Floor Color", new Color(210, 180, 140));
            if (newColor != null) {
                // Update floor color in Canvas3D
                if (canvas3D instanceof Canvas3D) {
                    ((Canvas3D) canvas3D).setFloorColor(newColor);
                    canvas3D.repaint();
                }
            }
        });
        roomPanel.add(floorColorBtn);
        
        boundaryToggleBtn = new JToggleButton("Show Boundary");
        boundaryToggleBtn.setSelected(true);
        boundaryToggleBtn.addActionListener(e -> {
            if (canvas3D instanceof Canvas3D) {
                ((Canvas3D) canvas3D).setShowBoundary(boundaryToggleBtn.isSelected());
                canvas3D.repaint();
            }
        });
        roomPanel.add(boundaryToggleBtn);
        
        // View controls
        ButtonGroup viewGroup = new ButtonGroup();
        
        freeViewBtn = new JToggleButton("Free View");
        freeViewBtn.addActionListener(e -> {
            if (freeViewBtn.isSelected()) {
                cameraController.resetCamera();
                canvas3D.repaint();
            }
        });
        freeViewBtn.setSelected(true);
        viewGroup.add(freeViewBtn);
        viewPanel.add(freeViewBtn);
        
        topViewBtn = new JToggleButton("Top View");
        topViewBtn.addActionListener(e -> {
            if (topViewBtn.isSelected()) {
                cameraController.setTopView();
                canvas3D.repaint();
            }
        });
        viewGroup.add(topViewBtn);
        viewPanel.add(topViewBtn);
        
        frontViewBtn = new JToggleButton("Front View");
        frontViewBtn.addActionListener(e -> {
            if (frontViewBtn.isSelected()) {
                cameraController.setFrontView();
                canvas3D.repaint();
            }
        });
        viewGroup.add(frontViewBtn);
        viewPanel.add(frontViewBtn);
        
        sideViewBtn = new JToggleButton("Side View");
        sideViewBtn.addActionListener(e -> {
            if (sideViewBtn.isSelected()) {
                cameraController.setSideView();
                canvas3D.repaint();
            }
        });
        viewGroup.add(sideViewBtn);
        viewPanel.add(sideViewBtn);
        
        rotateViewBtn = new JButton("Rotate Mode");
        rotateViewBtn.addActionListener(e -> {
            cameraController.setRotateMode(!cameraController.isRotateMode());
            if (cameraController.isRotateMode()) {
                rotateViewBtn.setBackground(new Color(200, 200, 255));
                canvas3D.setCursor(new Cursor(Cursor.MOVE_CURSOR));
            } else {
                rotateViewBtn.setBackground(null);
                canvas3D.setCursor(Cursor.getDefaultCursor());
            }
        });
        viewPanel.add(rotateViewBtn);
        
        // Item controls
        moveItemBtn = new JButton("Move Item");
        moveItemBtn.addActionListener(e -> {
            furnitureController.setMoveMode(true);
            cameraController.setRotateMode(false);
            rotateViewBtn.setBackground(null);
            canvas3D.setCursor(new Cursor(Cursor.HAND_CURSOR));
        });
        itemPanel.add(moveItemBtn);
        
        deleteItemBtn = new JButton("Delete Item");
        deleteItemBtn.addActionListener(e -> {
            furnitureController.setDeleteMode(true);
            cameraController.setRotateMode(false);
            rotateViewBtn.setBackground(null);
            canvas3D.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        });
        itemPanel.add(deleteItemBtn);
        
        // Add panels to tool panel
        add(roomPanel);
        add(viewPanel);
        add(itemPanel);
    }
    
    private void showRoomDialog() {
        JDialog dialog = new JDialog((Frame)SwingUtilities.getWindowAncestor(this), "Create Room", true);
        dialog.setLayout(new GridLayout(4, 2, 10, 10));
        dialog.setSize(300, 200);
        dialog.setLocationRelativeTo(this);
        
        JLabel widthLabel = new JLabel("Width (cm):");
        JTextField widthField = new JTextField("500");
        JLabel lengthLabel = new JLabel("Length (cm):");
        JTextField lengthField = new JTextField("300");
        JLabel heightLabel = new JLabel("Height (cm):");
        JTextField heightField = new JTextField("250");
        
        JButton createBtn = new JButton("Create");
        createBtn.addActionListener(e -> {
            try {
                int width = Integer.parseInt(widthField.getText());
                int length = Integer.parseInt(lengthField.getText());
                int height = Integer.parseInt(heightField.getText());
                
                roomController.createRoom(width, length, height);
                canvas3D.repaint();
                dialog.dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numbers", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        dialog.add(widthLabel);
        dialog.add(widthField);
        dialog.add(lengthLabel);
        dialog.add(lengthField);
        dialog.add(heightLabel);
        dialog.add(heightField);
        dialog.add(cancelBtn);
        dialog.add(createBtn);
        
        dialog.setVisible(true);
    }
    
    // Method to update UI state when a furniture item is selected
    public void updateSelectionState(boolean hasSelection) {
        moveItemBtn.setEnabled(hasSelection);
        deleteItemBtn.setEnabled(hasSelection);
    }
    
    // Method to reset all tool modes
    public void resetToolModes() {
        furnitureController.setMoveMode(false);
        furnitureController.setDeleteMode(false);
        cameraController.setRotateMode(false);
        rotateViewBtn.setBackground(null);
        canvas3D.setCursor(Cursor.getDefaultCursor());
    }
    
    // Method to update view mode buttons based on camera state
    public void updateViewModeButtons() {
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        
        if (Math.abs(rotX - (-Math.PI/2)) < 0.1 && Math.abs(rotY) < 0.1) {
            topViewBtn.setSelected(true);
        } else if (Math.abs(rotX) < 0.1 && Math.abs(rotY) < 0.1) {
            frontViewBtn.setSelected(true);
        } else if (Math.abs(rotX) < 0.1 && Math.abs(rotY - Math.PI/2) < 0.1) {
            sideViewBtn.setSelected(true);
        } else {
            freeViewBtn.setSelected(true);
        }
    }
    
    // Method to enable/disable room controls
    public void setRoomControlsEnabled(boolean enabled) {
        wallColorBtn.setEnabled(enabled);
        floorColorBtn.setEnabled(enabled);
        boundaryToggleBtn.setEnabled(enabled);
    }
    
    // Method to get the current state of the boundary toggle
    public boolean isBoundaryVisible() {
        return boundaryToggleBtn.isSelected();
    }
    
    // Method to set the boundary toggle state
    public void setBoundaryVisible(boolean visible) {
        boundaryToggleBtn.setSelected(visible);
        if (canvas3D instanceof Canvas3D) {
            ((Canvas3D) canvas3D).setShowBoundary(visible);
        }
    }
}