// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the cube class, creating a game object which is really just a plane currently

package main;

import java.awt.*;
import java.util.HashMap;

public class Player extends gameObject{
    private final Handler handler;
    private final Camera camera;
    private final Vector CAMERA_OFFSET = new Vector(0, 0, 4);
    private final Window window;
    public boolean[] movement = {false, false, false, false};
    public boolean locked = true;
    private final Color color;

    public Player(Point3D p, float scale, ID id, Handler handler, Color color, Window window){
        super(p, new Vector(Math.PI, 0, 0), id);
        this.color = color;
        this.handler = handler;
        this.window = window;
        this.camera = new Camera(p, 1, ID.Camera, handler, window);
        handler.addObject(this.camera);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        if (movement[0]) addForce(norm.mul(0.1));
        if (movement[1]) addForce(left.mul(-0.1));
        if (movement[2]) addForce(norm.mul(-0.1));
        if (movement[3]) addForce(left.mul(0.1));

        for (gameObject tempObject: handler.object){
            if (tempObject.getMesh() != null){
                for (Face face: tempObject.getMesh().faces){
                    Vector distance = tempObject.coords.add(face.centre).subtract(this.coords);
                    if (true) {
                        if (face.norm.dotProd(vel) < 0 && face.norm.dotProd(distance) > 0 && face.norm.dotProd(distance) + (face.norm.dotProd(vel)) < 0) {
                            this.vel = this.vel.mul(0.6);
                            addForce(face.norm.mul(-face.norm.dotProd(vel) / (face.norm.mag())));
                        }
                        if (face.norm.dotProd(vel) > 0 && face.norm.dotProd(distance) < 0 && face.norm.mul(-1).dotProd(distance) + (face.norm.mul(-1).dotProd(vel)) < 0) {
                            this.vel = this.vel.mul(0.6);
                            addForce(face.norm.mul(face.norm.mul(-1).dotProd(vel) / (face.norm.mag())));
                        }
                    }
                }
            }
        }

        this.coords = this.coords.add(this.vel);
        this.vel = this.vel.mul(0.5 );
//        this.addForce(new Vector(0, 0, -0.02));

        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            int screenX = this.window.getWidth() / 2;
            int screenY = this.window.getHeight() / 2;
            setRot(getAngles().add(new Vector(0, (-screenY + p.getY() - window.screenLoc().y) / 2000, (screenX - p.getX() + window.screenLoc().x) / 2000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX + window.screenLoc().x, screenY + window.screenLoc().y);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }

        camera.coords = this.coords.add(CAMERA_OFFSET);
    }

    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    public void switchLock() {
        locked = !locked;
    }

    public void setRot(Vector rot) {
        // Rotations over 360 degrees are modul-ised
        this.roll = rot.getX() % (2 * Math.PI);
        this.yaw = rot.getZ() % (2 * Math.PI);

        if (camera != null) {
            camera.roll = this.roll;
            camera.pitch += rot.getY() % (2 * Math.PI);
            camera.yaw = this.yaw;
            camera.updateRot();
        }

        updateRot();
    }


    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}