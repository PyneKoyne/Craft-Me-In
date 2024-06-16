// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: Creates a gameObject which has all the properties of a cube

package main;

import java.awt.*;
import java.util.ArrayList;

// Creates a Cube on screen
public class Cube extends gameObject {
    // Variables
    private final Handler handler;
    private final Color color;
    private int[] colors;

    // Cube constructor which extends game Object
    public Cube(Point3D p, float scale, ID id, Handler handler, Color color) {
        super(p, new Vector(0, 0, 0), id);
        ArrayList<Color> colors = new ArrayList<>();

        // adds the vertices and faces to the mesh
        ArrayList<Point3D> verts = new ArrayList<>();
        verts.add(Point3D.zero);
        verts.add(new Point3D(scale, 0, 0));
        verts.add(new Point3D(0, scale, 0));
        verts.add(new Point3D(scale, scale, 0));

        verts.add(new Point3D(0, 0, scale));
        verts.add(new Point3D(scale, 0, scale));
        verts.add(new Point3D(0, scale, scale));
        verts.add(new Point3D(scale, scale, scale));

        ArrayList<short[]> faceVerts = new ArrayList<>();
        faceVerts.add(new short[]{0, 1, 3, 2});
        faceVerts.add(new short[]{0, 4, 6, 2});
        faceVerts.add(new short[]{0, 1, 5, 4});
        faceVerts.add(new short[]{2, 6, 7, 3});
        faceVerts.add(new short[]{4, 5, 7, 6});
        faceVerts.add(new short[]{5, 7, 3, 1});

        this.color = color;
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);
        colors.add(color);

        // constructs the mesh
        this.mesh = new Mesh(verts, faceVerts, colors);
        this.colors = new int[this.mesh.colors.size()];
        for (int i = 0; i < this.mesh.colors.size(); i++){
            this.colors[i] = this.mesh.colors.get(i);
        }
        this.handler = handler;
        handler.regenerateObject(this);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords = coords.add(vel);
//        this.addForce(new Vector(0, 0, -0.000981));
    }

    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    // returns the color of the shape
    public Color[] getColor(int orientation) {
        return new Color[]{color, color, color, color, color, color};
    }
    public int[] getMeshColor() {
        return this.colors;
    }
}