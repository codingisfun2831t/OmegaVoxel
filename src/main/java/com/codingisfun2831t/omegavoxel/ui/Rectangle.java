package com.codingisfun2831t.omegavoxel.ui;

import org.joml.Vector2i;

public class Rectangle {
    private Vector2i position;
    private Vector2i size;

    public Rectangle(Vector2i pos, Vector2i size) {
        this.position = pos;
        this.size = size;
    }

    public Rectangle(int x, int y, int width, int height) {
        this.position = new Vector2i(x, y);
        this.size = new Vector2i(width, height);
    }

    public Rectangle(int x, int y, Vector2i size) {
        this.position = new Vector2i(x, y);
        this.size = size;
    }

    public Vector2i getPosition() {
        return this.position;
    }

    public Vector2i getSize() {
        return this.size;
    }

    public int getWidth() {
        return this.size.x;
    }

    public int getHeight() {
        return this.size.y;
    }

    public int getTop() {
        return this.position.y;
    }

    public int getLeft() {
        return this.position.x;
    }

    public int getRight() {
        return this.position.x + this.size.x;
    }

    public int getBottom() {
        return this.position.y + this.size.y;
    }

    public void set(Rectangle r) {
        this.position.set(r.position);
        this.size.set(r.size);
    }

    public void setPosition(Vector2i v) {
        this.position.set(v);
    }

    public void setSize(Vector2i v) {
        this.size.set(v);
    }

    public void setPosition(int x, int y) {
        this.position.set(x, y);
    }

    public void setSize(int width, int height) {
        this.size.set(width, height);
    }

    public void setTop(int top) {
        this.position.y = top;
    }

    public void setLeft(int left) {
        this.position.x = left;
    }

    public void setBottom(int bottom) {
        this.position.y = bottom - size.y;
    }

    public void setRight(int right) {
        this.position.x = right - size.x;
    }

    public void setWidth(int width) {
        this.size.x = width;
    }

    public void setHeight(int height) {
        this.size.y = height;
    }

    public int getCenterX() {
        return this.position.x + this.size.x / 2;
    }

    public int getCenterY() {
        return this.position.y + this.size.y / 2;
    }

    public Vector2i getCenter() {
        return new Vector2i(getCenterX(), getCenterY());
    }

    public void setCenterX(int x) {
        this.position.x = x - this.size.x / 2;
    }

    public void setCenterY(int y) {
        this.position.y = y - this.size.y / 2;
    }

    public void setCenter(Vector2i c) {
        setCenterX(c.x);
        setCenterY(c.y);
    }

    public void set(int x, int y, int width, int height) {
        this.position.set(x, y);
        this.size.set(width, height);
    }
}
