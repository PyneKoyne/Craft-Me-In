// Author: Kenny Z & Anish Nagariya
// Date: June 11th
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a dirt block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Dirt extends gameObject {
    // Variables
    private Color color;

    // Dirt constructor which extends game Object
    public Dirt(Point3D coords, ID id) {
        super(coords, null, id);
        this.color = new Color(78, 153, 82); // color for grass
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        
    }

    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color getColor() {
        return color;
    }
}