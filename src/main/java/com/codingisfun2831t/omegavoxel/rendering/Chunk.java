package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.Camera;
import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.level.RenderPass;
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

    private int lists;
    private boolean dirty = true;

    public static final int MAX_UPDATES = 50;
    public static int updatesThisFrame = 0;
    public static int totalUpdates = 0;

    private int vertexCount = 0;

    public Chunk(Level lvl, Renderer r, int x, int y, int z) {
        this.x0 = x;
        this.y0 = y;
        this.z0 = z;
        this.x1 = x + CHUNK_SIZE;
        this.y1 = y + CHUNK_SIZE;
        this.z1 = z + CHUNK_SIZE;
        this.lvl = lvl;
        this.r = r;
        this.lists = GL11.glGenLists(2);
    }

    public void markDirty() {
        dirty = true;
    }

    private void rebuildPass(int idx, RenderPass pass) {
        r.startList(lists+idx);
        r.start(GL11.GL_QUADS);

        for (int x = x0; x < x1; x++) {
            for (int y = y0; y < y1; y++) {
                for (int z = z0; z < z1; z++) {
                    int id = lvl.getBlockID(x, y, z);

                    if (id != 0) {
                        Block block = Block.blocks[id];

                        if (block != null && block.getRenderPass() == pass) {
                            block.render(lvl, r, x, y, z);
                        }
                    }
                }
            }
        }
        r.flush();
        vertexCount += r.endList();
    }

    private boolean built;
    public void rebuild() {
        vertexCount = 0;
        rebuildPass(0, RenderPass.NORMAL);
        rebuildPass(1, RenderPass.TRANSPARENT);

        dirty = false;
        built = true;
    }

    public void destroy() {
        GL11.glDeleteLists(lists, 2);
    }

    public void render(RenderPass pass) {
        if (dirty && updatesThisFrame < MAX_UPDATES) {
            rebuild();
            updatesThisFrame++;
            totalUpdates++;
        }

        if (!built) return;

        if (pass == RenderPass.NORMAL)
            GL11.glCallList(this.lists);
        else if (pass == RenderPass.TRANSPARENT) {
            GL11.glCallList(this.lists+1);
        }
    }

    public boolean isBuilt() {
        return built;
    }

    public int chunkX() {
        return x0 + ((x1 - x0) / 2);
    }

    public int chunkY() {
        return y0 + ((y1 - y0) / 2);
    }

    public int chunkZ() {
        return z0 + ((z1 - z0) / 2);
    }

    public boolean isVisible(Camera c) {
        return c.boxInFrustum(x0, y0, z0, x1, y1, z1);
    }

    public int getVertexCount() {
        return vertexCount;
    }
}
