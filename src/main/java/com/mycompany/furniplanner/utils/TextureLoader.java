package com.mycompany.furniplanner.utils;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

public class TextureLoader {
    private static Map<String, Image> textureCache = new HashMap<>();
    
    public static Image loadTexture(String filePath) {
        // Check if texture is already loaded
        if (textureCache.containsKey(filePath)) {
            return textureCache.get(filePath);
        }
        
        try {
            // Load the texture
            Image texture = ImageIO.read(new File(filePath));
            textureCache.put(filePath, texture);
            return texture;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void clearCache() {
        textureCache.clear();
    }
}