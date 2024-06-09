// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the Chunk Class which covers the terrain of the player field


package main;

import java.awt.*;
import java.util.*;

// Chunk Class
public class Chunk extends gameObject {

    // Variables
    private final Handler handler;
    private final Player reference;
    private final double FACTOR = 2;
    private Color color;
    private boolean active;
    public static final int SIZE = 8; // size of perlin noise
    public static int render_distance = 3;
//    public final HashMap<Point3D, gameObject> blocks = new HashMap<>();
    public final Set<Point3D> blocks = new HashSet<Point3D>();

    // creates a new chunk and generates it's mesh
    public Chunk(Point3D p, ID id, Handler handler, Color color, int id2, Player reference) {

        super(p, new Vector(0, 0, 0), id);

        this.reference = reference;
        this.active = true;

        int count = 0; // count of # of vertices

        ArrayList<Point3D> verts = new ArrayList<Point3D>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed
        PerlinNoise perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
        double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap
        for (int i = 0; i < SIZE; i++){
            for (int j = 0; j < SIZE; j++) {
                double height = Math.round(heatmap[i + SIZE * id2][j] * FACTOR);
                System.out.println(height);
                while (height > -2){
                    blocks.add(new Point3D(i, j, height));
                    height--;
                }
            }
        }
        for (Point3D point: blocks){
            // check if there is a face above
            if (!blocks.contains(new Point3D(point.x, point.y, point.z + 1))){
                verts.add(new Point3D(point.x, point.y, point.z));
                verts.add(new Point3D(point.x + 1, point.y, point.z));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z));
                verts.add(new Point3D(point.x, point.y + 1, point.z));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
            }
            // check if there is a face to the right
            if (!blocks.contains(new Point3D(point.x + 1, point.y, point.z))){
                verts.add(new Point3D(point.x + 1, point.y, point.z));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z - 1));
                verts.add(new Point3D(point.x + 1, point.y, point.z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
            }
            // check if there is a face to the left
            if (!blocks.contains(new Point3D(point.x - 1, point.y, point.z))){
                verts.add(new Point3D(point.x, point.y, point.z));
                verts.add(new Point3D(point.x, point.y + 1, point.z));
                verts.add(new Point3D(point.x, point.y + 1, point.z - 1));
                verts.add(new Point3D(point.x, point.y, point.z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
            // check if there is a face behind
            if (!blocks.contains(new Point3D(point.x, point.y - 1, point.z))){
                verts.add(new Point3D(point.x, point.y, point.z));
                verts.add(new Point3D(point.x + 1, point.y , point.z));
                verts.add(new Point3D(point.x + 1, point.y, point.z - 1));
                verts.add(new Point3D(point.x, point.y, point.z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
            // check if there is a face in front
            if (!blocks.contains(new Point3D(point.x, point.y + 1, point.z))){
                verts.add(new Point3D(point.x, point.y + 1, point.z));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z - 1));
                verts.add(new Point3D(point.x, point.y + 1, point.z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
            // check if there is a face beneath
            if (!blocks.contains(new Point3D(point.x, point.y, point.z - 1))){
                verts.add(new Point3D(point.x, point.y, point.z - 1));
                verts.add(new Point3D(point.x + 1, point.y, point.z - 1));
                verts.add(new Point3D(point.x + 1, point.y + 1, point.z - 1));
                verts.add(new Point3D(point.x, point.y + 1, point.z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
            }

        }
        this.mesh = new Mesh(verts, faceVerts); // create mesh
        this.color = new Color(78, 153, 82);
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