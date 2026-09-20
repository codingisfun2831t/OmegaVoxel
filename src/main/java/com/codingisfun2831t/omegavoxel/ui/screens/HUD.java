package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Player;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.widgets.Crosshair;
import com.codingisfun2831t.omegavoxel.ui.widgets.Hotbar;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

public class HUD extends Screen {
    private Label debug;
    private Crosshair crosshair;
    private Hotbar hotbar;

    @Override
    public void init() {
        debug = new Label("...");
        root.addChild(debug);

        crosshair = new Crosshair();
        root.addChild(crosshair);

        hotbar = new Hotbar();
        root.addChild(hotbar);
    }

    @Override
    public void fixLayout(LayoutContext ctx) {
        debug.autoSize(ctx);
        debug.setLeft(root.getLeft() + 2);
        debug.setTop(root.getTop() + 2);

        crosshair.setCenter(root.getCenter());

        hotbar.setCenterX(root.getCenterX());
        hotbar.setBottom(root.getBottom());
    }

    public void setDebug(String text) {
        debug.text = text;
    }
    public void setPlayer(Player plr) {
    hotbar.setPlayer(plr);
    }
}
