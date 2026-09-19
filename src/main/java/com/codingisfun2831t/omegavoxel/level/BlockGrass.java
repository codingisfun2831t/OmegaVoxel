package com.codingisfun2831t.omegavoxel.level;

import com.codingisfun2831t.omegavoxel.Face;
import com.codingisfun2831t.omegavoxel.ui.Color;

public class BlockGrass extends Block {
    private static final Color COLOR = Color.fromRGB(71, 205, 51);

    @Override
    protected Color getSideColor(Face face) {
        if (face == Face.TOP) return COLOR;
        return super.getSideColor(face);
    }
}

