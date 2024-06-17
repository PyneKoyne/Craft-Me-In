// Author: Anish Nagariya and Kenny Zheng
// Date: June 11th
// Program Name: Craft Me In
// Description: This is the perlin noise generation code. Creates slopes and terrains.

package main;

import java.util.Arrays;
import java.util.Random;

// Perlin noise class
public class PerlinNoise {

    // dimensions of heatmap
    private int width;
    private int height;
    private int[][] noise;
    private Random random;

    // Constructs a new Perlin Noise object
    public PerlinNoise(int width, int height) {
        this.width = width;
        this.height = height;
        this.noise = new int[width][height];
        this.random = new Random();
    }

    public int[][] generateNoise() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                this.noise[i][j] = random.nextInt(30); // assign random value
            }
        }

        // smooth about my taking average of neighbours, repeat for k times
        int[][] noise2 = new int[this.width][this.height];
        for (int k = 0; k < 5; k++) {
            for (int i = 0; i < this.width; i++) {
                for (int j = 0; j < this.height; j++) {
                    double sum = 0;
                    int count = 0;
                    for (int dx = -1; dx <= 1; dx++) {
                        for (int dy = -1; dy <= 1; dy++) {
                            if (dx == 0 && dy == 0) continue;
                            int x = i + dx;
                            int y = j + dy;
                            if (x >= 0 && x < this.width && y >= 0 && y < this.height) {
                                sum += noise[x][y];
                                count++;
                            }
                        }
                    }
                    noise2[i][j] = (int) sum / count;
                }
            }
            noise = noise2;
        }
        System.out.println(Arrays.deepToString(noise));
        return noise;
    }
}
