// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a stone block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Stone extends gameObject {
    // Variables
    private Color color;

    // Dirt constructor which extends game Object
    public Stone(Point3D coords, ID id) {
        super(coords, null, id);
        this.color = new Color(30, 100, 100); // color for stone
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {

    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color[] getColor() {
        return new Color[]{this.color};
    }
}