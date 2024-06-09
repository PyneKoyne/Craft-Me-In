// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the Player class, creating a game object which acts as the player

package main;

// imports
import java.awt.*;
import java.util.HashMap;

// Player object class
public class Player extends gameObject{
    // variables
    private final Handler handler;
    private final Camera camera; // has a camera object as a child
    private final Vector CAMERA_OFFSET = new Vector(0, 0, 2);
    private final Window window;
    public boolean[] movement = {false, false, false, false};
    public boolean locked = true;
    private Color color;

    // player constructor which extends game object
    public Player(Point3D p, float scale, ID id, Handler handler, Color color, Window window){
        super(p, new Vector(Math.PI, 0, 0), id);
        this.color = color;
        this.handler = handler;
        this.window = window;
        // creates a new camera
        this.camera = new Camera(p, 0.16, ID.Camera, handler, window);
        this.camera.pitch = Math.PI/2;
        handler.addObject(this.camera);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (movement[0]) addForce(norm.mul(0.05));
        if (movement[1]) addForce(left.mul(-0.05));
        if (movement[2]) addForce(norm.mul(-0.05));
        if (movement[3]) addForce(left.mul(0.05));

        for (int i = 0; i < handler.object.size(); i++){
            gameObject tempObject = handler.object.get(i);
            if (tempObject.getMesh() != null){
                checkCollision(tempObject);
            }
        }

        this.coords = this.coords.add(this.vel);
        this.vel = this.vel.mul(0.5);
        this.addForce(new Vector(0, 0, -0.05));

        // Moves the mouse to the centre of the screen if not shift locked
        // rotates and moves the camera to follow the player
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            int screenX = this.window.getWidth() / 2;
            int screenY = this.window.getHeight() / 2;
            setRot(getAngles().add(new Vector(0, (-screenY + p.getY() - window.screenLoc().y) / 2000, (screenX - p.getX() + window.screenLoc().x) / 2000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX + window.screenLoc().x, screenY + window.screenLoc().y);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
        camera.coords = this.coords.add(CAMERA_OFFSET);
    }

    // helper code to run on every render
    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    private boolean checkCollision(gameObject object){
        for (Face face: object.getMesh().faces){
            Vector normForce = face.intersects(this.coords, vel, object.coords);
            if (normForce != null){
                addForce(normForce);
                return checkCollision(object);
            }
        }
        return true;
    }

    // switches if the mouse is locked in the center or not
    public void switchLock() {
        locked = !locked;
    }

    // an override of the gameObject method to also rotate the camera's pitch but not the players
    public void setRot(Vector rot) {
        // Rotations over 360 degrees are modul-ised
        this.roll = rot.getX() % (2 * Math.PI);
        this.yaw = rot.getZ() % (2 * Math.PI);

        if (camera != null) {
            camera.roll = this.roll;
            camera.pitch += rot.getY() % (2 * Math.PI);
            camera.yaw = this.yaw;
            camera.updateRot();
        }

        updateRot();
    }


    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}