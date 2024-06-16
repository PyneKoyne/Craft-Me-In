// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: This class creates the mesh data structure, which manages the 3d graphics portion of game objects

package main;

import java.awt.*;
import java.util.ArrayList;

// Mesh object class which is used to display game objects
public class Mesh {
	// Variables
	public ArrayList<Point3D> verts;
	public ArrayList<short[]> faceVerts;
	public ArrayList<Point3D> mesh;
	public ArrayList<Color> rawColors;
	public ArrayList<Integer> colors;
	public int points;
	public short count;
	public ArrayList<Face> faces;
	public float[] rawMesh;

	// Defines the mesh based on the given parameters
	public Mesh(ArrayList<Point3D> vertices, ArrayList<short[]> faceStructure, ArrayList<Color> colors) {
		this.mesh = new ArrayList<>();
		this.faces = new ArrayList<>();
		this.rawColors = colors;
		this.colors = new ArrayList<>();
		setMesh(vertices, faceStructure);
	}

	// empty constructor
	public Mesh(){
		this.verts = new ArrayList<>();
		this.faceVerts = new ArrayList<>();
		this.mesh = new ArrayList<>();
		this.rawColors = new ArrayList<>();
		this.colors = new ArrayList<>();
		this.faces = new ArrayList<>();
		this.count = 0;
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
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0])));
	}

	// draws each point with the current state of the mesh
	public void setMesh(){
		for (short[] face : this.faceVerts) {
			faces.add(new Face(face));
		}
		setRawMesh(Point3D.toFloat(createMesh().toArray(new Point3D[0])));
	}

	// Creates the total mesh by drawing each face
	public ArrayList<Point3D> createMesh() {
		// converts the flexible vertices ArrayList into a normal array
		mesh = new ArrayList<>();
		colors = new ArrayList<>();
		Point3D[] meshVertices = new Point3D[verts.size()];
		for (int i = 0; i < verts.size(); i++){
			meshVertices[i] = verts.get(i);
		}

		// goes through each face and sets their normal and centers
		for (Face face: faces){
			face.setFace(meshVertices);
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
		this.verts = new ArrayList<>();
		this.faceVerts = new ArrayList<>();
		this.count = 0;
		return mesh;
	}

	// sets the raw mesh and points field of the object
	public void setRawMesh(float[] rawMesh) {
		this.rawMesh = rawMesh;
		this.points = rawMesh.length;
	}
}
