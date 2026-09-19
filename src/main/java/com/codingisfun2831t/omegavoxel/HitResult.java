package com.codingisfun2831t.omegavoxel;

import org.joml.Vector3i;

public class HitResult {
    public Vector3i blockPos = new Vector3i();
    public Face hitFace = Face.NONE;
    public boolean hit = false;
}
