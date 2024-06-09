// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class handles all keyboard inputs

package main;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// KeyInput class
public class KeyInput extends KeyAdapter {
    private final Handler handler;

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
            if (tempObject.getid() == ID.Player) {
                Player player = getPlayer((Player) tempObject, key);
            }
            if (tempObject.getid() == ID.Camera) {
                Camera cam = (Camera) tempObject;
                if (key == KeyEvent.VK_X){
                    System.out.println(cam.getLocation().add(cam.getNorm().mul(10)));
                    handler.addObject(new Cube(cam.getLocation().add(cam.getNorm().mul(10)), 10, ID.Cube, handler, Color.yellow));
                }

                // If the e key is pressed, increases the number of cosines applied
                if (key == KeyEvent.VK_E) {
                    cam.setCos(cam.cos + 1);
                }

                // If the q key is pressed, decreases the number of cosines applied
                if (key == KeyEvent.VK_Q) {
                    cam.setCos(cam.cos - 1);
                }

                // If the c key is pressed, increases the number of tangents applied
                if (key == KeyEvent.VK_C) {
                    cam.setTan(cam.tan + 1);
                }

                // If the z key is pressed, decreases the number of tangents applied
                if (key == KeyEvent.VK_Z) {
                    cam.setTan(cam.tan - 1);
                }
            }
        }
    }

    private static Player getPlayer(Player player, int key) {
        // If the shift key is pressed, it switches between the camera being locked or not
        if (key == KeyEvent.VK_SHIFT) {
            player.switchLock();
        }
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
            player.addForce(Vector.k.mul(1.5));
        }
        return player;
    }

    // If it detects a key is released, and it was a key that was pressed down and moving the camera, it stops moving the camera
    public void keyReleased(KeyEvent e){
        int key = e.getKeyCode();
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);
            if (tempObject.getid() == ID.Player) {
                Player player = (Player) tempObject;

                //Key Release Events
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
            }
        }

    }
}
