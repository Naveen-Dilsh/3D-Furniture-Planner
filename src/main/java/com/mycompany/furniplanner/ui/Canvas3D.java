package com.mycompany.furniplanner.ui;

import com.mycompany.furniplanner.controller.CameraController;
import com.mycompany.furniplanner.controller.FurnitureController;
import com.mycompany.furniplanner.controller.RoomController;
import com.mycompany.furniplanner.model.Furniture;
import com.mycompany.furniplanner.model.Room;
import com.mycompany.furniplanner.model.Vector3D;
import com.mycompany.furniplanner.model.FurnitureModel;
import com.mycompany.furniplanner.utils.FurnitureModelManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Canvas3D extends JPanel {
    private RoomController roomController;
    private FurnitureController furnitureController;
    private CameraController cameraController;
    
    private Point lastMousePos;
    private Furniture selectedFurniture;
    private BufferedImage floorTexture;
    
    // Room colors
    private Color wallColor = new Color(120, 80, 60); // Brown walls
    private Color floorColor = new Color(210, 180, 140); // Wooden floor
    private Color outsideColor = new Color(230, 230, 240); // Light gray outside
    private Color gridColor = new Color(200, 200, 200, 100); // Grid lines
    
    // Boundary indicator
    private boolean showBoundary = true;
    private Color boundaryColor = new Color(50, 50, 50);
    
    // Wall thickness
    private double wallThickness = 15.0; // Wall thickness in cm
    
    // Maximum room dimensions
    public static final int MAX_ROOM_DIMENSION = 1500; // Maximum room dimension in cm
    
    public Canvas3D(RoomController roomController, FurnitureController furnitureController, CameraController cameraController) {
        this.roomController = roomController;
        this.furnitureController = furnitureController;
        this.cameraController = cameraController;
        
        setBackground(outsideColor);
        createFloorTexture();
        setupMouseListeners();
        
        // Set preferred size
        setPreferredSize(new Dimension(800, 600));
    }
    
    public void setWallColor(Color color) {
        this.wallColor = color;
        repaint();
    }
    
    public void setFloorColor(Color color) {
        this.floorColor = color;
        createFloorTexture(); // Recreate texture with new color
        repaint();
    }
    
    public void setShowBoundary(boolean show) {
        this.showBoundary = show;
        repaint();
    }
    
    public void setWallThickness(double thickness) {
        this.wallThickness = thickness;
        repaint();
    }
    
    public double getWallThickness() {
        return wallThickness;
    }
    
    private void createFloorTexture() {
        // Create a wood-like texture for the floor
        floorTexture = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = floorTexture.createGraphics();
        
        // Base color
        g.setColor(floorColor);
        g.fillRect(0, 0, 100, 100);
        
        // Wood grain
        g.setColor(new Color(
            Math.max(0, floorColor.getRed() - 30),
            Math.max(0, floorColor.getGreen() - 30),
            Math.max(0, floorColor.getBlue() - 30)
        ));
        
        for (int i = 0; i < 10; i++) {
            int y = i * 10 + (int)(Math.random() * 5);
            g.drawLine(0, y, 100, y);
        }
        
        g.dispose();
    }
    
    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                lastMousePos = e.getPoint();
                requestFocusInWindow(); // For keyboard events
                
                if (furnitureController.isDeleteMode()) {
                    Furniture furniture = getFurnitureAt(e.getPoint());
                    if (furniture != null) {
                        furnitureController.removeFurniture(furniture);
                        repaint();
                    }
                    furnitureController.setDeleteMode(false);
                    setCursor(Cursor.getDefaultCursor());
                } else if (furnitureController.isMoveMode()) {
                    selectedFurniture = getFurnitureAt(e.getPoint());
                    if (selectedFurniture != null) {
                        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                }
            }
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedFurniture != null) {
                    selectedFurniture = null;
                    setCursor(Cursor.getDefaultCursor());
                }
                furnitureController.setMoveMode(false);
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                int dx = e.getX() - lastMousePos.x;
                int dy = e.getY() - lastMousePos.y;
                
                if (cameraController.isRotateMode()) {
                    // Rotate the view - invert dx for more intuitive rotation
                    cameraController.rotateCamera(-dx, -dy);
                } else if (selectedFurniture != null) {
                    // Move the furniture
                    moveFurnitureWithMouse(selectedFurniture, e.getPoint(), lastMousePos);
                }
                
                lastMousePos = e.getPoint();
                repaint();
            }
        };
        
        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
        
        // Mouse wheel for zoom
        addMouseWheelListener(e -> {
            cameraController.zoom(e.getWheelRotation());
            repaint();
        });
        
        // Add key listener for additional controls
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_R:
                        // Reset view
                        cameraController.resetCamera();
                        repaint();
                        break;
                    case KeyEvent.VK_T:
                        // Top view
                        cameraController.setTopView();
                        repaint();
                        break;
                    case KeyEvent.VK_F:
                        // Front view
                        cameraController.setFrontView();
                        repaint();
                        break;
                    case KeyEvent.VK_S:
                        // Side view
                        cameraController.setSideView();
                        repaint();
                        break;
                    case KeyEvent.VK_B:
                        // Toggle boundary
                        showBoundary = !showBoundary;
                        repaint();
                        break;
                    case KeyEvent.VK_LEFT:
                        // Rotate left
                        cameraController.rotateCamera(10, 0);
                        repaint();
                        break;
                    case KeyEvent.VK_RIGHT:
                        // Rotate right
                        cameraController.rotateCamera(-10, 0);
                        repaint();
                        break;
                    case KeyEvent.VK_UP:
                        // Rotate up
                        cameraController.rotateCamera(0, -10);
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        // Rotate down
                        cameraController.rotateCamera(0, 10);
                        repaint();
                        break;
                }
            }
        });
    }
    
    private void moveFurnitureWithMouse(Furniture furniture, Point currentPos, Point lastPos) {
        // Convert screen coordinates to world coordinates
        Vector3D worldDelta = screenToWorldDelta(currentPos, lastPos);
        
        // Apply the delta to the furniture position
        furniture.move(worldDelta);
        
        // Ensure furniture stays within room bounds
        Room room = roomController.getRoom();
        if (room != null) {
            Vector3D pos = furniture.getPosition();
            double width = room.getWidth() / 2.0;
            double length = room.getLength() / 2.0;
            
            // Clamp position to room bounds
            double x = Math.max(-width + wallThickness, Math.min(width - wallThickness, pos.getX()));
            double z = Math.max(-length + wallThickness, Math.min(length - wallThickness, pos.getZ()));
            
            furniture.setPosition(new Vector3D(x, pos.getY(), z));
        }
    }
    
    private Vector3D screenToWorldDelta(Point currentPos, Point lastPos) {
        // Convert screen movement to world space movement
        double rotY = cameraController.getRotationY();
        
        double dx = currentPos.x - lastPos.x;
        double dz = currentPos.y - lastPos.y;
        
        // Scale factor based on zoom
        double scale = 1.0 / cameraController.getZoom();
        
        // Apply rotation to get world space delta
        // Fixed the direction to match mouse movement
        double worldDx = (dx * Math.cos(rotY) + dz * Math.sin(rotY)) * scale;
        double worldDz = (dz * Math.cos(rotY) - dx * Math.sin(rotY)) * scale;
        
        return new Vector3D(worldDx, 0, worldDz);
    }
    
        // Update the getFurnitureAt method in Canvas3D.java
        private Furniture getFurnitureAt(Point point) {
    // Get all furniture
    List<Furniture> furnitureList = furnitureController.getAllFurniture();
    
    // Check furniture in reverse order (top to bottom) for better selection
    for (int i = furnitureList.size() - 1; i >= 0; i--) {
        Furniture furniture = furnitureList.get(i);
        
        // Convert 3D position to 2D screen position
        Point screenPos = worldToScreen(furniture.getPosition());
        
        // Get furniture dimensions
        double width = furniture.getDimension().getWidth() * cameraController.getZoom();
        double length = furniture.getDimension().getLength() * cameraController.getZoom();
        double height = furniture.getDimension().getHeight() * cameraController.getZoom();
        
        // Create a larger bounding rectangle for better hit detection
        // Increase the hit area by 20% for easier selection
        int halfWidth = (int)(width / 2 * 1.2);
        int halfLength = (int)(length / 2 * 1.2);
        
        Rectangle bounds = new Rectangle(
            screenPos.x - halfWidth, 
            screenPos.y - halfLength,
            halfWidth * 2,
            halfLength * 2
        );
        
        if (bounds.contains(point)) {
            System.out.println("Selected furniture: " + furniture.getName());
            return furniture;
        }
    }
    
    System.out.println("No furniture selected at point: " + point);
    return null;
}
    
    private Point worldToScreen(Vector3D worldPos) {
        // Convert 3D world coordinates to 2D screen coordinates
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        double zoom = cameraController.getZoom();
        
        // Apply rotation
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
        
        // Convert to screen coordinates
        int screenX = (int)(getWidth() / 2 + tempX * scale);
        int screenY = (int)(getHeight() / 2 - tempY * scale); // Invert Y for screen coordinates
        
        return new Point(screenX, screenY);
    }
    
    @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g;
    
    // Enable anti-aliasing
    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    
    // Draw background
    drawBackground(g2d);
    
    // Draw grid
    drawGrid(g2d);
    
    // Draw room
    Room room = roomController.getRoom();
    if (room != null) {
        drawRoom(g2d, room);
    }
    
    // Draw furniture
    List<Furniture> furnitureList = furnitureController.getAllFurniture();
    
    // Sort furniture by Z-order for proper rendering
    furnitureList.sort((f1, f2) -> {
        double z1 = transformZ(f1.getPosition());
        double z2 = transformZ(f2.getPosition());
        return Double.compare(z2, z1); // Draw back-to-front
    });
    
    // First pass: Draw all furniture models/shapes
    for (Furniture furniture : furnitureList) {
        drawFurniture(g2d, furniture);
    }
    
    // Draw UI overlays
    drawOverlays(g2d);
}
    
    private double transformZ(Vector3D position) {
        // Transform Z coordinate for depth sorting
        double rotY = cameraController.getRotationY();
        double rotX = cameraController.getRotationX();
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
    
    private void drawBackground(Graphics2D g2d) {
        // Draw a gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(230, 230, 240),
            0, getHeight(), new Color(200, 200, 220)
        );
        g2d.setPaint(gradient);
        g2d.fillRect(0, 0, getWidth(), getHeight());
    }
    
    private void drawGrid(Graphics2D g2d) {
        // Draw a grid on the floor for reference
        Room room = roomController.getRoom();
        if (room == null) return;
        
        double zoom = cameraController.getZoom();
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        
        int gridSize = 50;
        int gridExtent = 2000; // How far the grid extends
        
        g2d.setColor(gridColor);
        
        // Draw grid lines
        for (int x = -gridExtent; x <= gridExtent; x += gridSize) {
            Point p1 = worldToScreen(new Vector3D(x, 0, -gridExtent));
            Point p2 = worldToScreen(new Vector3D(x, 0, gridExtent));
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
        
        for (int z = -gridExtent; z <= gridExtent; z += gridSize) {
            Point p1 = worldToScreen(new Vector3D(-gridExtent, 0, z));
            Point p2 = worldToScreen(new Vector3D(gridExtent, 0, z));
            g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
        }
    }
    
    private void drawRoom(Graphics2D g2d, Room room) {
        double width = room.getWidth();
        double length = room.getLength();
        double height = room.getHeight();
        
        // Calculate inner dimensions (accounting for wall thickness)
        double innerWidth = width - (wallThickness * 2);
        double innerLength = length - (wallThickness * 2);
        
        // Floor vertices (clockwise order for correct normal)
        Vector3D[] outerFloorVertices = {
            new Vector3D(-width/2, 0, -length/2),
            new Vector3D(-width/2, 0, length/2),
            new Vector3D(width/2, 0, length/2),
            new Vector3D(width/2, 0, -length/2)
        };
        
        Vector3D[] innerFloorVertices = {
            new Vector3D(-innerWidth/2, 0, -innerLength/2),
            new Vector3D(-innerWidth/2, 0, innerLength/2),
            new Vector3D(innerWidth/2, 0, innerLength/2),
            new Vector3D(innerWidth/2, 0, -innerLength/2)
        };
        
        // Wall vertices (clockwise order for correct normal)
        Vector3D[] outerWallVertices = {
            new Vector3D(-width/2, height, -length/2),
            new Vector3D(-width/2, height, length/2),
            new Vector3D(width/2, height, length/2),
            new Vector3D(width/2, height, -length/2)
        };
        
        Vector3D[] innerWallVertices = {
            new Vector3D(-innerWidth/2, height, -innerLength/2),
            new Vector3D(-innerWidth/2, height, innerLength/2),
            new Vector3D(innerWidth/2, height, innerLength/2),
            new Vector3D(innerWidth/2, height, -innerLength/2)
        };
        
        // Draw floor with texture
        Polygon floor = new Polygon();
        for (Vector3D v : innerFloorVertices) {
            Point p = worldToScreen(v);
            floor.addPoint(p.x, p.y);
        }
        
        // Create a TexturePaint for the floor
        TexturePaint texturePaint = new TexturePaint(floorTexture, 
            new Rectangle(0, 0, floorTexture.getWidth(), floorTexture.getHeight()));
        
        g2d.setPaint(texturePaint);
        g2d.fill(floor);
        
        // Draw walls with thickness
        // Left wall (outer and inner)
        drawThickWall(g2d, 
            outerFloorVertices[0], outerFloorVertices[1], outerWallVertices[1], outerWallVertices[0],
            innerFloorVertices[0], innerFloorVertices[1], innerWallVertices[1], innerWallVertices[0],
            wallColor);
        
        // Front wall (outer and inner)
        drawThickWall(g2d, 
            outerFloorVertices[1], outerFloorVertices[2], outerWallVertices[2], outerWallVertices[1],
            innerFloorVertices[1], innerFloorVertices[2], innerWallVertices[2], innerWallVertices[1],
            wallColor);
        
        // Right wall (outer and inner)
        drawThickWall(g2d, 
            outerFloorVertices[2], outerFloorVertices[3], outerWallVertices[3], outerWallVertices[2],
            innerFloorVertices[2], innerFloorVertices[3], innerWallVertices[3], innerWallVertices[2],
            wallColor);
        
        // Back wall (outer and inner)
        drawThickWall(g2d, 
            outerFloorVertices[3], outerFloorVertices[0], outerWallVertices[0], outerWallVertices[3],
            innerFloorVertices[3], innerFloorVertices[0], innerWallVertices[0], innerWallVertices[3],
            wallColor);
        
        // Draw room boundary if enabled
        if (showBoundary) {
            g2d.setColor(boundaryColor);
            g2d.setStroke(new BasicStroke(2));
            
            // Draw inner floor boundary
            g2d.draw(floor);
            
            // Draw inner wall top edges
            Polygon innerWallTop = new Polygon();
            for (Vector3D v : innerWallVertices) {
                Point p = worldToScreen(v);
                innerWallTop.addPoint(p.x, p.y);
            }
            g2d.draw(innerWallTop);
            
            // Draw outer floor boundary
            Polygon outerFloor = new Polygon();
            for (Vector3D v : outerFloorVertices) {
                Point p = worldToScreen(v);
                outerFloor.addPoint(p.x, p.y);
            }
            g2d.draw(outerFloor);
            
            // Draw outer wall top edges
            Polygon outerWallTop = new Polygon();
            for (Vector3D v : outerWallVertices) {
                Point p = worldToScreen(v);
                outerWallTop.addPoint(p.x, p.y);
            }
            g2d.draw(outerWallTop);
            
            // Draw vertical edges
            for (int i = 0; i < 4; i++) {
                // Inner vertical edges
                Point innerBottom = worldToScreen(innerFloorVertices[i]);
                Point innerTop = worldToScreen(innerWallVertices[i]);
                g2d.drawLine(innerBottom.x, innerBottom.y, innerTop.x, innerTop.y);
                
                // Outer vertical edges
                Point outerBottom = worldToScreen(outerFloorVertices[i]);
                Point outerTop = worldToScreen(outerWallVertices[i]);
                g2d.drawLine(outerBottom.x, outerBottom.y, outerTop.x, outerTop.y);
            }
            
            // Reset stroke
            g2d.setStroke(new BasicStroke(1));
        }
    }
    
    private void drawThickWall(Graphics2D g2d, 
                              Vector3D outerBottom1, Vector3D outerBottom2, Vector3D outerTop2, Vector3D outerTop1,
                              Vector3D innerBottom1, Vector3D innerBottom2, Vector3D innerTop2, Vector3D innerTop1,
                              Color color) {
        // Draw outer wall face
        drawWall(g2d, outerBottom1, outerBottom2, outerTop2, outerTop1, color);
        
        // Draw inner wall face
        drawWall(g2d, innerBottom2, innerBottom1, innerTop1, innerTop2, color.darker());
        
        // Draw top wall face (between outer and inner top edges)
        drawWall(g2d, outerTop1, outerTop2, innerTop2, innerTop1, color.brighter());
        
        // Draw side wall faces if visible
        drawWall(g2d, outerBottom1, outerTop1, innerTop1, innerBottom1, color.darker().darker());
        drawWall(g2d, outerBottom2, innerBottom2, innerTop2, outerTop2, color.darker().darker());
    }
    
    private void drawWall(Graphics2D g2d, Vector3D v1, Vector3D v2, Vector3D v3, Vector3D v4, Color color) {
        // Convert 3D vertices to 2D points
        Point p1 = worldToScreen(v1);
        Point p2 = worldToScreen(v2);
        Point p3 = worldToScreen(v3);
        Point p4 = worldToScreen(v4);
        
        // Create polygon for the wall
        Polygon wall = new Polygon();
        wall.addPoint(p1.x, p1.y);
        wall.addPoint(p2.x, p2.y);
        wall.addPoint(p3.x, p3.y);
        wall.addPoint(p4.x, p4.y);
        
        // Calculate normal for backface culling
        Vector3D edge1 = v2.subtract(v1);
        Vector3D edge2 = v4.subtract(v1);
        Vector3D normal = edge1.cross(edge2).normalize();
        
        // Calculate view vector based on camera position
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        
        // Create a view vector pointing into the scene
        Vector3D viewDir = new Vector3D(
            -Math.sin(rotY) * Math.cos(rotX),
            -Math.sin(rotX),
            -Math.cos(rotY) * Math.cos(rotX)
        );
        
        // Only draw if facing camera (dot product < 0)
        if (normal.dot(viewDir) < 0) {
            // Apply lighting
            double lightIntensity = Math.max(0.3, -normal.dot(new Vector3D(0.5, -1, 0.5).normalize()));
            Color shadedColor = shadeColor(color, lightIntensity);
            
            g2d.setColor(shadedColor);
            g2d.fill(wall);
            
            // Draw outline if boundary is shown
            if (showBoundary) {
                g2d.setColor(boundaryColor);
                g2d.draw(wall);
            }
        }
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
    
    // The rest of the furniture drawing methods remain the same...
    
    // Modify the drawFurniture method in Canvas3D.java
private void drawFurniture(Graphics2D g2d, Furniture furniture) {
    Vector3D position = furniture.getPosition();
    Vector3D rotation = furniture.getRotation();
    
    // Try to use 3D model if available
    FurnitureModelManager modelManager = FurnitureModelManager.getInstance();
    if (modelManager.hasModel(furniture.getType())) {
        FurnitureModel model = modelManager.getModel(furniture.getType());
        model.render(g2d, position, rotation, 
                    cameraController.getRotationX(), 
                    cameraController.getRotationY(), 
                    cameraController.getZoom());
    } else {
        // Fallback to primitive shape rendering if model not available
        switch (furniture.getType()) {
            case TABLE:
                drawTable(g2d, furniture);
                break;
            case CHAIR:
                drawChair(g2d, furniture);
                break;
            case SOFA:
                drawSofa(g2d, furniture);
                break;
            case BED:
                drawBed(g2d, furniture);
                break;
            case WINDOW:
                drawWindow(g2d, furniture);
                break;
            case DOOR:
                drawDoor(g2d, furniture);
                break;
            case BOOKSHELF:
                drawBookshelf(g2d, furniture);
                break;
            case CABINET:
                drawCabinet(g2d, furniture);
                break;
            default:
                drawGenericFurniture(g2d, furniture);
        }
    }
    
    // Draw selection highlight if this furniture is selected
    if (furniture == selectedFurniture) {
        drawSelectionHighlight(g2d, furniture);
    }
    
    // Draw furniture name - CHANGE THIS LINE to avoid recursion
    drawFurnitureName(g2d, furniture, position);
}
    // Add this new method with an additional parameter to avoid recursion
private void drawFurnitureName(Graphics2D g2d, Furniture furniture, Vector3D position) {
    double height = furniture.getDimension().getHeight();
    
    // Position the name above the furniture
    Vector3D namePos = new Vector3D(position.getX(), position.getY() + height/2 + 10, position.getZ());
    Point screenPos = worldToScreen(namePos);
    
    // Draw name with shadow for better visibility
    g2d.setFont(new Font("Arial", Font.BOLD, 12));
    FontMetrics fm = g2d.getFontMetrics();
    int textWidth = fm.stringWidth(furniture.getName());
    
    g2d.setColor(Color.BLACK);
    g2d.drawString(furniture.getName(), screenPos.x - textWidth/2 + 1, screenPos.y + 1);
    g2d.setColor(Color.WHITE);
    g2d.drawString(furniture.getName(), screenPos.x - textWidth/2, screenPos.y);
}
    
    private void drawTable(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double length = furniture.getDimension().getLength();
        
        // Table top vertices
        Vector3D[] topVertices = {
            new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() - length/2),
            new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() + length/2),
            new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() + length/2),
            new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() - length/2)
        };
        
        // Table legs
        double legWidth = 5;
        Vector3D[] legPositions = {
            new Vector3D(position.getX() - width/2 + legWidth, position.getY(), position.getZ() - length/2 + legWidth),
            new Vector3D(position.getX() + width/2 - legWidth, position.getY(), position.getZ() - length/2 + legWidth),
            new Vector3D(position.getX() + width/2 - legWidth, position.getY(), position.getZ() + length/2 - legWidth),
            new Vector3D(position.getX() - width/2 + legWidth, position.getY(), position.getZ() + length/2 - legWidth)
        };
        
        // Draw table top
        Polygon top = new Polygon();
        for (Vector3D v : topVertices) {
            Point p = worldToScreen(v);
            top.addPoint(p.x, p.y);
        }
        
        // Calculate normal for backface culling
        Vector3D edge1 = topVertices[1].subtract(topVertices[0]);
        Vector3D edge2 = topVertices[3].subtract(topVertices[0]);
        Vector3D normal = edge1.cross(edge2).normalize();
        
        // Calculate view vector
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        Vector3D viewDir = new Vector3D(
            -Math.sin(rotY) * Math.cos(rotX),
            -Math.sin(rotX),
            -Math.cos(rotY) * Math.cos(rotX)
        );
        
        // Only draw if facing camera
        if (normal.dot(viewDir) < 0) {
            g2d.setColor(new Color(139, 69, 19)); // Brown
            g2d.fill(top);
            g2d.setColor(Color.BLACK);
            g2d.draw(top);
        }
        
        // Draw table legs
        for (Vector3D legPos : legPositions) {
            drawBox(g2d, legPos, legWidth, height, legWidth, new Color(120, 60, 15));
        }
    }
    
    private void drawChair(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double length = furniture.getDimension().getLength();
        
        // Chair seat
        drawBox(g2d, new Vector3D(position.getX(), position.getY() + height/6, position.getZ()), 
                width, height/3, length, new Color(160, 82, 45));
        
        // Chair back
        drawBox(g2d, new Vector3D(position.getX(), position.getY() + height*2/3, position.getZ() - length/2 + 5), 
                width, height*2/3, 5, new Color(140, 70, 35));
        
        // Chair legs
        double legWidth = 3;
        Vector3D[] legPositions = {
            new Vector3D(position.getX() - width/2 + legWidth, position.getY() - height/6, position.getZ() - length/2 + legWidth),
            new Vector3D(position.getX() + width/2 - legWidth, position.getY() - height/6, position.getZ() - length/2 + legWidth),
            new Vector3D(position.getX() + width/2 - legWidth, position.getY() - height/6, position.getZ() + length/2 - legWidth),
            new Vector3D(position.getX() - width/2 + legWidth, position.getY() - height/6, position.getZ() + length/2 - legWidth)
        };
        
        for (Vector3D legPos : legPositions) {
            drawBox(g2d, legPos, legWidth, height/3, legWidth, new Color(120, 60, 15));
        }
    }
    
    private void drawSofa(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double length = furniture.getDimension().getLength();
        
        // Sofa base
        drawBox(g2d, new Vector3D(position.getX(), position.getY(), position.getZ()), 
                width, height/2, length, new Color(70, 130, 180));
        
        // Sofa back
        drawBox(g2d, new Vector3D(position.getX(), position.getY() + height/4, position.getZ() - length/2 + 10), 
                width, height/2, 20, new Color(60, 110, 160));
        
        // Sofa arms
        drawBox(g2d, new Vector3D(position.getX() - width/2 + 10, position.getY() + height/8, position.getZ()), 
                20, height/4, length, new Color(50, 100, 150));
        drawBox(g2d, new Vector3D(position.getX() + width/2 - 10, position.getY() + height/8, position.getZ()), 
                20, height/4, length, new Color(50, 100, 150));
    }
    
    private void drawBed(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double length = furniture.getDimension().getLength();
        
        // Bed base
        drawBox(g2d, new Vector3D(position.getX(), position.getY() - height/4, position.getZ()), 
                width, height/2, length, new Color(160, 82, 45));
        
        // Mattress
        drawBox(g2d, new Vector3D(position.getX(), position.getY(), position.getZ()), 
                width - 10, 10, length - 10, new Color(220, 220, 220));
        
        // Headboard
        drawBox(g2d, new Vector3D(position.getX(), position.getY() + height/4, position.getZ() - length/2 + 5), 
                width, height/2, 10, new Color(140, 70, 35));
    }
    
    private void drawWindow(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = 10;
        
        // Window frame
        drawBox(g2d, position, width, height, depth, new Color(160, 82, 45));
        
        // Window glass
        drawBox(g2d, new Vector3D(position.getX(), position.getY(), position.getZ() + 1), 
                width - 10, height - 10, depth - 2, new Color(173, 216, 230, 150));
    }
    
    private void drawDoor(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = 5;
        
        // Door
        drawBox(g2d, position, width, height, depth, new Color(160, 82, 45));
        
        // Door handle
        Point handlePos = worldToScreen(new Vector3D(
            position.getX() + width/3, 
            position.getY(), 
            position.getZ() + depth/2 + 2
        ));
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(handlePos.x - 3, handlePos.y - 3, 6, 6);
    }
    
    private void drawBookshelf(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = furniture.getDimension().getLength();
        
        // Bookshelf frame
        drawBox(g2d, position, width, height, depth, new Color(160, 82, 45));
        
        // Shelves
        int numShelves = 4;
        for (int i = 1; i < numShelves; i++) {
            double shelfY = position.getY() - height/2 + (i * height / numShelves);
            drawBox(g2d, new Vector3D(position.getX(), shelfY, position.getZ()), 
                    width - 4, 2, depth - 4, new Color(140, 70, 35));
        }
    }
    
    private void drawCabinet(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = furniture.getDimension().getLength();
        
        // Cabinet body
        drawBox(g2d, position, width, height, depth, new Color(160, 82, 45));
        
        // Cabinet doors
        drawBox(g2d, new Vector3D(position.getX() - width/4, position.getY(), position.getZ() + depth/2 - 1), 
                width/2, height - 4, 2, new Color(140, 70, 35));
        drawBox(g2d, new Vector3D(position.getX() + width/4, position.getY(), position.getZ() + depth/2 - 1), 
                width/2, height - 4, 2, new Color(140, 70, 35));
        
        // Cabinet handles
        Point handle1Pos = worldToScreen(new Vector3D(
            position.getX() - width/4, 
            position.getY(), 
            position.getZ() + depth/2 + 1
        ));
        Point handle2Pos = worldToScreen(new Vector3D(
            position.getX() + width/4, 
            position.getY(), 
            position.getZ() + depth/2 + 1
        ));
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillOval(handle1Pos.x - 3, handle1Pos.y - 3, 6, 6);
        g2d.fillOval(handle2Pos.x - 3, handle2Pos.y - 3, 6, 6);
    }
    
    private void drawGenericFurniture(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = furniture.getDimension().getLength();
        
        // Simple box representation
        drawBox(g2d, position, width, height, depth, Color.GRAY);
    }
    
    private void drawBox(Graphics2D g2d, Vector3D position, double width, double height, double depth, Color color) {
        // Calculate vertices for a box centered at position
        Vector3D[] vertices = new Vector3D[8];
        
        // Bottom vertices (counter-clockwise looking from bottom)
        vertices[0] = new Vector3D(position.getX() - width/2, position.getY() - height/2, position.getZ() - depth/2);
        vertices[1] = new Vector3D(position.getX() - width/2, position.getY() - height/2, position.getZ() + depth/2);
        vertices[2] = new Vector3D(position.getX() + width/2, position.getY() - height/2, position.getZ() + depth/2);
        vertices[3] = new Vector3D(position.getX() + width/2, position.getY() - height/2, position.getZ() - depth/2);
        
        // Top vertices (counter-clockwise looking from top)
        vertices[4] = new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() - depth/2);
        vertices[5] = new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() + depth/2);
        vertices[6] = new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() + depth/2);
        vertices[7] = new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() - depth/2);
        
        // Define the faces of the box (each face is defined by 4 vertices)
        int[][] faces = {
            {0, 1, 2, 3},  // Bottom face
            {4, 5, 6, 7},  // Top face
            {0, 1, 5, 4},  // Left face
            {1, 2, 6, 5},  // Front face
            {2, 3, 7, 6},  // Right face
            {3, 0, 4, 7}   // Back face
        };
        
        // Colors for each face (slightly different shades)
        Color[] faceColors = {
            color.darker(),       // Bottom - darker
            color,                // Top - normal
            color.darker(),       // Left - darker
            color,                // Front - normal
            color.darker(),       // Right - darker
            color                 // Back - normal
        };
        
        // Calculate view vector for backface culling
        double rotX = cameraController.getRotationX();
        double rotY = cameraController.getRotationY();
        Vector3D viewDir = new Vector3D(
            -Math.sin(rotY) * Math.cos(rotX),
            -Math.sin(rotX),
            -Math.cos(rotY) * Math.cos(rotX)
        );
        
        // Draw each face with backface culling
        for (int i = 0; i < faces.length; i++) {
            int[] face = faces[i];
            
            // Calculate face normal
            Vector3D edge1 = vertices[face[1]].subtract(vertices[face[0]]);
            Vector3D edge2 = vertices[face[3]].subtract(vertices[face[0]]);
            Vector3D normal = edge1.cross(edge2).normalize();
            
            // Only draw if facing camera (dot product < 0)
            if (normal.dot(viewDir) < 0) {
                // Create polygon for the face
                Polygon poly = new Polygon();
                for (int j = 0; j < 4; j++) {
                    Point p = worldToScreen(vertices[face[j]]);
                    poly.addPoint(p.x, p.y);
                }
                
                // Apply lighting
                double lightIntensity = Math.max(0.3, -normal.dot(new Vector3D(0.5, -1, 0.5).normalize()));
                Color shadedColor = shadeColor(faceColors[i], lightIntensity);
                
                g2d.setColor(shadedColor);
                g2d.fill(poly);
                g2d.setColor(Color.DARK_GRAY);
                g2d.draw(poly);
            }
        }
    }
    
    private void drawSelectionHighlight(Graphics2D g2d, Furniture furniture) {
        Vector3D position = furniture.getPosition();
        double width = furniture.getDimension().getWidth();
        double height = furniture.getDimension().getHeight();
        double depth = furniture.getDimension().getLength();
        
        // Draw a bounding box around the selected furniture
        Vector3D[] vertices = {
            new Vector3D(position.getX() - width/2, position.getY() - height/2, position.getZ() - depth/2),
            new Vector3D(position.getX() + width/2, position.getY() - height/2, position.getZ() - depth/2),
            new Vector3D(position.getX() + width/2, position.getY() - height/2, position.getZ() + depth/2),
            new Vector3D(position.getX() - width/2, position.getY() - height/2, position.getZ() + depth/2),
            new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() - depth/2),
            new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() - depth/2),
            new Vector3D(position.getX() + width/2, position.getY() + height/2, position.getZ() + depth/2),
            new Vector3D(position.getX() - width/2, position.getY() + height/2, position.getZ() + depth/2)
        };
        
        // Convert to screen coordinates
        Point[] points = new Point[8];
        for (int i = 0; i < 8; i++) {
            points[i] = worldToScreen(vertices[i]);
        }
        
        // Draw edges
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0));
        
        // Bottom face
        g2d.drawLine(points[0].x, points[0].y, points[1].x, points[1].y);
        g2d.drawLine(points[1].x, points[1].y, points[2].x, points[2].y);
        g2d.drawLine(points[2].x, points[2].y, points[3].x, points[3].y);
        g2d.drawLine(points[3].x, points[3].y, points[0].x, points[0].y);
        
        // Top face
        g2d.drawLine(points[4].x, points[4].y, points[5].x, points[5].y);
        g2d.drawLine(points[5].x, points[5].y, points[6].x, points[6].y);
        g2d.drawLine(points[6].x, points[6].y, points[7].x, points[7].y);
        g2d.drawLine(points[7].x, points[7].y, points[4].x, points[4].y);
        
        // Connecting edges
        g2d.drawLine(points[0].x, points[0].y, points[4].x, points[4].y);
        g2d.drawLine(points[1].x, points[1].y, points[5].x, points[5].y);
        g2d.drawLine(points[2].x, points[2].y, points[6].x, points[6].y);
        g2d.drawLine(points[3].x, points[3].y, points[7].x, points[7].y);
        
        // Reset stroke
        g2d.setStroke(new BasicStroke());
    }
    

