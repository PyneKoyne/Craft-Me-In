// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the GameObject Data Structure, which creates a structure for all game objects

package main;

import java.awt.*;

// Abstract class to define game objects such as Camera and CUbe
public abstract class gameObject {

	// Default Variables
	protected Point3D coords;
	protected Vector vel;
	protected double roll, pitch, yaw;
	protected Vector norm;
	protected Vector up;
	protected ID id;
	protected Mesh mesh;

	// Average game object constructor
	public gameObject(Point3D cos, Vector rot, ID id) {
		this.roll = rot.getX();
		this.pitch = rot.getY();
		this.yaw = rot.getZ();
		
		norm = Quaternion.rotateVectorByEuclid(new Vector(1, 0, 0), rot);
		coords = cos;
		this.vel = new Vector(0, 0, 0);
		this.id = id;
	}

	// requires a tick and render method in every game object
	public abstract void tick();

	public abstract void render(Graphics g);

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
	
	public void setMesh(Mesh mesh) {
		this.mesh = mesh;
	}
	
	public Mesh getMesh() {
		return mesh;
	}
	
	public Point3D getLocation() {
		return coords;
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

	public void setId(ID id) {
		this.id = id;
	}

	public ID getid() {
		return id;
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

	public void setUp(Vector up){
		this.up = up;
	}

	public void setRot(Vector rot) {
		// Rotations over 360 degrees are modul-ised
		this.roll = rot.getX() % (2 * Math.PI);
		this.pitch = rot.getY() % (2 * Math.PI);
		this.yaw = rot.getZ() % (2 * Math.PI);

		// Sets the norm when the rotation is set as well
		this.setNorm(Quaternion.rotateVectorByEuclid(Vector.i, rot));

		// Sets the up vector when the rotation vector is set
		this.setUp(Quaternion.rotateVectorByEuclid(Vector.k, rot));
	}
	
	public void setRoll(double roll) {
		this.roll = roll;
	}
	
	public void setPitch(double pitch) {
		this.pitch = pitch;
	}
	
	public void setYaw(double yaw) {
		this.yaw = yaw;
	}

	// adds acceleration
	public void addForce(Vector force) {
		setVelX(getVelX() + force.getX());
		setVelY(getVelY() + force.getY());
		setVelZ(getVelZ() + force.getZ());
	}
	
	public Vector getRot() {
		return new Vector(this.roll, this.pitch, this.yaw);
	}
}