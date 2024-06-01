// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the cube class, creating a game object which is really just a plane currently

package main;

import java.awt.*;
import java.util.HashMap;

public class Cube extends gameObject {
    private final Handler handler;
    private final Color color;


    public Cube(Point3D p, float scale, ID id, Handler handler, Color color) {
        super(p, new Vector(0, 0, 0), id);

        Point3D[] verts = {
                Point3D.zero,
                new Point3D(scale, 0, 0),
                new Point3D(0, scale, 0),
                new Point3D(scale, scale, 0),

                new Point3D(0, 0, scale),
                new Point3D(scale, 0, scale),
                new Point3D(0, scale, scale),
                new Point3D(scale, scale, scale)
        };
        int[][] faceVerts = new int[][]
                {
                        {0, 1, 3, 2},
                        {0, 4, 6, 2},
                        {0, 1, 5, 4},
                        {2, 6, 7, 3},
                        {4, 5, 7, 6},
                        {5, 7, 3, 1}
                };

        this.mesh = new Mesh(verts, faceVerts);
        this.color = color;
        mesh.createMesh();

        this.handler = handler;
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords = coords.add(vel);
//        this.addForce(new Vector(0, 0, -0.000981));
    }

    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    // returns the color of the shape
    public Color getColor() {
        return color;
    }

}