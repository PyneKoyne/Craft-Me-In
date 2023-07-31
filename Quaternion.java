// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Quaternion Data Structure, which is really just for rotations

package main;

public class Quaternion implements Cloneable {
	public double a, b, c, d;

	// Constructs a normal Quaternion
	public Quaternion(double theta, double x, double y, double z) {
		this.a = Math.cos(theta/2);
		this.b = x * Math.sin(theta/2);
		this.c = y * Math.sin(theta/2);
		this.d = z * Math.sin(theta/2);
	}

	// Constructs a Quaternion from Euler Angles
	public Quaternion(double u, double v, double w) {
		this.a = Math.cos(u/2) * Math.cos(v/2) * Math.cos(w/2) + Math.sin(u/2) * Math.sin(v/2) * Math.sin(w/2);
		this.b = Math.sin(u/2) * Math.cos(v/2) * Math.cos(w/2) - Math.cos(u/2) * Math.sin(v/2) * Math.sin(w/2);
		this.c = Math.cos(u/2) * Math.sin(v/2) * Math.cos(w/2) + Math.sin(u/2) * Math.cos(v/2) * Math.sin(w/2);
		this.d = Math.cos(u/2) * Math.cos(v/2) * Math.sin(w/2) - Math.sin(u/2) * Math.sin(v/2) * Math.cos(w/2);
	}

	// Rotates a vector given a quaternion
	public Vector rotateVectorByQuat(Vector v)
	{
	    // Extract the vector part of the quaternion
	    Vector u = new Vector(b, c, d);

	    // Extract the scalar part of the quaternion
	    double s = a;

	    // Do the math
	    return(u.mul(2.0 * u.dotProd(v)).add(
	          v.mul(s*s - u.dotProd(v))).add( 
	          u.crossProd(v).mul(2.0f * s)));
	}

	// Rotates a Vector given Euler Angles
	public static Vector rotateVectorByEuclid(Vector v, Vector angles)
	{
		Quaternion q = new Quaternion(angles.getX(), angles.getY(), angles.getZ());

	    // Extract the vector part of the quaternion
	    Vector u = new Vector(q.b, q.c, q.d);
	    
	    // Extract the scalar part of the quaternion
	    double s = q.a;

	    // Do the math
	    return(u.mul(2.0 * u.dotProd(v)).add(
	          v.mul(s*s - u.dotProd(u))).add( 
	          u.crossProd(v).mul(2.0 * s)));
	}

	public void setLocation(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[a=" + a + ",b=" + b + ",c=" + c + "d=" + d + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	// Finds the magnitude of a quaternion
	public double mag(double a, double b, double c, double d) {
		return Math.sqrt(a * a + b * b + c * c + d * d);
	}

	// Finds the magnitude of the quaternion
	public double mag() {
		return Math.sqrt(a * a + b * b + c * c + d * d);
	}

	// Prints whether the magnitude of the Quaternion is 1
	public boolean isUnit() {
		return mag() == 1;
	}

	// Quaternion to angle
	public double quat2angle() {
		return 2 * Math.acos(a);
	}

	// returns a Quaternion with magnitude 1
	public Quaternion normalize() {
		double hyp = mag();

		return new Quaternion(a / hyp, b / hyp, c / hyp, d / hyp);
	}

	// returns the inverse of the Quaternion
	public Quaternion inv() {
		return new Quaternion(a, -b, -c, -d);
	}

	// Rotates a point based on a Quaternion
	public Point3D rotate(Quaternion rot, Point3D point) {
		Quaternion quatPoint = new Quaternion(0, point.getX(), point.getY(), point.getZ());
		Quaternion pPrime = rot.inv().mul(quatPoint).mul(rot);
		return new Point3D(pPrime.b, pPrime.c, pPrime.d);
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}


	@Override
	public int hashCode() {
		long l = java.lang.Double.doubleToLongBits(a);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(b);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(c);
		l = l * 31 ^ java.lang.Double.doubleToLongBits(d);
		return (int) ((l >> 31) ^ l);
	}

	// Defines the equal function for a Quaternion
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Quaternion) {
			Quaternion q = (Quaternion) obj;
			return a == q.a && b == q.b && c == q.c && d == q.d;
		}
		return false;
	}

	// Multiples a Quaternion with another Quaternion
	public Quaternion mul(Quaternion q) {
		double p0 = a * q.a - b * q.b - c * q.c - d * q.d;
		double p1 = a * q.b + b * q.a - c * q.d + d * q.c;
		double p2 = a * q.c + b * q.d + c * q.a - d * q.b;
		double p3 = a * q.d - b * q.c + c * q.b + d * q.a;
		return new Quaternion(p0, p1, p2, p3);
	}
}
