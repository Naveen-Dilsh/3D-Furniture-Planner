package com.mycompany.furniplanner.model;

public class Vector3D {
    private double x;
    private double y;
    private double z;
    
    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public double getZ() {
        return z;
    }
    
    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }
    
    public Vector3D normalize() {
        double len = length();
        if (len > 0) {
            return new Vector3D(x / len, y / len, z / len);
        }
        return new Vector3D(0, 0, 0);
    }
    
    public Vector3D add(Vector3D other) {
        return new Vector3D(x + other.x, y + other.y, z + other.z);
    }
    
    public Vector3D subtract(Vector3D other) {
        return new Vector3D(x - other.x, y - other.y, z - other.z);
    }
    
    public Vector3D multiply(double scalar) {
        return new Vector3D(x * scalar, y * scalar, z * scalar);
    }
    
    public double dot(Vector3D other) {
        return x * other.x + y * other.y + z * other.z;
    }
    
    public Vector3D cross(Vector3D other) {
        return new Vector3D(
            y * other.z - z * other.y,
            z * other.x - x * other.z,
            x * other.y - y * other.x
        );
    }
}