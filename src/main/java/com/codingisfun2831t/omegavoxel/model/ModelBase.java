package com.codingisfun2831t.omegavoxel.model;

import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class ModelBase {
    protected final List<ModelPart> parts = new ArrayList<>();

    public float yawRotation = 0.0f;

    public void render(Renderer r, float x, float y, float z) {
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glPushMatrix();
        
        GL11.glTranslatef(x, y, z);
        GL11.glRotatef(-yawRotation, 0.0f, 1.0f, 0.0f);

        for (ModelPart part : parts) {
            part.render(r, x, y, z);
        }

        r.flush();

        GL11.glPopMatrix();
    }

    protected void part(ModelPart part) {
        parts.add(part);
    }
}