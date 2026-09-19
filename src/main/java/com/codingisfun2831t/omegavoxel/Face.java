package com.codingisfun2831t.omegavoxel;

public enum Face {
    FRONT,
    BACK,
    TOP,
    BOTTOM,
    RIGHT,
    LEFT,
    NONE;

    public int getX(int x) {
        return switch(this) {
            case LEFT -> x - 1;
            case RIGHT -> x + 1;
            default -> x;
        };
    }

    public int getY(int y) {
        return switch(this) {
            case BOTTOM -> y - 1;
            case TOP -> y + 1;
            default -> y;
        };
    }

    public int getZ(int z) {
        return switch(this) {
            case BACK -> z - 1;
            case FRONT -> z + 1;
            default -> z;
        };
    }
}
