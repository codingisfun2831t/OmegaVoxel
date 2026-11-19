package com.codingisfun2831t.omegavoxel.assets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to load/keep textures.
 */
public class Textures {
    private final Assets assets;
    private final HashMap<String, Texture> textures = new HashMap<>();

    public Textures(Assets assets) {
        this.assets = assets;
    }

    public Texture create(ByteBuffer pixels, int width, int height) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

            int id = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_NEAREST
            );

            GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER,
                    GL11.GL_NEAREST
            );

            GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    GL11.GL_RGBA,
                    width,
                    height,
                    0,
                    GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    pixels
            );

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);

            return new Texture(id, width, height, pixels);
        }
    }

    /**
     * Get a Texture handle from a path.
     */
    public Texture load(String path) {
        Texture texture = textures.get(path);

        if (texture != null) {
            return texture;
        }

        byte[] data = assets.loadBinaryAsset(path);

        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            IntBuffer channels = stack.mallocInt(1);

            ByteBuffer image = MemoryUtil.memAlloc(data.length);
            image.put(data).flip();

            ByteBuffer pixels = STBImage.stbi_load_from_memory(
                    image,
                    width,
                    height,
                    channels,
                    4
            );

            if (pixels == null) {
                MemoryUtil.memFree(image);
                throw new RuntimeException(
                        "Failed to load texture: " + path
                );
            }

            int previousTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

            int id = GL11.glGenTextures();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);

            GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MIN_FILTER,
                    GL11.GL_NEAREST
            );

            GL11.glTexParameteri(
                    GL11.GL_TEXTURE_2D,
                    GL11.GL_TEXTURE_MAG_FILTER,
                    GL11.GL_NEAREST
            );

            GL11.glTexImage2D(
                    GL11.GL_TEXTURE_2D,
                    0,
                    GL11.GL_RGBA,
                    width.get(0),
                    height.get(0),
                    0,
                    GL11.GL_RGBA,
                    GL11.GL_UNSIGNED_BYTE,
                    pixels
            );

            GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTexture);

            MemoryUtil.memFree(image);

            texture = new Texture(id, width.get(0), height.get(0), pixels);
            textures.put(path, texture);
            return texture;
        }
    }

    public static class Atlas {
        private static final Logger logger = LoggerFactory.getLogger(Atlas.class);

        public String name;
        private Map<String, Integer> tex = new HashMap<>();
        private int atlasSize;
        private int partSize;
        private Texture atlas = null;

        public Atlas(String name, int partSize) {
            this.name = name;
            this.partSize = partSize;
            atlasSize = 1;
        }

        public int put(String partName) {
            if (atlas != null) {
                throw new IllegalStateException("Can't add a new texture to a atlas if already generated.");
            }

            if (tex.containsKey(partName))
                return tex.get(partName);

            tex.put(partName, tex.size());
            if (tex.size() > atlasSize * atlasSize) {
                atlasSize++;
            }

            return tex.size() - 1;
        }

        public void generate(Textures texs, Assets assets) throws IOException {
            if (atlas != null) return;

            int size = partSize * atlasSize;
            BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);

            Graphics2D g2d = img.createGraphics();
            for (String item : tex.keySet()) {
                int idx = tex.get(item);
                int x = idx % atlasSize;
                int y = idx / atlasSize;

                try (ByteArrayInputStream is = new ByteArrayInputStream(assets.loadBinaryAsset(name + "/" + item + ".png"))) {
                    BufferedImage sprite = ImageIO.read(is);
                    g2d.drawImage(sprite, x * partSize, y * partSize, null);
                }
            }

            g2d.dispose();

            int[] rawPixels = new int[size * size];
            img.getRGB(0, 0, size, size, rawPixels, 0, size);

            ByteBuffer pixels = MemoryUtil.memAlloc(size * size * 4);
            try {
                for (int y = 0; y < size; y++) {
                    for (int x = 0; x < size; x++) {
                        int pixel = rawPixels[y * size + x];
                        pixels.put((byte) ((pixel >> 16) & 0xFF));
                        pixels.put((byte) ((pixel >> 8) & 0xFF));
                        pixels.put((byte) (pixel & 0xFF));
                        pixels.put((byte) ((pixel >> 24) & 0xFF));
                    }
                }

                pixels.flip();
                atlas = texs.create(pixels, size, size);
            } finally {
                MemoryUtil.memFree(pixels);
            }


            logger.info("Generated {}x{} atlas {}", size, size, name);
        }

        public int getAtlasSize() {
            return atlasSize;
        }

        public Texture getAtlas() {
            return atlas;
        }
    }
}
