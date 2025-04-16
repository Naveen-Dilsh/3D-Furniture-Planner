package com.mycompany.furniplanner.model;

public class Dimension3D {
    private double width;
    private double height;
    private double length;
    
    public Dimension3D(double width, double height, double length) {
        this.width = width;
        this.height = height;
        this.length = length;
    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public double getLength() {
        return length;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public void setLength(double length) {
        this.length = length;
    }
}