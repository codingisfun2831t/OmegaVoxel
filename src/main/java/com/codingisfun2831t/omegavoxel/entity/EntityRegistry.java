package com.codingisfun2831t.omegavoxel.entity;

import java.util.HashMap;
import java.util.Map;

public final class EntityRegistry {
    private static Map<String, Class<? extends Entity>> idToClass = new HashMap<>();
    private static Map<Class<? extends Entity>, String> classToId = new HashMap<>();

    private EntityRegistry() {}

    private static void register(String id, Class<? extends Entity> clazz) {
        idToClass.put(id, clazz);
        classToId.put(clazz, id);
    }

    public static Class<? extends Entity> getClassFromId(String id) {
        return idToClass.get(id);
    }

    public static String getIdFromClass(Class<? extends Entity> clazz) {
        return classToId.get(clazz);
    }

    static {
        register("player", EntityPlayer.class);
        register("steve", EntitySteve.class);
    }
}
