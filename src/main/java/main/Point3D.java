// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class creates the Point3D Data Structure, which keep tracks of all points within the 3d space

package main;

// Point3D class
public class Point3D implements Cloneable {
    // variables
    public double x;
    public double y;
    public double z;

    public static Point3D zero = new Point3D(0, 0, 0); // a static zeroed Point3D for reference

    // Constructs a Point3D
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    // Distance Squared with 2 points
    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        x2 -= x1;
        y2 -= y1;
        z2 -= z1;
        return x2 * x2 + y2 * y2 + z2 * z2;
    }

    // Getters and Setters for the x, y, and z coordinates
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    // To String Method to print a Point3D
    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    // finds the distance squared between the point and another coordinate tuple
    public double distanceSq(double px, double py, double pz) {
        return Point3D.distanceSq(getX(), getY(), getZ(), px, py, pz);
    }

    // finds the distance squared between the point and another point3D

    public double distanceSq(Point3D p) {
        return Point3D.distanceSq(getX(), getY(), getZ(), p.getX(), p.getY(), p.getZ());
    }


    // finds the displacement between the point and another coordinate tuple
    public Vector displacement(Point3D p1, Point3D p2) {
        return new Vector(p2.x - p1.x, p2.y - p1.y, p2.z - p1.z);
    }

    // finds the distance between the current point and another Point3D
    public double mag(Point3D p) {
        return Math.sqrt(distanceSq(p));
    }

    // allows a Point3D to be cloned
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
        long x = java.lang.Double.doubleToLongBits(getX());
        long y = java.lang.Double.doubleToLongBits(getY());
        long z = java.lang.Double.doubleToLongBits(getZ());
        return (int) (x + (y << 16) + (z << 32));
    }


    // Defines the equal function for Point3D
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Point3D) {
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
    // Adds a point to a point creating another point
    public Point3D add(Point3D p) { return new Point3D(getX() + p.x, getY() + p.y, getZ() + p.z); }

    // Turns a list of Point3Ds into a 1-dimensional float array
    public static float[] toFloat(Point3D[] points){
        float[] output = new float[points.length * 3];
        for(int i = 0; i < points.length; i++){
            output[i * 3] = (float) points[i].getX();
            output[i * 3 + 1] = (float) points[i].getY();
            output[i * 3 + 2] = (float) points[i].getZ();
        }
        return output;
    }

    // Turns a point into a Vector with its tail at the origin
    public Vector toVect() {
        return new Vector(getX(), getY(), getZ());
    }

    // turns a Point3D into a float array
    public float[] toFloat() {
        return new float[] {(float) getX(), (float) getY(), (float) getZ()};
    }
}
