// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class creates a data structure which handles all game objects

package main;

import java.awt.*;
import java.util.LinkedList;

// Handler Class
public class Handler {
    // A list of all the gameObjects
    LinkedList<gameObject> object = new LinkedList<gameObject>();

    // Ticks and renders every game object
    public void tick(){
        for(int i = 0; i < object.size(); i ++){
            gameObject tempObject = object.get(i);

            tempObject.tick();
        }
    }
    public void render(Graphics g){
        for(int i = 0; i < object.size(); i ++) {
            gameObject tempObject = object.get(i);

            tempObject.render(g);
        }
    }
    //Adds a gameObject to the list
    public void addObject(gameObject object){
        this.object.add(object);
    }
    //Removes a gameObject from the list
    public void removeObject(gameObject object){
        this.object.remove(object);
    }
}
