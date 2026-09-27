package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.widgets.Background;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

public class PauseMenu extends Screen {
    private Label pauseLabel;
    private Background bg;
    private Button backToGame;
    private Button options;
    private Button quit;

    @Override
    public void init() {
        bg = new Background();
        root.addChild(bg);

        pauseLabel = new Label("Pause Menu");
        root.addChild(pauseLabel);

        backToGame = new Button(Translations.get("menu.pause.backToGame"), 200, () -> {
            game.navigateTo(null);
        });
        root.addChild(backToGame);

        options = new Button(Translations.get("options.title"), 200, () -> {
            game.navigateTo(new MainOptionsScreen(game.options).withParent(this));
        });
        root.addChild(options);

        quit = new Button(Translations.get("menu.pause.quit"), 200, () -> {
            game.saveLevel();
            game.quitLevel();
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

        backToGame.setCenterX(root.getCenterX());
        backToGame.setTop(buttonY);
        buttonY = backToGame.getBottom() + 4;

        options.setCenterX(root.getCenterX());
        options.setTop(buttonY);
        buttonY = options.getBottom() + 4;

        quit.setCenterX(root.getCenterX());
        quit.setTop(buttonY);
    }

    @Override
    public boolean pausesGame() {
        return true;
    }
}
