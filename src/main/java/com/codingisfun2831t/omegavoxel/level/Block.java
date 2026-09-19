package com.codingisfun2831t.omegavoxel.level;

import com.codingisfun2831t.omegavoxel.Face;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import com.codingisfun2831t.omegavoxel.ui.Color;

import java.util.Arrays;

/**
 * Class for a single block type.
 */
public class Block {
    private byte id;
    private byte[] tex;

    /**
     * List of all blocks.
     */
    public static final Block[] blocks = new Block[256];

    public static final Block STONE = new Block().texture(1).register(1);
    public static final Block GRASS = new BlockGrass().texture(0, 3, 2).register(2);
    public static final Block DIRT = new Block().texture(2).register(3);
    public static final Block COBBLESTONE = new Block().texture(16).register(4);

    public Block() {
        this.id = 0;
        tex = new byte[6];
    }

    /**
     * Use a single texture index for all sides.
     */
    public Block texture(int all) {
        Arrays.fill(tex, (byte)all);
        return this;
    }

    public Block texture(int top, int side, int bottom) {
        texture(side);
        tex[Face.TOP.ordinal()] = (byte)top;
        tex[Face.BOTTOM.ordinal()] = (byte)bottom;

        return this;
    }


    /**
     * Register this block to an ID.
     */
    public Block register(int id) {
        this.id = (byte)id;
        blocks[id] = this;
        return this;
    }

    /**
     * Get numeric ID of this block.
     */
    public int getId() {
        return id;
    }

    /**
     * Get all textures for this block.
     * @return
     */
    public byte[] getTex() {
        return tex;
    }

    private static final float YBRIGHT = 1.0F;
    private static final float ZBRIGHT = 0.8F;
    private static final float XBRIGHT = 0.6F;

    protected void renderFace(Renderer r, float x0, float y0, float z0, float x1, float y1, float z1, byte tex, Face face) {
        int u0 = (tex % 16) * 16;
        int v1 = (tex / 16) * 16;
        int u1 = u0 + 16;
        int v0 = v1 + 16;

        r.c(getSideColor(face).brightness(
                switch (face) {
                    case Face.TOP, Face.BOTTOM -> YBRIGHT;
                    case Face.FRONT, Face.BACK -> ZBRIGHT;
                    case Face.RIGHT, Face.LEFT -> XBRIGHT;
                    default -> 1.0F;
                }
        ));

        switch (face) {
            case Face.FRONT:
                r.vuv(x0, y0, z1, u0, v0);
                r.vuv(x1, y0, z1, u1, v0);
                r.vuv(x1, y1, z1, u1, v1);
                r.vuv(x0, y1, z1, u0, v1);
                break;
            case Face.BACK:
                r.vuv(x1, y0, z0, u0, v0);
                r.vuv(x0, y0, z0, u1, v0);
                r.vuv(x0, y1, z0, u1, v1);
                r.vuv(x1, y1, z0, u0, v1);
                break;
            case Face.TOP:
                r.vuv(x0, y1, z1, u0, v0);
                r.vuv(x1, y1, z1, u1, v0);
                r.vuv(x1, y1, z0, u1, v1);
                r.vuv(x0, y1, z0, u0, v1);
                break;
            case Face.BOTTOM:
                r.vuv(x0, y0, z0, u0, v0);
                r.vuv(x1, y0, z0, u1, v0);
                r.vuv(x1, y0, z1, u1, v1);
                r.vuv(x0, y0, z1, u0, v1);
                break;
            case Face.RIGHT:
                r.vuv(x1, y0, z1, u0, v0);
                r.vuv(x1, y0, z0, u1, v0);
                r.vuv(x1, y1, z0, u1, v1);
                r.vuv(x1, y1, z1, u0, v1);
                break;
            case Face.LEFT:
                r.vuv(x0, y0, z0, u0, v0);
                r.vuv(x0, y0, z1, u1, v0);
                r.vuv(x0, y1, z1, u1, v1);
                r.vuv(x0, y1, z0, u0, v1);
                break;
        }
    }

    protected void renderCulledFace(Level l, Renderer r, float x0, float y0, float z0, float x1, float y1, float z1, byte tex, Face face) {
        if (l != null) {
            int fx = face.getX((int)x0);
            int fy = face.getY((int)y0);
            int fz = face.getZ((int)z0);

            if (l.getBlockID(fx, fy, fz) != 0) return;
        }

        renderFace(r, x0, y0, z0, x1, y1, z1, tex, face);
    }

    /**
     * Render this block.
     * @param l Level to check for cullable faces. If null, no faces will be culled.
     * @param r Renderer to render to
     * @param x X position.
     * @param y Y position.
     * @param z Z position.
     */
    public void render(Level l, Renderer r, int x, int y, int z) {
        r.bind("terrain.png");
        float x0 = x;
        float y0 = y;
        float z0 = z;
        float x1 = x + 1.0F;
        float y1 = y + 1.0F;
        float z1 = z + 1.0F;

        r.c(ZBRIGHT, ZBRIGHT, ZBRIGHT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.FRONT.ordinal()], Face.FRONT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.BACK.ordinal()], Face.BACK);

        r.c(YBRIGHT, YBRIGHT, YBRIGHT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.TOP.ordinal()], Face.TOP);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.BOTTOM.ordinal()], Face.BOTTOM);

        r.c(XBRIGHT, XBRIGHT, XBRIGHT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.RIGHT.ordinal()], Face.RIGHT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.LEFT.ordinal()], Face.LEFT);
    }

    protected Color getSideColor(Face face) {
        return Color.WHITE;
    }
}
