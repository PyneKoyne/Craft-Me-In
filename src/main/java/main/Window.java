// Author: Kenny Z & Anish Nagariya
// Date: June 16th
// Program Name: Craft Me In
// Description: This class handles the JFrame of which the program is displayed on

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Window class which creates a JFrame the game is displayed on
public class Window extends Canvas{
    // Variables
    public JFrame frame;
    private java.awt.Dimension dimension;
    private Point loc;

    // Constructs a new window
    public Window(int width, int height, String title, Engine engine){
        // creates a new window
    	frame = new JFrame(title);

        // default parameters
        frame.setPreferredSize(new Dimension(width, height)); //1024x768, 1600/900\

        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        
        dimension = frame.getSize();
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                JFrame c = (JFrame) evt.getSource();
                dimension = c.getSize();
                loc = c.getLocationOnScreen();
            }
        });

        // adds the engine on to the frame
        frame.add(engine);
        frame.setVisible(true);

        // starts the game
        loc = frame.getLocationOnScreen();
        engine.start();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                engine.stop();
            }
        });
        // closes the window if the escape key is pressed
        frame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {} // not used

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                    engine.stop();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {} // not used
        });
    }

    // closes the game
    public void dispose() {
        frame.dispose();
        Main.main(null);
    }

    // Gets the Width
    public int getWidth() {
    	return (int) dimension.getWidth();
    }

    // Gets the height
    public int getHeight() {
    	return (int) dimension.getHeight();
    }

    // gets the location of the window on the screen
    public Point screenLoc() {
    	return loc;
    }
}
