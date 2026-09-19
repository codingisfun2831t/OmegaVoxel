package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.Color;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;
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
        renderer.drawTexturedRect(renderer.loadTex("gui/gui.png"), getBounds(), 0, 66 + (this.hovered ? 20 : 0));
        renderer.drawCenteredTextWithShadow(getCenterX(), getTop() + 6, text, hovered ? Color.BUTTON_TEXT_HOVER : Color.WHITE);
    }

    public void mouseClick(int x, int y, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action != null) action.run();
    }
}
