package com.codingisfun2831t.omegavoxel.rendering;

import com.codingisfun2831t.omegavoxel.ui.Rectangle;

public class ScaledResolution {
    public static final int MIN_WIDTH = 320;
    public static final int MIN_HEIGHT = 240;

    public final int framebufferWidth;
    public final int framebufferHeight;

    public final int width;
    public final int height;
    public final int scale;

    public ScaledResolution(int width, int height, int guiScale) {
        this.framebufferWidth = width;
        this.framebufferHeight = height;

        int maxScale = guiScale == 0 ? Integer.MAX_VALUE : guiScale;

        int scale = 1;
        while (scale < maxScale &&
                width / (scale + 1) >= MIN_WIDTH &&
                height / (scale + 1) >= MIN_HEIGHT) {
            scale++;
        }

        this.scale = scale;

        this.width = (width + scale - 1) / scale;
        this.height = (height + scale - 1) / scale;
    }

    public Rectangle getRectangle() {
        return new Rectangle(0, 0, width, height);
    }
}
