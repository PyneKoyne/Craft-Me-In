// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

public class Mesh {
	// Variables
	public Point3D[] vertices;
	public int points;
	public HashMap<Integer, ArrayList<Integer>> edges = new HashMap<Integer, ArrayList<Integer>>();
	public Face[] faces;
	public ArrayList<Point3D> mesh = new ArrayList<Point3D>();
	public float[] rawMesh;

	// Defines the mesh based on the given parameters
	public Mesh(Point3D[] vertices, int[][] faceStructure) {
		this.vertices = vertices;
		Face[] faces = new Face[faceStructure.length];

		for (int i = 0; i < faceStructure.length; i++){
			faces[i] = new Face(faceStructure[i]);
		}
		this.faces = faces;

//		for (Point edge : edges) {
//			if (this.edges.get(edge.x) != null) {
//				this.edges.get(edge.x).add(edge.y);
//			} else {
//				this.edges.put(edge.x, new ArrayList<>());
//			}
//
//			if (this.edges.get(edge.y) != null) {
//				this.edges.get(edge.y).add(edge.x);
//			} else {
//				this.edges.put(edge.y, new ArrayList<>());
//			}
//		}
	}

	// Creates the total mesh by drawing each face
	public void createMesh() {
		for (Face face : faces) {
			for (Point3D p : face.drawFace(vertices)) {
				mesh.add(p);
				System.out.println(p);
			}
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
