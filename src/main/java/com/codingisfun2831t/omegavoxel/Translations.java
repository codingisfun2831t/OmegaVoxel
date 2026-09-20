package com.codingisfun2831t.omegavoxel;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Translations {
    private static final Translations INSTANCE;

    static {
        try {
            INSTANCE = new Translations();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Properties props;

    private Translations() throws IOException {
        props = new Properties();

        InputStream stream = Translations.class.getResourceAsStream("/lang.txt");
        if (stream == null) {
            throw new IOException("Missing lang.txt");
        }

        try (stream) {
            props.load(stream);
        }
    }

    public static String get(String key) {
        return INSTANCE.props.getProperty(key, key);
    }
}