package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;

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
