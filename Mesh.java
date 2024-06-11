// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.util.ArrayList;

// Mesh object class which is used to display game objects
public class Mesh {
	// Variables
	public ArrayList<Point3D> verts;
	public ArrayList<int[]> faceVerts;
	ArrayList<Point3D> mesh;
	public int points, count;
	public ArrayList<Face> faces = new ArrayList<>();
	public float[] rawMesh;

	// Defines the mesh based on the given parameters
	public Mesh(ArrayList<Point3D> vertices, ArrayList<int[]> faceStructure) {
		mesh = new ArrayList<>();
		setMesh(vertices, faceStructure);
	}

	// empty constructor
	public Mesh(){
		verts = new ArrayList<>();
		faceVerts = new ArrayList<>();
		mesh = new ArrayList<>();
		count = 0;
	}

	// sets the faces from the face verts
	public void setFaces(){
		for (int[] face : this.faceVerts) {
			faces.add(new Face(face));
		}
	}

	// sets the main variables of the mesh and draws each point
	public void setMesh(ArrayList<Point3D> vertices, ArrayList<int[]> faceStructure) {
		this.verts = vertices;
		for (int[] face : faceStructure) {
			faces.add(new Face(face));
		}
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0])));
	}

	// draws each point with the current state of the mesh
	public void setMesh(){
		for (int[] face : this.faceVerts) {
			faces.add(new Face(face));
		}
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0])));
	}

	// Creates the total mesh by drawing each face
	public ArrayList<Point3D> createMesh() {
		// converts the flexible vertices ArrayList into a normal array
		Point3D[] meshVertices = new Point3D[verts.size()];
		for (int i = 0; i < verts.size(); i++){
			meshVertices[i] = verts.get(i);
		}

		// goes through each face and sets their normal and centers
		for (Face face: faces){
			face.setFace(meshVertices);
		}
		for (Face face : faces) { // checks if any faces are overlapping
			int cnt = 0;
			for (Face face2: faces){
				if (face.equals(face2)) cnt++;
			}
			if (cnt < 2) mesh.addAll(face.drawFace()); // adds the points from the face to the mesh
		}
		return mesh;
	}

	// sets the raw mesh and points field of the object
	public void setRawMesh(float[] rawMesh) {
		this.rawMesh = rawMesh;
		this.points = rawMesh.length;
	}
}
