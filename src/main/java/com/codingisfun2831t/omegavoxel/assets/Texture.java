package com.codingisfun2831t.omegavoxel.assets;

import java.nio.ByteBuffer;

/**
 * A handle for a single texture.
 * @param id OpenGL Texture ID
 * @param width Width of the texture.
 * @param height Height of the texture
 * @param data Four bytes (RGBA) per pixel, size width * height * 4.
 */
public record Texture(int id, int width, int height, ByteBuffer data) {
}
