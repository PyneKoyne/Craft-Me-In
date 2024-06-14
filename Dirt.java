// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a dirt block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Dirt extends gameObject {
    // Variables
    private static final Color COLOR = new Color(78, 153, 82);

    // Dirt constructor which extends game Object
    public Dirt(Point3D coords, ID id) {
        super(coords, null, id);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        
    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color[] getColor() {
        return new Color[]{Dirt.COLOR};
    }
}