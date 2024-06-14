// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: This is the Player class, creating a game object which acts as the player

package main;

// imports
import java.awt.*;

// Player object class
public class Player extends gameObject{
    // variables
    private final Handler handler;
    private final Camera camera; // has a camera object as a child
    private static final Vector CAMERA_OFFSET = new Vector(0, 0, 2);
    private static final double REACH = 5; // the reach of the player to add and remove blocks
    private final Window window;
    public boolean[] movement = {false, false, false, false}; // 4 movement vectors of the player
    public boolean locked = true; // if the camera is locked

    // player constructor which extends game object
    public Player(Point3D p, float scale, ID id, Handler handler, Window window){
        super(p, new Vector(Math.PI, 0, 0), id);
        this.handler = handler;
        this.window = window;
        // creates a new camera
        this.camera = new Camera(p, 0.08, ID.Camera, handler, window);
        this.camera.pitch = Math.PI/2;
        handler.addObject(this.camera);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        // moves the player
        if (movement[0]) addForce(norm.mul(0.05));
        if (movement[1]) addForce(left.mul(-0.05));
        if (movement[2]) addForce(norm.mul(-0.05));
        if (movement[3]) addForce(left.mul(0.05));
        checkCollision(); // checks for collisions

        // updates velocity and coordinates
        this.coords = this.coords.add(this.vel);
        if (this.coords.z < -10) {
            this.coords.z = -10;
        }
        this.vel = this.vel.mul(0.5);
        this.addForce(new Vector(0, 0, -0.05)); // gravity

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
        camera.coords = this.coords.add(CAMERA_OFFSET); // moves the camera with the player
    }

    // helper code to run on every render
    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    // tries to place a block
    public void placeBlock(){
        Point3D loc;
        Chunk chunk;
        gameObject object;
        Vector normForce;
        for (int i = 0; i < handler.object.size(); i++){ // loops through every game object
            object = handler.object.get(i);
            if (object.getMesh() != null){
                for (Face face: object.getMesh().faces){
                    normForce = face.intersects(this.camera.coords, this.camera.norm.mul(REACH), object.coords); // checks if anything intersects the ray from the players head to where they are pointing
                    if (normForce != null && object.id == ID.Chunk){ // if there's a normal force, we hit something
                        chunk = (Chunk) object;
                        loc = face.centre.add(normForce.fastNormalize(0.8));
                        loc = new Point3D(Math.floor(loc.x), Math.floor(loc.y), Math.ceil(loc.z));
                        chunk.blocks.put(loc, new Stone(loc, ID.Stone));
                        chunk.updateChunk = 1;
                    }
                }
            }
        }
    }

    // checks if there's a block to be removed
    public void removeBlock(){
        Point3D loc;
        Chunk chunk;
        gameObject object;
        Vector normForce;
        for (int i = 0; i < handler.object.size(); i++){ // goes through every game object
            object = handler.object.get(i);
            if (object.getMesh() != null){
                for (Face face: object.getMesh().faces){
                    normForce = face.intersects(this.camera.coords, this.camera.norm.mul(REACH), object.coords);
                    if (normForce != null && object.id == ID.Chunk){ // if there's a normal force, and it's a chunk, then we hit a block
                        chunk = (Chunk) object;
                        loc = face.centre.add(normForce.fastNormalize(-0.8));
                        loc = new Point3D(Math.floor(loc.x), Math.floor(loc.y), Math.ceil(loc.z));
                        chunk.blocks.remove(loc); // removes the block at the location we hit
                        chunk.updateChunk = 1;
                    }
                }
            }
        }
    }

    // checks if the player collides with any objects
    private boolean checkCollision(){
        // loops through every game object
        for (int i = 0; i < handler.object.size(); i++){
            gameObject object = handler.object.get(i);
            if (object.getMesh() != null){ // if the object has a mesh
                for (Face face: object.getMesh().faces){
                    Vector normForce = face.intersects(this.coords, vel, object.coords); // the normal force
                    if (normForce != null){ // if there is a normal force, the player collided
                        addForce(normForce);
                        return checkCollision();
                    }
                }
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

        updateRot(); // updates the rotation of the player
    }


    // returns null for the colour
    public Color[] getColor(){
        return null;
    }
}