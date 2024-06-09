// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the Chunk Class which covers the terrain of the player field


package main;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

// Chunk Class
public class Chunk extends gameObject {

    // Variables
    private final Handler handler;
    private final Player playerRef;
    private Color color;
    private boolean active;
    public static final int SIZE = 4; // size of perlin noise
    public static int render_distance = 1;
    public final HashMap<Point3D, gameObject> blocks = new HashMap<>();
    public final HashMap<Point3D, Chunk> chunkRef;

    // creates a new chunk and generates it's mesh
    public Chunk(Point3D p, ID id, Handler handler, Color color, int id2, Player playerRef, HashMap<Point3D, Chunk> chunkRef) {
        super(p, new Vector(0, 0, 0), id);

        this.playerRef = playerRef;
        this.chunkRef = chunkRef;
        this.active = true;
        int count = 0; // count of # of vertices
        ArrayList<Point3D> verts = new ArrayList<Point3D>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed
        PerlinNoise perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
        double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap
        Point3D location;

        // create x, y, z points for each point in heatmap
        for (int i = 0; i < SIZE; i++){
            heatmap[i + SIZE*id2][0] = -1;
            heatmap[i + SIZE*id2][SIZE - 1] = -1;
            heatmap[0][i] = -1;
            heatmap[SIZE - 1][i] = -1;
        }

        for (int i = 1; i < SIZE - 1; i++) {
            for (int j = 1; j < SIZE - 1; j++) {
                for (int z = (int) Math.round(heatmap[i + SIZE * id2][j]); z > 0; z--) {
                    location = new Point3D(i, j, Math.round(heatmap[i + SIZE * id2][j] * 1.5));
                    blocks.put(location, new Dirt(location, ID.Dirt, Color.green));
                }

                verts.add(new Point3D(i, j, Math.round(heatmap[i + SIZE * id2][j] * 1.5)));
                verts.add(new Point3D(i + 1, j, Math.round(heatmap[i + SIZE * id2][j] * 1.5)));
                verts.add(new Point3D(i + 1, j + 1, Math.round(heatmap[i + SIZE * id2][j] * 1.5)));
                verts.add(new Point3D(i, j + 1, Math.round(heatmap[i + SIZE * id2][j] * 1.5)));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
                int k = count - 4;
                double curr = Math.round(heatmap[i + SIZE * id2][j] * 3.5);
                boolean start = false;
                while (curr > Math.round(heatmap[i + SIZE * id2 + 1][j] * 3.5)){
                    verts.add(new Point3D(i, j, curr - 1));
                    verts.add(new Point3D(i + 1, j, curr - 1));
                    if (!start) {
                        faceVerts.add(new int[]{count - 3, count + 1, count, count - 4});
                        start = true;
                    }else{
                        faceVerts.add(new int[]{count - 2, count - 1, count + 1, count});
                    }
                    count += 2;
                    curr--;
                }
                start = false;
                curr = Math.round(heatmap[i + SIZE * id2][j] * 3.5);
                while (curr > Math.round(heatmap[i + SIZE * id2 - 1][j] * 3.5)){
                    verts.add(new Point3D(i, j + 1, curr - 1));
                    verts.add(new Point3D(i + 1, j + 1, curr - 1));
                    if (!start){
                        faceVerts.add(new int[]{k + 2, k + 3, count, count + 1});
                        start = true;
                    }else{
                        faceVerts.add(new int[]{count - 2, count - 1, count + 1, count});
                    }
                    count += 2;
                    curr--;
                }
                start = false;
                curr = Math.round(heatmap[i + SIZE * id2][j] * 3.5);
                while (curr > Math.round(heatmap[i + SIZE * id2 - 1][j + 1] * 3.5)){
                    verts.add(new Point3D(i + 1, j, curr - 1));
                    verts.add(new Point3D(i + 1, j + 1, curr - 1));
                    if (!start){
                        faceVerts.add(new int[]{k + 1, k + 2, count + 1, count});
                        start = true;
                    }else{
                        faceVerts.add(new int[]{count - 2, count - 1, count + 1, count});
                    }
                    count += 2;
                    curr--;
                }
                start = false;
                curr = Math.round(heatmap[i + SIZE * id2][j] * 3.5);
                while (curr > Math.round(heatmap[i + SIZE * id2 - 1][j - 1] * 3.5)){
                    verts.add(new Point3D(i, j, curr - 1));
                    verts.add(new Point3D(i, j + 1, curr - 1));
                    if (!start){
                        faceVerts.add(new int[]{k, k + 3, count + 1, count});
                        start = true;
                    }else{
                        faceVerts.add(new int[]{count - 2, count - 1, count + 1, count});
                    }
                    count += 2;
                    curr--;
                }
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
        if (playerRef != null){
            if (Math.abs(Vector.i.dotProd(this.coords.subtract(playerRef.coords))) + Math.abs(Vector.j.dotProd(this.coords.subtract(playerRef.coords))) > render_distance * SIZE + 10){
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