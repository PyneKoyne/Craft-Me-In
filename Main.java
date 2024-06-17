package main;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class Main extends JFrame {

    // width and height
    private final static int X = 1366;
    private final static int Y = 768;

    public static void main(String[] args) {
        new Main().setVisible(true);
    }

    // Display main menu
    private Main() {
        super("Craft Me In");
        playAudio();
        setSize(X, Y);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = getPanel();

        // Title
        ImageIcon titleImage = new ImageIcon("Craft-Me-In-6-10-2024.png");
        JLabel titleLabel = new JLabel(titleImage, JLabel.CENTER);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Instructions
        JLabel instructionsLabel1 = new JLabel("Press W A S D to move around, Space Bar to Jump", JLabel.CENTER);
        JLabel instructionsLabel2 = new JLabel("Left Click to place blocks, Right click to delete blocks, Scroll to Zoom in/out", JLabel.CENTER);
        JLabel instructionsLabel3 = new JLabel("Shift to unlock the mouse. Escape to return to the Main Menu", JLabel.CENTER);
        JLabel instructionsLabel4 = new JLabel("Press Survival to play with realistic jumps. Press Creative for endless jumps.", JLabel.CENTER);

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

        // Buttons
        JButton survivalButton = new JButton("Survival");
        JButton creativeButton = new JButton("Creative");
        JButton clearWorldButton = new JButton("Clear World");
        JButton quitButton = new JButton("Quit Game");

        customizeButton(survivalButton);
        customizeButton(creativeButton);
        customizeButton(clearWorldButton);
        customizeButton(quitButton);

        survivalButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        creativeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        clearWorldButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        survivalButton.setMaximumSize(new Dimension(300, 50));
        creativeButton.setMaximumSize(new Dimension(300, 50));
        clearWorldButton.setMaximumSize(new Dimension(200, 50));
        quitButton.setMaximumSize(new Dimension(200, 50));

        survivalButton.addActionListener(e -> startSurvivalMode());
        creativeButton.addActionListener(e -> startCreativeMode());
        clearWorldButton.addActionListener(e -> clearWorld());
        quitButton.addActionListener(e -> quitGame());

        // creates the main menu
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Reduced space between title and instructions
        panel.add(instructionsLabel1);
        panel.add(instructionsLabel2);
        panel.add(instructionsLabel3);
        panel.add(Box.createRigidArea(new Dimension(0, 10))); // Reduced space between instructions
        panel.add(instructionsLabel4);
        panel.add(Box.createRigidArea(new Dimension(0, 120))); // Added space between instructions and buttons
        panel.add(survivalButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        panel.add(creativeButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        panel.add(clearWorldButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20))); // Add space
        panel.add(quitButton);

        add(panel);
    }

    // for background image
    private JPanel getPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("background.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 50, 50, 50)); // Reduced top padding to move content up
        return panel;
    }

    // Customize button appearance
    private void customizeButton(JButton button) {
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(50, 50, 50)); // Dark gray color for the buttons
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    // Methods to start game modes
    private void startSurvivalMode() {
        try {
            Engine.main(true);
            this.dispose();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void startCreativeMode() {
        try {
            Engine.main(false);
            this.dispose();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    // delete stored chunks and start a new world
    private void clearWorld() {
        File folder = new File("craftmein/Chunks");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
                JOptionPane.showMessageDialog(this, "World cleared successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "No files found to delete.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Directory not found.");
        }
    }

    // closes the whole game
    private void quitGame() {
        this.dispose();
    }

    // Method to create a thread which plays music the entire time
    private void playAudio(){
        try {
            File audioFile = new File("Minecraft.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Error | LineUnavailableException | IOException ignored){
        } catch (UnsupportedAudioFileException e) {
            throw new RuntimeException(e);
        }
    }
}
