// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates a data structure which handles all game objects

package main;

import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Handler Class
public class Handler {
    // A list of all the gameObjects
    LinkedList<gameObject> object = new LinkedList<gameObject>();
    HashMap<String, ArrayGPU> gpu = new HashMap<>();
    ExecutorService executorService = Executors.newFixedThreadPool(1);

    // Ticks and renders every game object
    public void tick(){
        for (gameObject tempObject : object) {
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
        for (ArrayGPU tempGPU: gpu.values()){
            Mesh tempMesh = object.getMesh();
            if (tempMesh != null) {
                tempGPU.allocateMemory(tempMesh.points, tempMesh.rawMesh, 12, object.getHash());
            }
        }
    }
    //Removes a gameObject from the list
    public void removeObject(gameObject object){
        this.object.remove(object);
    }
}
