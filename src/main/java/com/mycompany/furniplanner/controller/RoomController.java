package com.mycompany.furniplanner.controller;

import com.mycompany.furniplanner.model.Room;

public class RoomController {
    private Room room;
    
    public RoomController(Room room) {
        this.room = room;
    }
    
    public Room getRoom() {
        return room;
    }
    
    public void createRoom(int width, int length, int height) {
        this.room = new Room(width, length, height);
    }
    
    public void resizeRoom(int width, int length, int height) {
        room.setWidth(width);
        room.setLength(length);
        room.setHeight(height);
    }
}