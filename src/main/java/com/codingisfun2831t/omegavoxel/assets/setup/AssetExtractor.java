package com.codingisfun2831t.omegavoxel.assets.setup;

import java.io.IOException;
import java.nio.file.Path;
import java.util.zip.ZipFile;

public abstract class AssetExtractor {
    protected String jarVersion;

    public AssetExtractor(String jarVersion) {
        this.jarVersion = jarVersion;
    }

    public abstract void extract(Path assets, ZipFile jar) throws IOException;
    public boolean shouldExtract(Path assets) {
        return true;
    }
}
