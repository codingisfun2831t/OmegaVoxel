package com.codingisfun2831t.omegavoxel.level;

import com.codingisfun2831t.omegavoxel.Face;
import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.assets.Assets;
import com.codingisfun2831t.omegavoxel.assets.Textures;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import com.codingisfun2831t.omegavoxel.ui.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Class for a single block type.
 */
public class Block {
    protected byte id;
    protected int[] tex;
    protected String[] stex;
    protected boolean hideInInventory = false;
    protected RenderPass renderPass = RenderPass.NORMAL;

    private String stringId = null;

    private static Textures.Atlas textureAtlas = new Textures.Atlas("block", 16);

    /**
     * List of all blocks.
     */
    public static final Block[] blocks = new Block[256];

    private static final List<Block> blocksInInventory = new ArrayList<>();

    public static final Block STONE = new Block().texture("stone").register(1, "stone");
    public static final Block GRASS = new BlockGrass().texture("grass_top", "grass_side", "dirt").register(2, "grass");
    public static final Block DIRT = new Block().texture("dirt").register(3, "dirt");
    public static final Block COBBLESTONE = new Block().texture("cobblestone").register(4, "cobblestone");
    public static final Block LOG = new Block().texture("log_top", "log_side", "log_top").register(17, "log");
    public static final Block PLANKS = new Block().texture("planks").register(5, "planks");
    public static final Block SAND = new Block().texture("sand").register(12, "sand");
    public static final Block GRAVEL = new Block().texture("gravel").register(13, "gravel");
    public static final Block BEDROCK = new Block().texture("bedrock").register(7, "bedrock");
    public static final Block GLASS = new BlockGlass().texture("glass").register(20, "glass");

    public Block() {
        this.id = 0;
        tex = new int[6];
        stex = new String[6];
    }

    /**
     * Use a single texture for all sides.
     */
    public Block texture(String all) {
        Arrays.fill(stex, all);
        return this;
    }

    public Block texture(String top, String side, String bottom) {
        texture(side);
        stex[Face.TOP.ordinal()] = top;
        stex[Face.BOTTOM.ordinal()] = bottom;

        return this;
    }

    /**
     * Set the render pass this block belongs to.
     */
    public Block setRenderPass(RenderPass pass) {
        this.renderPass = pass;
        return this;
    }

    public RenderPass getRenderPass() {
        return renderPass;
    }


    public static void generateAtlas(Textures tex, Assets assets) throws IOException {
        textureAtlas.generate(tex, assets);
    }

    /**
     * Register this block to an ID.
     */
    public Block register(int id, String stringId) {
        this.id = (byte)id;
        this.stringId = stringId;
        blocks[id] = this;

        if (!hideInInventory) blocksInInventory.add(this);

        for (int i = 0; i < 6; i++) {
            tex[i] = textureAtlas.put(stex[i]);
        }
        stex = null;

        return this;
    }

    public Block hideInInventory() {
        this.hideInInventory = true;
        return this;
    }

    /**
     * Get numeric ID of this block.
     */
    public int getId() {
        return id;
    }

    /**
     * Get all texture indexes for this block.
     * @return
     */
    public int[] getTex() {
        return tex;
    }

    public static List<Block> getBlocksInInventory() {
        return Collections.unmodifiableList(blocksInInventory);
    }

    private static final float YBRIGHT = 1.0f;
    private static final float ZBRIGHT = 0.8f;
    private static final float XBRIGHT = 0.6f;

    protected void renderFace(Renderer r, float x0, float y0, float z0, float x1, float y1, float z1, int tex, Face face, float b) {
        int u0 = (tex % textureAtlas.getAtlasSize()) * 16;
        int v1 = (tex / textureAtlas.getAtlasSize()) * 16;
        int u1 = u0 + 16;
        int v0 = v1 + 16;

        r.c(getSideColor(face).brightness(
                switch (face) {
                    case Face.TOP, Face.BOTTOM -> YBRIGHT;
                    case Face.FRONT, Face.BACK -> ZBRIGHT;
                    case Face.RIGHT, Face.LEFT -> XBRIGHT;
                    default -> 1.0F;
                } * b
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

    protected void renderCulledFace(Level l, Renderer r, float x0, float y0, float z0, float x1, float y1, float z1, int tex, Face face) {
        float b = 1.0F;
        if (l != null) {
            int fx = face.getX((int)x0);
            int fy = face.getY((int)y0);
            int fz = face.getZ((int)z0);

            byte id = l.getBlockID(fx, fy, fz);
            if (id != 0 && blocks[id].cullNeighborsFace(this, face)) return;

            b = l.getBrightness(fx, fy, fz);
        }

        renderFace(r, x0, y0, z0, x1, y1, z1, tex, face, b);
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
        r.bind(textureAtlas.getAtlas());
        float x0 = x;
        float y0 = y;
        float z0 = z;
        float x1 = x + 1.0F;
        float y1 = y + 1.0F;
        float z1 = z + 1.0F;

        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.FRONT.ordinal()], Face.FRONT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.BACK.ordinal()], Face.BACK);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.TOP.ordinal()], Face.TOP);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.BOTTOM.ordinal()], Face.BOTTOM);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.RIGHT.ordinal()], Face.RIGHT);
        renderCulledFace(l, r, x0, y0, z0, x1, y1, z1, tex[Face.LEFT.ordinal()], Face.LEFT);
    }

    protected Color getSideColor(Face face) {
        return Color.WHITE;
    }

    public String getNameKey() {
        return "block." + stringId;
    }

    public String getName() {
        return Translations.get(getNameKey());
    }

    public String getStringId() {
        return stringId;
    }

    public boolean cullNeighborsFace(Block neighbor, Face face) {
        return true;
    }
}
