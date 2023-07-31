// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the camera  class, creating a game object of which points can be displayed on screen according to its location and rotation

package main;

import java.awt.*;
import java.util.Random;

public class Camera extends gameObject {

    private Handler handler;
    public double focal_length;
    public double size;
    public Window window;
    public double focal_vel;
    public boolean locked = true;
    public int cos = 0;
    public int tan = 0;

    public Camera(Point3D coords, double focal, ID id, Handler handler, Window window) {
        super(coords, new Vector(0, 0, 0), id);
        this.focal_length = focal;
        this.handler = handler;
        this.window = window;
    }
    
    public double getFocal() {
    	return focal_length;
    }

    // Sets Focal Length Change Rate
    public void setFocalVel(double vel) {
        focal_vel = vel;
    }

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
        coords = coords.add(vel.mul(1));

        // Changes the focal length based on the focal length velocity
        focal_length += focal_vel;
        focal_vel /= 4;
    }

    public void switchLock(){
        locked = !locked;
    }

    // Renders the screen
    public void render(Graphics g) {
    	Point3D focalPoint = coords.add(getNorm().normalize(getFocal()));
    	Point centre = window.centre();
    	int screenX = window.getWidth() / 2;
    	int screenY = window.getHeight() / 2;
    	
        // Loops through all objects
        for(int i = 0; i < handler.object.size(); i ++) {
            gameObject tempObject = handler.object.get(i);

            // If the object is a cube, it renders it
            if (tempObject.getid() == ID.Cube) {
                Cube cube = (Cube) tempObject;

                // Finds the mesh
            	Point3D[] mesh = cube.getMesh().getPoints();

                // Sets the colour to the colour of the object
                g.setColor(cube.getColor());
            	for (Point3D p: mesh) {

                    // Calculates where on screen the point should map to
            		Point3D camP = p.screenOrthoCoordinates(this, focalPoint, getNorm(), cos, tan);
            		g.fillRect((int) (camP.getY() * 40 + screenX), (int) (camP.getZ() * 40 + screenY), 2, 2);
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
            setRot(getRot().add(new Vector(0, (centre.getY() - p.getY())/1000, (centre.getX() - p.getX())/1000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove((int) (centre.getX()), (int) (centre.getY()));

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

}
