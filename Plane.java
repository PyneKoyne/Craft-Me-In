// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: Creates a 3d plane on screen

package main;

import java.awt.*;
import java.util.ArrayList;

// creates a new plane object
public class Plane extends gameObject{
    private final Handler handler;
    private final Color color;

    // constructor of the plane class which extends game object
    public Plane(Point3D p, float scale, ID id, Handler handler, Color color){
        super(p, new Vector(1, 0, 0), id);
        this.handler = handler;

        // adds the vertices and faces of the object to render
        ArrayList<Point3D> verts = new ArrayList<>();
        verts.add(p);
        verts.add(p.add(new Vector(scale, 0, 0)));
        verts.add(p.add(new Vector(0, scale, 0)));
        verts.add(p.add(new Vector(scale, scale, 0)));

        ArrayList<int[]> faceVerts = new ArrayList<>();
        faceVerts.add(new int[]{0, 1, 3, 2});

        // creates the mesh to draw
        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.setMesh();

    }

    // any code which runs every tick
    public void tick() {
        coords.add(vel);
    }

    // any additional helper code to help render the object
    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}