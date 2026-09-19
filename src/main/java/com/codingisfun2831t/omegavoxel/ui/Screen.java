package com.codingisfun2831t.omegavoxel.ui;

import com.codingisfun2831t.omegavoxel.Game;

public abstract class Screen {
    protected Widget root;
    public Game game;

    private Widget hoveredElement;

    public Screen() {
        root = new Widget();
    }

    public void fixLayout(LayoutContext ctx) {}

    public abstract void init();

    public Widget getRoot() {
        return this.root;
    }

    public void render(UIRenderer r) {
        root.render(r);
    }

    public boolean pausesGame() {
        return false;
    }

    public void mouseMove(int x, int y) {
        if (hoveredElement != null) hoveredElement.hovered = false;
        hoveredElement = root.hitTest(x, y);
        if (hoveredElement != null) hoveredElement.hovered = true;
    }

    public void mouseClick(int x, int y, int button) {
        if (hoveredElement != null) hoveredElement.mouseClick(x, y, button);
    }
}
