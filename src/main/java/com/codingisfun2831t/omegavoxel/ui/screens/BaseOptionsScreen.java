package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.options.IntOption;
import com.codingisfun2831t.omegavoxel.options.Option;
import com.codingisfun2831t.omegavoxel.options.Options;
import com.codingisfun2831t.omegavoxel.ui.LayoutContext;
import com.codingisfun2831t.omegavoxel.ui.Screen;
import com.codingisfun2831t.omegavoxel.ui.Widget;
import com.codingisfun2831t.omegavoxel.ui.widgets.Button;
import com.codingisfun2831t.omegavoxel.ui.widgets.Label;
import com.codingisfun2831t.omegavoxel.ui.widgets.Slider;

public abstract class BaseOptionsScreen extends Screen {
    private Widget[] optionButtons;
    private Label title;
    private Button done;

    protected Options opts;

    public BaseOptionsScreen(Options opts) {
        this.opts = opts;
    }

    private static final int OPTION_SIZE = 100;
    private static final int OPTION_SPACING = 2;
    private Widget container;

    @Override
    public void init() {
        Option<?>[] options = getOptions();
        optionButtons = new Widget[options.length];

        title = new Label(getTitle());
        root.addChild(title);

        container = new Widget();

        int rows = (options.length + 1) / 2;
        container.setSize(
                OPTION_SIZE * 2 + OPTION_SPACING,
                rows * (20 + OPTION_SPACING) - OPTION_SPACING
        );

        for (int i = 0; i < options.length; i++) {
            Option<?> opt = options[i];

            Widget widget;
            if (opt instanceof IntOption intOpt) {
                widget = new Slider(
                        Translations.get(opt.getTranslationKey()) + ": " + opt.formatValue(),
                        OPTION_SIZE, intOpt.getValue())

                        .range(intOpt.min, intOpt.max).step(1)
                        .onChange(((slider, val) -> {
                            intOpt.setValue(val);
                            slider.text = Translations.get(opt.getTranslationKey()) + ": " + opt.formatValue();
                        }));
            } else {
                continue;
            }

            widget.setWidth(OPTION_SIZE);
            widget.setPosition((i % 2) * (OPTION_SPACING + OPTION_SIZE), (20 + OPTION_SPACING) * (i / 2));
            container.addChild(widget);
        }

        root.addChild(container);

        done = new Button(Translations.get("gui.done"), 200, this::goBack);
        root.addChild(done);
    }

    @Override
    public void fixLayout(LayoutContext ctx) {
        title.autoSize(ctx);
        title.setCenterX(root.getCenterX());
        title.setTop(100);

        container.setCenter(root.getCenter());

        done.setCenterX(root.getCenterX());
        done.setTop(container.getBottom() + 10);
    }

    public abstract Option<?>[] getOptions();
    public abstract String getTitle();

    @Override
    public boolean pausesGame() {
        return true;
    }
}
