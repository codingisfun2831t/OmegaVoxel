package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.level.Block;
import com.codingisfun2831t.omegavoxel.ui.*;
import com.codingisfun2831t.omegavoxel.ui.widgets.Background;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

import java.util.List;
import java.util.function.Consumer;

public class BlockSelectionScreen extends Screen {
    private Label title;
    private Background container;

    private static final int SLOT_SIZE = 24;
    private static final int SLOT_PADDING = 4;
    private static final int PADDING = 8;

    private static class BlockSelectionWidget extends Widget {
        private Block b;
        private Consumer<Block> action;

        private static final Color HOVER_COLOR = Color.fromRGBA(255, 255, 255, 128);

        public BlockSelectionWidget(Block b, Consumer<Block> action) {
            super();
            setSize(24, 24);

            this.b = b;
            this.action = action;
        }

        @Override
        public void renderContent(UIRenderer renderer) {
            if (hovered) {
                renderer.drawRect(getBounds(), HOVER_COLOR);
            }
            renderer.drawBlock(b, getLeft() + (hovered ? 1 : 4), getTop() + (hovered ? 1 : 4), (hovered ? 24 : 16));
        }

        @Override
        public void mouseClick(int x, int y, int button) {
            super.mouseClick(x, y, button);

            action.accept(b);
        }
    }

    @Override
    public void init() {
        container = new Background();

        List<Block> blocks = Block.getBlocksInInventory();
        int rows = blocks.size() / 9;
        int rowsHeight = rows * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING;

        container.setSize(PADDING * 2 + (9 * (SLOT_SIZE + SLOT_PADDING) - SLOT_PADDING), PADDING * 3 + 8 + rowsHeight);
        root.addChild(container);

        for (int i = 0; i < blocks.size(); i++) {
            int y = i / 9;
            int x = i % 9;

            BlockSelectionWidget widget = new BlockSelectionWidget(blocks.get(i), (b) -> {
                game.getPlayer().switchToBlock(b);
                game.navigateTo(null);
            });
            widget.setPosition(PADDING + (x * (SLOT_SIZE + SLOT_PADDING)), PADDING * 2 + 8 + (y * (SLOT_SIZE + SLOT_PADDING)));
            container.addChild(widget);
        }

        title = new Label("Block Selection");
        container.addChild(title);
    }

    @Override
    public void fixLayout(LayoutContext ctx) {
        container.setCenter(root.getCenter());

        title.autoSize(ctx);
        title.setCenterX(container.getWidth() / 2);
        title.setTop(8);
    }

    @Override
    public boolean pausesGame() {
        return true;
    }
}
