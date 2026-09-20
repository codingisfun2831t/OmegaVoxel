package com.codingisfun2831t.omegavoxel.assets;

import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.stb.STBImage;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

/**
 * Class to load/keep textures.
 */
public class Textures {
    private final Assets assets;
    private final HashMap<String, Texture> textures = new HashMap<>();

    public Textures(Assets assets) {
        this.assets = assets;
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
}
