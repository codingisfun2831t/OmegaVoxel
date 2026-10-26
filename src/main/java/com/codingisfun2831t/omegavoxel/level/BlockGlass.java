package com.codingisfun2831t.omegavoxel.level;

import com.codingisfun2831t.omegavoxel.Face;

public class BlockGlass extends Block {
    public BlockGlass() {
        super();

        renderPass = RenderPass.TRANSPARENT;
    }

    @Override
    public boolean cullNeighborsFace(Block neighbor, Face face) {
        return neighbor instanceof BlockGlass;
    }
}
