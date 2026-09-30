package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Game;
import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.widgets.Background;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;

public class MainMenuScreen extends Screen {
    private Label pauseLabel;
    private Label versionLabel;
    private Background bg;
    private Button play;
    private Button settings;
    private Button quit;

    @Override
    public void init() {
        bg = new Background();
        root.addChild(bg);

        pauseLabel = new Label("OmegaVoxel");
        root.addChild(pauseLabel);

        versionLabel = new Label(Game.TITLE);
        root.addChild(versionLabel);

        play = new Button(Translations.get("menu.play"), 200, () -> {
            game.play();
        });
        root.addChild(play);

        settings = new Button(Translations.get("options.title"), 200, () -> {
            game.navigateTo(new MainOptionsScreen(game.options).withParent(this));
        });
        root.addChild(settings);

        quit = new Button(Translations.get("menu.quit"), 200, () -> {
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

        settings.setCenterX(root.getCenterX());
        settings.setTop(buttonY);
        buttonY = settings.getBottom() + 4;

        quit.setCenterX(root.getCenterX());
        quit.setTop(buttonY);

        versionLabel.autoSize(ctx);
        versionLabel.setLeft(root.getLeft() + 2);
        versionLabel.setBottom(root.getBottom() - 2);
    }
}
