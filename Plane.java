// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the cube class, creating a game object which is really just a plane currently

package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Plane extends gameObject{
    private final Handler handler;
    private final Color color;


    public Plane(Point3D p, float scale, ID id, Handler handler, Color color){
        super(p, new Vector(1, 0, 0), id);
        this.handler = handler;

        ArrayList<Point3D> verts = new ArrayList<>();
        verts.add(p);
        verts.add(p.add(new Vector(scale, 0, 0)));
        verts.add(p.add(new Vector(0, scale, 0)));
        verts.add(p.add(new Vector(scale, scale, 0)));

        ArrayList<int[]> faceVerts = new ArrayList<>();
        faceVerts.add(new int[]{0, 1, 3, 2});

        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.createMesh();

    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords.add(vel);
    }

    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}