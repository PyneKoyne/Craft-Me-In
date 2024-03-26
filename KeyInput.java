// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class handles all keyboard inputs

package main;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// KeyInput class
public class KeyInput extends KeyAdapter {
    private final Handler handler;

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
            if (tempObject.getid() == ID.Camera) {
                Camera cam = getCamera((Camera) tempObject, key);

                if (key == KeyEvent.VK_X){
                    handler.addObject(new Cube(cam.getLocation().add(cam.getNorm()), 10, ID.Cube, handler, Color.yellow));
                }
            }
        }
    }

    private static Camera getCamera(Camera tempObject, int key) {

        if(key == KeyEvent.VK_W ){
            tempObject.movement[0] = true;
        }
        if(key == KeyEvent.VK_D ){
            tempObject.movement[1] = true;
        }
        if(key == KeyEvent.VK_S ){
            tempObject.movement[2] = true;
        }
        if(key == KeyEvent.VK_A ){
            tempObject.movement[3] = true;
        }

        // If the shift key is pressed, it switches between the camera being locked or not
        if (key == KeyEvent.VK_SHIFT) {
            tempObject.switchLock();
        }

        // If the e key is pressed, increases the number of cosines applied
        if (key == KeyEvent.VK_E) {
            tempObject.setCos(tempObject.cos + 1);
        }

        // If the q key is pressed, decreases the number of cosines applied
        if (key == KeyEvent.VK_Q) {
            tempObject.setCos(tempObject.cos - 1);
        }

        // If the c key is pressed, increases the number of tangents applied
        if (key == KeyEvent.VK_C) {
            tempObject.setTan(tempObject.tan + 1);
        }

        // If the z key is pressed, decreases the number of tangents applied
        if (key == KeyEvent.VK_Z) {
            tempObject.setTan(tempObject.tan - 1);
        }
        return tempObject;
    }

    // If it detects a key is released, and it was a key that was pressed down and moving the camera, it stops moving the camera
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            
            if (tempObject.getid() == ID.Camera) {
                Camera cam = (Camera) tempObject;
                //Key Release Events
                if (key == KeyEvent.VK_W) {
                    cam.movement[0] = false;
                }
                if (key == KeyEvent.VK_D) {
                    cam.movement[1] = false;
                }
                if (key == KeyEvent.VK_S) {
                    cam.movement[2] = false;
                }
                if (key == KeyEvent.VK_A) {
                    cam.movement[3] = false;
                }
            }
        }

    }
}
