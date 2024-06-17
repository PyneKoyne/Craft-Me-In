// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This is the Chunk Class which covers the terrain of the player field

package main;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// Chunk Class
public class Chunk extends gameObject {

    // Variables
    private final Handler handler;
    private final Player playerRef;
    private ArrayList<Face> innerFaces;
    private LinkedHashMap<Point3D, Short> verts;
    private ArrayList<Point3D> rawVerts;
    private short count;
    private int[] colors;
    private boolean active;
    public static final int SIZE = 8; // size of perlin noise
    public static final int HEIGHT_MAX = 25; // max height of a block
    public static final String CHUNK_PATH = "./craftmein/Chunks/";
    private File f;
    public static int renderDistance = 3; // how many chunks to render
    public int updateChunk = -1;
    public boolean updateFront = false, updateLeft = false, updateRight = false, updateBack = false;
    public HashMap<Point3D, gameObject> blocks;
    public final HashMap<Point3D, Chunk> chunkRef; // the chunk hashmap
    public Mesh[] meshes;

    // creates a new chunk and generates its mesh
    public Chunk(Point3D p, ID id, Handler handler, Player playerRef, HashMap<Point3D, Chunk> chunkRef) {
        super(p, new Vector(0, 0, 0), id); // game object constructor
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
        readChunk(f);
        updateChunk = 1;
    }

