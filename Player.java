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
    private final Window window;
    public boolean[] movement = {false, false, false, false};
    public boolean locked = true;
    private final Color color;

    public Player(Point3D p, float scale, ID id, Handler handler, Color color, Window window){
        super(p, new Vector(0, 0, 0), id);
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
        this.coords = this.coords.add(this.vel);
        this.vel = this.vel.mul(0.1);

        camera.coords = this.coords;
        camera.rot = this.rot;
        camera.setNorm(this.norm);
        camera.setUp(this.up);
        camera.setLeft(this.left);

        // Moves the mouse to the centre of the screen if not shift locked
        if (locked) {
            // Finds the difference in mouse coordinates
            Point p = MouseInfo.getPointerInfo().getLocation();
            int screenX = this.window.getWidth() / 2;
            int screenY = this.window.getHeight() / 2;
            setRot(getAngles().add(new Vector(0, (screenY - p.getY() + window.screenLoc().y) / 2000, (screenX - p.getX() + window.screenLoc().x) / 2000)));

            try {
                Robot robot = new Robot();
                robot.mouseMove(screenX + window.screenLoc().x, screenY + window.screenLoc().y);

            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    public void render(Graphics g, ArrayGPU[] gpu) {

    }

    public void switchLock() {
        locked = !locked;
    }


    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}