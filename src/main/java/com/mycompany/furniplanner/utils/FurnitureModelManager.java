package com.mycompany.furniplanner.utils;

import java.util.HashMap;
import java.util.Map;

import com.mycompany.furniplanner.model.FurnitureModel;
import com.mycompany.furniplanner.model.FurnitureType;
import com.mycompany.furniplanner.model.Vector3D;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class FurnitureModelManager {
    private static FurnitureModelManager instance;
    private Map<FurnitureType, FurnitureModel> models = new HashMap<>();
    private String basePath;
    
    private FurnitureModelManager() {
        // Private constructor for singleton
        // Add this to your FurnitureModelManager constructor or loadModels method
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
            System.out.println("Trying to load models from: " + basePath);

        // List all resources in the classpath (this can help verify if your resources are properly included)
        try {
            Enumeration<URL> resources = getClass().getClassLoader().getResources("com/mycompany/resources/models");
            System.out.println("Available resources:");
            while (resources.hasMoreElements()) {
                System.out.println(resources.nextElement());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadModels();
    }
    
    public static FurnitureModelManager getInstance() {
        
        if (instance == null) {
            instance = new FurnitureModelManager();
        }
        return instance;
    }
    
    private void loadModels() {
    // Load models for each furniture type
    try {
        // Use the correct path to your models based on the actual location
        String basePath = "src/main/java/com/mycompany/resources/models/";
        System.out.println("Trying to load models from: " + basePath);
        
        // Table - use direct file access since we know the exact path
        File tableFile = new File(basePath + "table.obj");
        if (tableFile.exists()) {
            FurnitureModel tableModel = new FurnitureModel(tableFile.getAbsolutePath());
            tableModel.setScale(1.0);
            tableModel.setOffset(new Vector3D(0, 0, 0));
            models.put(FurnitureType.TABLE, tableModel);
            System.out.println("Successfully loaded table model");
        } else {
            System.err.println("Table model file not found at: " + tableFile.getAbsolutePath());
        }
        
        // Chair - use direct file access
        File chairFile = new File(basePath + "chair.obj");
        if (chairFile.exists()) {
            FurnitureModel chairModel = new FurnitureModel(chairFile.getAbsolutePath());
            chairModel.setScale(1.0);
            chairModel.setOffset(new Vector3D(0, 0, 0));
            models.put(FurnitureType.CHAIR, chairModel);
            System.out.println("Successfully loaded chair model");
        } else {
            System.err.println("Chair model file not found at: " + chairFile.getAbsolutePath());
        }
        
        // Add more furniture models as needed using the same pattern
        
    } catch (Exception e) {
        System.err.println("Error loading furniture models: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    public FurnitureModel getModel(FurnitureType type) {
        return models.get(type);
    }
    
    public boolean hasModel(FurnitureType type) {
        return models.containsKey(type) && models.get(type) != null;
    }

}