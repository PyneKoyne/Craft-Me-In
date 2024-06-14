// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: This is the Chunk Class which covers the terrain of the player field

package main;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Chunk Class
public class Chunk extends gameObject {

    // Variables
    private final Handler handler;
    private final Player playerRef;
    private static final double FACTOR = 3;
    private ArrayList<Face> innerFaces;
    private Color[] colors;
    private boolean active;
    public static final int SIZE = 10; // size of perlin noise
    public static final int HEIGHT_MAX = 10; // max height of a block
    public static final String CHUNK_PATH = "./craftmein/Chunks/";
    public static int render_distance = 3; // how many chunks to render
    public int updateChunk = -1;
    public boolean updateFront = false, updateLeft = false, updateRight = false, updateBack = false;
    public HashMap<Point3D, gameObject> blocks;
    public final HashMap<Point3D, Chunk> chunkRef; // the chunk hashmap
    public Mesh[] meshes;

    // creates a new chunk and generates it's mesh
    public Chunk(Point3D p, ID id, Handler handler, int id2, Player playerRef, HashMap<Point3D, Chunk> chunkRef) throws IOException {
        super(p, new Vector(0, 0, 0), id); // game object constructor
        File f;
        Point3D key;
        this.playerRef = playerRef;
        this.chunkRef = chunkRef;
        this.active = true;
        this.handler = handler;
        this.handler.addObject(this);
        this.meshes = new Mesh[5];
        this.mesh = new Mesh();
        for (int i = 0; i < 5; i++) {
            this.meshes[i] = new Mesh();
        }
        f = new File(CHUNK_PATH + p.toString() + ".txt");
        this.blocks = new HashMap<>();

        if (!f.exists()) {
            // generates the perlin noise of the chunk
            PerlinNoise perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
            double[][] heatmap = perlinNoise.generateNoise(); // generate basic heatmap
            Point3D loc;

            // add blocks according to heatmap
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int height = (int) (Math.round(heatmap[i + SIZE * id2][j] * FACTOR) + HEIGHT_MAX - FACTOR);
                    while (height >= 0) { // give each coordinate height based on perlin noise
                        loc = new Point3D(i, j, height);
                        blocks.put(loc, new Dirt(loc, ID.Dirt));
                        height--;
                    }
                }
            }
        }
        else{
            Scanner chunkReader = new Scanner(f);
            while (chunkReader.hasNextLine()){
                String[] line = chunkReader.nextLine().split("=");
                String[] rawPoint = line[0].split(", ");
                key = new Point3D(Double.parseDouble(rawPoint[0]), Double.parseDouble(rawPoint[1]), Double.parseDouble(rawPoint[2]));
                ID blockId = ID.valueOf(line[1]);
                if (blockId == ID.Dirt) {
                    blocks.put(key, new Dirt(key, ID.Dirt));
                }
                else if (blockId == ID.Stone){
                    blocks.put(key, new Stone(key, ID.Stone));
                }
            }
        }

        System.out.println(blocks);

        generateMesh(); // generates the mess
        updateNeighbours();
        saveChunk(f);
    }

    private void saveChunk(File f) {
        try {
            FileWriter chunkWriter = new FileWriter(f);
            for (Point3D key : blocks.keySet()) {
                chunkWriter.write(key.toString().substring(1, key.toString().length() - 1) + "=" + blocks.get(key).id.toString() + "\n");
            }
            chunkWriter.close();
        } catch (IOException e) {
            System.out.println("A Chunk Saving Error Occurred.");
            e.printStackTrace();
        }
    }

    // updates all chunks around the current chunk
    private void updateNeighbours() {
        Point3D neighbourChunk;
        neighbourChunk = coords.add(Vector.j.mul(-Chunk.SIZE)); // check if there is a chunk in front
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateBack = true;
        }

        neighbourChunk = coords.add(Vector.j.mul(Chunk.SIZE)); // check if chunk behind
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateFront = true;
        }

        neighbourChunk = coords.add(Vector.i.mul(Chunk.SIZE)); // check chunk to the right
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateLeft = true;
        }

        neighbourChunk = coords.add(Vector.i.mul(-Chunk.SIZE)); // check chunk to the left
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkRef.get(neighbourChunk).updateRight = true;
        }
    }

    // generates the mesh for the corner blocks
    private void generateCorners() {
        int x, y, z; // the x y and z for the blocks in the
        int count = 0; // count of # of vertices
        Point3D key; // the current point being looked at
        ArrayList<Point3D> verts = new ArrayList<>(); // store all the vertices
        ArrayList<int[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed
        ArrayList<Color> colors = new ArrayList<>();

        for (z = HEIGHT_MAX; z > 0; z--) {
            // check the left-bottom corner
            x = 0;
            y = 0;
            key = new Point3D(x, y, z);
            // check left, right, top, bottom, front, back for chunk faces
            if (blocks.containsKey(key)) {
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceRight(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceFront(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count, colors);
                count = checkChunkLeft(key, chunkRef, y, z, verts, faceVerts, count, colors);
                count = checkChunkBehind(key, chunkRef, x, z, verts, faceVerts, count, colors);
            }

            // check the right bottom corner
            x = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceLeft(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceFront(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count, colors);
                count = checkChunkRight(key, chunkRef, y, z, verts, faceVerts, count, colors);
                count = checkChunkBehind(key, chunkRef, x, z, verts, faceVerts, count, colors);
            }

            // check the top right corner
            y = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceLeft(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceBehind(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count, colors);
                count = checkChunkRight(key, chunkRef, y, z, verts, faceVerts, count, colors);
                count = checkChunkFront(key, chunkRef, x, z, verts, faceVerts, count, colors);
            }

            // check the top left corner
            x = 0;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                count = checkFaceUp(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceRight(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceBehind(key, verts, x, y, z, faceVerts, count, colors);
                count = checkFaceDown(key, verts, x, y, z, faceVerts, count, colors);
                count = checkChunkLeft(key, chunkRef, y, z, verts, faceVerts, count, colors);
                count = checkChunkFront(key, chunkRef, x, z, verts, faceVerts, count, colors);
            }
        }
        this.meshes[4] = new Mesh(verts, faceVerts, colors); // create mesh
    }

    // generates the mesh for the outermost left side of the chunk
    private void generateLeft() {
        int x, y, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[0].colors = new ArrayList<>();
        this.meshes[0].faces = new ArrayList<>();

        for (z = HEIGHT_MAX; z > 0; z--) {
            for (y = 1; y < Chunk.SIZE - 1; y++) {
                x = 0;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    meshes[0].count = checkFaceUp(key, meshes[0].verts, x, y, z, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                    meshes[0].count = checkFaceRight(key, meshes[0].verts, x, y, z, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                    meshes[0].count = checkFaceBehind(key, meshes[0].verts, x, y, z, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                    meshes[0].count = checkFaceFront(key, meshes[0].verts, x, y, z, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                    meshes[0].count = checkFaceDown(key, meshes[0].verts, x, y, z, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                    meshes[0].count = checkChunkLeft(key, chunkRef, y, z, meshes[0].verts, meshes[0].faceVerts, meshes[0].count, meshes[0].colors);
                }
            }
        }
        this.meshes[0].setFaces();
    }

    // generates the mesh for the outermost right side of the chunk
    private void generateRight() {
        int x = Chunk.SIZE - 1, y, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[2].colors = new ArrayList<>();
        this.meshes[2].faces = new ArrayList<>();

        for (z = HEIGHT_MAX; z > 0; z--) {
            for (y = 1; y < Chunk.SIZE - 1; y++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    meshes[2].count = checkFaceUp(key, meshes[2].verts, x, y, z, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                    meshes[2].count = checkFaceLeft(key, meshes[2].verts, x, y, z, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                    meshes[2].count = checkFaceBehind(key, meshes[2].verts, x, y, z, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                    meshes[2].count = checkFaceFront(key, meshes[2].verts, x, y, z, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                    meshes[2].count = checkFaceDown(key, meshes[2].verts, x, y, z, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                    meshes[2].count = checkChunkRight(key, chunkRef, y, z, meshes[2].verts, meshes[2].faceVerts, meshes[2].count, meshes[2].colors);
                }
            }
        }
        this.meshes[2].setFaces();
    }

    // generates the mesh for the outermost back side of the chunk
    private void generateBack() {
        int x, y = 0, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[1].colors = new ArrayList<>();
        this.meshes[1].faces = new ArrayList<>();

        for (z = HEIGHT_MAX; z > 0; z--) {
            // Loops through every x value
            for (x = 1; x < Chunk.SIZE - 1; x++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    meshes[1].count = checkFaceUp(key, meshes[1].verts, x, y, z, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                    meshes[1].count = checkFaceRight(key, meshes[1].verts, x, y, z, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                    meshes[1].count = checkFaceLeft(key, meshes[1].verts, x, y, z, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                    meshes[1].count = checkFaceFront(key, meshes[1].verts, x, y, z, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                    meshes[1].count = checkFaceDown(key, meshes[1].verts, x, y, z, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                    meshes[1].count = checkChunkBehind(key, chunkRef, x, z, meshes[1].verts, meshes[1].faceVerts, meshes[1].count, meshes[1].colors);
                }
            }
        }
        this.meshes[1].setFaces();
    }

    // generates the mesh for the outermost front side of the chunk
    private void generateFront() {
        int x, y = Chunk.SIZE - 1, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[3].colors = new ArrayList<>();
        this.meshes[3].faces = new ArrayList<>();

        for (z = HEIGHT_MAX; z > 0; z--) {
            // Loops through every x value
            for (x = 1; x < Chunk.SIZE - 1; x++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    meshes[3].count = checkFaceUp(key, meshes[3].verts, x, y, z, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                    meshes[3].count = checkFaceRight(key, meshes[3].verts, x, y, z, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                    meshes[3].count = checkFaceLeft(key, meshes[3].verts, x, y, z, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                    meshes[3].count = checkFaceBehind(key, meshes[3].verts, x, y, z, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                    meshes[3].count = checkFaceDown(key, meshes[3].verts, x, y, z, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                    meshes[3].count = checkChunkFront(key, chunkRef, x, z, meshes[3].verts, meshes[3].faceVerts, meshes[3].count, meshes[3].colors);
                }
            }
        }
        this.meshes[3].setFaces();
    }

    // method to regenerate the mesh of the chunk
    private void generateMesh() {
        int x, y, z; // the x y and z for the blocks in the
        this.mesh.colors = new ArrayList<>();
        this.mesh.faces = new ArrayList<>();
        Point3D key; // the current point being looked at
        ArrayList<Point3D> tempMesh;

        generateCorners(); // generates the mesh for the corner blocks
        generateLeft();
        generateRight();
        generateFront();
        generateBack();
        // create point for each face in chunks
        for (z = HEIGHT_MAX; z > 0; z--) {
            // Loops through every x value
            for (x = 1; x < Chunk.SIZE - 1; x++) {
                // adds in all inner blocks into the mesh
                for (y = 1; y < Chunk.SIZE - 1; y++) {
                    key = new Point3D(x, y, z);
                    if (blocks.containsKey(key)) {
                        // check left, right, top, bottom, front, back for chunk faces
                        mesh.count = checkFaceUp(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                        mesh.count = checkFaceRight(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                        mesh.count = checkFaceLeft(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                        mesh.count = checkFaceBehind(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                        mesh.count = checkFaceFront(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                        mesh.count = checkFaceDown(key, mesh.verts, x, y, z, mesh.faceVerts, mesh.count, mesh.colors);
                    }
                }
            }
        }
        // adds the meshes into a full array list of points
        tempMesh = new ArrayList<>(this.meshes[4].mesh);
        this.mesh.setFaces();
        tempMesh.addAll(this.mesh.createMesh());
        this.innerFaces = (ArrayList<Face>) this.mesh.faces.clone();
        this.mesh.faces.addAll(this.meshes[4].faces);
        for (int i = 0; i < 4; i++) {
            tempMesh.addAll(this.meshes[i].createMesh());
            this.mesh.faces.addAll(this.meshes[i].faces);
        }

        this.mesh.setRawMesh(Point3D.toFloat(tempMesh.toArray(new Point3D[0]))); // sets the raw mesh with the temp array
        setColors();
        this.handler.regenerateObject(this);
    }

    // check if there is a chunk in front
    private int checkChunkFront(Point3D key, HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<
            Point3D> verts, ArrayList<int[]> faceVerts, int count, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;

        neighbourChunk = coords.add(Vector.j.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkKey = new Point3D(x, 0, z);

            if (!chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) {
                verts.add(new Point3D(x, Chunk.SIZE, z));
                verts.add(new Point3D(x + 1, Chunk.SIZE, z));
                verts.add(new Point3D(x + 1, Chunk.SIZE, z - 1));
                verts.add(new Point3D(x, Chunk.SIZE, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                colors.add(blocks.get(key).getColor()[0]);
                count += 4;
            }
        }
        return count;
    }

    // Checks if there's block in the chunk behind
    private int checkChunkBehind(Point3D key, HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<
            Point3D> verts, ArrayList<int[]> faceVerts, int count, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        neighbourChunk = coords.add(Vector.j.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkKey = new Point3D(x, Chunk.SIZE - 1, z);
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) {
                verts.add(new Point3D(x, 0, z));
                verts.add(new Point3D(x + 1, 0, z));
                verts.add(new Point3D(x + 1, 0, z - 1));
                verts.add(new Point3D(x, 0, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                colors.add(blocks.get(key).getColor()[0]);
                count += 4;
            }
        }
        return count;
    }

    // checks if there's a block in the right chunk
    private int checkChunkRight(Point3D key, HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<
            Point3D> verts, ArrayList<int[]> faceVerts, int count, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        neighbourChunk = coords.add(Vector.i.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkKey = new Point3D(0, y, z);
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) {
                verts.add(new Point3D(Chunk.SIZE, y, z));
                verts.add(new Point3D(Chunk.SIZE, y + 1, z));
                verts.add(new Point3D(Chunk.SIZE, y + 1, z - 1));
                verts.add(new Point3D(Chunk.SIZE, y, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
                colors.add(blocks.get(key).getColor()[0]);
                count += 4;
            }
        }
        return count;
    }

    // checks the left chunk
    private int checkChunkLeft(Point3D key, HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<
            Point3D> verts, ArrayList<int[]> faceVerts, int count, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        neighbourChunk = coords.add(Vector.i.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) {
            chunkKey = new Point3D(Chunk.SIZE - 1, y, z);
            if (!chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) {
                verts.add(new Point3D(0, y, z));
                verts.add(new Point3D(0, y + 1, z));
                verts.add(new Point3D(0, y + 1, z - 1));
                verts.add(new Point3D(0, y, z - 1));
                faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
                colors.add(blocks.get(key).getColor()[0]);
                count += 4;
            }
        }
        return count;
    }

    // check if there is a face beneath
    private int checkFaceDown(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]>
            faceVerts, int count, ArrayList<Color> colors) {
        if (!blocks.containsKey(key.add(Vector.k.mul(-1)))) { // display face beneath
            verts.add(new Point3D(x, y, z - 1));
            verts.add(new Point3D(x + 1, y, z - 1));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x, y + 1, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // check if there is a face in front
    private int checkFaceFront(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]>
            faceVerts, int count, ArrayList<Color> colors) {
        if (!blocks.containsKey(key.add(Vector.j.mul(-1)))) { // display face in front
            verts.add(new Point3D(x, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x, y + 1, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // checks if there's a face behind the chunk
    private int checkFaceBehind(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]>
            faceVerts, int count, ArrayList<Color> colors) {
        // check if there is a face behind
        if (!blocks.containsKey(key.add(Vector.j))) {
            verts.add(key);
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y, z - 1));
            verts.add(new Point3D(x, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // check if there is a face to the left
    private int checkFaceLeft(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]>
            faceVerts, int count, ArrayList<Color> colors) {
        if (!blocks.containsKey(key.add(Vector.i.mul(-1)))) { // display face to left
            verts.add(key);
            verts.add(new Point3D(x, y + 1, z));
            verts.add(new Point3D(x, y + 1, z - 1));
            verts.add(new Point3D(x, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3,});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // check if face to right
    private int checkFaceRight(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]>
            faceVerts, int count, ArrayList<Color> colors) {
        if (!blocks.containsKey(key.add(Vector.i))) { // display face to right
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x + 1, y + 1, z - 1));
            verts.add(new Point3D(x + 1, y, z - 1));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // check if face is above
    private int checkFaceUp(Point3D key, ArrayList<Point3D> verts, int x, int y, int z, ArrayList<int[]> faceVerts,
                            int count, ArrayList<Color> colors) {
        if (!blocks.containsKey(key.add(Vector.k))) { // display face above
            verts.add(key);
            verts.add(new Point3D(x + 1, y, z));
            verts.add(new Point3D(x + 1, y + 1, z));
            verts.add(new Point3D(x, y + 1, z));
            faceVerts.add(new int[]{count, count + 1, count + 2, count + 3});
            colors.add(blocks.get(key).getColor()[0]);
            count += 4;
        }
        return count;
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (playerRef != null) { // if the player is too far away from the chunk, it despawns
            if (Math.abs(Vector.i.dotProd(this.coords.subtract(playerRef.coords))) + Math.abs(Vector.j.dotProd(this.coords.subtract(playerRef.coords))) > render_distance * SIZE + 10) {
                setInactive();
            }
        }
        if (updateChunk > 0) { // if the chunk is supposed to be updated in n ticks, it subtracts n by one
            updateChunk--;
        } else if (updateChunk == 0) { // if the chunk is supposed to be updated it updates
            generateMesh(); // updates the chunk
            updateNeighbours();
            updateChunk = -1;
        }

        // if the chunk needs to update any of its out-most layers of blocks
        if (updateBack || updateFront || updateLeft || updateRight) {
            generateCorners();
            ArrayList<Point3D> tempMesh = new ArrayList<>(this.meshes[4].mesh);
            tempMesh.addAll(this.mesh.mesh);

            if (updateLeft) { // updates the left side of the chunk
                generateLeft();
                // adds the meshes into a full array list of points
                tempMesh.addAll(this.meshes[0].createMesh());
                updateLeft = false;
            } else {
                tempMesh.addAll(this.meshes[0].mesh);
            }

            if (updateBack) { // updates the back of the chunk
                generateBack();
                // adds the meshes into a full array list of points
                tempMesh.addAll(this.meshes[1].createMesh());
                updateBack = false;
            } else {
                tempMesh.addAll(this.meshes[1].mesh);
            }

            if (updateFront) { // updates the front of the chunk
                generateFront();
                // adds the meshes into a full array list of points
                tempMesh.addAll(this.meshes[3].createMesh());
                updateFront = false;
            } else {
                tempMesh.addAll(this.meshes[3].mesh);
            }

            if (updateRight) { // updates the right side of the chunk
                generateRight();
                // adds the meshes into a full array list of points
                tempMesh.addAll(this.meshes[2].createMesh());
                updateRight = false;
            } else {
                tempMesh.addAll(this.meshes[2].mesh);
            }
            this.mesh.faces = (ArrayList<Face>) innerFaces.clone();
            this.mesh.faces.addAll(this.meshes[4].faces);
            for (int i = 0; i < 4; i++) {
                this.mesh.faces.addAll(this.meshes[i].faces);
            }
            this.mesh.setRawMesh(Point3D.toFloat(tempMesh.toArray(new Point3D[0]))); // sets the raw mesh with the temp array
            setColors();
            this.handler.regenerateObject(this);
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
    public Color[] getColor() {
        if (colors != null) {
            return colors;
        } else return null;
    }

    // sets the colours of the mesh
    public void setColors() {
        this.colors = new Color[meshes[0].colors.size() +
                meshes[1].colors.size() +
                meshes[2].colors.size() +
                meshes[3].colors.size() +
                meshes[4].colors.size() +
                mesh.colors.size()];

        int offset = 0;
        for (int i = 0; i < meshes[4].colors.size(); i++) {
            this.colors[i + offset] = meshes[4].colors.get(i);
        }
        offset += meshes[4].colors.size();
        for (int i = 0; i < mesh.colors.size(); i++) {
            this.colors[i + offset] = mesh.colors.get(i);
        }
        offset += mesh.colors.size();
        for (int i = 0; i < meshes[0].colors.size(); i++) {
            this.colors[i + offset] = meshes[0].colors.get(i);
        }
        offset += meshes[0].colors.size();
        for (int i = 0; i < meshes[1].colors.size(); i++) {
            this.colors[i + offset] = meshes[1].colors.get(i);
        }
        offset += meshes[1].colors.size();
        for (int i = 0; i < meshes[2].colors.size(); i++) {
            this.colors[i + offset] = meshes[2].colors.get(i);
        }
        offset += meshes[2].colors.size();
        for (int i = 0; i < meshes[3].colors.size(); i++) {
            this.colors[i + offset] = meshes[3].colors.get(i);
        }
    }
}