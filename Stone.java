// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a stone block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Stone extends gameObject {
    // Variables
    private static final Color[] COLOR = new Color[]{new Color(30, 100, 100)};

    // Dirt constructor which extends game Object
    public Stone(Point3D coords, ID id) {
        super(coords, null, id);
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {

    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color[] getColor(int orientation) {
        return Stone.COLOR;
    }
}