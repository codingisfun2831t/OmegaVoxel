package com.codingisfun2831t.omegavoxel;

import net.querz.nbt.tag.CompoundTag;
import net.querz.nbt.tag.FloatTag;

public class NBTUtils {
    public static void ensureNBTValue(CompoundTag tag, String key) {
        if (!tag.containsKey(key)) {
            throw new RuntimeException("Key \"" + key + "\" doesn't exist.");
        }
    }

    public static float getFloatDefault(CompoundTag tag, String key, float def) {
        return (tag.containsKey(key) && tag.get(key) instanceof FloatTag) ? tag.getFloat(key) : def;
    }
}
