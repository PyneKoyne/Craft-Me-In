// Author: Kenny Z & Anish Nagariya
// Date: June 3rd
// Program Name: Craft Me In
// Description: This is the runner class, creating a window beforehand to put information

package main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JFrame implements KeyListener {

    // Sets default width and height
    private final static int X = 1366;
    private final static int Y = 768;

    // main function
    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    // Display main menu
    private Main() {
        //Properties
        super("Craft Me In");
        setSize(X, Y);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        addKeyListener(this);

        // main menu panel
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.BLACK);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 50, 100, 50));

        // Title
        JLabel titleLabel = new JLabel("Craft Me In", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 48));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Instructions
        JLabel instructionsLabel1 = new JLabel("Press W A S D to move around", JLabel.CENTER);
        JLabel instructionsLabel2 = new JLabel("Left Click to place blocks", JLabel.CENTER);
        JLabel instructionsLabel3 = new JLabel("Right Click to delete blocks", JLabel.CENTER);
        JLabel instructionsLabel4 = new JLabel("Press Enter to Start", JLabel.CENTER);

        instructionsLabel1.setFont(new Font("Arial", Font.PLAIN, 24));
        instructionsLabel2.setFont(new Font("Arial", Font.PLAIN, 24));
        instructionsLabel3.setFont(new Font("Arial", Font.PLAIN, 24));
        instructionsLabel4.setFont(new Font("Arial", Font.PLAIN, 24));

        instructionsLabel1.setForeground(Color.WHITE);
        instructionsLabel2.setForeground(Color.WHITE);
        instructionsLabel3.setForeground(Color.WHITE);
        instructionsLabel4.setForeground(Color.WHITE);

        instructionsLabel1.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsLabel2.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsLabel3.setAlignmentX(Component.CENTER_ALIGNMENT);
        instructionsLabel4.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 50))); // Add space
        panel.add(instructionsLabel1);
        panel.add(instructionsLabel2);
        panel.add(instructionsLabel3);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        panel.add(instructionsLabel4);

        add(panel);
    }

    // Start the game when Enter key is pressed
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            Engine.main();
            this.dispose();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}
