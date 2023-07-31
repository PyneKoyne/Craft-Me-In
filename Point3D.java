// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Point3D Data Structure, which keep tracks of all points within the 3d space

package main;

// Point3D class
public class Point3D implements Cloneable {
	public double x;
	public double y;
	public double z;

	// Constructs a Point3D
	public Point3D(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Getters and Setters
	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}

	// Adds to a specific direction
	
	public void addX(double x) {
		this.x += x;
	}
	
	public void addY(double y) {
		this.y += y;
	}
	
	public void addZ(double z) {
		this.z += z;
	}

	// Changes the location of the point

	public void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public String toString() {
		return "(" + x + ", " + y + ", " + z + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// Distance Squared with 2 points
	public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
		x2 -= x1;
		y2 -= y1;
		z2 -= z1;
		return x2 * x2 + y2 * y2 + z2 * z2;
	}

	public double distanceSq(double px, double py, double pz) {
		return Point3D.distanceSq(getX(), getY(), getZ(), px, py, pz);
	}

	public double distanceSq(Point3D p) {
		return Point3D.distanceSq(getX(), getY(), getZ(), p.getX(), p.getY(), p.getZ());
	}

	// finds the magnitude of a vector between two inputs
	public static double mag(double x1, double y1, double z1, double x2, double y2, double z2) {
		return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
	}

	public double mag(double px, double py, double pz) {
		return Math.sqrt(distanceSq(px, py, pz));
	}
	
	public Vector displacement(Point3D p1, Point3D p2) {
		return new Vector(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
	}

	// finds the distance between the current point and another Point3D
	public double mag(Point3D p) {
		return Math.sqrt(distanceSq(p));
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	// Hash code for a hash map

	@Override
	public int hashCode() {
		long l = java.lang.Double.doubleToLongBits(getZ());
		l = l * 31 ^ java.lang.Double.doubleToLongBits(getY());
		l = l * 31 ^ java.lang.Double.doubleToLongBits(getX());
		return (int) ((l >> 31) ^ l);
	}

	
	// Defines the equal function for Point3D
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Point3D) {
			Point3D p = (Point3D) obj;
			return getX() == p.getX() && getY() == p.getY() && getZ() == p.getZ();
		}
		return false;
	}

	// Creates a Vector from one point to another point
	public Vector subtract(Point3D p) {
		return new Vector(p.x - getX(), p.y - getY(), p.z - getZ());
	}

	// Adds a vector to a point creating another point
	public Point3D add(Vector v) {
		return new Point3D(getX() + v.x, getY() + v.y, getZ() + v.z);
	}

	// Maps a point onto the camera
	public Point3D screenOrthoCoordinates(Camera cam, Point3D focPoint, Vector norm, int cos, int tan) {
		Vector vect2cam = new Point3D(this.x, this.y, this.z).subtract(focPoint);
		double length = 99999999;
		double angle = vect2cam.diffAngles(norm);

		// Experiment with adding trigonometric functions to the projection
		for (int i = 0; i < cos; i ++){
			angle = Math.cos(angle);
		}

		for (int i = 0; i < tan; i ++){
			angle = Math.tan(angle);
		}

		// In case the angle is 0
		if (angle != 0) {
			length = cam.getFocal() / Math.cos(angle);
		}

		// Rotates the vector to the camera towards the screen
		vect2cam = vect2cam.normalize(length);
		vect2cam = Quaternion.rotateVectorByEuclid(vect2cam, cam.getRot().mul(-1));

		return vect2cam.toPoint();
	}

	// Turns a point into a Vector from the origin
	public Vector toVect() {
		return new Vector(getX(), getY(), getZ());
	}
	
}
