package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.assets.Textures;
import com.codingisfun2831t.omegavoxel.ui.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

public class Renderer {
    private static final Renderer INSTANCE = new Renderer(250_000);
    public static Renderer getInstance() { return INSTANCE; }

    private static final int STRIDE = 9; // 3 for XYZ, 4 for RGBA, 2 for UV

    private final int maxVertex;
    private FloatBuffer buf;
    private int vertexCount = 0;

    private float r = 1.0f;
    private float g = 1.0f;
    private float b = 1.0f;
    private float a = 1.0f;
    private int primitive;

    private float u = 0.0f;
    private float v = 0.0f;

    private Texture boundTexture = null;
    private Textures texs;

    private Renderer(int maxVertex) {
        this.maxVertex = maxVertex;
        this.buf = MemoryUtil.memAllocFloat(maxVertex * STRIDE);
    }

    public void start(int prim) {
        // If there are left-over vertices from a different primitive shape type,
        // draw them first before re-assigning the new primitive style mode.
        if (vertexCount > 0 && primitive != prim) {
            flush();
        }
        primitive = prim;
    }

    public void flush() {
        // If there's absolutely nothing to draw, reset the buffer position pointers
        // to WRITING mode and exit immediately to protect against locked buffer traps.
        if (vertexCount == 0) {
            buf.clear();
            return;
        }

        // Switch from WRITING mode to GPU READING mode
        buf.flip();

        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        buf.position(0);
        GL11.glVertexPointer(3, GL11.GL_FLOAT, STRIDE * Float.BYTES, buf);

        buf.position(3);
        GL11.glColorPointer(4, GL11.GL_FLOAT, STRIDE * Float.BYTES, buf);

        buf.position(7);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, STRIDE * Float.BYTES, buf);

        GL11.glDrawArrays(primitive, 0, vertexCount);

        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        // Safely reset buffer pointers back to wide-open WRITING mode for the next batch
        buf.clear();
        vertexCount = 0;
    }

    public void c(float r, float g, float b, float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void c(float r, float g, float b) {
        c(r, g, b, 1.0F);
    }
    public void c(Color c) {
        this.c(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
    }

    public void uv(float u, float v) {
        this.u = u;
        this.v = v;
    }

    public void uv(int u, int v) {
        if (boundTexture == null) {
            throw new RuntimeException("Can't base UV off texture size when there isn't one.");
        }

        uv((float) u / boundTexture.width(), (float) v / boundTexture.height());
    }

    public void v(float x, float y, float z) {
        // Pre-emptive safety check: if this vertex will push us over the edge, empty the buffer first!
        if (vertexCount + 1 >= maxVertex) {
            flush();
        }

        buf.put(x);
        buf.put(y);
        buf.put(z);
        buf.put(this.r);
        buf.put(this.g);
        buf.put(this.b);
        buf.put(this.a);
        buf.put(this.u);
        buf.put(this.v);

        vertexCount++;
    }

    public void vuv(float x, float y, float z, float u, float v) {
        uv(u, v);
        v(x, y, z);
    }

    public void vuv(float x, float y, float z, int u, int v) {
        uv(u, v);
        v(x, y, z);
    }

    // FIXED: Explicitly passes the Z depth value (0) to break the recursive self-call loop trap!
    public void v2d(float x, float y) {
        this.v(x, y, 0.0f);
    }
    public void v2duv(float x, float y, int u, int v) {
        this.vuv(x, y, 0.0f, u, v);
    }

    // Call this during game engine shutdown loops to completely eliminate native off-heap RAM memory leaks
    public void destroy() {
        if (buf != null) {
            MemoryUtil.memFree(buf);
            buf = null;
        }
    }

    public void bind(Texture texture) {
        if (boundTexture != texture) flush();

        if (texture == null) {
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            boundTexture = null;
            return;
        }

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        boundTexture = texture;
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id());
    }

    public Texture loadTex(String path) {
        return texs.load(path);
    }

    public void bind(String path) {
        if (this.texs == null) {
            throw new RuntimeException("No textures!");
        }

        bind(texs.load(path));
    }

    public void useTextures(Textures texs) {
        this.texs = texs;
    }
}
