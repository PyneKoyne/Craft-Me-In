// Author: Anish Nagariya and Kenny Zheng
// Date: June 11th
// Program Name: Craft Me In
// Description: This is the perlin noise generation code. Creates slopes and terrains.

package main;

import java.util.Random;

// Perlin noise class
public class PerlinNoise {

    // dimensions of heatmap
    private int width;
    private int height;
    private final long WORLD_SEED;
    private final float FREQUENCY = 0.08f;
    private int[] perm_table;

    // Constructs a new Perlin Noise object
    public PerlinNoise(int width, int height, long seed) {
        this.width = width;
        this.height = height;
        this.WORLD_SEED = seed;

        int[] perm_table = new int[512];
        Random random = new Random(WORLD_SEED);
        for (int i = 0; i < 256; i++) {
            perm_table[i] = i;
        }
        for (int i = 255; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int temp = perm_table[i];
            perm_table[i] = perm_table[j];
            perm_table[j] = temp;
        }
        for (int i = 0; i < 256; i++) {
            perm_table[i + 256] = perm_table[i];
        }
        this.perm_table = perm_table;
    }

    public int[][] generateNoise(int worldX, int worldY) {
        int[][] noise = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                float n = perlin((worldX + i) * FREQUENCY, (worldY + j) * FREQUENCY);
                noise[i][j] = (int) Math.round((n + 1) * 0.5 * Chunk.HEIGHT_MAX);
            }
        }
        return noise;
    }

    public float perlin(float x, float y) {
        int x0 = (int) Math.floor(x);
        int x1 = x0 + 1;
        int y0 = (int) Math.floor(y);
        int y1 = y0 + 1;

        float dx = x - x0;
        float dy = y - y0;

        float u = fade(dx);
        float v = fade(dy);

        float g00 = grad(perm_table[(x0 + perm_table[y0 & 255]) & 255], dx, dy);
        float g10 = grad(perm_table[(x1 + perm_table[y0 & 255]) & 255], dx - 1, dy);
        float g01 = grad(perm_table[(x0 + perm_table[y1 & 255]) & 255], dx, dy - 1);
        float g11 = grad(perm_table[(x1 + perm_table[y1 & 255]) & 255], dx - 1, dy - 1);

        float v00 = g00 * dx + g00 * dy;
        float v10 = g10 * (dx - 1) + g10 * dy;
        float v01 = g01 * dx + g01 * (dy - 1);
        float v11 = g11 * (dx - 1) + g11 * (dy - 1);

        float x_bot = v00 + u * (v10 - v00);
        float x_top = v01 + u * (v11 - v01);

        return x_bot + v * (x_top - x_bot);
    }

    private static float grad(int hash, float x, float y) {
        switch (hash & 7) {
            case 0: return x + y;
            case 1: return -x + y;
            case 2: return x - y;
            case 3: return -x - y;
            case 4: return x;
            case 5: return -x;
            case 6: return y;
            default: return -y;
        }
    }

    public float fade(float t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
}