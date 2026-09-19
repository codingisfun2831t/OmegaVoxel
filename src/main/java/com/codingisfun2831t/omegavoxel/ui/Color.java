package com.codingisfun2831t.omegavoxel.ui;

public class Color {
    public static final Color BLACK = Color.fromRGB(0, 0, 0);
    public static final Color WHITE = Color.fromRGB(255, 255, 255);
    public static final Color TEXT_SHADOW = Color.fromRGBA(0, 0, 0, 128);
    public static final Color PAUSE_TOP = Color.fromRGBA(0, 0, 0, 64);
    public static final Color PAUSE_BOTTOM = Color.fromRGBA(0, 0, 0, 128);
    public static final Color BUTTON_TEXT_HOVER = Color.fromRGB(0xFF, 0xFF, 0xA0);

    private final int _value;

    public Color(int val) {
        _value = val;
    }

    // Get raw ARGB integer value
    public int getValue() {
        return _value;
    }

    /**
     * Converts the int color into a 4-byte array.
     * Index 0: Red, Index 1: Green, Index 2: Blue, Index 3: Alpha
     */
    public byte[] getBytes() {
        return new byte[]{
                (byte) getRed(),
                (byte) getGreen(),
                (byte) getBlue(),
                (byte) getAlpha()
        };
    }

    /**
     * Factory method to create a Color from a 4-byte array (RGBA).
     */
    public static Color fromBytes(byte[] bytes) {
        if (bytes == null || bytes.length < 4) {
            throw new IllegalArgumentException("Byte array must have at least 4 bytes.");
        }

        return fromRGBA(
                bytes[0] & 0xFF,
                bytes[1] & 0xFF,
                bytes[2] & 0xFF,
                bytes[3] & 0xFF
        );
    }

    public static Color fromRGBA(int r, int g, int b, int a) {
        return new Color(
                ((a & 0xFF) << 24) |
                        ((r & 0xFF) << 16) |
                        ((g & 0xFF) << 8) |
                        (b & 0xFF)
        );
    }

    public static Color fromRGB(int r, int g, int b) {
        return fromRGBA(r, g, b, 255);
    }

    public int getAlpha() { return (_value >> 24) & 0xFF; }
    public int getRed()   { return (_value >> 16) & 0xFF; }
    public int getGreen() { return (_value >> 8) & 0xFF; }
    public int getBlue()  { return _value & 0xFF; }

    public Color brightness(float f) {
        return Color.fromRGBA(
                (int) (getRed() * f),
                (int) (getGreen() * f),
                (int) (getBlue() * f),
                getAlpha()
        );
    }
}