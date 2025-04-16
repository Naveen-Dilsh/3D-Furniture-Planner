package com.mycompany.furniplanner.controller;

public class CameraController {
    private double rotationX; // in radians (pitch)
    private double rotationY; // in radians (yaw)
    private double zoom;
    private boolean rotateMode;
    
    // Camera position
    private double cameraDistance;
    private double cameraHeight;
    
    public CameraController() {
        this.rotationX = Math.PI / 6; // 30 degrees
        this.rotationY = Math.PI / 4; // 45 degrees
        this.zoom = 0.5;
        this.rotateMode = false;
        this.cameraDistance = 800;
        this.cameraHeight = 400;
    }
    
    public double getRotationX() {
        return rotationX;
    }
    
    public double getRotationY() {
        return rotationY;
    }
    
    public double getZoom() {
        return zoom;
    }
    
    public boolean isRotateMode() {
        return rotateMode;
    }
    
    public void setRotateMode(boolean rotateMode) {
        this.rotateMode = rotateMode;
    }
    
    public double getCameraDistance() {
        return cameraDistance;
    }
    
    public double getCameraHeight() {
        return cameraHeight;
    }
    
    public void rotateCamera(double dx, double dy) {
        rotationY += Math.toRadians(dx);
        rotationX += Math.toRadians(dy);
        
        // Clamp rotation X to avoid flipping
        rotationX = Math.max(-Math.PI/2 + 0.1, Math.min(Math.PI/2 - 0.1, rotationX));
    }
    
    public void zoom(int steps) {
        // Adjust zoom level
        zoom += steps * -0.05; // Invert for more intuitive zoom
        
        // Limit zoom range
        if (zoom < 0.1) {
            zoom = 0.1;
        } else if (zoom > 2.0) {
            zoom = 2.0;
        }
    }
    
    public void resetCamera() {
        rotationX = Math.PI / 6;
        rotationY = Math.PI / 4;
        zoom = 0.5;
    }
    
    public void setTopView() {
        rotationX = -Math.PI / 2 + 0.1; // Slightly offset to avoid rendering issues
        rotationY = 0;
        zoom = 0.7;
    }
    
    public void setFrontView() {
        rotationX = 0;
        rotationY = 0;
        zoom = 0.7;
    }
    
    public void setSideView() {
        rotationX = 0;
        rotationY = Math.PI / 2;
        zoom = 0.7;
    }
    
    // Add orbit camera controls
    public void orbitCamera(double deltaX, double deltaY) {
        rotationY += deltaX * 0.01;
        rotationX += deltaY * 0.01;
        
        // Limit vertical rotation
        if (rotationX > Math.PI / 2 - 0.1) {
            rotationX = Math.PI / 2 - 0.1;
        } else if (rotationX < -Math.PI / 2 + 0.1) {
            rotationX = -Math.PI / 2 + 0.1;
        }
    }
    
    // Pan camera
    public void panCamera(double deltaX, double deltaY) {
        // Implement camera panning if needed
    }
}