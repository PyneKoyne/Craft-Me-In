// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class creates the GameObject Data Structure, which creates a structure for all game objects

package main;

// imports
import java.awt.*;

// Abstract class to define game objects such as Camera and CUbe
public abstract class gameObject {

    // Default Variables for a game object
    protected Point3D coords;
    protected Vector vel;
    protected double roll, pitch, yaw;
    protected Quaternion rot;
    protected Vector norm;
    protected Vector up;
    protected Vector left;
    protected ID id;
    protected int hash = System.identityHashCode(this);
    protected Mesh mesh;

    // Average game object constructor
    public gameObject(Point3D coords, Vector rot, ID id) {
        if (rot != null) { // if no rotation is given, doesn't create the vectors
            setRot(rot);
        }
        if (coords != null) { // if no coordinates are given
            this.coords = coords;
            this.vel = Vector.zero;
        }
        this.id = id;
    }

    // requires a tick method in every game object
    public abstract void tick();

    // requires a render method in every game object to help with rendering
    public abstract void render(Graphics g, ArrayGPU[] gpu);

    // Getters and Setters for x, y, and z coordinates
    public void setX(double x) {
        this.coords.setX(x);
    }
    public void setY(double y) {
        this.coords.setY(y);
    }
    public void setZ(double z) {
        this.coords.setZ(z);
    }
    public double getX() {
        return coords.getX();
    }
    public double getY() {
        return coords.getY();
    }
    public double getZ() {
        return coords.getZ();
    }

    // gets the location of the game object
    public Point3D getLocation() {
        return coords;
    }

    // sets the mesh
    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    // gets the mesh
    public Mesh getMesh() {
        return mesh;
    }

    // sets the id
    public void setId(ID id) {
        this.id = id;
    }

    // gets the id
    public ID getId() {
        return id;
    }

    // gets the hash of the game object
    public int getHash() {
        return hash;
    }

    // sets the velocity of the game object
    public void setVel(Vector vel) {
        this.vel.setX(vel.getX());
        this.vel.setY(vel.getY());
        this.vel.setZ(vel.getZ());
    }

    // gets and sets the x, y, and z components of the velocity
    public void setVelX(double velX) {
        this.vel.setX(velX);
    }
    public void setVelY(double velY) {
        this.vel.setY(velY);
    }
    public void setVelZ(double velZ) {
        this.vel.setZ(velZ);
    }
    public double getVelX() {
        return vel.getX();
    }
    public double getVelY() {
        return vel.getY();
    }
    public double getVelZ() {
        return vel.getZ();
    }

    // gets the normal of the game object
    public Vector getNorm() {
        return norm;
    }

    // sets the normal of the game object
    public void setNorm(Vector norm) {
        this.norm = norm;
    }

    // gets the up vector of the game object
    public Vector getUp() {
        return up;
    }

    // sets the up vector of the game object
    public void setUp(Vector up) {
        this.up = up;
    }

    // gets the left vector of the game object
    public Vector getLeft() {
        return left;
    }

    // sets the left vector of the game object
    public void setLeft(Vector left) {
        this.left = left;
    }

    // sets the rotation of the game object
    public void setRot(Vector rot) {
        // Rotations over 360 degrees are modul-ised
        this.roll = rot.getX() % (2 * Math.PI);
        this.pitch = rot.getY() % (2 * Math.PI);
        this.yaw = rot.getZ() % (2 * Math.PI);

        updateRot();
    }

    // updates the rotation of the game object
    protected void updateRot() {
        this.rot = new Quaternion(this.roll, this.pitch, this.yaw);

        // Sets the norm, up, and right vectors when the rotation is set as well
        this.setNorm(this.rot.rotateVector(Vector.i, false));
        this.setUp(this.rot.rotateVector(Vector.k, false));
        this.setLeft(this.rot.rotateVector(Vector.j, false));
    }

    // gets the euler angles
    public Vector getAngles() {
        return new Vector(this.roll, this.pitch, this.yaw);
    }

    // gets the colors
    protected Color[] getColor(int orientation) {
        return null;
    }

    // gets the integer rgb colours of the mesh
    protected int[] getMeshColor() {
        return null;
    }

    // adds acceleration
    public void addForce(Vector force) {
        setVelX(getVelX() + force.getX());
        setVelY(getVelY() + force.getY());
        setVelZ(getVelZ() + force.getZ());
    }
}