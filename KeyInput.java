// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class handles all keyboard inputs

package main;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// KeyInput class
public class KeyInput extends KeyAdapter {
    private final Handler handler; // game object handler

    // constructor which sets the handler variable
    public KeyInput(Handler handler){
        this.handler = handler;
    }

    // When a key is pressed on the keyboard
    public void keyPressed(KeyEvent e){

        // Grabs the key
        int key = e.getKeyCode();
        // Moves forward, right, backwards, and left respectively based on if the key is held down

        // Loops through every game object
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            if (tempObject.getId() == ID.Player) {
                getPlayer((Player) tempObject, key);
            }
            // creates a new cube for testing when the x key is pressed (KEPT AS AN EASTER EGG)
            if (tempObject.getId() == ID.Camera) {
                Camera cam = (Camera) tempObject;
                if (key == KeyEvent.VK_X){
                    handler.addObject(new Cube(cam.getLocation().add(cam.getNorm().mul(10)), 10, ID.Cube, handler, Color.yellow));
                }
            }
        }
    }

    // Player specific key inputs
    private static void getPlayer(Player player, int key) {
        // If the shift key is pressed, it switches between the camera being locked or not
        if (key == KeyEvent.VK_SHIFT) {
            player.switchLock();
        }
        // movement key inputs
        if(key == KeyEvent.VK_W ){
            player.movement[0] = true;
        }
        if(key == KeyEvent.VK_D ){
            player.movement[1] = true;
        }
        if(key == KeyEvent.VK_S ){
            player.movement[2] = true;
        }
        if(key == KeyEvent.VK_A ){
            player.movement[3] = true;
        }
        if(key == KeyEvent.VK_SPACE){
            player.movement[4] = true; // jumping
        }
    }

    // If it detects a key is released, and it was a key that was pressed down and moving the camera, it stops moving the camera
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            if (tempObject.getId() == ID.Player) { // only for the player class
                Player player = (Player) tempObject;
                // Key Release Events for movement
                if (key == KeyEvent.VK_W) {
                    player.movement[0] = false;
                }
                if (key == KeyEvent.VK_D) {
                    player.movement[1] = false;
                }
                if (key == KeyEvent.VK_S) {
                    player.movement[2] = false;
                }
                if (key == KeyEvent.VK_A) {
                    player.movement[3] = false;
                }
                if (key == KeyEvent.VK_SPACE) {
                    player.movement[4] = false;
                }
            }
        }

    }
}
