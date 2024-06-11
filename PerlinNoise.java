// Author: Unknown
// Date: June 11th
// Program Name: Craft Me In
// Description: This is the perlin noise generation code found online. Creates slopes and terrains.

package main;

import java.util.Random;

// Perlin noise class
public class PerlinNoise {

    // dimensions of heatmap
    private int width;
    private int height;
    private double[][] noise;
    private Random random;

    // Constructs a new Perlin Noise object
    public PerlinNoise(int width, int height) {
        this.width = width;
        this.height = height;
        this.noise = new double[width][height];
        this.random = new Random();
    }

    // fade field
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    // lerp function
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    // add gradient between two objects
    private double grad(int hash, double x, double y) {
        int h = hash & 15;
        double u = h < 8 ? x : y;
        double v = h < 4 ? y : h == 12 || h == 14 ? x : 0;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
    }

    // add fades and gradients to make perlin noise like a field
    private double perlin(double x, double y) {
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;

        x -= Math.floor(x);
        y -= Math.floor(y);

        double u = fade(x);
        double v = fade(y);

        int[] p = new int[512];
        for (int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = random.nextInt(256);
        }

        int A = p[X] + Y;
        int AA = p[A];
        int AB = p[A + 1];
        int B = p[X + 1] + Y;
        int BA = p[B];
        int BB = p[B + 1];

        return lerp(v, lerp(u, grad(p[AA], x, y),
                        grad(p[BA], x - 1, y)),
                lerp(u, grad(p[AB], x, y - 1),
                        grad(p[BB], x - 1, y - 1)));
    }

    // randomly generate the heatmap
    public double[][] generateNoise() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                double x = (double) i / width * 5;
                double y = (double) j / height * 5;
                noise[i][j] = perlin(x, y);
            }
        }
        return noise;
    }
}
