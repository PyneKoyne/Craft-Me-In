// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class creates the Vector Data Structure, which allows for vector algebra and point manipulation

package main;

// Vector Class
public class Vector implements Cloneable {
	public double x;
	public double y;
	public double z;

	// Common Vectors
	public static Vector i = new Vector(1, 0, 0);
	public static Vector j = new Vector(0, -1, 0);
	public static Vector k = new Vector(0, 0, 2);
	public static Vector zero = new Vector(0, 0, 0);

	// Constructs a Vector
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	// Constructs a vector from a float array
	public Vector(float[] v){
		this.x = v[0];
		this.y = v[1];
		this.z = v[2];
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

	// Prints a Vector
	@Override
	public String toString() {
		return getClass().getName() + "[x=" + x + ", y=" + y + ", z=" + z + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// finds the magnitude of the vector
	public double mag() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	// returns a vector with magnitude 1
	public Vector normalize() {
		double hyp = 1/mag();
		return new Vector(x * hyp, y * hyp, z * hyp);
	}

	// returns a vector with magnitude n
	public Vector fastNormalize(double n) {
		double square = Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2);
		double hyp = invSquare(square) * n;
		return new Vector(x * hyp, y * hyp, z * hyp);
	}

	// Inverse Square algorithm taken from Quake 3
	public double invSquare(double square){
		double half = 0.5d * square;
		long i = Double.doubleToLongBits(square);
		i = 0x5fe6ec85e7de30daL - (i >> 1);
		square = Double.longBitsToDouble(i);
		square *= (1.5d - half * square * square);

		return square;
	}

	// allows for cloning a vector
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
	public Vector rotateByEuclid(Vector angles)
	{
		Quaternion q = new Quaternion(angles.getX(), angles.getY(), angles.getZ());
		q.normalize();

		// Calculate Rotation
		return Quaternion.rotateVector(q, this);
	}

	// Finds the difference between two angles
	public double diffAngles(Vector vector) {
		double ratio = dotProd(vector);
		ratio /= (mag() * vector.mag());
		return Math.acos(ratio);
	}

	// Adds two vectors
	public Vector add(Vector v) {
		return new Vector(v.getX() + getX(), v.getY() + getY(), v.getZ() + getZ());
	}

	// Turns a Vector into a Point if the Vectors tail was at the origin
	public Point3D toPoint() {
		return new Point3D(getX(), getY(), getZ());
	}

	// converts a Vector into a float array
	public float[] toFloat() {
		return new float[]{(float) getX(), (float) getY(), (float) getZ()};
	}

	// subtracts a Vector from a Point3D to return a Vector
	public Vector subtract(Point3D p) {
		return new Vector(p.getX() - getX(), p.getY() - getY(), p.getZ() - getZ());
	}
}
