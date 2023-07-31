// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the Face Data Structure, which comprises a mesh

package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Face implements Cloneable {

	// Vertices
	public int[] verts;

	// Constructor
	public Face(int[] verts) {
		this.verts = verts;
	}

	// Get Vert methods
	public int[] getVerts() {
		return verts;
	}

	public Point3D getVert(Point3D[] vertices, int n) {
		return vertices[verts[n]];
	}
	
	public int getVert(int n) {
		return verts[n];
	}

	// Methods to add vertices
	public void addVert(int vert, int index) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		for (int i = 0, j = 0; i < temp_verts.length + 1; i++, j++) {
			if (i == index) {
				verts[j] = vert;
				i++;
			}
			verts[j] = temp_verts[i];
		}
	}

	public void addVert(int vert) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		System.arraycopy(temp_verts, 0, verts, 0, temp_verts.length);
		verts[temp_verts.length] = vert;
	}

	public void addVerts(int[] new_verts) {
		int[] temp_verts = verts;
		verts = new int[temp_verts.length + 1];

		System.arraycopy(temp_verts, 0, verts, 0, temp_verts.length);

		if (new_verts.length - 1 >= 0)
			System.arraycopy(new_verts, 0, verts, temp_verts.length - 1, new_verts.length - 1);
	}

	// Number of vertices
	public int length() {
		return verts.length;
	}

	@Override
	public String toString() {
		return getClass().getName() + "[" + verts + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	// Draw all points within a face given vertices
	// Currently only draws a plane, using the difference between the first 3 vertices as unit vectors
	public ArrayList<Point3D> drawFace(Point3D[] vertices) {
		ArrayList<Point3D> Points = new ArrayList<Point3D>();
		Vector unitX;
		Vector unitY;
		unitX = vertices[verts[0]].subtract(vertices[verts[1]]).normalize(0.45);
		unitY = vertices[verts[0]].subtract(vertices[verts[3]]).normalize(0.45);

		for (int i = -50; i < 50; i++) {
			for (int j = -50; j < 50; j++) {
				Points.add(vertices[verts[0]].add(unitX.mul(i)).add(unitY.mul(j)));
			}
		}

		return Points;
	}

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
