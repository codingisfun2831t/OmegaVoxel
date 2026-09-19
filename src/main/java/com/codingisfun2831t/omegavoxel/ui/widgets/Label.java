package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Rectangle;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;

import java.util.ArrayList;

public class Label extends Widget {
    public String text;

    public Label(String text) {
        super();

        this.text = text;
    }

    @Override
    public void renderContent(UIRenderer renderer) {
        renderer.drawTextWithShadow(getLeft(), getTop(), text);
    }

    public void autoSize(LayoutContext context) {
        setSize(context.measureText(text), 10);
    }
}
