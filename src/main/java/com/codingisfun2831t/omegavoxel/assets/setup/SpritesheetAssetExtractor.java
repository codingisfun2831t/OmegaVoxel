package com.codingisfun2831t.omegavoxel.assets.setup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class SpritesheetAssetExtractor extends AssetExtractor {
    private static final Logger logger = LoggerFactory.getLogger(SpritesheetAssetExtractor.class);
    private String source;
    private List<SpritesheetPart> parts;
    private String outFolder;

    public SpritesheetAssetExtractor(String jarVersion, String source, String outFolder, List<SpritesheetPart> parts) {
        super(jarVersion);
        this.source = source;
        this.outFolder = outFolder;
        this.parts = parts;
    }

    @Override
    public void extract(Path assets, ZipFile jar) throws IOException {
        ZipEntry entry = jar.getEntry(source);

        if (entry == null) {
            throw new IOException("File " + source + " not found in version " + this.jarVersion + ".");
        }

        BufferedImage img;
        try (InputStream is = jar.getInputStream(entry)) {
            img = ImageIO.read(is);
            logger.info("Extracted spritesheet {}", entry);
        }

        Path out = assets.resolve(outFolder);
        Files.createDirectories(out);

        int count = 0;
        for (SpritesheetPart part : parts) {
            Path partFile = out.resolve(part.file + ".png");
            if (Files.isRegularFile(partFile)) continue;

            BufferedImage tile = img.getSubimage(part.x, part.y, part.width, part.height);
            ImageIO.write(tile, "png", partFile.toFile());

            count++;
        }

        logger.info("Extracted {} sprite(s) from spritesheet {}", count, source);
    }

    @Override
    public boolean shouldExtract(Path assets) {
        Path out = assets.resolve(outFolder);
        for (SpritesheetPart part : parts) {
            if (!Files.isRegularFile(out.resolve(part.file + ".png"))) {
                return true;
            }
        }

        return false;
    }

    public record SpritesheetPart(String file, int x, int y, int width, int height) {}
}
