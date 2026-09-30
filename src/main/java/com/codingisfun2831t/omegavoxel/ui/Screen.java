package com.codingisfun2831t.omegavoxel.ui;

import com.codingisfun2831t.omegavoxel.Game;

public abstract class Screen {
    protected Widget root;
    public Game game;

    private Widget hoveredElement;
    private Widget activeElement;

    protected Screen parent = null;
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

    public Screen withParent(Screen parent) {
        this.parent = parent;

        return this;
    }

    public void mouseMove(int x, int y) {
        if (hoveredElement != null) hoveredElement.hovered = false;

        hoveredElement = root.hitTest(x, y);

        if (hoveredElement != null) {
            hoveredElement.hovered = true;
            hoveredElement.mouseMove(x - hoveredElement.absoluteX(), y - hoveredElement.absoluteY());
        }

        if (activeElement != null) {
            activeElement.mouseMove(x - activeElement.absoluteX(), y - activeElement.absoluteY());
        }
    }

    public void mouseDown(int x, int y, int button) {
        activeElement = hoveredElement;

        if (activeElement != null) {
            activeElement.mouseDown(
                    x - activeElement.absoluteX(),
                    y - activeElement.absoluteY(),
                    button
            );
        }
    }

    public void mouseUp(int x, int y, int button) {
        if (activeElement != null) {
            activeElement.mouseUp(
                    x - activeElement.absoluteX(),
                    y - activeElement.absoluteY(),
                    button
            );

            if (hoveredElement == activeElement) {
                activeElement.mouseClick(
                        x - activeElement.absoluteX(),
                        y - activeElement.absoluteY(),
                        button
                );
            }

            activeElement = null;
        }
    }

    public void goBack() {
        if (parent != null) game.navigateTo(parent);
    }

    public int getDescendantCount() {
        return root.getDescendantCount();
    }
}