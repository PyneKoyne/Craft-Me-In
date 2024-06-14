// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the camera class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

// Imports
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

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
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    // Constructor of the class, extends gameObject
    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focalLength = focal;
        this.handler = handler;
        this.window = window;
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
        this.focalPoint = this.coords.add(norm.mul(this.focalLength)); // sets the focal point as the coordinates of the camera plus the normal multiplied by the length

        // Changes the focal length based on the focal length velocity
        if (this.focalVel < 0 && this.focalLength < 1) {
            this.focalLength += this.focalLength * this.focalVel / 4;
        } else {
            this.focalLength += this.focalVel / 4;
        }

        this.focalVel /= 4;
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
    }

    // Renders the screen
    public void render(Graphics gParent, ArrayGPU[] gpu) {
        this.screenX = this.window.getWidth() / 2;
        this.screenY = this.window.getHeight() / 2;

        // if the screen size has changed, creates a new canvas
        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()){
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
        }

        // resets the image
        int[] pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixelData, 0x000000);

        gpu[0].setCamMem(cameraMemory); // sets variables required for computing the screen location of the point in the GPU

        // Loops through all objects
        for (int i = 0; i < handler.object.size(); i++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getMesh() != null && tempObject.getColor() != null) {
                float[] focal = tempObject.coords.subtract(this.getFocalPoint()).toFloat();
                float[] vectors = gpu[0].runProgram(tempObject.getMesh().points, focal, tempObject.getMesh().points/3, tempObject.getHash());
                Color[] colors = tempObject.getColor();

                for (int j = 0; j < tempObject.getMesh().points / 3; j++) {
                    if (vectors[j * 3 + 1] > 0 && vectors[j * 3 + 2] > 0) {
                        fillRect(pixelData, (int) vectors[j * 3 + 1], (int) vectors[j * 3 + 2], colors[j]);
                        fillRect(pixelData, (int) vectors[j * 3 + 1], (int) vectors[j * 3 + 2], colors[j]);
                    }
                }
            }
        }

        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.black);

        // Prints the focal-length on screen and number of cosines and tangents applied
        gParent.drawString("Focal Length: " + focalLength, 600, 600);
        gParent.drawString("Coordinates: " + coords, 600, 625);
    }

    // fills a one by two rectangle on the image
    private void fillRect(int[] pixelData, int x, int y, Color color) {
        pixelData[x + y * screenX * 2] = color.getRGB();
        pixelData[x + 1 + (y + 1) * screenX * 2] = color.getRGB();
    }
}
