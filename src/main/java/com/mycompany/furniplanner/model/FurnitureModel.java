package com.mycompany.furniplanner.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.util.HashMap;
import java.util.Map;

import com.mycompany.furniplanner.utils.ModelLoader;
import com.mycompany.furniplanner.utils.TextureLoader;

public class FurnitureModel {
    private ModelLoader.Model model;
    private Map<String, Color> materialColors = new HashMap<>();
    private double scale = 1.0;
    private Vector3D offset = new Vector3D(0, 0, 0);
    
    public FurnitureModel(String objPath) {
        model = ModelLoader.loadOBJ(objPath);
        initializeMaterialColors();
    }
    
    private void initializeMaterialColors() {
        // Create default colors for materials
        for (String materialName : model.materials.keySet()) {
            ModelLoader.Material material = model.materials.get(materialName);
            float[] diffuse = material.diffuse;
            materialColors.put(materialName, new Color(diffuse[0], diffuse[1], diffuse[2]));
        }
        
        // Add a default material if none exists
        if (materialColors.isEmpty()) {
            materialColors.put("default", new Color(0.8f, 0.8f, 0.8f));
        }
    }
    
    public void setScale(double scale) {
        this.scale = scale;
    }
    
    public void setOffset(Vector3D offset) {
        this.offset = offset;
    }
    
    public void render(Graphics2D g2d, Vector3D position, Vector3D rotation, double viewRotX, double viewRotY, double zoom) {
        if (model == null || model.faces.isEmpty()) {
            return;
        }
        
        // Sort faces by Z-order for proper rendering (back-to-front)
        model.faces.sort((f1, f2) -> {
            double z1 = getAverageFaceZ(f1, position, rotation, viewRotX, viewRotY);
            double z2 = getAverageFaceZ(f2, position, rotation, viewRotX, viewRotY);
            return Double.compare(z2, z1); // Draw back-to-front
        });
        
        // Draw each face
        for (ModelLoader.Face face : model.faces) {
            drawFace(g2d, face, position, rotation, viewRotX, viewRotY, zoom);
        }
    }
    
    private double getAverageFaceZ(ModelLoader.Face face, Vector3D position, Vector3D rotation, double viewRotX, double viewRotY) {
        double sumZ = 0;
        for (int i = 0; i < face.vertexIndices.length; i++) {
            Vector3D vertex = model.vertices.get(face.vertexIndices[i]);
            Vector3D transformedVertex = transformVertex(vertex, position, rotation);
            sumZ += transformZ(transformedVertex, viewRotX, viewRotY);
        }
        return sumZ / face.vertexIndices.length;
    }
    
    private void drawFace(Graphics2D g2d, ModelLoader.Face face, Vector3D position, Vector3D rotation, 
                          double viewRotX, double viewRotY, double zoom) {
        // Get vertices for this face
        int[] vertexIndices = face.vertexIndices;
        
        // Create polygon for the face
        Polygon poly = new Polygon();
        Vector3D[] transformedVertices = new Vector3D[vertexIndices.length];
        
        for (int i = 0; i < vertexIndices.length; i++) {
            Vector3D vertex = model.vertices.get(vertexIndices[i]);
            
            // Apply model scale and offset
            vertex = new Vector3D(
                vertex.getX() * scale + offset.getX(),
                vertex.getY() * scale + offset.getY(),
                vertex.getZ() * scale + offset.getZ()
            );
            
            // Transform vertex by position and rotation
            transformedVertices[i] = transformVertex(vertex, position, rotation);
            
            // Convert to screen coordinates
            Point p = worldToScreen(transformedVertices[i], viewRotX, viewRotY, zoom);
            poly.addPoint(p.x, p.y);
        }
        
        // Calculate face normal for backface culling
        Vector3D normal = calculateFaceNormal(transformedVertices);
        
        // Create a view vector pointing into the scene
        Vector3D viewDir = new Vector3D(
            -Math.sin(viewRotY) * Math.cos(viewRotX),
            -Math.sin(viewRotX),
            -Math.cos(viewRotY) * Math.cos(viewRotX)
        );
        
        // Only draw if facing camera (dot product < 0)
        if (normal.dot(viewDir) < 0) {
            // Get material color
            Color color = materialColors.getOrDefault(face.materialName, Color.GRAY);
            
            // Apply lighting
            double lightIntensity = Math.max(0.3, -normal.dot(new Vector3D(0.5, -1, 0.5).normalize()));
            Color shadedColor = shadeColor(color, lightIntensity);
            
            g2d.setColor(shadedColor);
            g2d.fill(poly);
            g2d.setColor(Color.DARK_GRAY);
            g2d.draw(poly);
        }
    }
    
