// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Point3D Data Structure, which keep tracks of all points within the 3d space

package main;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;

// Point3D class
public class Point3D implements Cloneable {
    public double x;
    public double y;
    public double z;

    public static Point3D zero = new Point3D(0, 0, 0);

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

    // finds the magnitude of a vector between two inputs
    public static double mag(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
    }

    // Getters and Setters
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

    // Adds to a specific direction

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void addX(double x) {
        this.x += x;
    }

    // Changes the location of the point

    public void addY(double y) {
        this.y += y;
    }

    public void addZ(double z) {
        this.z += z;
    }

    public void setLocation(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    public double distanceSq(double px, double py, double pz) {
        return Point3D.distanceSq(getX(), getY(), getZ(), px, py, pz);
    }

    public double distanceSq(Point3D p) {
        return Point3D.distanceSq(getX(), getY(), getZ(), p.getX(), p.getY(), p.getZ());
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
        l *= 31 ^ java.lang.Double.doubleToLongBits(getY());
        l *= 31 ^ java.lang.Double.doubleToLongBits(getX());
        return (int) ((l >> 31) ^ l);
    }


    // Defines the equal function for Point3D
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Point3D p) {
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
    public Point3D add(Point3D p) { return new Point3D(getX() + p.x, getY() + p.y, getZ() + p.z); }

    // Maps a point onto the camera
    public Vector screenOrthoCoordinates(Camera cam, Vector focalPoint, int cos, int tan) {
        final double ROTATION_LIMIT = Math.PI/2.0;
        Vector norm = cam.getNorm();
        Vector vector2cam = focalPoint.subtract(this);
        double angle = vector2cam.diffAngles(norm);
        if (angle > ROTATION_LIMIT){
            return null;
        }

        // Experiment with adding trigonometric functions to the projection
        for (int i = 0; i < cos; i++) angle = Math.cos(angle);
        for (int i = 0; i < tan; i++) angle = Math.tan(angle);

//        double length = 99999999;
//        // length of the vector to reach the screen plane
//        // In case the angle is 0
//        if (angle != 0) {
//            length = focalLength / Math.cos(Math.PI - angle);
//        }

        // Rotates the vector to the camera towards the screen
        vector2cam = cam.getRot().rotateVector(vector2cam, true);
        vector2cam.setX(0);

        return vector2cam.normalize(angle * cam.getFocalLength() * 5000);
    }

    public static Vector[] screenOrthoCoordinatesTotal(Camera cam, int points, Vector focalPoint, int hash, HashMap<String, ArrayGPU> gpu) {
        final double ROTATION_LIMIT = Math.PI/2.0;
        Vector norm = cam.getNorm();
        float[] focal = focalPoint.toFloat();
        Quaternion realign = cam.getRot();
        Vector[] output = new Vector[points/3];
        float[] vectors = gpu.get("Sub").runProgram(points, new float[] {
                (float) norm.x,
                (float) norm.y,
                (float) norm.z,
                (float) Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z),
                focal[0],
                focal[1],
                focal[2],
                (float) ((-1) * realign.x),
                (float) ((-1) * realign.y),
                (float) ((-1) * realign.z),
                (float) realign.w,
                (float) (realign.w * realign.w - (realign.x * realign.x + realign.y * realign.y + realign.z * realign.z))
        }, points/3, hash);

        for (int i = 0; i < points/3; i++) {
            if (vectors[i * 3] < ROTATION_LIMIT) {
                output[i] = new Vector(0, vectors[i * 3 + 1], vectors[i * 3 + 2]).normalize(vectors[i * 3] * cam.getFocalLength() * 8192);
            }
        }
        return output;
    }

    public static float[] toFloat(Point3D[] points){
        float[] output = new float[points.length * 3];
        for(int i = 0; i < points.length; i++){
            output[i * 3] = (float) points[i].getX();
            output[i * 3 + 1] = (float) points[i].getY();
            output[i * 3 + 2] = (float) points[i].getZ();
        }
        return output;
    }

    // Turns a point into a Vector from the origin
    public Vector toVect() {
        return new Vector(getX(), getY(), getZ());
    }

    public float[] toFloat() {
        return new float[] {(float) getX(), (float) getY(), (float) getZ()};
    }

}
