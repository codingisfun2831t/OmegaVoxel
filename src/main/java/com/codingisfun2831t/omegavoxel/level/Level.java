package com.codingisfun2831t.omegavoxel.level;

import com.codingisfun2831t.omegavoxel.AABB;

import java.util.ArrayList;
import java.util.Arrays;

public class Level {
    private int width;
    private int depth;
    private int height;
    private byte[] blocks;

    private ArrayList<LevelListener> listeners = new ArrayList<>();

    public Level(int width, int height, int depth) {
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blocks = new byte[width * height * depth];

        int surface = height / 2;
        setFullLayers(0, surface - 4, Block.STONE);
        setFullLayers(surface - 4, 3, Block.DIRT);
        setFullLayers(surface - 1, 1, Block.GRASS);
    }


    public int getWidth() {
        return width;
    }

    public int getDepth() {
        return depth;
    }

    public int getHeight() {
        return height;
    }

    public boolean inBounds(int x, int y, int z) {
        return x >= 0 && x < width &&
                y >= 0 && y < height &&
                z >= 0 && z < depth;
    }

    private int getBlockIndex(int x, int y, int z) {
        return (y * depth + z) * width + x;
    }

    public byte getBlockID(int x, int y, int z) {
        if (!inBounds(x, y, z)) return 0;

        return blocks[getBlockIndex(x, y, z)];
    }

    public Block getBlock(int x, int y, int z) {
        return Block.blocks[getBlockID(x, y, z)];
    }

    public void setBlockID(int x, int y, int z, byte id) {
        if (!inBounds(x, y, z)) return;

        blocks[getBlockIndex(x, y, z)] = id;

        for (LevelListener l : listeners) {
            l.blockChanged(x, y, z);
        }
    }

    public void setBlock(int x, int y, int z, Block b) {
        if (!inBounds(x, y, z)) return;

        blocks[getBlockIndex(x, y, z)] = (byte)b.getId();

        for (LevelListener l : listeners) {
            l.blockChanged(x, y, z);
        }
    }

    public void setFullLayers(int y, int size, Block b) {
        Arrays.fill(blocks, y * width * depth, (y + size) * width * depth, (byte) b.getId());
    }

    public void addListener(LevelListener l) {
        listeners.add(l);
    }

    public ArrayList<AABB> getCubes(AABB aABB) {
        ArrayList aABBs = new ArrayList();
        int x0 = (int)aABB.x0;
        int x1 = (int)(aABB.x1 + 1.0F);
        int y0 = (int)aABB.y0;
        int y1 = (int)(aABB.y1 + 1.0F);
        int z0 = (int)aABB.z0;
        int z1 = (int)(aABB.z1 + 1.0F);
        if(x0 < 0) {
            x0 = 0;
        }

        if(y0 < 0) {
            y0 = 0;
        }

        if(z0 < 0) {
            z0 = 0;
        }

        if(x1 > this.width) {
            x1 = this.width;
        }

        if(y1 > this.depth) {
            y1 = this.depth;
        }

        if(z1 > this.height) {
            z1 = this.height;
        }

        for(int x = x0; x < x1; ++x) {
            for(int y = y0; y < y1; ++y) {
                for(int z = z0; z < z1; ++z) {
                    if(this.getBlockID(x, y, z) != 0) {
                        aABBs.add(new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1)));
                    }
                }
            }
        }

        return aABBs;
    }
}
