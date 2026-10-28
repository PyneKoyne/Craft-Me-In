// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This is the camera class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

// Imports

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Class to manage the camera of the game
public class Camera extends gameObject {
    // Variables
    private final Handler handler;
    private double focalLength;
    private Window window;
    private double focalVel;
    private float[] cameraMemory;
    private volatile float screenX, screenY;
    private Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg;     // image creation
    private volatile int[] pixelData;
    private volatile short[] pixelCount;
    private boolean survival;
    public boolean sceneChanged = true;
    private double dayPercentage = 0;
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private ExecutorService executor = Executors.newFixedThreadPool(10); // threads

    // Constructor of the class, extends gameObject
    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window, boolean survival) {
        super(coords, new Vector(0, 0, 0), id);
        this.focalLength = focal;
        this.handler = handler;
        this.window = window;
        this.survival = survival;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight()); // sets the image
        this.screenX = this.window.getWidth() / 2.0f;
        this.screenY = this.window.getHeight() / 2.0f;
        cameraMemory = new float[]{
                (float) this.norm.x,
                (float) this.norm.y,
                (float) this.norm.z,
                (float) norm.mag(),
                (float) ((-1) * this.rot.x),
                (float) ((-1) * this.rot.y),
                (float) ((-1) * this.rot.z),
                (float) this.rot.w,
                (float) (this.rot.w * this.rot.w - (this.rot.x * this.rot.x + this.rot.y * this.rot.y + this.rot.z * this.rot.z)),
                (float) this.focalLength,
                screenX,
                screenY
        };
        pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        pixelCount = new short[pixelData.length];
    }

    // Retrieves the focal length of the camera
    public double getFocalLength() {
        return focalLength;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focalVel = vel;
    }

    // Retrieves the focal point of the camera
    public Point3D getFocalPoint() {
        return focalPoint;
    }

    // Runs every tick
    public void tick() {
        float[] tempMemory;
        Point3D tempFocal;
        tempMemory = new float[]{
                (float) this.norm.x,
                (float) this.norm.y,
                (float) this.norm.z,
                (float) norm.mag(),
                (float) ((-1) * this.rot.x),
                (float) ((-1) * this.rot.y),
                (float) ((-1) * this.rot.z),
                (float) this.rot.w,
                (float) (this.rot.w * this.rot.w - (this.rot.x * this.rot.x + this.rot.y * this.rot.y + this.rot.z * this.rot.z)),
                (float) this.focalLength,
                screenX,
                screenY
        };

        sceneChanged = !Arrays.equals(tempMemory, cameraMemory);
        cameraMemory = tempMemory;
        tempFocal = this.coords.add(norm.mul(this.focalLength)); // sets the focal point as the coordinates of the camera plus the normal multiplied by the length
        if (!tempFocal.equals(this.focalPoint)) {
            sceneChanged = true;
        }
        this.focalPoint = tempFocal;

        // Changes the focal length based on the focal length velocity
        if (this.focalVel < 0 && this.focalLength < 1) {
            this.focalLength += this.focalLength * this.focalVel / 4;
        } else {
            this.focalLength += this.focalVel / 4;
        }

        this.focalVel /= 4;
        if (survival) {
            dayPercentage += 0.0003;
            if (dayPercentage > 1) {
                dayPercentage = -1;
            }
            sceneChanged = true;
        }
    }

    // Renders the screen
    public void render(Graphics gParent, ArrayGPU[] gpu) {
        Vector tempFocal;
        float[] focal;
        // if the screen size has changed, creates a new canvas
        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()) {
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
            pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
            pixelCount = new short[pixelData.length];
            this.screenX = bufferedImg.getWidth() / 2.0f; // updates the dimension variables of the screen
            this.screenY = bufferedImg.getHeight() / 2.0f;
            sceneChanged = false; // waits until next tick
        }

        if (sceneChanged) { // only renders if the scene has changed
            // resets the image
            Arrays.fill(pixelData, blendColor(0, 0xfffff, Math.abs(dayPercentage)));
            Arrays.fill(pixelCount, (short) 0);
            ArrayList<Future<String>> renders = new ArrayList<>();

            gpu[0].setCamMem(cameraMemory); // sets variables required for computing the screen location of the point in the GPU

            // Loops through all objects
            for (int i = 0; i < handler.object.size(); i++) {
                gameObject tempObject = handler.object.get(i);

                // If the object is a cube, it renders it
                if (tempObject.getMesh() != null && tempObject.getMeshColor() != null) {
                    tempFocal = tempObject.coords.subtract(this.getFocalPoint());
                    if (tempFocal.dotProd(norm) > 0.2 && tempFocal.mag() > Chunk.SIZE * 2){ // if the chunk is behind the user, it doesn't render
                        continue;
                    }

                    focal = tempFocal.toFloat();
                    int[] vectors = gpu[0].runProgram(tempObject.getMesh().points / 3, focal, tempObject.getMesh().points / 3, tempObject.getHash()); // grabs the screen locations of all the points by sending a script to the GPU
                    // renders the game object in a new thread
                    renders.add((Future<String>) executor.submit(new Thread(() -> {
                        int x, y;
                        int[] colors = tempObject.getMeshColor();
                        for (int j = 0; j < tempObject.getMesh().points / 3; j++) {
                            x = (int) Math.round(vectors[j] / 10000.0);
                            y = vectors[j] % 10000;
                            if (x > 0 && x < screenX * 2 - 2 && y > 0 && y < screenY * 2 - 2) {
                                fillRect(pixelData, x, y, colors[j]);
                                fillRect(pixelData, x, y + 1, colors[j]);
                                fillRect(pixelData, x + 1, y, colors[j]);
                                fillRect(pixelData, x + 1, y + 1, colors[j]);
                            }
                        }
                    })));
                }
            }
            // waits for all objects to be rendered
            for (Future<String> f : renders) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // draws the image onscreen
        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.white);

        // Prints the focal-length on screen and number of cosines and tangents applied
        gParent.drawString("Focal Length: " + focalLength, 600, 600);
    }

    // fills a one by two rectangle on the image
    private void fillRect(int[] pixelData, int x, int y, int color) {
        float ratio = (float) ((pixelCount[x + y * this.window.getWidth()] + 1.0) / (pixelCount[x + y * this.window.getWidth()] + 2.0));
        pixelCount[x + y * this.window.getWidth()] ++;
        pixelData[x + y * this.window.getWidth()] = blendColor(color, pixelData[x + y * this.window.getWidth()], ratio); // blends the new colour with the old colour so the order at which pixels are drawn on screen is irrelevant
    }

    // a method to blend two rgb colours together
    private int blendColor(int color1, int color2, double ratio){
        int a1 = (color1 >> 24 & 0xff);
        int r1 = ((color1 & 0xff0000) >> 16);
        int g1 = ((color1 & 0xff00) >> 8);
        int b1 = (color1 & 0xff);

        int a2 = (color2 >> 24 & 0xff);
        int r2 = ((color2 & 0xff0000) >> 16);
        int g2 = ((color2 & 0xff00) >> 8);
        int b2 = (color2 & 0xff);

        int a = (int) ((a1 * (1 - ratio)) + (a2 * ratio));
        int r = (int) ((r1 * (1 - ratio)) + (r2 * ratio));
        int g = (int) ((g1 * (1 - ratio)) + (g2 * ratio));
        int b = (int) ((b1 * (1 - ratio)) + (b2 * ratio));
        return(a << 24 | r << 16 | g << 8 | b);
    }
}
