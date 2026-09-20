package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.Camera;
import com.codingisfun2831t.omegavoxel.HitResult;
import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.level.Level;
import com.codingisfun2831t.omegavoxel.level.LevelListener;
import org.lwjgl.opengl.GL11;

public class LevelRenderer implements LevelListener {
    private Level lvl;
    private Renderer r;

    private int xChunks;
    private int yChunks;
    private int zChunks;
    private Chunk[] chunks;

    private int getChunkIndex(int x, int y, int z) {
        return (y * zChunks + z) * xChunks + x;
    }

    public LevelRenderer(Renderer r) {
        this.r = r;
    }

    public void setLevel(Level lvl) {
        if (this.lvl != null) {
            this.lvl.removeListener(this);

            for (Chunk chunk : chunks) {
                chunk.destroy();
            }

            chunks = null;
        }

        this.lvl = lvl;
        if (lvl != null) {
            this.xChunks = (lvl.getWidth() + Chunk.CHUNK_SIZE - 1) / Chunk.CHUNK_SIZE;
            this.yChunks = (lvl.getHeight() + Chunk.CHUNK_SIZE - 1) / Chunk.CHUNK_SIZE;
            this.zChunks = (lvl.getDepth() + Chunk.CHUNK_SIZE - 1) / Chunk.CHUNK_SIZE;
            this.chunks = new Chunk[xChunks * yChunks * zChunks];

            for (int x = 0; x < xChunks; x++) {
                for (int y = 0; y < yChunks; y++) {
                    for (int z = 0; z < zChunks; z++) {
                        this.chunks[getChunkIndex(x, y, z)] = new Chunk(
                                lvl,
                                r,
                                x * Chunk.CHUNK_SIZE,
                                y * Chunk.CHUNK_SIZE,
                                z * Chunk.CHUNK_SIZE
                        );
                    }
                }
            }

            lvl.addListener(this);
        }
    }

    public void render(Camera c) {
        if (lvl == null) return;
        for (Chunk chunk : chunks) {
            if (chunk.isVisible(c))
                chunk.render();
        }
    }

    public void drawSelectionBox(HitResult h) {
        if (!h.hit) return;

        float e = 0.002f;

        float x0 = h.blockPos.x - e;
        float y0 = h.blockPos.y - e;
        float z0 = h.blockPos.z - e;
        float x1 = h.blockPos.x + 1 + e;
        float y1 = h.blockPos.y + 1 + e;
        float z1 = h.blockPos.z + 1 + e;

        r.bind((Texture) null);
        r.c(0.0f, 0.0f, 0.0f, 0.6f);

        GL11.glDepthMask(false);
        GL11.glLineWidth(2.0f);

        r.start(GL11.GL_LINES);

        r.v(x0, y0, z0);
        r.v(x1, y0, z0);

        r.v(x1, y0, z0);
        r.v(x1, y1, z0);

        r.v(x1, y1, z0);
        r.v(x0, y1, z0);

        r.v(x0, y1, z0);
        r.v(x0, y0, z0);

        r.v(x0, y0, z1);
        r.v(x1, y0, z1);

        r.v(x1, y0, z1);
        r.v(x1, y1, z1);

        r.v(x1, y1, z1);
        r.v(x0, y1, z1);

        r.v(x0, y1, z1);
        r.v(x0, y0, z1);

        r.v(x0, y0, z0);
        r.v(x0, y0, z1);

        r.v(x1, y0, z0);
        r.v(x1, y0, z1);

        r.v(x1, y1, z0);
        r.v(x1, y1, z1);

        r.v(x0, y1, z0);
        r.v(x0, y1, z1);

        r.flush();

        GL11.glDepthMask(true);
    }

    private void setDirty(int x0, int y0, int z0, int x1, int y1, int z1)
    {
        x0 /= 16;
        x1 /= 16;
        y0 /= 16;
        y1 /= 16;
        z0 /= 16;
        z1 /= 16;
        if (x0 < 0)
        {
            x0 = 0;
        }

        if (y0 < 0)
        {
            y0 = 0;
        }

        if (z0 < 0)
        {
            z0 = 0;
        }

        if (x1 >= xChunks)
        {
            x1 = xChunks - 1;
        }

        if (y1 >= yChunks)
        {
            y1 = yChunks - 1;
        }

        if (z1 >= zChunks)
        {
            z1 = zChunks - 1;
        }

        for (int x = x0; x <= x1; x++)
        {
            for (int y = y0; y <= y1; y++)
            {
                for (int z = z0; z <= z1; z++)
                {
                    chunks[getChunkIndex(x, y, z)].markDirty();
                }
            }
        }
    }


    @Override
    public void blockChanged(int x, int y, int z) {
        setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);

    }

    @Override
    public void lightColumnChanged(int x, int z, int y0, int y1)
    {
        setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }

}