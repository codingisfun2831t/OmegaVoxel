package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.assets.Textures;
import com.codingisfun2831t.omegavoxel.ui.Color;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.Random;

/**
 * Main class responsible for rendering vertex data.
 */
public class Renderer {
    // global instance
    private static final Renderer INSTANCE = new Renderer(250_000);

    /**
     * Get global renderer instance.
     */
    public static Renderer getInstance() { return INSTANCE; }

    //
    // 4-byte floats:
    // X, Y, Z - float
    // Color - int (0xAABBGGRR)
    // U, V - float
    private static final int STRIDE_BYTES = 24;

    private final int maxVertex; // max vertex count
    private ByteBuffer buf; // buffer
    private int vertexCount = 0; // current amount of vertices in buf.

    private int colorPacked = 0xFFFFFFFF; // color (0xAABBGGRR)
    private int primitive; // GL primitive

    // texture coords
    private float u = 0.0f;
    private float v = 0.0f;

    private Texture boundTexture = null; // currently bound texture
    private Textures texs; // textures for path lookup

    private Renderer(int maxVertex) {
        this.maxVertex = maxVertex;
        this.buf = MemoryUtil.memAlloc(maxVertex * STRIDE_BYTES);
    }

    /**
     * Start drawing a new type of primitives. Flushes if new type of primitive.
     */
    public void start(int prim) {
        // flush if theres any existing vertices and this is a new prim
        if (vertexCount > 0 && primitive != prim) {
            flush();
        }

        primitive = prim;
    }

    /**
     * Send the vertex data to the GPU.
     */
    public void flush() {
        // ignore if no vertexs
        if (vertexCount == 0) {
            buf.clear();
            return;
        }

        // reading
        buf.flip();

        // arrays
        GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glEnableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glEnableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        // XYZ, floats
        buf.position(0);//random.nextInt(11));
        GL11.glVertexPointer(3, GL11.GL_FLOAT, STRIDE_BYTES, buf);

        // RGBA, unsigned bytes
        buf.position(12);
        GL11.glColorPointer(4, GL11.GL_UNSIGNED_BYTE, STRIDE_BYTES, buf);

        // UV, floats
        buf.position(16);
        GL11.glTexCoordPointer(2, GL11.GL_FLOAT, STRIDE_BYTES, buf);

        // draw!
        GL11.glDrawArrays(primitive, 0, vertexCount);

        // cleanup
        GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
        GL11.glDisableClientState(GL11.GL_COLOR_ARRAY);
        GL11.glDisableClientState(GL11.GL_TEXTURE_COORD_ARRAY);

        buf.clear();
        vertexCount = 0;
    }

    /**
     * Set the vertex color (RGBA, 0 to 255)
     */
    public void c(int r, int g, int b, int a) {
        // bit math... yay...
        this.colorPacked = ((a & 0xFF) << 24) | ((b & 0xFF) << 16) | ((g & 0xFF) << 8) | (r & 0xFF);
    }

    /**
     * Set the vertex color (RGB, 0 to 255)
     */
    public void c(int r, int g, int b) {
        c(r, g, b, 255);
    }

    /**
     * Set the vertex color (Color class)
     */
    public void c(Color c) {
        this.c(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());
    }

    /**
     * Set UV using floats
     */
    public void uv(float u, float v) {
        this.u = u;
        this.v = v;
    }

    /**
     * Set UV using int's, translated to floats using the bound texture.
     * Can't work if theres no bound texture!
     */
    public void uv(int u, int v) {
        if (boundTexture == null) {
            throw new RuntimeException("Can't base UV off texture size when there isn't one.");
        }
        uv((float) u / boundTexture.width(), (float) v / boundTexture.height());
    }

    /**
     * Add a new vertex.
     */
    public void v(float x, float y, float z) {
        // too many?
        if (vertexCount + 1 >= maxVertex) {
            flush();
        }

        // put vertex data
        buf.putFloat(x);
        buf.putFloat(y);
        buf.putFloat(z);
        buf.putInt(colorPacked);
        buf.putFloat(this.u);
        buf.putFloat(this.v);

        vertexCount++;
    }

    /**
     * Add a vertex with a specfic UV.
     */
    public void vuv(float x, float y, float z, float u, float v) {
        uv(u, v);
        v(x, y, z);
    }

    /**
     * Add a vertex with a specfic UV (ints)
     */
    public void vuv(float x, float y, float z, int u, int v) {
        uv(u, v);
        v(x, y, z);
    }

    /**
     * Add a 2d vertex (Z = 0.0f).
     */
    public void v2d(float x, float y) {
        this.v(x, y, 0.0f);
    }

    /**
     * Add a vertex with a specfic UV.
     */
    public void v2duv(float x, float y, int u, int v) {
        this.vuv(x, y, 0.0f, u, v);
    }

    /**
     * Free the memory used.
     */
    public void destroy() {
        if (buf != null) {
            MemoryUtil.memFree(buf);
            buf = null;
        }
    }

    /**
     * Bind a certain texture.
     */
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

    /**
     * Load a texture.
     */
    public Texture loadTex(String path) {
        return texs.load(path);
    }

    /**
     * Bind a texture based off a path.
     */
    public void bind(String path) {
        if (this.texs == null) {
            throw new RuntimeException("No textures!");
        }
        bind(texs.load(path));
    }

    /**
     * Use a Textures instance.
     */
    public void useTextures(Textures texs) {
        this.texs = texs;
    }
}
