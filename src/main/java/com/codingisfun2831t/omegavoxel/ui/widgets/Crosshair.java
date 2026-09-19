package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.Color;
import com.codingisfun2831t.omegavoxel.ui.Rectangle;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;

public class Crosshair extends Widget {
    public Crosshair() {
        super();
        setSize(7, 7);
    }

    @Override
    public void renderContent(UIRenderer renderer) {
        renderer.drawRect(new Rectangle(getLeft() + 3, getTop(), 1, 7), Color.WHITE);
        renderer.drawRect(new Rectangle(getLeft(), getTop() + 3, 7, 1), Color.WHITE);
    }
}
