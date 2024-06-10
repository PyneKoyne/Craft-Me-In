// Author: Kenny Z
// Date: June 14th
// Program Name: Craft Me In
// Description: This is the main class of the engine, running the game loop and calling all game object methods, as well as listeners

package main;

// Imports
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.HashMap;

// Class to start the main engine which starts the game
public class Engine extends Canvas implements Runnable{
    private static final long serialVersionUID = 1L;
    public static int WIDTH = 1280, HEIGHT = 768; //Dimensions

    //Variables
    private Thread thread;
    private boolean running = false;
    private final Handler handler;
    private final Window window;
    private final HashMap<Point3D, Chunk> chunkHashMap;
    private final Player player;

    //To start and stop the loop
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true;
    }
    public synchronized void stop(){
        try{
            running = false;
            thread.join();
            window.dispose();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Main Class
    public Engine(){
        handler = new Handler(); // Creates a handler class to manage all our game objects
        handler.gpu[0] = new ArrayGPU(); // sets the GPU integration of the handler
        handler.gpu[0].startProgram(ArrayGPU.projectionSource); // starts the

        this.chunkHashMap = new HashMap<>();

        // Adds KeyInputs
        this.addKeyListener(new KeyInput(handler));

        // Adds mouse inputs for placing and removing blocks
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1) { // left click
                    player.placeBlock();
                }
                if (e.getButton() == 3) { // right click
                    player.removeBlock();
                }
            }
        });

        // Mouse Wheel Listener
        this.addMouseWheelListener(e -> {
        	int notches = e.getWheelRotation();
        	
            // Changes the focal length of the camera to zoom in and out
            for(int i = 0; i < handler.object.size(); i ++){
            	gameObject tempObject = handler.object.get(i);
            	if (tempObject.id == ID.Camera) {
            		Camera camera = (Camera) tempObject;

                    // Sets focal length velocity change to make it more seamless
            		camera.setFocalVel(-0.2 * notches);
            	}
            }
        });
        
        //Starts the Window
        this.window = new Window(WIDTH, HEIGHT, "Craft Me In", this);

        //Places the Camera
        player = new Player(new Point3D(0, 0, 10), 0.2F, ID.Player, handler, Color.black, window);
        handler.addObject(player);

        // Places cubes which are actually planes
//        handler.addObject(new Cube(new Point3D(10, 10, -8), 10, ID.Cube, handler, Color.black));
//        handler.addObject(new Plane(new Point3D(-20, -20, -20), 1, ID.Plane, handler, Color.black));
    }

    //Game Loop
    public void run(){

        // Time variables to control the game loop
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;

        // Game Loop
        while(running){

            // Adds to Delta Time
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            // Ticks all game objects
            while(delta >=1)
            {
                tick();
                delta--;
            }
            if(running) {
                render();
            }
            frames++;

            // Prints every second
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;

                //Prints the FPS into the console
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        } // end of game loop
        handler.gpu[0].closeGPU(); // closes GPU integration
    }

    // Method which runs every 60th of a second
    private void tick(){
		// Finds the width and height of the screen every tick
    	WIDTH = window.getWidth();
        HEIGHT = window.getHeight();

        if (player != null) {
            for (int i = 0; i < Chunk.render_distance; i++){
                for (double theta = 0; theta < 2 * Math.PI; theta += Math.atan((double)1/i)){
                    Point3D tempKey = new Point3D(
                            (int) (Math.round((player.coords.x + (i * Chunk.SIZE) * Math.cos(theta))/Chunk.SIZE) * Chunk.SIZE),
                            (int) (Math.round((player.coords.y + (i * Chunk.SIZE) * Math.sin(theta))/Chunk.SIZE) * Chunk.SIZE),
                            0
                    );
                    if (chunkHashMap.containsKey(tempKey)) {
                        chunkHashMap.get(tempKey).setActive();
                    }
                    else {
                        chunkHashMap.put(tempKey, new Chunk(tempKey, ID.Chunk, handler, 0, player, chunkHashMap));
                    }
                }
            }
        }
        handler.tick();
    }

    // Method which runs every single frame to draw all game objects
    private void render(){
        // To render the Screen every frame
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null){
            this.createBufferStrategy(3);
            return;
        }
        
        //Sets the background for the game
        Graphics g = bs.getDrawGraphics();
        g.setColor(Color.lightGray);
        g.fillRect(0, 0, WIDTH, HEIGHT);
		
        //Renders the Game
        handler.render(g);
        g.dispose();
        bs.show();
    }

    // creates new Engine
    public static void main(){
        new Engine();
    }
}