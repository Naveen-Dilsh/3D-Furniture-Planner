package com.mycompany.furniplanner.controller;

import com.mycompany.furniplanner.model.Furniture;
import com.mycompany.furniplanner.model.FurnitureType;
import com.mycompany.furniplanner.model.Room;
import com.mycompany.furniplanner.model.Vector3D;

import java.util.List;

public class FurnitureController {
    private Room room;
    private boolean moveMode;
    private boolean deleteMode;
    
    public FurnitureController(Room room) {
        this.room = room;
        this.moveMode = false;
        this.deleteMode = false;
    }
    
    public void addFurniture(String name, FurnitureType type, Vector3D position) {
        Furniture furniture = new Furniture(name, type, position);
        room.addFurniture(furniture);
    }
    
    public void removeFurniture(Furniture furniture) {
        room.removeFurniture(furniture);
    }
    
    public void moveFurniture(Furniture furniture, Vector3D delta) {
        Vector3D newPosition = furniture.getPosition().add(delta);
        
        // Check if the new position is within room bounds
        if (room.isWithinBounds(newPosition)) {
            furniture.setPosition(newPosition);
        }
    }
    
    public void rotateFurniture(Furniture furniture, double degrees) {
        furniture.rotate(degrees);
    }
    
    public List<Furniture> getAllFurniture() {
        return room.getFurnitureList();
    }
    
    public void clearFurniture() {
        room.clearFurniture();
    }
    
    public boolean isMoveMode() {
        return moveMode;
    }
    
    public void setMoveMode(boolean moveMode) {
        this.moveMode = moveMode;
        if (moveMode) {
            this.deleteMode = false;
        }
    }
    
    public boolean isDeleteMode() {
        return deleteMode;
    }
    
    public void setDeleteMode(boolean deleteMode) {
        this.deleteMode = deleteMode;
        if (deleteMode) {
            this.moveMode = false;
        }
    }
    // Add these methods to your FurnitureController class
public void addTable() {
    addFurniture("Table", FurnitureType.TABLE, new Vector3D(0, 0, 0));
}

public void addChair() {
    addFurniture("Chair", FurnitureType.CHAIR, new Vector3D(0, 0, 0));
}

public void addSofa() {
    addFurniture("Sofa", FurnitureType.SOFA, new Vector3D(0, 0, 0));
}

public void addBed() {
    addFurniture("Bed", FurnitureType.BED, new Vector3D(0, 0, 0));
}
}