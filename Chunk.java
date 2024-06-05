// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the Chunk Class which covers the terrain of the player field


package main;

import java.awt.*;
import java.util.ArrayList;

// Chunk Class
public class Chunk extends gameObject {

    // Variables
    private final Handler handler;
    private final Player reference;
    private Color color;
    private boolean active;
    public static final int SIZE = 16; // size of perlin noise
    public static int render_distance = 3;

    // creates a new chunk and generates it's mesh
    public Chunk(Point3D p, ID id, Handler handler, Color color, int id2, Player reference) {

        super(p, new Vector(0, 0, 0), id);

        this.reference = reference;
        this.active = true;

        // int[][] heatmap = new int[32][32];
        int count = 0; // count of # of vertices

        ArrayList<Point3D> verts = new ArrayList<Point3D>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed

        PerlinNoise perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
        double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap

        // create x, y, z points for each point in heatmap
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                // heatmap[i][j] = (int) (Math.random() * 5);
                verts.add(new Point3D(i, j, Math.round(heatmap[i + SIZE * id2][j] * 3.5)));
                verts.add(new Point3D(i + 1, j, Math.round(heatmap[i + SIZE * id2][j] * 3.5)));
                verts.add(new Point3D(i + 1, j + 1, Math.round(heatmap[i + SIZE * id2][j] * 3.5)));
                verts.add(new Point3D(i, j + 1, Math.round(heatmap[i + SIZE * id2][j] * 3.5)));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
            }
        }

        this.mesh = new Mesh(verts, faceVerts); // create mesh
        this.color = color;
        mesh.createMesh();

        System.out.println(mesh.rawMesh.length);

        this.handler = handler;
        this.handler.addObject(this);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (reference != null){
            if (Math.abs(Vector.i.dotProd(this.coords.subtract(reference.coords))) + Math.abs(Vector.j.dotProd(this.coords.subtract(reference.coords))) > render_distance * SIZE + 10){
                setInactive();
            }
        }
    }

    // any helper code when drawing the chunk
    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // adds the chunk to the screen if it's not already active
    public boolean setActive(){
        if (!active){
            handler.addObject(this);
            active = true;
            return true;
        }
        return false;
    }

    // removes the chunk from the screen if it's not already active
    public boolean setInactive(){
        if (active){
            handler.removeObject(this);
            active = false;
            return true;
        }
        return false;
    }

    // returns the color of the shape
    public Color getColor() {
        return color;
    }
}