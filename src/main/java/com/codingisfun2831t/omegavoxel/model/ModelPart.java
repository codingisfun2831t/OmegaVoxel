package com.codingisfun2831t.omegavoxel.model;

import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import org.lwjgl.opengl.GL11;

public class ModelPart {
    public final float x;
    public final float y;
    public final float z;

    private float pivotX = 0.0f;
    private float pivotY = 0.0f;
    private float pivotZ = 0.0f;
    private boolean doRotation = false;

    private float rotationPitch = 0.0f;

    public final int width;
    public final int height;
    public final int depth;

    public final int u;
    public final int v;

    private String tex;

    public ModelPart(float x, float y, float z, int width, int height, int depth, int u, int v, String tex) {
        this.x = x / 16f;
        this.y = y / 16f;
        this.z = z / 16f;
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.u = u;
        this.v = v;
        this.tex = tex;
    }

    public ModelPart pivot(float pivotX, float pivotY, float pivotZ) {
        this.pivotX = pivotX / 16f;
        this.pivotY = pivotY / 16f;
        this.pivotZ = pivotZ / 16f;
        doRotation = true;
        return this;
    }

    public ModelPart pitch(float pitch) {
        this.rotationPitch = pitch;
        return this;
    }

    public void render(Renderer r, float ox, float oy, float oz) {
        r.start(GL11.GL_QUADS);
        r.bind(tex);
        r.c(255, 255, 255, 255);

        float w = (float) width / 16;
        float h = (float) height / 16;
        float d = (float) depth / 16;

        float x0 = x - w / 2;
        float y0 = y;
        float z0 = z - d / 2;
        float x1 = x + w / 2;
        float y1 = y + h;
        float z1 = z + d / 2;

        if (doRotation) {
            GL11.glPushMatrix();
            GL11.glTranslatef(pivotX, pivotY, pivotZ);
            GL11.glRotatef(rotationPitch, 1.0F, 0.0f, 0.0f);
            GL11.glTranslatef(-pivotX, -pivotY, -pivotZ);
        }

        int tu0 = u + depth + width;
        int tu1 = u + depth;
        int tv0 = v;
        int tv1 = v + depth;

        int bu0 = u + depth + width * 2;
        int bu1 = u + depth + width;
        int bv0 = v;
        int bv1 = v + depth;

        int ru0 = u + depth;
        int ru1 = u;
        int rv0 = v + depth;
        int rv1 = v + depth + height;

        int fu0 = u + depth + width;
        int fu1 = u + depth;
        int fv0 = v + depth;
        int fv1 = v + depth + height;

        int lu0 = u + depth + width + depth;
        int lu1 = u + depth + width;
        int lv0 = v + depth;
        int lv1 = v + depth + height;

        int bau0 = u + depth * 2 + width * 2;
        int bau1 = u + depth * 2 + width;
        int bav0 = v + depth;
        int bav1 = v + depth + height;

        // FRONT
        r.vuv(x0, y0, z1, fu0, fv1);
        r.vuv(x1, y0, z1, fu1, fv1);
        r.vuv(x1, y1, z1, fu1, fv0);
        r.vuv(x0, y1, z1, fu0, fv0);

        // BACK
        r.vuv(x1, y0, z0, bau0, bav1);
        r.vuv(x0, y0, z0, bau1, bav1);
        r.vuv(x0, y1, z0, bau1, bav0);
        r.vuv(x1, y1, z0, bau0, bav0);

        // TOP
        r.vuv(x0, y1, z1, tu0, tv1);
        r.vuv(x1, y1, z1, tu1, tv1);
        r.vuv(x1, y1, z0, tu1, tv0);
        r.vuv(x0, y1, z0, tu0, tv0);

        // BOTTOM
        r.vuv(x0, y0, z0, bu0, bv1);
        r.vuv(x1, y0, z0, bu1, bv1);
        r.vuv(x1, y0, z1, bu1, bv0);
        r.vuv(x0, y0, z1, bu0, bv0);

        // RIGHT
        r.vuv(x1, y0, z1, ru0, rv1);
        r.vuv(x1, y0, z0, ru1, rv1);
        r.vuv(x1, y1, z0, ru1, rv0);
        r.vuv(x1, y1, z1, ru0, rv0);

        // LEFT
        r.vuv(x0, y0, z0, lu0, lv1);
        r.vuv(x0, y0, z1, lu1, lv1);
        r.vuv(x0, y1, z1, lu1, lv0);
        r.vuv(x0, y1, z0, lu0, lv0);

        r.flush();
        GL11.glPopMatrix();
    }
}
