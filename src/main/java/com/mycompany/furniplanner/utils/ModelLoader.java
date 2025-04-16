package com.mycompany.furniplanner.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mycompany.furniplanner.model.Vector3D;
import java.io.File;
import java.io.FileInputStream;

public class ModelLoader {
    
    public static class Model {
        public List<Vector3D> vertices = new ArrayList<>();
        public List<Vector3D> normals = new ArrayList<>();
        public List<Vector3D> textureCoords = new ArrayList<>();
        public List<Face> faces = new ArrayList<>();
        public Map<String, Material> materials = new HashMap<>();
        public String currentMaterial = "default";
        
        public void addFace(Face face) {
            face.materialName = currentMaterial;
            faces.add(face);
        }
    }
    
    public static class Face {
        public int[] vertexIndices;
        public int[] textureIndices;
        public int[] normalIndices;
        public String materialName = "default";
        
        public Face(int[] vertexIndices, int[] textureIndices, int[] normalIndices) {
            this.vertexIndices = vertexIndices;
            this.textureIndices = textureIndices;
            this.normalIndices = normalIndices;
        }
    }
    
    public static class Material {
        public float[] ambient = {0.2f, 0.2f, 0.2f};
        public float[] diffuse = {0.8f, 0.8f, 0.8f};
        public float[] specular = {1.0f, 1.0f, 1.0f};
        public float shininess = 0.0f;
        public String texturePath = null;
    }
    
    public static Model loadOBJ(String resourcePath) {
    Model model = new Model();
    
    // Extract just the filename
    String fileName = new File(resourcePath).getName();
    
    // Try direct file access for development
    File file = new File("src/main/java/com/mycompany/resources/models/" + fileName);
    System.out.println("Trying to load from direct file: " + file.getAbsolutePath());
    
    if (file.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            loadOBJFromReader(reader, model, file.getPath());
            System.out.println("Successfully loaded model from file: " + file.getPath());
            return model;
        } catch (IOException e) {
            System.err.println("Error reading file: " + file.getPath());
            e.printStackTrace();
        }
    } else {
        System.err.println("File does not exist: " + file.getAbsolutePath());
    }
    
