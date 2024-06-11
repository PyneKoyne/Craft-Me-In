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
    private final Player playerRef;
    private final double FACTOR = 1.5;
    private Color color;
    private boolean active;
    public static final int SIZE = 10; // size of perlin noise
    public static final int HEIGHT_MAX = 4; // max height of a block
    public static int render_distance = 3; // how many chunks to render
    public int updateChunk = -1;
    public final HashMap<Point3D, gameObject> blocks = new HashMap<>();
    public final HashMap<Point3D, Chunk> chunkRef;

    // creates a new chunk and generates it's mesh
    public Chunk(Point3D p, ID id, Handler handler, int id2, Player playerRef, HashMap<Point3D, Chunk> chunkRef) {
        super(p, new Vector(0, 0, 0), id);

        this.playerRef = playerRef;
        this.chunkRef = chunkRef;
        this.active = true;
        this.handler = handler;
        this.handler.addObject(this);

        PerlinNoise perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
        double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap
        Point3D loc, neighbourChunk;

        // add blocks according to heatmap
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                int height = (int) (Math.round(heatmap[i + SIZE * id2][j] * FACTOR) + HEIGHT_MAX - FACTOR);
                while (height >= 0) { // give each coordinate height based on perline noise
                    loc = new Point3D(i, j, height);
                    blocks.put(loc, new Dirt(loc, ID.Dirt, Color.green));
                    height--;
                }
            }
        }
        this.color = new Color(78, 153, 82); // color for grass
        generateMesh();

        neighbourChunk = coords.add(Vector.j.mul(-Chunk.SIZE)); // check if there is a chunk on left
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateChunk = 10;
        }

        neighbourChunk = coords.add(Vector.j.mul(Chunk.SIZE)); // check if chunk on right
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateChunk = 10;
        }

        neighbourChunk = coords.add(Vector.i.mul(Chunk.SIZE)); // check chunk below
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateChunk = 10;
        }

        neighbourChunk = coords.add(Vector.i.mul(-Chunk.SIZE)); // check chunk above
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateChunk = 10;
        }
    }

    private void generateMesh() {
        int z;
        int y;
        int x;
        int count = 0; // count of # of vertices
        Point3D key;
        ArrayList<Point3D> verts = new ArrayList<>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed

        // create point for each face in chunks
        for (z = HEIGHT_MAX; z > 0; z--) {
            x = 0; y = 0;
            key = new Point3D(x, y, z);
            // check left, right, top, bottom, front, back for chunk faces
            if (blocks.containsKey(key)) {
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                count = checkChunkLeft(chunkRef, y, z, verts, faceVerts, count);
                count = checkChunkBehind(chunkRef, x, z, verts, faceVerts, count);
            }

            // for lower chunk
            x = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                count = checkChunkRight(chunkRef, y, z, verts, faceVerts, count);
                count = checkChunkBehind(chunkRef, x, z, verts, faceVerts, count);
            }

            // for chunk to left
            y = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                count = checkChunkRight(chunkRef, y, z, verts, faceVerts, count);
                count = checkChunkFront(chunkRef, x, z, verts, faceVerts, count);
            }

            x = 0;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                count = checkChunkLeft(chunkRef, y, z, verts, faceVerts, count);
                count = checkChunkFront(chunkRef, x, z, verts, faceVerts, count);
            }

            for (y = 1; y < Chunk.SIZE - 1; y++) {
                x = 0;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                    count = checkChunkLeft(chunkRef, y, z, verts, faceVerts, count);
                }
                x = Chunk.SIZE - 1;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                    count = checkChunkRight(chunkRef, y, z, verts, faceVerts, count);
                }
            }

            for (x = 1; x < Chunk.SIZE - 1; x++) {
                y = 0;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                    count = checkChunkBehind(chunkRef, x, z, verts, faceVerts, count);
                }
                y = Chunk.SIZE - 1;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                    count = checkFaceDown(key, verts, x, y, z, faceVerts, count);
                    count = checkChunkFront(chunkRef, x, z, verts, faceVerts, count);
                }
                for (y = 1; y < Chunk.SIZE - 1; y++) {
                    key = new Point3D(x, y, z);
                    if (blocks.containsKey(key)) {
                        count = checkFaceUp(key, verts, x, y, z, faceVerts, count);
                        count = checkFaceRight(key, verts, x, y, z, faceVerts, count);
                        count = checkFaceLeft(key, verts, x, y, z, faceVerts, count);
                        count = checkFaceBehind(key, verts, x, y, z, faceVerts, count);
                        count = checkFaceFront(key, verts, x, y, z, faceVerts, count);
                        count = checkFaceDown(key, verts, x, y, z, faceVerts, count);

                    }
                }
            }
        }
        this.mesh = new Mesh(verts, faceVerts); // create mesh
        this.mesh.createMesh();
        this.handler.regenerateObject(this);
    }

    // check if there is a chunk infront
    private int checkChunkFront(HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<Point3D> verts, ArrayList<int[]> faceVerts, int count) {
        Point3D neighbourChunk;
        neighbourChunk = coords.add(Vector.j.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(new Point3D(x, 0, z))) {
                verts.add(new Point3D(x, Chunk.SIZE, z));
                verts.add(new Point3D(x + 1, Chunk.SIZE, z));
                verts.add(new Point3D(x + 1, Chunk.SIZE, z - 1));
                verts.add(new Point3D(x, Chunk.SIZE, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
        }
        return count;
    }

    // Checks if there's block in the chunk behind
    private int checkChunkBehind(HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<Point3D> verts, ArrayList<int[]> faceVerts, int count) {
        Point3D neighbourChunk;
        neighbourChunk = coords.add(Vector.j.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(new Point3D(x, Chunk.SIZE - 1, z))) {
                verts.add(new Point3D(x, 0, z));
                verts.add(new Point3D(x + 1, 0, z));
                verts.add(new Point3D(x + 1, 0, z - 1));
                verts.add(new Point3D(x, 0, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
        }
        return count;
    }

    // checks if there's a block in the right chunk
    private int checkChunkRight(HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<Point3D> verts, ArrayList<int[]> faceVerts, int count) {
        Point3D neighbourChunk;
        neighbourChunk = coords.add(Vector.i.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(new Point3D(0, y, z))) {
                verts.add(new Point3D(Chunk.SIZE, y, z));
                verts.add(new Point3D(Chunk.SIZE, y + 1, z));
                verts.add(new Point3D(Chunk.SIZE, y + 1, z - 1));
                verts.add(new Point3D(Chunk.SIZE, y, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                count += 4;
            }
        }
        return count;
    }

    // checks the left chunk
    private int checkChunkLeft(HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<Point3D> verts, ArrayList<int[]> faceVerts, int count) {
        Point3D neighbourChunk;
        neighbourChunk = coords.add(Vector.i.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(new Point3D(Chunk.SIZE - 1, y, z))) {
                verts.add(new Point3D(0, y, z));
                verts.add(new Point3D(0, y + 1, z));
                verts.add(new Point3D(0, y + 1, z - 1));
                verts.add(new Point3D(0, y, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                count += 4;
            }
        }
        return count;
    }

    // check if there is a face beneath
    private int checkFaceDown(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        if (!blocks.containsKey(key.add(Vector.k.mul(-1)))) { // display face beneath
            verts.add(new Point3D(x, y, z - 1));
            verts.add(new Point3D(x + 1, y, z - 1));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x, y + 1, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            count += 4;
        }
        return count;
    }

    // check if there is a face in front
    private int checkFaceFront(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        if (!blocks.containsKey(key.add(Vector.j.mul(-1)))) { // display face in front
            verts.add(new Point3D(x, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x, y + 1, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            count += 4;
        }
        return count;
    }

    private int checkFaceBehind(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        // check if there is a face behind
        if (!blocks.containsKey(key.add(Vector.j))) {
            verts.add(key);
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y, z - 1));
            verts.add(new Point3D(x, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            count += 4;
        }
        return count;
    }

    // check if there is a face to the left
    private int checkFaceLeft(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        if (!blocks.containsKey(key.add(Vector.i.mul(-1)))) { // display face to left
            verts.add(key);
            verts.add(new Point3D(x, y + 1, z));
            verts.add(new Point3D(x, y + 1, z - 1));
            verts.add(new Point3D(x, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            count += 4;
        }
        return count;
    }

    // check if face to right
    private int checkFaceRight(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        if (!blocks.containsKey(key.add(Vector.i))) { // display face to right
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x + 1, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            count += 4;
        }
        return count;
    }

    // check if face is above
    private int checkFaceUp(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts, int count) {
        if (!blocks.containsKey(key.add(Vector.k))) { // display face above
            verts.add(key);
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x, y + 1, z));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            count += 4;
        }
        return count;
    }


    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (playerRef != null) {
            if (Math.abs(Vector.i.dotProd(this.coords.subtract(playerRef.coords))) + Math.abs(Vector.j.dotProd(this.coords.subtract(playerRef.coords))) > render_distance * SIZE + 10) {
                setInactive();
            }
        }
        if (updateChunk > 0) {
            System.out.println(updateChunk);
            updateChunk --;
        }
        else if (updateChunk == 0){
            generateMesh();
            updateChunk = -1;
        }
    }

    // any helper code when drawing the chunk
    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // adds the chunk to the screen if it's not already active
    public boolean setActive() {
        if (!active) {
            handler.addObject(this);
            active = true;
            return true;
        }
        return false;
    }

    // removes the chunk from the screen if it's not already active
    public boolean setInactive() {
        if (active) {
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