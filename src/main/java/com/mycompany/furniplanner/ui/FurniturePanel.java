package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.model.FurnitureType;
import com.mycompany.furniplanner.model.Vector3D;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class FurniturePanel extends JPanel {
    private FurnitureController furnitureController;
    private Canvas3D canvas3D;
    
    public FurniturePanel(FurnitureController furnitureController, Canvas3D canvas3D) {
        this.furnitureController = furnitureController;
        this.canvas3D = canvas3D;
        
        setLayout(new GridLayout(0, 1, 10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initComponents();
    }
    
    private void initComponents() {
        JLabel titleLabel = new JLabel("Furniture Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        add(titleLabel);
        
        // Add furniture items
        addFurnitureItem("Table", FurnitureType.TABLE);
        addFurnitureItem("Chair", FurnitureType.CHAIR);
        addFurnitureItem("Window", FurnitureType.WINDOW);
        addFurnitureItem("Door", FurnitureType.DOOR);
        addFurnitureItem("Sofa", FurnitureType.SOFA);
        addFurnitureItem("Bed", FurnitureType.BED);
        addFurnitureItem("Bookshelf", FurnitureType.BOOKSHELF);
        addFurnitureItem("Cabinet", FurnitureType.CABINET);
    }
    
    private void addFurnitureItem(String name, FurnitureType type) {
        JPanel itemPanel = new JPanel(new BorderLayout());
        itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        JLabel nameLabel = new JLabel(name);
        nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            String itemName = JOptionPane.showInputDialog(this, "Enter a name for this " + name + ":", name);
            if (itemName != null && !itemName.trim().isEmpty()) {
                // Add furniture at center of room
                Vector3D position = new Vector3D(0, 0, 0);
                furnitureController.addFurniture(itemName, type, position);
                canvas3D.repaint();
            }
        });
        
        itemPanel.add(nameLabel, BorderLayout.CENTER);
        itemPanel.add(addButton, BorderLayout.EAST);
        
        // Make the panel draggable
        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                JPanel panel = (JPanel) e.getSource();
                TransferHandler handler = panel.getTransferHandler();
                handler.exportAsDrag(panel, e, TransferHandler.COPY);
            }
        });
        
        add(itemPanel);
    }
}