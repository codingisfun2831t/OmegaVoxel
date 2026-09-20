package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.widgets.Background;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

public class MainMenuScreen extends Screen {
    private Label pauseLabel;
    private Background bg;
    private Button play;
    private Button quit;

    @Override
    public void init() {
        bg = new Background();
        root.addChild(bg);

        pauseLabel = new Label("OmegaVoxel");
        root.addChild(pauseLabel);

        play = new Button("Play", 200, () -> {
            game.play();
        });
        root.addChild(play);

        quit = new Button("Quit Game", 200, () -> {
            game.close();
        });
        root.addChild(quit);
    }

    @Override
    public void fixLayout(LayoutContext ctx) {
        bg.setBounds(root.getBounds());

        pauseLabel.autoSize(ctx);
        pauseLabel.setCenterX(root.getCenterX());
        pauseLabel.setTop(100);
        int buttonY = pauseLabel.getBottom() + 10;

        play.setCenterX(root.getCenterX());
        play.setTop(buttonY);
        buttonY = play.getBottom() + 4;

        quit.setCenterX(root.getCenterX());
        quit.setTop(buttonY);
    }
}
