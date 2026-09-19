package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.widgets.Background;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

public class PauseMenu extends Screen {
    private Label pauseLabel;
    private Background bg;
    private Button backToGame;

    @Override
    public void init() {
        bg = new Background();
        root.addChild(bg);

        pauseLabel = new Label("Pause Menu");
        root.addChild(pauseLabel);

        backToGame = new Button("Back to Game", 200, () -> {
            game.navigateTo(null);
        });
        root.addChild(backToGame);
    }

    @Override
    public void fixLayout(LayoutContext ctx) {
        bg.setBounds(root.getBounds());

        pauseLabel.autoSize(ctx);
        pauseLabel.setCenterX(root.getCenterX());
        pauseLabel.setTop(100);
        int buttonY = pauseLabel.getBottom() + 10;

        backToGame.setCenterX(root.getCenterX());
        backToGame.setTop(buttonY);
    }

    @Override
    public boolean pausesGame() {
        return true;
    }
}
