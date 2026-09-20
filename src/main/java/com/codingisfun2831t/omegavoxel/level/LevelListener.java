package com.codingisfun2831t.omegavoxel.level;

public interface LevelListener {
    public void blockChanged(int x, int y, int z);
    void lightColumnChanged(int x, int z, int y0, int y1);
}
