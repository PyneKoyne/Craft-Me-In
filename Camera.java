// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the camera  class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;

public class Camera extends gameObject {
    private final Handler handler;
    public double focalLength;
    public Window window;
    public double focalVel;
    public int cos = 0, tan = 0;
    public int screenX = 0, screenY = 0;
    public Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg; //     // image creation
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focalLength = focal;
        this.handler = handler;
        this.window = window;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
    }

    public double getFocalLength() {
        return focalLength;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focalVel = vel;
    }

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

    // Moves every tick
    public void tick() {
        this.focalPoint = this.coords.add(norm.mul(this.focalLength));

        // Changes the focal length based on the focal length velocity
        if (this.focalVel < 0 && this.focalLength < 1) {
            this.focalLength += this.focalLength * this.focalVel / 4;
        } else {
            this.focalLength += this.focalVel / 4;
        }

        this.focalVel /= 4;
    }

    // Renders the screen
    public void render(Graphics gParent, ArrayGPU[] gpu) {
        this.screenX = this.window.getWidth() / 2;
        this.screenY = this.window.getHeight() / 2;

        if (bufferedImg.getHeight() != window.getHeight() || bufferedImg.getWidth() != window.getWidth()){
            bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
        }

        int[] pixelData = ((DataBufferInt) bufferedImg.getRaster().getDataBuffer()).getData();
        Arrays.fill(pixelData, 0xd3d3d3);

        gpu[0].setCamMem(new float[]{
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
        });

        // Loops through all objects
        for (int i = 0; i < handler.object.size(); i++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getMesh() != null) {
                float[] focal = tempObject.coords.subtract(this.getFocalPoint()).toFloat();
                float[] vectors = gpu[0].runProgram(tempObject.getMesh().points, focal, tempObject.getMesh().points/3, tempObject.getHash());

                for (int j = 0; j < tempObject.getMesh().points / 3; j++) {
                    int[] renderPoint = new int[]{(int) vectors[j * 3 + 1], (int) vectors[j * 3 + 2]};
                    if (renderPoint[0] > 0 && renderPoint[1] > 0) {
                        fillRect(pixelData, tempObject, renderPoint);
                        fillRect(pixelData, tempObject, renderPoint);
                    }
                }
            }
        }
//        g.dispose();

        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.black);

        // Prints the focal-length on screen and number of cosines and tangents applied
        gParent.drawString("Focal Length: " + focalLength, 600, 600);
        gParent.drawString("Coordinates: " + coords, 600, 625);
        gParent.drawString("# of Cos Applied: " + cos, 600, 650);
        gParent.drawString("# of Tan Applied: " + tan, 600, 675);
    }

    private void fillRect(int[] pixelData, gameObject tempObject, int[] loc) {
        pixelData[loc[0] + loc[1] * screenX * 2] = tempObject.getColor().getRGB();
        pixelData[loc[0] + 1 + (loc[1] + 1) * screenX * 2] = tempObject.getColor().getRGB();
    }
}
