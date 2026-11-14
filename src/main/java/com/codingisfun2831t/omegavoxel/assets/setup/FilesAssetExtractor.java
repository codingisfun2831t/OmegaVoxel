package com.codingisfun2831t.omegavoxel.assets.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class FilesAssetExtractor extends AssetExtractor {
    private static final Logger logger = LoggerFactory.getLogger(FilesAssetExtractor.class);
    private List<String> files;

    public FilesAssetExtractor(String jarVersion, List<String> files) {
        super(jarVersion);
        this.files = files;
    }

    @Override
    public void extract(Path assets, ZipFile jar) throws IOException {
        for (String file : files) {
            if (Files.isRegularFile(assets.resolve(file))) continue;

            ZipEntry entry = jar.getEntry(file);

            if (entry == null) {
                throw new IOException("File " + file + " not found in version " + this.jarVersion + ".");
            }

            Path extract = assets.resolve(file);
            Files.createDirectories(extract.getParent());

            try (InputStream is = jar.getInputStream(entry)) {
                Files.copy(is, extract, StandardCopyOption.REPLACE_EXISTING);
                logger.info("Extracted " + entry);
            }
        }
    }

    @Override
    public boolean shouldExtract(Path assets) {
        for (String file : files) {
            if (!Files.isRegularFile(assets.resolve(file))) {
                return true;
            }
        }

        return false;
    }
}
