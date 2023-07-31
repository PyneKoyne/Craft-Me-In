// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This is the cube class, creating a game object which is really just a plane currently

package main;

import java.awt.*;

public class Cube extends gameObject{
    private Handler handler;
    private Color color;
    
    
    public Cube(Point3D p, ID id, Handler handler, Color color){
        super(p, new Vector(1, 0, 0), id);
        this.handler = handler;

        Point3D[] verts = {new Point3D(p.x, p.y, p.z), new Point3D(p.x + 2, p.y, p.z), new Point3D(p.x + 2, p.y + 2, p.z), new Point3D(p.x, p.y + 2, p.z)};
        Point[] edges = {new Point(0, 1), new Point(1, 2), new Point(2, 3), new Point(3, 0)};
        Face[] faces = {new Face(new int[]{0, 1, 2, 3})};

        this.mesh = new Mesh(verts, edges, faces);
        this.color = color;
        mesh.createMesh();
        
    }

    // changes its coordinates every tick based on its velocity
    public void tick() {
        coords.add(vel);
    }

    public void render(Graphics g) {

    }

    // returns the color of the shape
    public Color getColor(){
        return color;
    }

}