// Then modify the drawFurniture method in Canvas3D.java
// Modify the original drawFurnitureName method to call the new one
private void drawFurnitureName(Graphics2D g2d, Furniture furniture) {
    // This method is kept for backward compatibility
    // It now calls the new method with the position parameter
    drawFurnitureName(g2d, furniture, furniture.getPosition());
}
    
    private void drawOverlays(Graphics2D g2d) {
        // Draw camera controls help
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(10, 10, 200, 120, 10, 10);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("Camera Controls:", 20, 30);
        g2d.setFont(new Font("Arial", Font.PLAIN, 11));
        g2d.drawString("Drag - Rotate View", 20, 50);
        g2d.drawString("Mouse Wheel - Zoom", 20, 65);
        g2d.drawString("Arrow Keys - Rotate", 20, 80);
        g2d.drawString("R - Reset View", 20, 95);
        g2d.drawString("T/F/S - Top/Front/Side View", 20, 110);
        
        // Draw view mode indicator
        String viewMode = "Free View";
        if (cameraController.getRotationX() < -Math.PI/3 && Math.abs(cameraController.getRotationY()) < 0.1) {
            viewMode = "Top View";
        } else if (Math.abs(cameraController.getRotationX()) < 0.1 && Math.abs(cameraController.getRotationY()) < 0.1) {
            viewMode = "Front View";
        } else if (Math.abs(cameraController.getRotationX()) < 0.1 && Math.abs(cameraController.getRotationY() - Math.PI/2) < 0.1) {
            viewMode = "Side View";
        }
        
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRoundRect(getWidth() - 110, 10, 100, 25, 10, 10);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString(viewMode, getWidth() - 100, 27);
    }
}
