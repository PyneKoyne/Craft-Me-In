// Author: Kenny Z & Anish Nagariya
// Date: June 14th
// Program Name: Craft Me In
// Description: This class handles the JFrame of which the program is displayed on

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
    }

    // closes the game
    public void dispose() {
        frame.dispose();
        System.exit(0);
    }

    // Getters
    public int getWidth() {
    	return (int) dimension.getWidth();
    }
    
    public int getHeight() {
    	return (int) dimension.getHeight();
    }
    
    public Point screenLoc() {
    	return loc;
    }
}
