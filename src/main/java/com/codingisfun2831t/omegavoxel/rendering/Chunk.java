package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.Camera;
import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.level.Level;
import org.lwjgl.opengl.GL11;

public class Chunk {
    public static final int CHUNK_SIZE = 16;

    private int x0;
    private int y0;
    private int z0;
    private int x1;
    private int y1;
    private int z1;

    private Level lvl;
    private Renderer r;

    private int list;
    private boolean dirty = true;

    public static final int MAX_UPDATES = 50;
    public static int updatesThisFrame = 0;
    public static int totalUpdates = 0;

    public Chunk(Level lvl, Renderer r, int x, int y, int z) {
        this.x0 = x;
        this.y0 = y;
        this.z0 = z;
        this.x1 = x + CHUNK_SIZE;
        this.y1 = y + CHUNK_SIZE;
        this.z1 = z + CHUNK_SIZE;
        this.lvl = lvl;
        this.r = r;
        this.list = GL11.glGenLists(1);
    }

    public void markDirty() {
        dirty = true;
    }

    public void rebuild() {
        GL11.glNewList(list, GL11.GL_COMPILE);
        r.start(GL11.GL_QUADS);

        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    int id = lvl.getBlockID(x, y, z);

                    if (id != 0) {
                        Block block = Block.blocks[id];

                        if (block != null) {
                            block.render(lvl, r, x, y, z);
                        }
                    }
                }
            }
        }
        r.flush();
        GL11.glEndList();

        dirty = false;
    }

    public void destroy() {
        GL11.glDeleteLists(list, 1);
    }

    public void render() {
        if (dirty && updatesThisFrame < MAX_UPDATES) {
            rebuild();
            updatesThisFrame++;
            totalUpdates++;
        }

        GL11.glCallList(this.list);
    }

    public boolean isVisible(Camera c) {
        return c.boxInFrustum(x0, y0, z0, x1, y1, z1);
    }
}
