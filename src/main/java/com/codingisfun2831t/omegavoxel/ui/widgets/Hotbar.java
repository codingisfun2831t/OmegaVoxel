package com.codingisfun2831t.omegavoxel.ui.widgets;

import com.codingisfun2831t.omegavoxel.Player;
import com.codingisfun2831t.omegavoxel.ui.Rectangle;
import com.codingisfun2831t.omegavoxel.ui.UIRenderer;
import com.codingisfun2831t.omegavoxel.ui.Widget;

public class Hotbar extends Widget {
    private static final int WIDTH = 182;
    private static final int HEIGHT = 22;
    private static final int SLOT_SIZE = 16;
    private static final int SLOT_SPACING = 20;

    private Player plr = null;

    public Hotbar() {
        super();
        setSize(WIDTH, HEIGHT);
    }

    @Override
    public void renderContent(UIRenderer renderer) {
        renderer.drawTexturedRect(
                renderer.loadTex("gui/gui.png"),
                getBounds(),
                0, 0, WIDTH, HEIGHT
        );

        if (plr != null) {
            for (int i = 0; i < 9; i++) {
                if (plr.hotbar[i] == null) continue;

                renderer.drawBlock(
                        plr.hotbar[i],
                        getLeft() + 3 + SLOT_SPACING * i,
                        getTop() + 3,
                        SLOT_SIZE
                );
            }

            int selected = 1;
            renderer.drawTexturedRect(
                    renderer.loadTex("gui/gui.png"),
                    new Rectangle(getLeft() - 1 + plr.selectedSlot * SLOT_SPACING, getTop() - 1, 24, 24),
                    0, 22, 24, 24
            );
        }
    }

    public void setPlayer(Player plr) {
        this.plr = plr;
    }
}