// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: Creates a gameObject which acts as a dirt block

package main;

import java.awt.*;

// Creates a Cube on screen
public class Dirt extends gameObject {
    // Variables
    private static final Color[][] COLOR = new Color[][]{{new Color(78, 153, 82)}, {new Color(150, 75, 0)}};

    // Dirt constructor which extends game Object
    public Dirt(Point3D coords, ID id) {
        super(coords, null, id);
    }

    // tick method from handler
    public void tick() {
    }

    // render method from handler
    public void render(Graphics g, ArrayGPU[] gpu) {
    }

    // returns the color of the shape
    public Color[] getColor(int orientation) {
        if (orientation == 1){
            return Dirt.COLOR[0];
        }
        return Dirt.COLOR[1];
    }
}