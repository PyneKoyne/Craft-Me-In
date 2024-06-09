// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This class creates a data structure which handles all game objects

package main;

import java.awt.*;
import java.util.LinkedList;

// Handler Class
public class Handler {
    // A list of all the gameObjects
    public LinkedList<gameObject> object = new LinkedList<gameObject>();
    public ArrayGPU[] gpu = new ArrayGPU[3];
//    ExecutorService executorService = Executors.newFixedThreadPool(1);

    // Ticks and renders every game object
    public void tick(){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            tempObject.tick();
//            executorService.execute(tempObject::tick);
        }
    }
    public void render(Graphics g){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            tempObject.render(g, gpu);
        }
    }
    //Adds a gameObject to the list
    public void addObject(gameObject object){
        this.object.add(object);
        // adds the mesh of the game object into the gpu memory
        regenerateObject(object);
    }

    public void regenerateObject(gameObject object){
        Mesh tempMesh = object.getMesh();
        if (tempMesh != null) {
            gpu[0].allocateMemory(tempMesh.points, tempMesh.rawMesh, 15, object.getHash());
        }
    }

    //Removes a gameObject from the list
    public void removeObject(gameObject object){
        this.object.remove(object);
    }
}
