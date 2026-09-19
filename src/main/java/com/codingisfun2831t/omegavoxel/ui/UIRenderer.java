package com.codingisfun2831t.omegavoxel.ui;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.rendering.FontRenderer;
import com.codingisfun2831t.omegavoxel.rendering.Renderer;
import com.codingisfun2831t.omegavoxel.rendering.ScaledResolution;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class UIRenderer {
    private Renderer r;
    private FontRenderer text;

    private Vector2i translation;
    private Stack<Vector2i> translations;

    public UIRenderer(Renderer r, FontRenderer text) {
        this.r = r;
        this.text = text;
        translations = new Stack<>();
        translation = new Vector2i();
    }

    public void begin(ScaledResolution res) {
        translations.clear();
        translation.set(0,0);

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_CULL_FACE);
        GL11.glDisable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, res.width, res.height, 0.0, -1.0, 1.0);

        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    public void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);

        GL11.glDisable(GL11.GL_BLEND);
    }

    public void pushTranslation(Vector2i addition) {
        translations.push(new Vector2i(translation));
        translation.add(addition);
    }

    public void popTranslation() {
        translation.set(translations.pop());
    }

    public void drawText(int x, int y, String str) {
        x += translation.x;
        y += translation.y;
        text.drawText(x, y, str);
    }

    public void drawTextWithShadow(int x, int y, String str) {
        x += translation.x;
        y += translation.y;
        text.drawTextWithShadow(x, y, str);
    }

    public void drawCenteredTextWithShadow(int x, int y, String str) {
        x += translation.x;
        y += translation.y;
        text.drawTextWithShadow(x - text.measureText(str) / 2, y, str);
    }

    public void drawCenteredTextWithShadow(int x, int y, String str, Color clr) {
        x += translation.x;
        y += translation.y;
        text.drawTextWithShadow(x - text.measureText(str) / 2, y, str, clr);
    }

    public void drawRect(Rectangle rect, Color color) {
        int left = rect.getLeft();
        int right = rect.getRight();
        int top = rect.getTop();
        int bottom = rect.getBottom();

        left += translation.x;
        top += translation.y;
        right += translation.x;
        bottom += translation.y;

        r.bind((Texture) null);
        r.start(GL11.GL_QUADS);
        r.c(color);
        r.v2d(left, top);
        r.v2d(right, top);
        r.v2d(right, bottom);
        r.v2d(left, bottom);
    }

    public void drawTexturedRect(Texture tex, Rectangle rect, int u, int v, int uvWidth, int uvHeight) {
        int left = rect.getLeft();
        int right = rect.getRight();
        int top = rect.getTop();
        int bottom = rect.getBottom();

        left += translation.x;
        top += translation.y;
        right += translation.x;
        bottom += translation.y;

        r.bind(tex);
        r.start(GL11.GL_QUADS);
        r.c(1.0F, 1.0F, 1.0F, 1.0F);

        r.v2duv(left, top, u, v);
        r.v2duv(right, top, u + uvWidth, v);
        r.v2duv(right, bottom, u + uvWidth, v + uvHeight);
        r.v2duv(left, bottom, u, v + uvHeight);
    }

    public void drawTexturedRect(Texture tex, Rectangle rect, int u, int v) {
        Vector2i size = rect.getSize();
        drawTexturedRect(tex, rect, u, v, size.x, size.y);
    }

    public void drawGradientRect(Rectangle rect, Color topColor, Color bottomColor) {
        int left = rect.getLeft();
        int right = rect.getRight();
        int top = rect.getTop();
        int bottom = rect.getBottom();

        left += translation.x;
        top += translation.y;
        right += translation.x;
        bottom += translation.y;

        r.bind((Texture) null);
        r.start(GL11.GL_QUADS);
        r.c(topColor);
        r.v2d(left, top);
        r.v2d(right, top);
        r.c(bottomColor);
        r.v2d(right, bottom);
        r.v2d(left, bottom);
    }

    public Texture loadTex(String path) {
        return r.loadTex(path);
    }
}
