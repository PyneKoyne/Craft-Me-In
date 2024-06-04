// Author: Anish Nagariya
// Date: June 14th
// Program Name: Engine
// Description: This is the Chunk Class which covers the terrain of the player field


package main;

import main.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class Chunk extends gameObject {

    private final Handler handler;
    private final Color color;
    private int SIZE = 32; // size of perlin noise

    public Chunk(Point3D p, float scale, ID id, Handler handler, Color color, int id2){

        super(p, new Vector(0, 0, 0), id);

        // int[][] heatmap = new int[32][32];
        int count = 0; // count of # of vertices

        ArrayList<Point3D>  verts = new ArrayList<Point3D>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<int[]>(); // store all the vertices that need to be displayed

        PerlinNoise perlinNoise = new PerlinNoise(SIZE*4, SIZE); // terrain map
        double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap

        // create x, y, z points for each point in heatmap
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++){
                // heatmap[i][j] = (int) (Math.random() * 5);
                verts.add(new Point3D(i, j, heatmap[i + SIZE*id2][j]*3.5));
                verts.add(new Point3D(i, j + 1, heatmap[i + SIZE*id2][j]*3.5));
                verts.add(new Point3D(i + 1, j + 1, heatmap[i + SIZE*id2][j]*3.5));
                verts.add(new Point3D(i + 1, j, heatmap[i + SIZE*id2][j]*3.5));
                faceVerts.add(new int[]{count, count + 1, count + 2,count + 3});
                count += 4;
            }
        }

        this.mesh = new Mesh(verts, faceVerts); // display mesh
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