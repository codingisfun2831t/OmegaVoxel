package com.codingisfun2831t.omegavoxel.assets;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;

/**
 * Handles asset loading from resources or extract 'assets' folder.
 */
public class Assets {
    private final HashMap<String, String> textAssets;
    private final HashMap<String, byte[]> byteAssets;

    private static final Path ASSETS = Path.of("assets");

    public Assets() {
        textAssets = new HashMap<>();
        byteAssets = new HashMap<>();
    }

    private String textAsset(String path, String text) {
        textAssets.put(path, text);
        return text;
    }

    private byte[] binaryAsset(String path, byte[] bytes) {
        byteAssets.put(path, bytes);
        return bytes;
    }

    /**
     * Load an asset containing text.
     */
    public String loadTextAsset(String resourcePath) {
        byte[] bytes = loadAsset(resourcePath);
        return textAsset(resourcePath, new String(bytes, StandardCharsets.UTF_8));
    }

    /**
     * Load an asset containing binary.
     */
    public byte[] loadBinaryAsset(String resourcePath) {
        return binaryAsset(resourcePath, loadAsset(resourcePath));
    }

    private byte[] loadAsset(String resourcePath) {
        Path file = ASSETS.resolve(resourcePath);

        try {
            if (Files.isRegularFile(file)) {
                return Files.readAllBytes(file);
            }

            try (InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (is == null) {
                    throw new RuntimeException("Resource not found: " + resourcePath);
                }

                return is.readAllBytes();
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load asset: " + resourcePath, e);
        }
    }
}