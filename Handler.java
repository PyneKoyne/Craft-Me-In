// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class creates a data structure which handles all game objects

package main;

import java.awt.*;
import java.util.LinkedList;

// Handler Class
public class Handler {
    // A list of all the gameObjects
    public LinkedList<gameObject> object = new LinkedList<gameObject>();
    public ArrayGPU[] gpu = new ArrayGPU[3]; // gpu rendering accessible for the game objects

    // Ticks and renders every game object
    public void tick(){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            tempObject.tick();
        }
    }

    // causes every game object to render every frame
    public void render(Graphics g){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            tempObject.render(g, gpu);
        }
    }
    //Adds a gameObject to the list
    public void addObject(gameObject object){
        this.object.add(object);
        changeScene();
    }

    // regenerates an objects gpu stored mesh in case it is updated but has not been removed
    public void regenerateObject(gameObject object){
        // adds the mesh of the game object into the gpu memory
        Mesh tempMesh = object.getMesh();
        if (tempMesh != null) {
            gpu[0].unallocateMemory(object.getHash());
            gpu[0].allocateMemory(tempMesh.points, tempMesh.rawMesh, object.getHash());
        }
        changeScene();
    }

    //Removes a gameObject from the list
    public void removeObject(gameObject object){
        this.object.remove(object);
        gpu[0].unallocateMemory(object.getHash());
        changeScene();
    }

    // Tells any camera objects that the scene has changed
    private void changeScene(){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);
            if (tempObject.getId() == ID.Camera){
                Camera c = (Camera) tempObject;
                c.sceneChanged = true;
            }
        }
    }
}
