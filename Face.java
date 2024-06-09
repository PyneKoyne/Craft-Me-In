// Author: Kenny Z
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class creates the Face Data Structure, which a mesh is made out of

package main;

import java.util.ArrayList;
import java.util.Arrays;

// Face object which is a set of vertices which are connected together to form a surface
public class Face implements Cloneable {

    // Vertices
    private int[] verts;
    private Point3D[] vertPointers;
    public Vector norm;
    public Point3D centre;
    private static final double LENGTH = 0.05;// constant Length


    // Constructor
    public Face(int[] verts) {
        this.verts = verts;
    }

    // Get Vert methods
    public int[] getVerts() {
        return verts;
    }

    // To String method for debugging
    @Override
    public String toString() {
        return getClass().getName() + "[" + Arrays.toString(verts) + "]";
    }

    // clone method if necessary
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    public void setFace(Point3D[] vertices) {
        Vector x, y;

        // finds the normal and centre of the face
        x = vertices[verts[3]].subtract(vertices[verts[0]]).add(vertices[verts[2]].subtract(vertices[verts[1]]));
        y = vertices[verts[3]].subtract(vertices[verts[2]]).add(vertices[verts[0]].subtract(vertices[verts[1]]));

        this.norm = x.crossProd(y).normalize();
        this.centre = new Point3D(
                (vertices[verts[0]].x + vertices[verts[1]].x + vertices[verts[2]].x + vertices[verts[3]].x) / 4,
                (vertices[verts[0]].y + vertices[verts[1]].y + vertices[verts[2]].y + vertices[verts[3]].y) / 4,
                (vertices[verts[0]].z + vertices[verts[1]].z + vertices[verts[2]].z + vertices[verts[3]].z) / 4
        );

        vertPointers = vertices;
    }

    // Draw all points within a face given vertices
    // Currently only draws Quadrilaterals
    public ArrayList<Point3D> drawFace() {
        ArrayList<Point3D> Points = new ArrayList<>();

        // Draws the two diagonals
        Vector diagX, diagY;
        diagX = vertPointers[verts[2]].subtract(vertPointers[verts[0]]).mul(.1);
        diagY = vertPointers[verts[3]].subtract(vertPointers[verts[1]]).mul(.1);

        // draws all the points between both diagonals
        for (int i = 0; i <= 10; i += 1) {
            Vector line = vertPointers[verts[3]].add(diagY.mul(i)).subtract(vertPointers[verts[2]].add(diagX.mul(i)));
            for (double j = 0; j <= line.mag(); j += LENGTH) {
                Points.add(vertPointers[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
            }
        }

        // draws all the points between the two diagonals in the other way
        for (int i = 0; i <= 10; i += 1) {
            Vector line = vertPointers[verts[3]].add(diagY.mul(i)).subtract(vertPointers[verts[0]].add(diagX.mul(-i)));
            for (double j = 0; j < line.mag() + LENGTH / 4; j += LENGTH) {
                Points.add(vertPointers[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
            }
        }

        return Points;
    }

    public Vector intersects(Point3D tail, Vector ray, Point3D loc) {
        double ratio = norm.dotProd(tail.subtract(loc.add(this.centre))) / norm.dotProd(ray);
        if (ratio <= 0 || ratio >= 1 || Double.isNaN(ratio)) {
            return null;
        }
        Vector intersection = loc.add(centre).subtract(tail.add(ray.mul(ratio)));
//        double area = intersection.subtract(vertPointers[0]).crossProd(intersection.subtract(vertPointers[1])).mag() +
//                intersection.subtract(vertPointers[1]).crossProd(intersection.subtract(vertPointers[2])).mag() +
//                intersection.subtract(vertPointers[2]).crossProd(intersection.subtract(vertPointers[3])).mag() +
//                intersection.subtract(vertPointers[3]).crossProd(intersection.subtract(vertPointers[0])).mag();
//
//        double trueArea = vertPointers[1].subtract(vertPointers[0]).crossProd(vertPointers[1].subtract(vertPointers[3])).mag() +
//                vertPointers[2].subtract(vertPointers[1]).crossProd(vertPointers[2].subtract(vertPointers[3])).mag();
        if (Math.abs(intersection.x) <= 0.5 && Math.abs(intersection.y) <= 0.5 && Math.abs(intersection.z) <= 0.5) {
            if (norm.dotProd(ray) < 0) {
                return (norm.mul(-norm.dotProd(ray) / (norm.mag())));
            }
            if (norm.dotProd(ray) > 0) {
                return (norm.mul(norm.mul(-1).dotProd(ray) / (norm.mag())));
            }
        }
        return null;
    }

    // allows for hashing of the face
    @Override
    public int hashCode() {
        long l = verts[0];
        for (int i = 1; i < verts.length; i++) {
            l = l * 31 ^ verts[i];
        }
        return (int) ((l >> 31) ^ l);
    }

    // Defines the equal function for Faces
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Face) {
            Face p = (Face) obj;
            return p.norm.equals(this.norm) && p.centre.equals(this.centre) || p.norm.equals(this.norm.mul(-1)) && p.centre.equals(this.centre);
        }
        return false;
    }
}
