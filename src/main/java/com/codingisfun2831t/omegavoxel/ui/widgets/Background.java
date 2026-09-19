package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.*;
import org.joml.Vector2i;

public class Background extends Widget {
    @Override
    public void renderContent(UIRenderer renderer) {
        renderer.drawGradientRect(getBounds(), Color.PAUSE_TOP, Color.PAUSE_BOTTOM);
    }}
