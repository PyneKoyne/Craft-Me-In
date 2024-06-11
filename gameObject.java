// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class creates the GameObject Data Structure, which creates a structure for all game objects

package main;

// imports
import java.awt.*;

// Abstract class to define game objects such as Camera and CUbe
public abstract class gameObject {

    // Default Variables
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
        if (rot != null) {
            setRot(rot);
        }
        if (coords != null) {
            this.coords = coords;
            this.vel = new Vector(0, 0, 0);
        }
        this.id = id;
    }

    // requires a tick and render method in every game object
    public abstract void tick();

    public abstract void render(Graphics g, ArrayGPU[] gpu);

    // Getters and Setters
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

    public void setLocation(Vector loc) {
        this.coords.setX(loc.x);
        this.coords.setY(loc.y);
        this.coords.setZ(loc.z);
    }

    public Point3D getLocation() {
        return coords;
    }

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public void setId(ID id) {
        this.id = id;
    }

    public ID getid() {
        return id;
    }

    public int getHash() {
        return hash;
    }

    public void setVel(Vector vel) {
        this.vel.setX(vel.getX());
        this.vel.setY(vel.getY());
        this.vel.setZ(vel.getZ());
    }

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

    public Vector getNorm() {
        return norm;
    }

    public void setNorm(Vector norm) {
        this.norm = norm;
    }

    public Vector getUp() {
        return up;
    }

    public void setUp(Vector up) {
        this.up = up;
    }

    public Vector getLeft() {
        return left;
    }

    public void setLeft(Vector left) {
        this.left = left;
    }

    public void setRot(Vector rot) {
        // Rotations over 360 degrees are modul-ised
        this.roll = rot.getX() % (2 * Math.PI);
        this.pitch = rot.getY() % (2 * Math.PI);
        this.yaw = rot.getZ() % (2 * Math.PI);

        updateRot();
    }

    protected void updateRot() {
        this.rot = new Quaternion(this.roll, this.pitch, this.yaw);

        // Sets the norm, up, and right vectors when the rotation is set as well
        this.setNorm(this.rot.rotateVector(Vector.i, false));
        this.setUp(this.rot.rotateVector(Vector.k, false));
        this.setLeft(this.rot.rotateVector(Vector.j, false));
    }

    public void setRoll(double roll) {
        this.roll = roll;
        updateRot();
    }

    public void setPitch(double pitch) {
        this.pitch = pitch;
        updateRot();
    }

    public void setYaw(double yaw) {
        this.yaw = yaw;
        updateRot();
    }

    // adds acceleration
    public void addForce(Vector force) {
        setVelX(getVelX() + force.getX());
        setVelY(getVelY() + force.getY());
        setVelZ(getVelZ() + force.getZ());
    }

    public Vector getAngles() {
        return new Vector(this.roll, this.pitch, this.yaw);
    }

    public Quaternion getRot() {
        return rot;
    }

    protected Color[] getColor() {
        return null;
    }
}