// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.util.ArrayList;
import java.util.Arrays;

public class Mesh {
	// Variables
	public ArrayList<Point3D> vertices;
	public int points;
	public ArrayList<Face> faces = new ArrayList<>();
	public ArrayList<Point3D> mesh = new ArrayList<Point3D>();
	public float[] rawMesh;

	// Defines the mesh based on the given parameters
	public Mesh(ArrayList<Point3D> vertices, ArrayList<int[]> faceStructure) {
		this.vertices = vertices;

        for (int[] face : faceStructure) {
            faces.add(new Face(face));
        }
	}

	// Creates the total mesh by drawing each face
	public void createMesh() {
		Point3D[] tempVertices = new Point3D[vertices.size()];
		for (int i = 0; i < vertices.size(); i++){
			tempVertices[i] = vertices.get(i);
		}

		for (Face face : faces) {
			mesh.addAll(face.drawFace(tempVertices));
		}
		rawMesh = Point3D.toFloat(mesh.toArray(new Point3D[0]));
		points = rawMesh.length;
	}

	// returns the total list of points
	public Point3D[] getPoints() {
		Point3D[] arr = new Point3D[mesh.size()];
		return mesh.toArray(arr);
	}
}
