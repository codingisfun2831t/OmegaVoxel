package com.codingisfun2831t.omegavoxel.ui.screens;

import com.codingisfun2831t.omegavoxel.Translations;
import com.codingisfun2831t.omegavoxel.options.Option;
import com.codingisfun2831t.omegavoxel.options.Options;

public class MainOptionsScreen extends BaseOptionsScreen {
    public MainOptionsScreen(Options opts) {
        super(opts);
    }

    @Override
    public Option<?>[] getOptions() {
        return new Option[] {
                opts.fovOption,
                opts.renderDistOption
        };
    }

    @Override
    public String getTitle() {
        return Translations.get("options.title");
    }
}
