// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a dirt block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Dirt extends gameObject {
    // Variables
    private Color color;

    // Cube constructor which extends game Object
    public Dirt(Point3D coords, ID id, Color color) {
        super(coords, null, id);

        // constructs the mesh
        this.color = color;
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