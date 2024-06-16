// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
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
    // Global Variables
    private final Handler handler;
    public double focalLength;
    public Window window;
    public double focalVel;
    public float[] cameraMemory;
    public int cos = 0, tan = 0;
    public int screenX = 0, screenY = 0;
    public Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg; //     // image creation
    private volatile int[] pixelData;
    private volatile short[] pixelCount;
    private boolean survival;
    public boolean sceneChanged = true;
    public double dayPrecentage = 0;
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
    private ExecutorService executor = Executors.newFixedThreadPool(30);

    // Constructor of the class, extends gameObject
    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window, boolean survival) {
        super(coords, new Vector(0, 0, 0), id);
        this.focalLength = focal;
        this.handler = handler;
        this.window = window;
        this.survival = survival;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight()); // sets the image
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
                (float) screenX,
                (float) screenY
        };
        pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
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

    // Sets the number of cosines applied in the projection
    public void setCos(int cos) {
        if (cos < 0) {
            cos = 0;
        }
        this.cos = cos;
    }

    // Sets the number of tangents applied in the projection
    public void setTan(int tan) {
        if (tan < 0) {
            tan = 0;
        }
        this.tan = tan;
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
                (float) screenX,
                (float) screenY
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
            dayPrecentage += 0.0003;
            if (dayPrecentage > 1) {
                dayPrecentage = -1;
            }
            sceneChanged = true;
        }
    }

    // Renders the screen
    public void render(Graphics gParent, ArrayGPU[] gpu) {
        this.screenX = this.window.getWidth() / 2;
        this.screenY = this.window.getHeight() / 2;
        Vector tempFocal;
        float[] focal;

        // if the screen size has changed, creates a new canvas
        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()) {
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
            pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        }

        if (sceneChanged) {
            // resets the image
            Arrays.fill(pixelData, blendColor(0, 0xfffff, Math.abs(dayPrecentage)));
            ArrayList<Future<String>> renders = new ArrayList<>();
            pixelCount = new short[pixelData.length];

            gpu[0].setCamMem(cameraMemory); // sets variables required for computing the screen location of the point in the GPU

            // Loops through all objects
            for (int i = 0; i < handler.object.size(); i++) {
                gameObject tempObject = handler.object.get(i);

                // If the object is a cube, it renders it
                if (tempObject.getMesh() != null && tempObject.getMeshColor() != null) {
                    tempFocal = tempObject.coords.subtract(this.getFocalPoint());
                    if (tempFocal.dotProd(norm) > 0.2 && tempFocal.mag() > Chunk.SIZE * 1.5){
                        continue;
                    }

                    focal = tempFocal.toFloat();
                    float[] vectors = gpu[0].runProgram(tempObject.getMesh().points / 3, focal, tempObject.getMesh().points / 3, tempObject.getHash());
                    renders.add((Future<String>) executor.submit(new Thread(() -> {
                        int x, y;
                        int[] colors = tempObject.getMeshColor();
                        for (int j = 0; j < tempObject.getMesh().points / 3; j++) {
                            x = Math.round(vectors[j] / 10000);
                            y = Math.round(vectors[j] % 10000);
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
            for (Future<String> f : renders) {
                try {
                    f.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.white);

        // Prints the focal-length on screen and number of cosines and tangents applied
//        gParent.drawString("Focal Length: " + focalLength, 600, 600);
//        gParent.drawString("Coordinates: " + coords, 600, 625);
    }

    // fills a one by two rectangle on the image
    private void fillRect(int[] pixelData, int x, int y, int color) {
        float ratio = (float) ((pixelCount[x + y * screenX * 2] + 1.0) / (pixelCount[x + y * screenX * 2] + 2.0));
        pixelCount[x + y * screenX * 2]++;
        pixelData[x + y * screenX * 2] = blendColor(color, pixelData[x + y * screenX * 2], ratio);
    }

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
