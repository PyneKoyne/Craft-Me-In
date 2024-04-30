// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the camera  class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

import com.aparapi.Kernel;
import com.aparapi.Range;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Camera extends gameObject {

    private final Handler handler;
    public double focal_length;
    public Window window;
    public double focal_vel;
    public boolean[] movement = {false, false, false, false};
    public boolean locked = true;
    public int cos = 0, tan = 0;
    public int screenX = 0, screenY = 0;
    public Camera thisCamera = this;
    public Point3D focalPoint = Point3D.zero;

    public static String screenSRC = "__kernel void " +
            "sampleKernel(__global const Point3D *p," +
            "             __global const float *b," +
            "             __global float *c)" +
            "{" +
            "    int gid = get_global_id(0);" +
            "    c[gid] = a[gid] * b[gid + 1] - a[gid + 1] * b[gid];" +
            "}";

    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focal_length = focal;
        this.handler = handler;
        this.window = window;
    }
    
    public double getFocalLength() {
    	return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
    }

    public Point3D getFocalPoint() {return focalPoint;}

    // Sets the number of cosines applied in the projection
    public void setCos(int cos) {
        if (cos < 0){
            cos = 0;
        }
        this.cos = cos;
    }

    // Sets the number of tangents applied in the projection
    public void setTan(int tan) {
        if (tan < 0){
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

        coords = coords.add(vel);
        vel = vel.mul(0.1);
        focalPoint = this.coords.add(norm.mul(this.focal_length));

        // Changes the focal length based on the focal length velocity
        if (focal_vel < 0 && focal_length < 1) {
            focal_length += focal_length * focal_vel / 4;
        }
        else{
            focal_length += focal_vel/4;
        }

        focal_vel /= 4;
    }

    public void switchLock(){
        locked = !locked;
    }

    // Renders the screen
    public void render(Graphics g, HashMap<String, ArrayGPU> gpu) {

        screenX = window.getWidth() / 2;
    	screenY = window.getHeight() / 2;
        List<Range> ranges = new ArrayList<>();
        // Loops through all objects
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getid() == ID.Cube || tempObject.getid() == ID.Plane) {

                // Finds the mesh
                Vector relativeFocal = tempObject.coords.subtract(this.getFocalPoint());
                Vector[] mesh = Point3D.screenOrthoCoordinatesTotal(this, tempObject.getMesh().points, relativeFocal, tempObject.getHash(), gpu);
                g.setColor(tempObject.getColor());
//                gameObject cube = tempObject;
//
//                // Finds the mesh
//                Point3D[] mesh = cube.getMesh().getPoints();
//
//                // Sets the colour to the colour of the object
//                g.setColor(cube.getColor());
//                for (Point3D p: mesh) {
//
//                    // Calculates where on screen the point should map to
//                    Vector camPoint = p.screenOrthoCoordinates(this, this.focalPoint.toVect(), 0, 0);
//                    if (camPoint !=  null){
//                        g.fillRect((int) (camPoint.getY() + screenX), (int) (camPoint.getZ() + screenY), 2, 2);
//                    }
//                }

//                Vector[] renders = new Vector[mesh.length];
//                Sets the colour to the colour of the object
//                Kernel kernel = new Kernel() {
//                    @Override
//                    public void run() {
//                        int i = getGlobalId();
//                        renders[i] = mesh[i].screenOrthoCoordinates(thisCamera, relativeFocal, cos, tan);
//                    }
//                };
//                ranges.add(Range.create(renders.length));
//                kernel.execute(ranges.getLast());
                for (Vector p: mesh) {
                    // Calculates where on screen the point should map to
                    if (p !=  null){
                        g.fillRect((int) (p.getY() + screenX), (int) (p.getZ() + screenY), 2, 2);
                    }
                }
            }
        }

        g.setColor(Color.black);

        // Prints the focal-length on screen and number of cosines and tangents applied
        g.drawString("Focal Length: " + focal_length, 600, 600);
        g.drawString("Coordinates: " + coords, 600, 625);
        g.drawString("# of Cos Applied: " + cos, 600, 650);
        g.drawString("# of Tan Applied: " + tan, 600, 675);

        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            setRot(getAngles().add(new Vector(0, (-screenY + p.getY() - window.screenLoc().y)/1000, (screenX - p.getX() + window.screenLoc().x)/1000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX + window.screenLoc().x, screenY + window.screenLoc().y);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }
}