    // reads a chunk from file, or generates a new one if it doesn't exist yet
    private void readChunk(File f) {
        // variables
        Point3D key, loc;
        PerlinNoise perlinNoise;
        int[][] heatmap;
        Scanner chunkReader;
        String[] line, rawPoint;
        ID blockId;
        int height, counter;

        if (!f.exists()) { // checks if the chunk exists
            // generates the perlin noise of the chunk
            perlinNoise = new PerlinNoise(SIZE * 4, SIZE); // terrain map
            heatmap = perlinNoise.generateNoise(); // generate basic heatmap
            // add blocks according to heatmap
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    height = heatmap[i + SIZE][j]; // initial height of the block
                    counter = 0;
                    while (height >= 0) { // give each coordinate height based on perlin noise
                        loc = new Point3D(i, j, height);
                        if (counter > 3) blocks.put(loc, new Stone(loc, ID.Stone)); // adds stone 3 blocks below
                        else blocks.put(loc, new Dirt(loc, ID.Dirt));
                        height--;
                        counter++;
                    }
                }
            }
        } else {
            try { // if the chunk does exist
                chunkReader = new Scanner(f); // reads the block data
                while (chunkReader.hasNextLine()) {
                    line = chunkReader.nextLine().split("="); // splits based on the equals sign
                    rawPoint = line[0].split(", ");
                    key = new Point3D(Double.parseDouble(rawPoint[0]), Double.parseDouble(rawPoint[1]), Double.parseDouble(rawPoint[2]));
                    blockId = ID.valueOf(line[1]); // finds which type of block it is
                    if (blockId == ID.Dirt) {
                        blocks.put(key, new Dirt(key, ID.Dirt));
                    } else if (blockId == ID.Stone) {
                        blocks.put(key, new Stone(key, ID.Stone));
                    }
                }
            } catch (FileNotFoundException |
                     IllegalArgumentException e) { // throws an error if the file exists but for some reason still isn't found
                throw new RuntimeException(e);
            }
        }
    }

    // method to save block data of the chunk to a file
    private void saveChunk(File f) {
        try {
            FileWriter chunkWriter = new FileWriter(f); // opens the object to write to the file
            for (Point3D key : blocks.keySet()) {
                chunkWriter.write(key.toString().substring(1, key.toString().length() - 1) + "=" + blocks.get(key).id.toString() + "\n"); // writes each line to be a new block
            }
            chunkWriter.close(); // closes the writer
        } catch (IOException e) {
            System.out.println("A Chunk Saving Error Occurred.");
            e.printStackTrace();
        }
        blocks = new HashMap<>(); // clears the block hashmap to save memory
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
        Point3D key; // the current point being looked at
        // resets all the following variables
        this.meshes[4].rawColors = new ArrayList<>();
        this.meshes[4].faces = new ArrayList<>();
        ArrayList<short[]> faceVerts = new ArrayList<>(); // store all the vertices that need to be displayed
        ArrayList<Color> colors = new ArrayList<>();

        // Goes through every z coordinate
        for (z = HEIGHT_MAX; z > 0; z--) {
            // check the left-bottom corner
            x = 0;
            y = 0;
            key = new Point3D(x, y, z);
            // check left, right, top, bottom, front, back for chunk faces
            if (blocks.containsKey(key)) {
                checkFaceUp(key, x, y, z, faceVerts, colors);
                checkFaceRight(key, x, y, z, faceVerts, colors);
                checkFaceFront(key, x, y, z, faceVerts, colors);
                checkFaceDown(key, x, y, z, faceVerts, colors);
                checkChunkLeft(key, chunkRef, y, z, faceVerts, colors);
                checkChunkBehind(key, chunkRef, x, z, faceVerts, colors);
            }

            // check the right bottom corner
            x = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                checkFaceUp(key, x, y, z, faceVerts, colors);
                checkFaceLeft(key, x, y, z, faceVerts, colors);
                checkFaceFront(key, x, y, z, faceVerts, colors);
                checkFaceDown(key, x, y, z, faceVerts, colors);
                checkChunkRight(key, chunkRef, y, z, faceVerts, colors);
                checkChunkBehind(key, chunkRef, x, z, faceVerts, colors);
            }

            // check the top right corner
            y = Chunk.SIZE - 1;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                checkFaceUp(key, x, y, z, faceVerts, colors);
                checkFaceLeft(key, x, y, z, faceVerts, colors);
                checkFaceBehind(key, x, y, z, faceVerts, colors);
                checkFaceDown(key, x, y, z, faceVerts, colors);
                checkChunkRight(key, chunkRef, y, z, faceVerts, colors);
                checkChunkFront(key, chunkRef, x, z, faceVerts, colors);
            }

            // check the top left corner
            x = 0;
            key = new Point3D(x, y, z);
            if (blocks.containsKey(key)) {
                // check left, right, top, bottom, front, back for chunk faces
                checkFaceUp(key, x, y, z, faceVerts, colors);
                checkFaceRight(key, x, y, z, faceVerts, colors);
                checkFaceBehind(key, x, y, z, faceVerts, colors);
                checkFaceDown(key, x, y, z, faceVerts, colors);
                checkChunkLeft(key, chunkRef, y, z, faceVerts, colors);
                checkChunkFront(key, chunkRef, x, z, faceVerts, colors);
            }
        }
        this.meshes[4] = new Mesh(rawVerts, faceVerts, colors); // create mesh
    }

    // generates the mesh for the outermost left side of the chunk
    private void generateLeft() {
        int x, y, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[0].rawColors = new ArrayList<>();
        this.meshes[0].faces = new ArrayList<>();

        // goes through every z coordinate
        for (z = HEIGHT_MAX; z > 0; z--) {
            for (y = 1; y < Chunk.SIZE - 1; y++) {
                x = 0;
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    checkFaceUp(key, x, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                    checkFaceRight(key, x, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                    checkFaceBehind(key, x, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                    checkFaceFront(key, x, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                    checkFaceDown(key, x, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                    checkChunkLeft(key, chunkRef, y, z, meshes[0].faceVerts, meshes[0].rawColors);
                }
            }
        }
        // sets the mesh
        this.meshes[0].verts = rawVerts;
        this.meshes[0].setFaces();
    }

    // generates the mesh for the outermost right side of the chunk
    private void generateRight() {
        int x = Chunk.SIZE - 1, y, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[2].rawColors = new ArrayList<>();
        this.meshes[2].faces = new ArrayList<>();

        // goes through every z coordinate
        for (z = HEIGHT_MAX; z > 0; z--) {
            for (y = 1; y < Chunk.SIZE - 1; y++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    checkFaceUp(key, x, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                    checkFaceLeft(key, x, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                    checkFaceBehind(key, x, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                    checkFaceFront(key, x, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                    checkFaceDown(key, x, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                    checkChunkRight(key, chunkRef, y, z, meshes[2].faceVerts, meshes[2].rawColors);
                }
            }
        }
        // sets the mesh
        this.meshes[2].verts = rawVerts;
        this.meshes[2].setFaces();
    }

    // generates the mesh for the outermost back side of the chunk
    private void generateBack() {
        int x, y = 0, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[1].rawColors = new ArrayList<>();
        this.meshes[1].faces = new ArrayList<>();

        // goes through every z coordinate
        for (z = HEIGHT_MAX; z > 0; z--) {
            // Loops through every x value
            for (x = 1; x < Chunk.SIZE - 1; x++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    checkFaceUp(key, x, y, z, meshes[1].faceVerts, meshes[1].rawColors);
                    checkFaceRight(key, x, y, z, meshes[1].faceVerts, meshes[1].rawColors);
                    checkFaceLeft(key, x, y, z, meshes[1].faceVerts, meshes[1].rawColors);
                    checkFaceFront(key, x, y, z, meshes[1].faceVerts, meshes[1].rawColors);
                    checkFaceDown(key, x, y, z, meshes[1].faceVerts, meshes[1].rawColors);
                    checkChunkBehind(key, chunkRef, x, z, meshes[1].faceVerts, meshes[1].rawColors);
                }
            }
        }
        // sets the mesh
        this.meshes[1].verts = rawVerts;
        this.meshes[1].setFaces();
    }

    // generates the mesh for the outermost front side of the chunk
    private void generateFront() {
        int x, y = Chunk.SIZE - 1, z; // the x y and z for the blocks in the
        Point3D key; // the current point being looked at
        this.meshes[3].rawColors = new ArrayList<>();
        this.meshes[3].faces = new ArrayList<>();

        // goes through every z coordinate
        for (z = HEIGHT_MAX; z > 0; z--) {
            // Loops through every x value
            for (x = 1; x < Chunk.SIZE - 1; x++) {
                key = new Point3D(x, y, z);
                if (blocks.containsKey(key)) {
                    // check left, right, top, bottom, front, back for chunk faces
                    checkFaceUp(key, x, y, z, meshes[3].faceVerts, meshes[3].rawColors);
                    checkFaceRight(key, x, y, z, meshes[3].faceVerts, meshes[3].rawColors);
                    checkFaceLeft(key, x, y, z, meshes[3].faceVerts, meshes[3].rawColors);
                    checkFaceBehind(key, x, y, z, meshes[3].faceVerts, meshes[3].rawColors);
                    checkFaceDown(key, x, y, z, meshes[3].faceVerts, meshes[3].rawColors);
                    checkChunkFront(key, chunkRef, x, z, meshes[3].faceVerts, meshes[3].rawColors);
                }
            }
        }
        // sets the mesh
        this.meshes[3].verts = rawVerts;
        this.meshes[3].setFaces();
    }

    // method to regenerate the mesh of the chunk
    private void generateMesh() {
        int x, y, z; // the x y and z for the blocks in the mesh
        // resets the following variables
        this.mesh.rawColors = new ArrayList<>();
        this.mesh.faces = new ArrayList<>();
        this.rawVerts = new ArrayList<>();
        this.verts = new LinkedHashMap<>();
        this.count = 0;

        // create point for each face in chunks
        for (Point3D key : blocks.keySet()) {
            x = (int) key.getX();
            y = (int) key.getY();
            z = (int) key.getZ();
            // if the block is in the center of the chunk
            if (x > 0 && x < Chunk.SIZE - 1 && y > 0 && y < Chunk.SIZE - 1 && z > 0) {
                // check left, right, top, bottom, front, back for chunk faces
                checkFaceUp(key, x, y, z, mesh.faceVerts, mesh.rawColors);
                checkFaceRight(key, x, y, z, mesh.faceVerts, mesh.rawColors);
                checkFaceLeft(key, x, y, z, mesh.faceVerts, mesh.rawColors);
                checkFaceBehind(key, x, y, z, mesh.faceVerts, mesh.rawColors);
                checkFaceFront(key, x, y, z, mesh.faceVerts, mesh.rawColors);
                checkFaceDown(key, x, y, z, mesh.faceVerts, mesh.rawColors);
            }
        }
        // adds the meshes into a full array list of points
        this.mesh.verts = rawVerts;
        this.mesh.setFaces();
        this.mesh.createMesh();
        this.innerFaces = (ArrayList<Face>) this.mesh.faces.clone();
        for (int i = 0; i < 4; i++) {
            this.meshes[i].createMesh();
        }
        // updates the outer layer of the chunk
        updateRight = true;
        updateBack = true;
        updateFront = true;
        updateLeft = true;
    }

    // checks if a vertex already exists, and adds the position of it into the face
    private void checkVert(Point3D vert, short[] face, int i) {
        if (verts.containsKey(vert)) {
            face[i] = verts.get(vert);
        } else {
            verts.put(vert, count);
            face[i] = count;
            rawVerts.add(vert);
            count += 1;
        }
    }

    // check if there is a chunk in front
    private void checkChunkFront(Point3D key, HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<short[]> faceVerts, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        short[] face;
        neighbourChunk = coords.add(Vector.j.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) { // checks if there's an existing chunk in front
            chunkKey = new Point3D(x, 0, z);
            if (!chunkRef.get(neighbourChunk).blocks.isEmpty() && !chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) { // checks if the block exists in the neighbouring chunk
                // adds the face
                face = new short[4];
                checkVert(new Point3D(x, Chunk.SIZE, z), face, 0);
                checkVert(new Point3D(x + 1, Chunk.SIZE, z), face, 1);
                checkVert(new Point3D(x + 1, Chunk.SIZE, z - 1), face, 2);
                checkVert(new Point3D(x, Chunk.SIZE, z - 1), face, 3);
                faceVerts.add(face);
                colors.add(blocks.get(key).getColor(0)[0]);
            }
        }
    }

    // Checks if there's block in the chunk behind
    private void checkChunkBehind(Point3D key, HashMap<Point3D, Chunk> chunkRef, int x, int z, ArrayList<short[]> faceVerts, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        short[] face;

        neighbourChunk = coords.add(Vector.j.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) { // checks if there's an existing chunk behind
            chunkKey = new Point3D(x, Chunk.SIZE - 1, z);
            if (!chunkRef.get(neighbourChunk).blocks.isEmpty() && !chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) { // checks if the block exists in the neighbouring chunk
                // adds the face
                face = new short[4];
                checkVert(new Point3D(x, 0, z), face, 0);
                checkVert(new Point3D(x + 1, 0, z), face, 1);
                checkVert(new Point3D(x + 1, 0, z - 1), face, 2);
                checkVert(new Point3D(x, 0, z - 1), face, 3);
                faceVerts.add(face);
                colors.add(blocks.get(key).getColor(0)[0]);
            }
        }
    }

    // checks if there's a block in the right chunk
    private void checkChunkRight(Point3D key, HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<short[]> faceVerts, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        short[] face;

        neighbourChunk = coords.add(Vector.i.mul(Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) { // checks if there's an existing chunk to the right
            chunkKey = new Point3D(0, y, z);
            if (!chunkRef.get(neighbourChunk).blocks.isEmpty() && !chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) { // checks if the block exists in the neighbouring chunk
                // adds the face
                face = new short[4];
                checkVert(new Point3D(Chunk.SIZE, y, z), face, 0);
                checkVert(new Point3D(Chunk.SIZE, y + 1, z), face, 1);
                checkVert(new Point3D(Chunk.SIZE, y + 1, z - 1), face, 2);
                checkVert(new Point3D(Chunk.SIZE, y, z - 1), face, 3);
                faceVerts.add(face);
                colors.add(blocks.get(key).getColor(0)[0]);
            }
        }
    }

    // checks the left chunk
    private void checkChunkLeft(Point3D key, HashMap<Point3D, Chunk> chunkRef, int y, int z, ArrayList<short[]> faceVerts, ArrayList<Color> colors) {
        Point3D neighbourChunk;
        Point3D chunkKey;
        short[] face;

        neighbourChunk = coords.add(Vector.i.mul(-Chunk.SIZE));
        if (chunkRef.containsKey(neighbourChunk)) { // checks if there's an existing chunk to the left
            chunkKey = new Point3D(Chunk.SIZE - 1, y, z);
            if (!chunkRef.get(neighbourChunk).blocks.isEmpty() && !chunkRef.get(neighbourChunk).blocks.containsKey(chunkKey)) { // checks if the block exists in the neighbouring chunk
                // adds the face
                face = new short[4];
                checkVert(new Point3D(0, y, z), face, 0);
                checkVert(new Point3D(0, y + 1, z), face, 1);
                checkVert(new Point3D(0, y + 1, z - 1), face, 2);
                checkVert(new Point3D(0, y, z - 1), face, 3);
                faceVerts.add(face);
                colors.add(blocks.get(key).getColor(0)[0]);
            }
        }
    }

    // check if there is a face beneath
    private void checkFaceDown(Point3D key, int x, int y, int z, ArrayList<short[]>
            faceVerts, ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.k.mul(-1)))) { // checks if there's a block beneath
            face = new short[4];
            checkVert(new Point3D(x, y, z - 1), face, 0);
            checkVert(new Point3D(x + 1, y, z - 1), face, 1);
            checkVert(new Point3D(x + 1, y + 1, z - 1), face, 2);
            checkVert(new Point3D(x, y + 1, z - 1), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(0)[0]);
        }
    }

    // check if there is a face in front
    private void checkFaceFront(Point3D key, int x, int y, int z, ArrayList<short[]>
            faceVerts, ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.j.mul(-1)))) { // checks if there's a block in front
            face = new short[4];
            checkVert(new Point3D(x, y + 1, z), face, 0);
            checkVert(new Point3D(x + 1, y + 1, z), face, 1);
            checkVert(new Point3D(x + 1, y + 1, z - 1), face, 2);
            checkVert(new Point3D(x, y + 1, z - 1), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(0)[0]);
        }
    }

    // checks if there's a face behind the chunk
    private void checkFaceBehind(Point3D key, int x, int y, int z, ArrayList<short[]>
            faceVerts, ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.j))) {         // checks if there is a block behind
            face = new short[4];
            checkVert(key, face, 0);
            checkVert(new Point3D(x + 1, y, z), face, 1);
            checkVert(new Point3D(x + 1, y, z - 1), face, 2);
            checkVert(new Point3D(x, y, z - 1), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(0)[0]);
        }
    }

    // check if there is a face to the left
    private void checkFaceLeft(Point3D key, int x, int y, int z, ArrayList<short[]>
            faceVerts, ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.i.mul(-1)))) { // checks if there's a block to left
            face = new short[4];
            checkVert(key, face, 0);
            checkVert(new Point3D(x, y + 1, z), face, 1);
            checkVert(new Point3D(x, y + 1, z - 1), face, 2);
            checkVert(new Point3D(x, y, z - 1), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(0)[0]);
        }
    }

    // check if face to right
    private void checkFaceRight(Point3D key, int x, int y, int z, ArrayList<short[]>
            faceVerts, ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.i))) { // check's if there's a block to right
            face = new short[4];
            checkVert(new Point3D(x + 1, y, z), face, 0);
            checkVert(new Point3D(x + 1, y + 1, z), face, 1);
            checkVert(new Point3D(x + 1, y + 1, z - 1), face, 2);
            checkVert(new Point3D(x + 1, y, z - 1), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(0)[0]);
        }
    }

    // check if face is above
    private void checkFaceUp(Point3D key, int x, int y, int z, ArrayList<short[]> faceVerts,
                             ArrayList<Color> colors) {
        short[] face;

        if (!blocks.containsKey(key.add(Vector.k))) { // checks if there's a block above
            face = new short[4];
            checkVert(key, face, 0);
            checkVert(new Point3D(x + 1, y, z), face, 1);
            checkVert(new Point3D(x + 1, y + 1, z), face, 2);
            checkVert(new Point3D(x, y + 1, z), face, 3);
            faceVerts.add(face);
            colors.add(blocks.get(key).getColor(1)[0]);
        }
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (playerRef != null) { // if the player is too far away from the chunk, it de-spawns
            if (Math.abs(Vector.i.dotProd(this.coords.subtract(playerRef.coords))) + Math.abs(Vector.j.dotProd(this.coords.subtract(playerRef.coords))) > renderDistance * SIZE + 10) {
                setInactive();
            }
        }

        if (updateChunk == 1) { // if the chunk is supposed to be updated it updates
            generateMesh(); // updates the chunk
            updateNeighbours();
            updateChunk = 0;
        }

        // if the chunk needs to update any of its out-most layers of blocks
        if (updateBack || updateFront || updateLeft || updateRight) {
            generateCorners();
            if (updateLeft) { // updates the left side of the chunk
                generateLeft();
                // adds the meshes into a full array list of points
                this.meshes[0].createMesh();
                updateLeft = false;
            }
            if (updateBack) { // updates the back of the chunk
                generateBack();
                // adds the meshes into a full array list of points
                this.meshes[1].createMesh();
                updateBack = false;
            }
            if (updateRight) { // updates the right side of the chunk
                generateRight();
                // adds the meshes into a full array list of points
                this.meshes[2].createMesh();
                updateRight = false;
            }
            if (updateFront) { // updates the front of the chunk
                generateFront();
                // adds the meshes into a full array list of points
                this.meshes[3].createMesh();
                updateFront = false;
            }
            // regenerates the mesh
            setMesh();
            this.mesh.faces = (ArrayList<Face>) innerFaces.clone();
            this.mesh.faces.addAll(this.meshes[4].faces);
            for (int i = 0; i < 4; i++) {
                this.mesh.faces.addAll(this.meshes[i].faces);
            }
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
            readChunk(f);
            generateMesh();
            return true;
        }
        return false;
    }

    // removes the chunk from the screen if it's not already active
    public boolean setInactive() {
        if (active) {
            handler.removeObject(this);
            active = false;
            saveChunk(f);
            this.mesh = new Mesh();
            for (int i = 0; i < 5; i++) {
                this.meshes[i] = new Mesh();
            }
            return true;
        }
        return false;
    }

    // returns the color of the shape
    public Color[] getColor() {
        return null;
    }

    // returns the colors of the mesh
    public int[] getMeshColor() {
        if (this.colors != null) {
            return this.colors;
        } else return null;
    }

    // sets the colours of the mesh
    private void setColors() {
        this.colors = new int[meshes[0].colors.size() +
                meshes[1].colors.size() +
                meshes[2].colors.size() +
                meshes[3].colors.size() +
                meshes[4].colors.size() +
                mesh.colors.size()]; // allocates the array

        // adds the contents of every mesh's colours
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

    // sets the raw mesh of the whole chunk
    private void setMesh() {
        Point3D tempPoint;
        float[] tempMesh = new float[(this.mesh.mesh.size() + this.meshes[0].mesh.size() + this.meshes[1].mesh.size() + this.meshes[2].mesh.size() + this.meshes[3].mesh.size() + this.meshes[4].mesh.size()) * 3]; // allocates the array

        // adds the contents of every mesh
        int offset = 0;
        for (int i = 0; i < meshes[4].mesh.size(); i++) {
            tempPoint = meshes[4].mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }
        offset += meshes[4].mesh.size();
        for (int i = 0; i < mesh.mesh.size(); i++) {
            tempPoint = mesh.mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }
        offset += mesh.mesh.size();
        for (int i = 0; i < meshes[0].mesh.size(); i++) {
            tempPoint = meshes[0].mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }
        offset += meshes[0].mesh.size();
        for (int i = 0; i < meshes[1].mesh.size(); i++) {
            tempPoint = meshes[1].mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }
        offset += meshes[1].mesh.size();
        for (int i = 0; i < meshes[2].mesh.size(); i++) {
            tempPoint = meshes[2].mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }
        offset += meshes[2].mesh.size();
        for (int i = 0; i < meshes[3].mesh.size(); i++) {
            tempPoint = meshes[3].mesh.get(i);
            tempMesh[(i + offset) * 3] = (float) tempPoint.x;
            tempMesh[(i + offset) * 3 + 1] = (float) tempPoint.y;
            tempMesh[(i + offset) * 3 + 2] = (float) tempPoint.z;
        }

        this.mesh.setRawMesh(tempMesh); // sets the raw mesh with the temp array
    }
}