    // If direct file access failed, try classpath
    try {
        InputStream is = ModelLoader.class.getResourceAsStream("/resources/models/" + fileName);
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            loadOBJFromReader(reader, model, resourcePath);
            is.close();
            System.out.println("Successfully loaded model from classpath: /resources/models/" + fileName);
            return model;
        }
    } catch (IOException e) {
        System.err.println("Error loading from classpath: " + e.getMessage());
    }
    
    System.err.println("Failed to load model: " + resourcePath);
    return model; // Return empty model as fallback
}
    
    private static void loadOBJFromReader(BufferedReader reader, Model model, String basePath) throws IOException {
        String line;
        String mtlPath = null;
        
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // Skip comments and empty lines
            }
            
            String[] parts = line.split("\\s+");
            switch (parts[0]) {
                case "v": // Vertex
                    if (parts.length >= 4) {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        model.vertices.add(new Vector3D(x, y, z));
                    }
                    break;
                    
                case "vn": // Normal
                    if (parts.length >= 4) {
                        double x = Double.parseDouble(parts[1]);
                        double y = Double.parseDouble(parts[2]);
                        double z = Double.parseDouble(parts[3]);
                        model.normals.add(new Vector3D(x, y, z));
                    }
                    break;
                    
                case "vt": // Texture coordinate
                    if (parts.length >= 3) {
                        double u = Double.parseDouble(parts[1]);
                        double v = parts.length > 2 ? Double.parseDouble(parts[2]) : 0;
                        model.textureCoords.add(new Vector3D(u, v, 0));
                    }
                    break;
                    
                case "f": // Face
                    if (parts.length >= 4) {
                        int[] vertexIndices = new int[parts.length - 1];
                        int[] textureIndices = new int[parts.length - 1];
                        int[] normalIndices = new int[parts.length - 1];
                        
                        for (int i = 1; i < parts.length; i++) {
                            String[] indices = parts[i].split("/");
                            
                            // OBJ indices are 1-based, so subtract 1
                            vertexIndices[i - 1] = Integer.parseInt(indices[0]) - 1;
                            
                            if (indices.length > 1 && !indices[1].isEmpty()) {
                                textureIndices[i - 1] = Integer.parseInt(indices[1]) - 1;
                            }
                            
                            if (indices.length > 2) {
                                normalIndices[i - 1] = Integer.parseInt(indices[2]) - 1;
                            }
                        }
                        
                        model.addFace(new Face(vertexIndices, textureIndices, normalIndices));
                    }
                    break;
                    
                case "mtllib": // Material library
                    if (parts.length >= 2) {
                        mtlPath = getDirectoryPath(basePath) + parts[1];
                        loadMTL(mtlPath, model);
                    }
                    break;
                    
                case "usemtl": // Use material
                    if (parts.length >= 2) {
                        model.currentMaterial = parts[1];
                        if (!model.materials.containsKey(model.currentMaterial)) {
                            model.materials.put(model.currentMaterial, new Material());
                        }
                    }
                    break;
            }
        }
        
        reader.close();
    }
    
    private static void loadMTL(String mtlPath, Model model) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mtlPath));
            String line;
            String currentMaterial = null;
            
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                
                String[] parts = line.split("\\s+");
                switch (parts[0]) {
                    case "newmtl":
                        if (parts.length >= 2) {
                            currentMaterial = parts[1];
                            model.materials.put(currentMaterial, new Material());
                        }
                        break;
                        
                    case "Ka": // Ambient color
                        if (parts.length >= 4 && currentMaterial != null) {
                            Material mat = model.materials.get(currentMaterial);
                            mat.ambient[0] = Float.parseFloat(parts[1]);
                            mat.ambient[1] = Float.parseFloat(parts[2]);
                            mat.ambient[2] = Float.parseFloat(parts[3]);
                        }
                        break;
                        
                    case "Kd": // Diffuse color
                        if (parts.length >= 4 && currentMaterial != null) {
                            Material mat = model.materials.get(currentMaterial);
                            mat.diffuse[0] = Float.parseFloat(parts[1]);
                            mat.diffuse[1] = Float.parseFloat(parts[2]);
                            mat.diffuse[2] = Float.parseFloat(parts[3]);
                        }
                        break;
                        
                    case "Ks": // Specular color
                        if (parts.length >= 4 && currentMaterial != null) {
                            Material mat = model.materials.get(currentMaterial);
                            mat.specular[0] = Float.parseFloat(parts[1]);
                            mat.specular[1] = Float.parseFloat(parts[2]);
                            mat.specular[2] = Float.parseFloat(parts[3]);
                        }
                        break;
                        
                    case "Ns": // Shininess
                        if (parts.length >= 2 && currentMaterial != null) {
                            Material mat = model.materials.get(currentMaterial);
                            mat.shininess = Float.parseFloat(parts[1]);
                        }
                        break;
                        
                    case "map_Kd": // Diffuse texture
                        if (parts.length >= 2 && currentMaterial != null) {
                            Material mat = model.materials.get(currentMaterial);
                            mat.texturePath = getDirectoryPath(mtlPath) + parts[1];
                        }
                        break;
                }
            }
            
            reader.close();
        } catch (IOException e) {
            System.err.println("Error loading material file: " + mtlPath);
            e.printStackTrace();
        }
    }
    
    private static String getDirectoryPath(String filePath) {
        int lastSeparatorIndex = Math.max(filePath.lastIndexOf('/'), filePath.lastIndexOf('\\'));
        if (lastSeparatorIndex >= 0) {
            return filePath.substring(0, lastSeparatorIndex + 1);
        }
        return "";
    }
}