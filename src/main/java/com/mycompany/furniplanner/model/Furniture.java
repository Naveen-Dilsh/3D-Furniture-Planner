package com.mycompany.furniplanner.model;

public class Furniture {
    private String name;
    private FurnitureType type;
    private Vector3D position;
    private Vector3D rotation; // in degrees
    private Dimension3D dimension;
    
    public Furniture(String name, FurnitureType type, Vector3D position) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.rotation = new Vector3D(0, 0, 0);
        
        // Set default dimensions based on type
        switch (type) {
            case TABLE:
                this.dimension = new Dimension3D(80, 75, 120); // width, height, length in cm
                break;
            case CHAIR:
                this.dimension = new Dimension3D(45, 90, 45);
                break;
            case SOFA:
                this.dimension = new Dimension3D(90, 85, 200);
                break;
            case BED:
                this.dimension = new Dimension3D(160, 50, 200);
                break;
            case WINDOW:
                this.dimension = new Dimension3D(100, 120, 10);
                break;
            case DOOR:
                this.dimension = new Dimension3D(90, 200, 10);
                break;
            case BOOKSHELF:
                this.dimension = new Dimension3D(40, 180, 100);
                break;
            case CABINET:
                this.dimension = new Dimension3D(50, 100, 80);
                break;
            default:
                this.dimension = new Dimension3D(50, 50, 50);
        }
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public FurnitureType getType() {
        return type;
    }
    
    public Vector3D getPosition() {
        return position;
    }
    
    public void setPosition(Vector3D position) {
        this.position = position;
    }
    
    public Vector3D getRotation() {
        return rotation;
    }
    
    public void setRotation(Vector3D rotation) {
        this.rotation = rotation;
    }
    
    public Dimension3D getDimension() {
        return dimension;
    }
    
    public void setDimension(Dimension3D dimension) {
        this.dimension = dimension;
    }
    
    public void rotate(double degrees) {
        this.rotation = new Vector3D(rotation.getX(), rotation.getY() + degrees, rotation.getZ());
    }
    
    public void move(Vector3D delta) {
        this.position = new Vector3D(
            position.getX() + delta.getX(),
            position.getY() + delta.getY(),
            position.getZ() + delta.getZ()
        );
    }
}