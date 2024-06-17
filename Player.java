// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This is the Player class, creating a game object which acts as the player

package main;

// imports
import java.awt.*;
import java.util.ArrayList;

// Player object class
public class Player extends gameObject{
    // variables
    private final Handler handler;
    public boolean canJump = false;
    private final Camera camera; // has a camera object as a child
    private static final Vector CAMERA_OFFSET = new Vector(0, 0, 2);
    private static final double REACH = 5; // the reach of the player to add and remove blocks
    private final Window window;
    public boolean[] movement = {false, false, false, false, false}; // 4 movement vectors of the player
    private boolean locked = true; // if the camera is locked
    private boolean survival;
    private ID currentObject = ID.Stone;

    // player constructor which extends game object
    public Player(Point3D p, ID id, Handler handler, Window window, boolean survival){
        super(p, new Vector(Math.PI, 0, 0), id);
        this.handler = handler;
        this.window = window;
        this.survival = survival;
        // creates a new camera
        this.camera = new Camera(p, 0.09, ID.Camera, handler, window, survival);
        this.camera.pitch = Math.PI/2;
        handler.addObject(this.camera);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        // moves the player
        canJump = false;
        if (movement[0]) addForce(norm.mul(0.05));
        if (movement[1]) addForce(left.mul(-0.05));
        if (movement[2]) addForce(norm.mul(-0.05));
        if (movement[3]) addForce(left.mul(0.05));
        // checks for collisions
        if (this.survival) {
            checkCollision();
            if (movement[4] && canJump) addForce(Vector.k.mul(1.50)); // only one jump for survival
        }else{
            if (movement[4]) addForce(Vector.k.mul(0.25)); // if it's creative mode, allows for flying
            checkCollision();
        }

        // updates velocity and coordinates
        if (this.vel.mag() < 0.01) this.vel = Vector.zero;
        this.coords = this.coords.add(this.vel);
        this.vel = this.vel.mul(0.5);
        this.addForce(new Vector(0, 0, -0.05)); // gravity

        if (this.coords.z < -20){
            this.coords = new Point3D(10, 10, 30);
        }

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
        ArrayList<Point3D> loc = new ArrayList<>();
        Point3D tempLoc;
        Chunk chunk;
        gameObject object;
        Vector normForce;
        double mag = (REACH + 2);
        int index = 0;
        for (int i = 0; i < handler.object.size(); i++){ // loops through every game object
            object = handler.object.get(i);
            if (object.getMesh() != null && object.id == ID.Chunk){
                chunk = (Chunk) object;
                for (Face face: object.getMesh().faces){
                    normForce = face.intersects(this.camera.coords, this.camera.norm.mul(REACH), object.coords); // checks if anything intersects the ray from the players head to where they are pointing
                    if (normForce != null){ // if there's a normal force, we hit something
                        tempLoc = face.centre.add(normForce.fastNormalize(0.8));
                        if (tempLoc.add(chunk.coords).subtract(this.coords).mag() > 1 && tempLoc.z < Chunk.HEIGHT_MAX) {
                            loc.add(new Point3D(Math.floor(tempLoc.x), Math.floor(tempLoc.y), Math.ceil(tempLoc.z)));
                        }
                    }
                }
                for (int j = 0; j < loc.size(); j++){
                    if (mag > Math.sqrt(loc.get(j).distanceSq(Point3D.zero))){
                        mag = Math.sqrt(loc.get(j).distanceSq(Point3D.zero));
                        index = j;
                    }
                }
                if (!loc.isEmpty()) {
                    if (currentObject == ID.Stone) {
                        chunk.blocks.put(loc.get(index), new Stone(loc.get(index), currentObject));
                    }
                    else if (currentObject == ID.Dirt){
                        chunk.blocks.put(loc.get(index), new Dirt(loc.get(index), currentObject));
                    }
                    chunk.updateChunk = 1;
                    return;
                }
            }
        }
    }

    // checks if there's a block to be removed
    public void removeBlock(){
        ArrayList<Point3D> loc = new ArrayList<>();
        Point3D tempLoc;
        Chunk chunk;
        gameObject object;
        Vector normForce;
        double mag = (REACH + 2);
        int index = 0;
        for (int i = 0; i < handler.object.size(); i++){ // goes through every game object
            object = handler.object.get(i);
            if (object.getMesh() != null && object.id == ID.Chunk){
                chunk = (Chunk) object;
                for (Face face: object.getMesh().faces){
                    normForce = face.intersects(this.camera.coords, this.camera.norm.mul(REACH), object.coords);
                    if (normForce != null){ // if there's a normal force, and it's a chunk, then we hit a block
                        tempLoc = face.centre.add(normForce.fastNormalize(-0.8));
                        loc.add(new Point3D(Math.floor(tempLoc.x), Math.floor(tempLoc.y), Math.ceil(tempLoc.z)));
                    }
                }
                for (int j = 0; j < loc.size(); j++){
                    if (mag > Math.sqrt(loc.get(j).distanceSq(Point3D.zero))){
                        mag = Math.sqrt(loc.get(j).distanceSq(Point3D.zero));
                        index = j;
                    }
                }
                if (!loc.isEmpty()) {
                    if (chunk.blocks.containsKey(loc.get(index))) {
                        currentObject = chunk.blocks.get(loc.get(index)).id;
                        chunk.blocks.remove(loc.get(index)); // removes the block at the location we hit
                        chunk.updateChunk = 1;
                    }
                    return;
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
                        canJump = true;
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