    private Vector3D transformVertex(Vector3D vertex, Vector3D position, Vector3D rotation) {
        // Apply rotation
        double cosY = Math.cos(Math.toRadians(rotation.getY()));
        double sinY = Math.sin(Math.toRadians(rotation.getY()));
        
        double x = vertex.getX();
        double y = vertex.getY();
        double z = vertex.getZ();
        
        // Rotate around Y axis (yaw)
        double newX = x * cosY - z * sinY;
        double newZ = x * sinY + z * cosY;
        
        // Apply position
        return new Vector3D(
            newX + position.getX(),
            y + position.getY(),
            newZ + position.getZ()
        );
    }
    
    private Vector3D calculateFaceNormal(Vector3D[] vertices) {
        if (vertices.length < 3) {
            return new Vector3D(0, 1, 0); // Default normal if not enough vertices
        }
        
        Vector3D edge1 = vertices[1].subtract(vertices[0]);
        Vector3D edge2 = vertices[vertices.length - 1].subtract(vertices[0]);
        return edge1.cross(edge2).normalize();
    }
    
    private double transformZ(Vector3D position, double rotX, double rotY) {
        // Transform Z coordinate for depth sorting
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();
        
        // Apply rotation to get view space Z
        double tempX = x * Math.cos(rotY) + z * Math.sin(rotY);
        double tempZ = -x * Math.sin(rotY) + z * Math.cos(rotY);
        
        // Apply X rotation
        double finalZ = -y * Math.sin(rotX) + tempZ * Math.cos(rotX);
        
        return finalZ;
    }
    
    private Point worldToScreen(Vector3D worldPos, double rotX, double rotY, double zoom) {
        // Convert 3D world coordinates to 2D screen coordinates
        double x = worldPos.getX();
        double y = worldPos.getY();
        double z = worldPos.getZ();
        
        // Rotate around Y axis (yaw)
        double tempX = x * Math.cos(rotY) + z * Math.sin(rotY);
        double tempZ = -x * Math.sin(rotY) + z * Math.cos(rotY);
        
        // Rotate around X axis (pitch)
        double tempY = y * Math.cos(rotX) + tempZ * Math.sin(rotX);
        tempZ = -y * Math.sin(rotX) + tempZ * Math.cos(rotX);
        
        // Apply perspective projection
        double scale = zoom * 800 / (1200 + tempZ);
        
        // Convert to screen coordinates (assuming 800x600 viewport)
        int screenX = (int)(400 + tempX * scale);
        int screenY = (int)(300 - tempY * scale); // Invert Y for screen coordinates
        
        return new Point(screenX, screenY);
    }
    
    private Color shadeColor(Color baseColor, double intensity) {
        int r = (int)(baseColor.getRed() * intensity);
        int g = (int)(baseColor.getGreen() * intensity);
        int b = (int)(baseColor.getBlue() * intensity);
        
        r = Math.min(255, Math.max(0, r));
        g = Math.min(255, Math.max(0, g));
        b = Math.min(255, Math.max(0, b));
        
        return new Color(r, g, b);
    }
}