// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.util.ArrayList;

// Mesh object class which is used to display game objects
public class Mesh {
	// Variables
	public ArrayList<Point3D> vertices, mesh = new ArrayList<Point3D>();
	public int points;
	public ArrayList<Face> faces = new ArrayList<>();
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
		// converts the flexible vertices ArrayList into a normal array
		Point3D[] meshVertices = new Point3D[vertices.size()];
		for (int i = 0; i < vertices.size(); i++){
			meshVertices[i] = vertices.get(i);
		}

		// goes through each face and finds the points
		for (Face face : faces) {
			mesh.addAll(face.drawFace(meshVertices));
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
