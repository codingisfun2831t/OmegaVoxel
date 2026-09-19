package com.codingisfun2831t.omegavoxel.ui;

import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Widget {
    private Rectangle bounds;
    public boolean hovered;

    public Vector2i getPosition() {
        return bounds.getPosition();
    }

    public Vector2i getSize() {
        return bounds.getSize();
    }

    public void setBounds(Rectangle r) {
        bounds.set(r);
    }
    public void setBounds(int x, int y, int width, int height) {
        bounds.set(x, y, width, height);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void setPosition(Vector2i v) {
        bounds.setPosition(v);
    }

    public void setSize(Vector2i v) {
        bounds.setSize(v);
    }

    public void setPosition(int x, int y) {
        bounds.setPosition(x, y);
    }

    public void setSize(int width, int height) {
        bounds.setSize(width, height);
    }

    public void setTop(int top) {
        bounds.setTop(top);
    }

    public void setLeft(int left) {
        bounds.setLeft(left);
    }

    public void setBottom(int bottom) {
        bounds.setBottom(bottom);
    }

    public void setRight(int right) {
        bounds.setRight(right);
    }

    public void setWidth(int width) {
        bounds.setWidth(width);
    }

    public void setHeight(int height) {
        bounds.setHeight(height);
    }

    public int getCenterX() {
        return bounds.getCenterX();
    }

    public int getCenterY() {
        return bounds.getCenterY();
    }

    public Vector2i getCenter() {
        return bounds.getCenter();
    }

    public void setCenterX(int x) {
        bounds.setCenterX(x);
    }

    public void setCenterY(int y) {
        bounds.setCenterY(y);
    }

    public void setCenter(Vector2i c) {
        bounds.setCenter(c);
    }

    public int getWidth() {
        return bounds.getWidth();
    }

    public int getHeight() {
        return bounds.getHeight();
    }

    public int getTop() {
        return bounds.getTop();
    }

    public int getLeft() {
        return bounds.getLeft();
    }

    public int getRight() {
        return bounds.getRight();
    }

    public int getBottom() {
        return bounds.getBottom();
    }

    protected List<Widget> children;
    private Widget parent;

    public Widget() {
        bounds = new Rectangle(0, 0, 50, 50);
        children = new ArrayList<>();
    }

    public void addChild(Widget child) {
        children.add(child);
        child.parent = this;
    }

    public void renderContent(UIRenderer renderer) {}
    public void render(UIRenderer renderer) {
        renderContent(renderer);

        renderer.pushTranslation(getPosition());
        for (Widget child : children) {
            child.render(renderer);
        }
        renderer.popTranslation();
    }

    public void fixLayout(LayoutContext context) {}

    public void mouseClick(int x, int y, int button) {
    }

    public Widget hitTest(int mouseX, int mouseY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            Widget child = children.get(i);

            int childX = mouseX - child.getLeft();
            int childY = mouseY - child.getTop();

            if (childX >= 0 && childX < child.getWidth() &&
                    childY >= 0 && childY < child.getHeight()) {
                Widget hit = child.hitTest(childX, childY);
                if (hit != null) return hit;
            }
        }

        if (mouseX >= 0 && mouseX < getWidth() &&
                mouseY >= 0 && mouseY < getHeight()) {
            return this;
        }

        return null;
    }
}
