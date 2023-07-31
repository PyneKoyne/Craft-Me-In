// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Vector Data Structure, which allows for vector algebra and point manipulation

package main;

public class Vector implements Cloneable {
	public double x;
	public double y;
	public double z;

	// Common Vectors
	public static Vector i = new Vector(1, 0, 0);
	public static Vector j = new Vector(0, 1, 0);
	public static Vector k = new Vector(0, 0, 1);
	public static Vector zero = new Vector(0, 0, 0);

	// Constructs a Vector
	public Vector(double x, double y, double z) {
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

	public void setLocation(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Prints a Vector
	@Override
	public String toString() {
		return getClass().getName() + "[x=" + x + ", y=" + y + ", z=" + z + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// finds the magnitude of a vector
	public double mag(double px, double py, double pz) {
		return Math.sqrt(px * px + py * py + pz * pz);
	}

	// finds the magnitude of the vector
	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	// returns a vector with magnitude 1
	public Vector normalize() {
		double hyp = 999999999;
		double magnitude = mag();
		if (magnitude != 0) {
			hyp = 1 / magnitude;
		}

		return new Vector(x * hyp, y * hyp, z * hyp);
	}

	// returns a vector with magnitude n
	public Vector normalize(double n) {
		double hyp = 999999999;
		double magnitude = mag();
		if (magnitude != 0) {
			hyp = n / magnitude;
		}

		return new Vector(x * hyp, y * hyp, z * hyp);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	// Returns a hashcode
	@Override
	public int hashCode() {
		long l = java.lang.Double.doubleToLongBits(getZ());
		l = l * 31 ^ java.lang.Double.doubleToLongBits(getY());
		l = l * 31 ^ java.lang.Double.doubleToLongBits(getX());
		return (int) ((l >> 31) ^ l);
	}

	// Defines the equal function for Vector
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Vector) {
			Vector p = (Vector) obj;
			return getX() == p.getX() && getY() == p.getY() && getZ() == p.getZ();
		}
		return false;
	}

	// Multiplies a Vector based on a number n
	public Vector mul(double n) {
		return new Vector(getX() * n, getY() * n, getZ() * n);
	}

	// Finds the dot product between two Vectors
	public double dotProd(Vector v) {
		double sum = 0;
		sum += getX() * v.getX();
		sum +=  getY() * v.getY();
		sum +=  getZ() * v.getZ();
		return sum;
	}

	// Finds the cross product between two Vectors
	public Vector crossProd(Vector v) {
		double x = getY() * v.getZ() - getZ() * v.getY();
		double y = getZ() * v.getX() - getX() * v.getZ();
		double z = getX() * v.getY() - getY() * v.getX();
		return new Vector(x, y, z);
	}

	// Rotates a Vector based on Euler Angles
	public Vector rotateVectorByEuclid(Vector angles)
	{
		Quaternion q = new Quaternion(angles.getX(), angles.getY(), angles.getZ());
		q.normalize();
	    // Extract the vector part of the quaternion
	    Vector u = new Vector(q.b, q.c, q.d);	

	    // Extract the scalar part of the quaternion
	    double s = q.a;

	    // Do the math
	    return(u.mul(2.0f * u.dotProd(this)).add(
	          this.mul(s*s - u.dotProd(u))).add( 
	          u.crossProd(this).mul(2.0f * s)));
	}

	// Finds the difference between two angles
	public double diffAngles(Vector v1) {
		double ratio = dotProd(v1);
		ratio /= (mag() * v1.mag());
		return Math.acos(ratio);
	}

	// Adds two vectors
	public Vector add(Vector v) {
		return new Vector(getX() + v.getX(), getY() + v.getY(), getZ() + v.getZ());
	}

	// Turns a Vector into a Point if the Vectors tail was at the origin
	public Point3D toPoint() {
		return new Point3D(getX(), getY(), getZ());
	}
}
