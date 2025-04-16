package com.mycompany.furniplanner.model;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int width;  // in cm
    private int length; // in cm
    private int height; // in cm
    private List<Furniture> furnitureList;
    private boolean hasCeiling; // Flag to control ceiling rendering
    
    public Room(int width, int length, int height) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.furnitureList = new ArrayList<>();
        this.hasCeiling = false; // Default to no ceiling for better visibility
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getLength() {
        return length;
    }
    
    public void setLength(int length) {
        this.length = length;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public boolean hasCeiling() {
        return hasCeiling;
    }
    
    public void setHasCeiling(boolean hasCeiling) {
        this.hasCeiling = hasCeiling;
    }
    
    public List<Furniture> getFurnitureList() {
        return furnitureList;
    }
    
    public void addFurniture(Furniture furniture) {
        furnitureList.add(furniture);
    }
    
    public void removeFurniture(Furniture furniture) {
        furnitureList.remove(furniture);
    }
    
    public void clearFurniture() {
        furnitureList.clear();
    }
    
    public boolean isWithinBounds(Vector3D position) {
        return position.getX() >= -width/2 && position.getX() <= width/2 &&
               position.getZ() >= -length/2 && position.getZ() <= 0 &&
               position.getY() >= 0 && position.getY() <= height;
    }
}