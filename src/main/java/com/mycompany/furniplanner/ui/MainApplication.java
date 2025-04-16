// Updated MainApplication.java with fixed controller initialization and new UI layout
package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.CameraController;
import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.controller.RoomController;
import com.mycompany.furniplanner.controller.UserController;
import com.mycompany.furniplanner.model.FurnitureType;
import com.mycompany.furniplanner.model.Room;
import com.mycompany.furniplanner.model.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainApplication extends JFrame {
    private UserController userController;
    private RoomController roomController;
    private FurnitureController furnitureController;
    private CameraController cameraController;
    private Canvas3D canvas3D;
    private Room room;
    
    public MainApplication() {
        userController = UserController.getInstance();
        
        // Check if user is logged in
        if (!userController.isLoggedIn()) {
            JOptionPane.showMessageDialog(this, 
                    "You must be logged in to use this application.", 
                    "Authentication Error", 
                    JOptionPane.ERROR_MESSAGE);
            
            // Redirect to login form
            LoginForm loginForm = new LoginForm();
            loginForm.setVisible(true);
            dispose();
            return;
        }
        
        setTitle("FurniPlanner - Welcome " + userController.getCurrentUser().getUsername());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create default room first
        room = new Room(500, 300, 250);
        
        // Initialize controllers with the room
        roomController = new RoomController(room);
        furnitureController = new FurnitureController(room);
        cameraController = new CameraController();
        
        // Initialize UI components
        initializeUI();
        
        // Add window listener to handle application close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Close database connection
                com.mycompany.furniplanner.database.DatabaseManager.getInstance().closeConnection();
            }
        });
    }
    
    private void initializeUI() {
        // Create main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Create toolbar
        JToolBar toolBar = createToolBar();
        mainPanel.add(toolBar, BorderLayout.NORTH);
        
        // Create canvas for the center
        canvas3D = new Canvas3D(roomController, furnitureController, cameraController);
        mainPanel.add(new JScrollPane(canvas3D), BorderLayout.CENTER);
        
        // Create left panel for camera controls
        JPanel leftPanel = createCameraControlPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);
        
        // Create right panel for furniture elements
        JPanel rightPanel = createFurniturePanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);
        
        // Add main panel to frame
        add(mainPanel);
    }
    
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem newProjectItem = new JMenuItem("New Project");
        JMenuItem openProjectItem = new JMenuItem("Open Project");
        JMenuItem saveProjectItem = new JMenuItem("Save Project");
        JMenuItem exitItem = new JMenuItem("Exit");
        
        fileMenu.add(newProjectItem);
        fileMenu.add(openProjectItem);
        fileMenu.add(saveProjectItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        JMenuItem undoItem = new JMenuItem("Undo");
        JMenuItem redoItem = new JMenuItem("Redo");
        
        editMenu.add(undoItem);
        editMenu.add(redoItem);
        
        // View menu
        JMenu viewMenu = new JMenu("View");
        JMenuItem resetViewItem = new JMenuItem("Reset View");
        JMenuItem topViewItem = new JMenuItem("Top View");
        JMenuItem frontViewItem = new JMenuItem("Front View");
        JMenuItem sideViewItem = new JMenuItem("Side View");
        
        viewMenu.add(resetViewItem);
        viewMenu.add(topViewItem);
        viewMenu.add(frontViewItem);
        viewMenu.add(sideViewItem);
        
        // User menu
        JMenu userMenu = new JMenu("User");
        JMenuItem profileItem = new JMenuItem("Profile");
        JMenuItem logoutItem = new JMenuItem("Logout");
        
        userMenu.add(profileItem);
        userMenu.add(logoutItem);
        
        // Add action listeners
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        resetViewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.resetCamera();
                canvas3D.repaint();
            }
        });
        
        topViewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setTopView();
                canvas3D.repaint();
            }
        });
        
        frontViewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setFrontView();
                canvas3D.repaint();
            }
        });
        
        sideViewItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setSideView();
                canvas3D.repaint();
            }
        });
        
        logoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userController.logoutUser();
                
                // Redirect to login form
                LoginForm loginForm = new LoginForm();
                loginForm.setVisible(true);
                dispose();
            }
        });
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(userMenu);
        
        return menuBar;
    }
    
    private JToolBar createToolBar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        // Add furniture buttons
        JButton addTableButton = new JButton("Add Table");
        JButton addChairButton = new JButton("Add Chair");
        JButton addSofaButton = new JButton("Add Sofa");
        JButton addBedButton = new JButton("Add Bed");
        
        // Add action listeners
        addTableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.addFurniture("Table", FurnitureType.TABLE, new Vector3D(0, 0, 0));
                canvas3D.repaint();
            }
        });
        
        addChairButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.addFurniture("Chair", FurnitureType.CHAIR, new Vector3D(0, 0, 0));
                canvas3D.repaint();
            }
        });
        
        addSofaButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.addFurniture("Sofa", FurnitureType.SOFA, new Vector3D(0, 0, 0));
                canvas3D.repaint();
            }
        });
        
        addBedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.addFurniture("Bed", FurnitureType.BED, new Vector3D(0, 0, 0));
                canvas3D.repaint();
            }
        });
        
        // Add buttons to toolbar
        toolBar.add(addTableButton);
        toolBar.add(addChairButton);
        toolBar.add(addSofaButton);
        toolBar.add(addBedButton);
        toolBar.addSeparator();
        
        // Add edit buttons
        JButton moveButton = new JButton("Move");
        JButton deleteButton = new JButton("Delete");
        JButton rotateButton = new JButton("Rotate");
        
        moveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.setMoveMode(true);
                canvas3D.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                furnitureController.setDeleteMode(true);
                canvas3D.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            }
        });
        
        rotateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement rotation functionality
                // This would need to be added to your FurnitureController
                canvas3D.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });
        
        toolBar.add(moveButton);
        toolBar.add(deleteButton);
        toolBar.add(rotateButton);
        
        return toolBar;
    }
    
    private JPanel createCameraControlPanel() {
        JPanel cameraPanel = new JPanel();
        cameraPanel.setLayout(new BoxLayout(cameraPanel, BoxLayout.Y_AXIS));
        cameraPanel.setBorder(BorderFactory.createTitledBorder("Camera Controls"));
        cameraPanel.setPreferredSize(new Dimension(200, 600));
        
        // Camera view buttons
        JPanel viewButtonsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        viewButtonsPanel.setBorder(BorderFactory.createTitledBorder("View"));
        
        JButton resetViewButton = new JButton("Reset View");
        JButton topViewButton = new JButton("Top View");
        JButton frontViewButton = new JButton("Front View");
        JButton sideViewButton = new JButton("Side View");
        
        resetViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.resetCamera();
                canvas3D.repaint();
            }
        });
        
        topViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setTopView();
                canvas3D.repaint();
            }
        });
        
        frontViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setFrontView();
                canvas3D.repaint();
            }
        });
        
        sideViewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.setSideView();
                canvas3D.repaint();
            }
        });
        
        viewButtonsPanel.add(resetViewButton);
        viewButtonsPanel.add(topViewButton);
        viewButtonsPanel.add(frontViewButton);
        viewButtonsPanel.add(sideViewButton);
        
        // Camera rotation controls
        JPanel rotationPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        rotationPanel.setBorder(BorderFactory.createTitledBorder("Rotation"));
        
        JButton rotateUpButton = new JButton("↑");
        JButton rotateDownButton = new JButton("↓");
        JButton rotateLeftButton = new JButton("←");
        JButton rotateRightButton = new JButton("→");
        JButton centerButton = new JButton("•");
        
        rotateUpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.rotateCamera(0, -10);
                canvas3D.repaint();
            }
        });
        
        rotateDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.rotateCamera(0, 10);
                canvas3D.repaint();
            }
        });
        
        rotateLeftButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.rotateCamera(10, 0);
                canvas3D.repaint();
            }
        });
        
        rotateRightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.rotateCamera(-10, 0);
                canvas3D.repaint();
            }
        });
        
        centerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.resetCamera();
                canvas3D.repaint();
            }
        });
        
        rotationPanel.add(new JLabel(""));
        rotationPanel.add(rotateUpButton);
        rotationPanel.add(new JLabel(""));
        rotationPanel.add(rotateLeftButton);
        rotationPanel.add(centerButton);
        rotationPanel.add(rotateRightButton);
        rotationPanel.add(new JLabel(""));
        rotationPanel.add(rotateDownButton);
        rotationPanel.add(new JLabel(""));
        
        // Camera zoom controls
        JPanel zoomPanel = new JPanel(new GridLayout(1, 2, 5, 5));
        zoomPanel.setBorder(BorderFactory.createTitledBorder("Zoom"));
        
        JButton zoomInButton = new JButton("Zoom In");
        JButton zoomOutButton = new JButton("Zoom Out");
        
        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.zoom(-1);
                canvas3D.repaint();
            }
        });
        
        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cameraController.zoom(1);
                canvas3D.repaint();
            }
        });
        
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        
        // Add all panels to camera panel
        cameraPanel.add(viewButtonsPanel);
        cameraPanel.add(Box.createVerticalStrut(10));
        cameraPanel.add(rotationPanel);
        cameraPanel.add(Box.createVerticalStrut(10));
        cameraPanel.add(zoomPanel);
        cameraPanel.add(Box.createVerticalGlue());
        
        return cameraPanel;
    }
    
    private JPanel createFurniturePanel() {
        JPanel furniturePanel = new JPanel();
        furniturePanel.setLayout(new BoxLayout(furniturePanel, BoxLayout.Y_AXIS));
        furniturePanel.setBorder(BorderFactory.createTitledBorder("Furniture Elements"));
        furniturePanel.setPreferredSize(new Dimension(250, 600));
        
        // Room settings
        JPanel roomPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        roomPanel.setBorder(BorderFactory.createTitledBorder("Room Settings"));
        
        JLabel widthLabel = new JLabel("Width (cm):");
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(500, 100, 1000, 10));
        
        JLabel lengthLabel = new JLabel("Length (cm):");
        JSpinner lengthSpinner = new JSpinner(new SpinnerNumberModel(300, 100, 1000, 10));
        
        JLabel heightLabel = new JLabel("Height (cm):");
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(250, 100, 500, 10));
        
        roomPanel.add(widthLabel);
        roomPanel.add(widthSpinner);
        roomPanel.add(lengthLabel);
        roomPanel.add(lengthSpinner);
        roomPanel.add(heightLabel);
        roomPanel.add(heightSpinner);
        
        // Apply button for room settings
        JButton applyRoomButton = new JButton("Apply Room Settings");
        applyRoomButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int width = (Integer) widthSpinner.getValue();
                int length = (Integer) lengthSpinner.getValue();
                int height = (Integer) heightSpinner.getValue();
                
                roomController.resizeRoom(width, length, height);
                canvas3D.repaint();
            }
        });
        
        // Furniture list
        JPanel furnitureListPanel = new JPanel(new BorderLayout());
        furnitureListPanel.setBorder(BorderFactory.createTitledBorder("Furniture List"));
        
        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> furnitureList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(furnitureList);
        
        JButton refreshListButton = new JButton("Refresh List");
        refreshListButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Update the furniture list
                listModel.clear();
                for (int i = 0; i < furnitureController.getAllFurniture().size(); i++) {
                    listModel.addElement(furnitureController.getAllFurniture().get(i).getName());
                }
            }
        });
        
        furnitureListPanel.add(listScrollPane, BorderLayout.CENTER);
        furnitureListPanel.add(refreshListButton, BorderLayout.SOUTH);
        
        // Furniture properties
        JPanel propertiesPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        propertiesPanel.setBorder(BorderFactory.createTitledBorder("Properties"));
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        
        JLabel typeLabel = new JLabel("Type:");
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"TABLE", "CHAIR", "SOFA", "BED", "BOOKSHELF", "CABINET"});
        
        JLabel positionLabel = new JLabel("Position:");
        JPanel positionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField xField = new JTextField(3);
        JTextField yField = new JTextField(3);
        JTextField zField = new JTextField(3);
        positionPanel.add(new JLabel("X:"));
        positionPanel.add(xField);
        positionPanel.add(new JLabel("Y:"));
        positionPanel.add(yField);
        positionPanel.add(new JLabel("Z:"));
        positionPanel.add(zField);
        
        JLabel rotationLabel = new JLabel("Rotation:");
        JTextField rotationField = new JTextField();
        
        propertiesPanel.add(nameLabel);
        propertiesPanel.add(nameField);
        propertiesPanel.add(typeLabel);
        propertiesPanel.add(typeComboBox);
        propertiesPanel.add(positionLabel);
        propertiesPanel.add(positionPanel);
        propertiesPanel.add(rotationLabel);
        propertiesPanel.add(rotationField);
        
        // Apply properties button
        JButton applyPropertiesButton = new JButton("Apply Properties");
        
        // Add all panels to furniture panel
        furniturePanel.add(roomPanel);
        furniturePanel.add(Box.createVerticalStrut(10));
        furniturePanel.add(applyRoomButton);
        furniturePanel.add(Box.createVerticalStrut(20));
        furniturePanel.add(furnitureListPanel);
        furniturePanel.add(Box.createVerticalStrut(10));
        furniturePanel.add(propertiesPanel);
        furniturePanel.add(Box.createVerticalStrut(10));
        furniturePanel.add(applyPropertiesButton);
        furniturePanel.add(Box.createVerticalGlue());
        
        return furniturePanel;
    }
}