// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.awt.*;
import java.util.ArrayList;

// Mesh object class which is used to display game objects
public class Mesh {
	// Variables
	public ArrayList<Point3D> verts, mesh; // the vertices and mesh
	public ArrayList<short[]> faceVerts; // the vertex tuples
	public ArrayList<Color> rawColors; // face colours
	public ArrayList<Integer> colors; // int colours
	public int points; // number of points within the raw mesh
	public ArrayList<Face> faces; // the actual face objects
	public float[] rawMesh; // the raw mesh to be displayed

	// Defines the mesh based on the given parameters
	public Mesh(ArrayList<Point3D> vertices, ArrayList<short[]> faceStructure, ArrayList<Color> colors) {
		this.mesh = new ArrayList<>();
		this.faces = new ArrayList<>();
		this.rawColors = colors;
		this.colors = new ArrayList<>();
		setMesh(vertices, faceStructure); // generates the mesh from the given variables
	}

	// empty constructor
	public Mesh(){
		this.verts = new ArrayList<>();
		this.faceVerts = new ArrayList<>();
		this.mesh = new ArrayList<>();
		this.rawColors = new ArrayList<>();
		this.colors = new ArrayList<>();
		this.faces = new ArrayList<>();
	}

	// sets the faces from the face verts
	public void setFaces(){
		for (short[] face : this.faceVerts) {
			faces.add(new Face(face));
		}
	}

	// sets the main variables of the mesh and draws each point
	public void setMesh(ArrayList<Point3D> vertices, ArrayList<short[]> faceStructure) {
		this.verts = vertices;
		for (short[] face : faceStructure) {
			faces.add(new Face(face));
		}
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0]))); // sets the raw mesh
	}

	// draws each point with the current state of the mesh
	public void setMesh(){
		for (short[] face : this.faceVerts) {
			faces.add(new Face(face));
		}
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0]))); // sets the raw mesh
	}

	// Creates the total mesh by drawing each face
	public ArrayList<Point3D> createMesh() {
		// converts the flexible vertices ArrayList into a normal array
		mesh = new ArrayList<>();
		colors = new ArrayList<>();

		// goes through each face and sets their normal and centers
		for (Face face: faces){
			face.setFace(verts);
		}
		for (int i = 0; i < faces.size(); i ++) { // checks if any faces are overlapping
			int cnt = 0;
			for (Face face2: faces){
				if (faces.get(i).equals(face2)) cnt++;
			}
			if (cnt < 2) {
                // adds the points from the face to the mesh
                mesh.addAll(faces.get(i).drawFace(colors, rawColors.get(i)));
			}
		}
		this.faceVerts = new ArrayList<>(); // resets face verts to free memory
		return mesh;
	}

	// sets the raw mesh and points field of the object
	public void setRawMesh(float[] rawMesh) {
		this.rawMesh = rawMesh;
		this.points = rawMesh.length;
	}
}
