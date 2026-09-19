package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.assets.Texture;
import com.codingisfun2831t.omegavoxel.assets.Textures;
import com.codingisfun2831t.omegavoxel.ui.Color;
import org.lwjgl.opengl.GL11;

public class FontRenderer {
    private Renderer r;
    private Texture font;
    private int[] widths;

    public FontRenderer(Renderer r, Textures t) {
        this.r = r;
        this.font = t.load("font/default.png");

        this.widths = new int[256];
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                int width = 1;

                for (int px = x * 8; px < x * 8 + 8; px++) {
                    boolean allEmpty = true;

                    for (int py = y * 8; py < y * 8 + 8; py++) {
                        int index = ((py * font.width()) + px) * 4 + 3;

                        if (font.data().get(index) != 0) {
                            allEmpty = false;
                            break;
                        }
                    }

                    if (allEmpty) {
                        break;
                    }

                    width++;
                }

                widths[y * 16 + x] = width;
            }
        }

        widths[' '] = 3;
    }

    private void drawGlyph(int x, int y, char c) {
        if (c > 0xFF) c = '?';

        int u0 = (c % 16) * 8;
        int v0 = (c / 16) * 8;
        int u1 = u0 + 8;
        int v1 = v0 + 8;

        r.bind(font);
        r.start(GL11.GL_QUADS);
        r.v2duv(x, y, u0, v0);
        r.v2duv(x + 8, y, u1, v0);
        r.v2duv(x + 8, y + 8, u1, v1);
        r.v2duv(x, y + 8, u0, v1);
    }

    public void renderText(int x, int y, String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c > 0xFF) c = '?';

            drawGlyph(x, y, c);
            x += widths[c];
        }

        r.flush();
    }

    public int measureText(String text) {
        int width = 0;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            width += widths[c];
        }

        return width;
    }

    public void drawText(int x, int y, String str, Color color) {
        r.c(color);
        renderText(x, y, str);
    }

    public void drawText(int x, int y, String str) {
        drawText(x, y, str, Color.WHITE);
    }

    public void drawTextWithShadow(int x, int y, String str, Color color) {
        r.c(Color.TEXT_SHADOW);
        renderText(x+1, y+1, str);
        r.c(color);
        renderText(x, y, str);
    }

    public void drawTextWithShadow(int x, int y, String str) {
        drawTextWithShadow(x, y, str, Color.WHITE);
    }
}
