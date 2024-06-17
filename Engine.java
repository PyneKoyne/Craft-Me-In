// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This is the main class of the engine, running the game loop and calling all game object methods, as well as listeners

package main;

// Imports
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

// Class to start the main engine which starts the game
public class Engine extends Canvas implements Runnable{
    //Variables
    public static int WIDTH = 1280, HEIGHT = 768; //Dimensions
    private Thread thread;
    private boolean running = false; // boolean for the loop
    private final Handler handler; // handler for the game objects
    private final Window window;
    private final HashMap<Point3D, Chunk> chunkHashMap;
    private final Player player;

    //To start the thread and consequently the game
    public synchronized void start(){
        thread = new Thread(this);
        thread.start();
        running = true; // starts the game loop
    }
    // To stop the game and the window
    public synchronized void stop(){
        try{
            running = false; // stops the game loop
            thread.join(); // stops the thread
            window.dispose(); // destroys the GUI
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Main Class
    public Engine(boolean survival) throws IOException {
        Files.createDirectories(Paths.get(Chunk.CHUNK_PATH));
        handler = new Handler(); // Creates a handler class to manage all our game objects
        handler.gpu[0] = new ArrayGPU(); // sets the GPU integration of the handler
        handler.gpu[0].startProgram(ArrayGPU.projectionSource); // starts the

        this.chunkHashMap = new HashMap<>(); // the chunk hashmap

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
        player = new Player(new Point3D(0, 0, 30), ID.Player, handler, window, survival);
        handler.addObject(player);

        handler.addObject(new Cube(new Point3D(10, 10, 300), 10, ID.Cube, handler, Color.yellow));
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
        int seconds = 0;

        // Game Loop
        while(running){

            // Adds to Delta Time
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;

            // Ticks all game objects
            while(delta >=1)
            {
                try {
                    tick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                delta--;
            }
            if(running) {
                render();
            }
            frames++;

            // Prints every second
            if(System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
                seconds += 1;
                if (seconds > 20){
                    (new Thread(System::gc)).start();
                    seconds = 0;
                }

                //Prints the FPS into the console
                System.out.println("FPS: " + frames);
                frames = 0;
            }
        } // end of game loop
        for (Chunk c: chunkHashMap.values()){
            c.setInactive();
        } // saves all chunks
        handler.gpu[0].closeGPU(); // closes GPU integration
    }

    // Method which runs every 60th of a second
    private void tick() throws IOException {
		// Finds the width and height of the screen every tick
    	WIDTH = window.getWidth();
        HEIGHT = window.getHeight();
        handler.tick();

        if (player != null) { // if a player exists
            for (int i = 0; i < Chunk.renderDistance; i++){
                // checks if a zone around the player, and generates chunks
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
                        chunkHashMap.put(tempKey, new Chunk(tempKey, ID.Chunk, handler, player, chunkHashMap));
                    }
                }
            }
        }
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

        handler.render(g);
        g.dispose();
        bs.show();
    }

    // creates new Engine
    public static void main(boolean survival) throws IOException {
        new Engine(survival);
    }
}