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
    public double focal_length;
    public Window window;
    public double focal_vel;
    public boolean[] movement = {false, false, false, false};
    public boolean locked = true;
    public int cos = 0, tan = 0;
    public int screenX = 0, screenY = 0;
    public Point3D focalPoint = Point3D.zero;
    private BufferedImage bufferedImg; //     // image creation
    private final GraphicsConfiguration CONFIG = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();

    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focal_length = focal;
        this.handler = handler;
        this.window = window;
        bufferedImg = CONFIG.createCompatibleImage(window.getWidth(), window.getHeight());
    }

    public double getFocalLength() {
        return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
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

        if (movement[0]) addForce(norm.mul(0.1));
        if (movement[1]) addForce(left.mul(-0.1));
        if (movement[2]) addForce(norm.mul(-0.1));
        if (movement[3]) addForce(left.mul(0.1));

        this.coords = this.coords.add(this.vel);
        this.vel = this.vel.mul(0.1);
        this.focalPoint = this.coords.add(norm.mul(this.focal_length));

        // Changes the focal length based on the focal length velocity
        if (this.focal_vel < 0 && this.focal_length < 1) {
            this.focal_length += this.focal_length * this.focal_vel / 4;
        } else {
            this.focal_length += this.focal_vel / 4;
        }

        this.focal_vel /= 4;

        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            setRot(getAngles().add(new Vector(0, (-screenY + p.getY() - window.screenLoc().y) / 2000, (screenX - p.getX() + window.screenLoc().x) / 2000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX + window.screenLoc().x, screenY + window.screenLoc().y);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void switchLock() {
        locked = !locked;
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
                (float) Math.sqrt(norm.x * norm.x + norm.y * norm.y + norm.z * norm.z),
                (float) ((-1) * this.rot.x),
                (float) ((-1) * this.rot.y),
                (float) ((-1) * this.rot.z),
                (float) this.rot.w,
                (float) (this.rot.w * this.rot.w - (this.rot.x * this.rot.x + this.rot.y * this.rot.y + this.rot.z * this.rot.z)),
                (float) this.focal_length,
                (float) screenX,
                (float) screenY
        });

        // Loops through all objects
        for (int i = 0; i < handler.object.size(); i++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getid() == ID.Cube || tempObject.getid() == ID.Plane) {
                float[] focal = tempObject.coords.subtract(this.getFocalPoint()).toFloat();
                Point3D.screenOrthoCoordinatesTotal(tempObject.getMesh().points, focal, tempObject.getHash(), gpu, pixelData, window.getWidth(), tempObject.getColor().getRGB());
            }
        }
//        g.dispose();

        gParent.drawImage(bufferedImg, 0, 0, null);
        gParent.setColor(Color.black);

        // Prints the focal-length on screen and number of cosines and tangents applied
        gParent.drawString("Focal Length: " + focal_length, 600, 600);
        gParent.drawString("Coordinates: " + coords, 600, 625);
        gParent.drawString("# of Cos Applied: " + cos, 600, 650);
        gParent.drawString("# of Tan Applied: " + tan, 600, 675);
    }
}
