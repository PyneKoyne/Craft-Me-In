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
	public Vector norm;
	public Point3D centre;
	private static final double LENGTH = 0.1; // constant Length


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

	// Draw all points within a face given vertices
	// Currently only draws Quadrilaterals
	public ArrayList<Point3D> drawFace(Point3D[] vertices) {
		ArrayList<Point3D> Points = new ArrayList<Point3D>();
		Vector x, y;

		// Draws the two diagonals
		Vector diagX, diagY;
		diagX = vertices[verts[2]].subtract(vertices[verts[0]]).mul(.02);
		diagY = vertices[verts[3]].subtract(vertices[verts[1]]).mul(.02);

		// draws all the points between both diagonals
		for (int i = 0; i <= 50; i += 1){
			Vector line = vertices[verts[3]].add(diagY.mul(i)).subtract(vertices[verts[2]].add(diagX.mul(i)));
			for (double j = 0; j <= line.mag(); j += LENGTH){
				Points.add(vertices[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
			}
		}

		// draws all the points between the two diagonals in the other way
		for (int i = 0; i <= 50; i += 1){
			Vector line = vertices[verts[3]].add(diagY.mul(i)).subtract(vertices[verts[0]].add(diagX.mul(-i)));
			for (double j = 0; j < line.mag() + LENGTH/2; j += LENGTH){
				Points.add(vertices[verts[3]].add(diagY.mul(i)).add(line.fastNormalize(j)));
			}
		}

		// finds the normal and centre of the face
		x = vertices[verts[3]].subtract(vertices[verts[0]]).add(vertices[verts[2]].subtract(vertices[verts[1]]));
		y = vertices[verts[3]].subtract(vertices[verts[2]]).add(vertices[verts[0]].subtract(vertices[verts[1]]));

		this.norm = x.crossProd(y).normalize();
		this.centre = new Point3D(
				(vertices[verts[0]].x + vertices[verts[1]].x + vertices[verts[2]].x + vertices[verts[3]].x)/4,
				(vertices[verts[0]].y + vertices[verts[1]].y + vertices[verts[2]].y + vertices[verts[3]].y)/4,
				(vertices[verts[0]].z + vertices[verts[1]].z + vertices[verts[2]].z + vertices[verts[3]].z)/4
		);

		return Points;
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
			return Arrays.equals(verts, p.getVerts());
		}
		return false;
	}
}
