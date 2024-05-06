// Author: Kenny Z
// Date: June 14th
// Program Name: Engine
// Description: This class handles the JFrame of which the program is displayed on

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Window extends Canvas{
    private static final long serialVersionUID = 1L;
    public JFrame frame;
    public java.awt.Dimension d;
    public Point loc;
    
    public Window(int width, int height, String title, Engine engine){
    	frame = new JFrame(title);
        
        frame.setPreferredSize(new Dimension(width, height)); //1024x768, 1600/900\

        frame.setMaximumSize(new Dimension(width, height));
        frame.setMinimumSize(new Dimension(width, height));
        
        
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.requestFocus();
        
        d = frame.getSize();
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent evt) {
                JFrame c = (JFrame) evt.getSource();
                d = c.getSize();
                loc = c.getLocationOnScreen();
            }
        });
        
        frame.add(engine);
        frame.setVisible(true);
        
        loc = frame.getLocationOnScreen();
        engine.start();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event){
                engine.stop();
            }
        });
    }

    public void dispose() {
        frame.dispose();
        System.exit(0);
    }
    
    public int getWidth() {
    	return (int) d.getWidth();
    }
    
    public int getHeight() {
    	return (int) d.getHeight();
    }
    
    public Point screenLoc() {
    	return 	loc;
    }
    
    public Point centre() {
    	Point loc = screenLoc();
    	return new Point((int) (getWidth()/2 + loc.getX()), (int) (getHeight()/2 + loc.getY()));
    }
}
