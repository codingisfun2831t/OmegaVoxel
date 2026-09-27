package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.*;
import org.lwjgl.glfw.GLFW;

public class Button extends Widget {
    public String text;
    public Runnable action;

    public Button(String text, int width, Runnable action) {
        super();

        this.text = text;
        this.action = action;
        setSize(width, 20);
    }

    @Override
    public void renderContent(UIRenderer renderer) {
        drawButtonSprite(renderer, this.hovered ? 1 : 0, getBounds());
        renderer.drawCenteredTextWithShadow(getCenterX(), getTop() + 6, text, hovered ? Color.BUTTON_TEXT_HOVER : Color.WHITE);
    }

    public void mouseClick(int x, int y, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action != null) action.run();
    }

    public static void drawButtonSprite(UIRenderer renderer, int index, Rectangle rect) {
        renderer.drawTexturedRect(renderer.loadTex("gui/gui.png"), rect, 0, 66 + (index * 20), rect.getWidth() - 2, 20);
        renderer.drawTexturedRect(renderer.loadTex("gui/gui.png"), rect.getRight() - 2,
                rect.getTop(), rect.getRight(), rect.getBottom(), 198,
                66 + (index * 20), 2, 20);
    }
}
