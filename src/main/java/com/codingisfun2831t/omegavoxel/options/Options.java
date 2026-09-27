package com.codingisfun2831t.omegavoxel.options;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Options {
    public IntOption fovOption;
    public IntOption renderDistOption;
    private Path optionsFile;
    private List<Option<?>> options;

    public Options(Path dataDir) {
        optionsFile = dataDir.resolve("options.txt");
        options = new ArrayList<>();
        fovOption = new IntOption(70, "fov").range(30, 120);
        options.add(fovOption);
        renderDistOption = new IntOption(4, "renderDist").range(2, 16);
        options.add(renderDistOption);
    }

    public void load() throws IOException {
        if (!Files.exists(optionsFile)) {
            save();
            return;
        }

        Properties props = new Properties();
        props.load(Files.newInputStream(optionsFile));

        for (Option<?> opt : options) {
            if (props.containsKey(opt.saveKey)) {
                opt.deserialize(props.getProperty(opt.saveKey));
            }
        }
    }

    public void save() throws IOException {
        Properties props = new Properties();
        for (Option<?> opt : options) {
            props.setProperty(opt.saveKey, opt.serialize());
        }

        props.store(Files.newOutputStream(optionsFile), "OmegaVoxel settings");
    }

    public int getFOV() {
        return fovOption.value;
    }
    public int getRenderDist() { return renderDistOption.value; }
}
