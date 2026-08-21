package com.tap.framework.config;

import com.tap.framework.constants.FrameworkConstants;
import com.tap.framework.exceptions.FrameworkException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads {@code config.properties} once and exposes typed getters. A matching system property
 * always wins over the file value, which makes CI overrides trivial
 * ({@code -Dbrowser=firefox -Dheadless=false}).
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream stream = ConfigReader.class.getClassLoader()
                .getResourceAsStream(FrameworkConstants.CONFIG_FILE)) {
            if (stream == null) {
                throw new FrameworkException(FrameworkConstants.CONFIG_FILE + " not found on the classpath");
            }
            PROPERTIES.load(stream);
        } catch (Exception e) {
            throw new FrameworkException("Unable to load " + FrameworkConstants.CONFIG_FILE, e);
        }
    }

    private ConfigReader() {
    }

    public static String get(ConfigKey key) {
        return get(key.getKey());
    }

    public static String get(String key) {
        String value = System.getProperty(key, PROPERTIES.getProperty(key));
        if (value == null || value.isBlank()) {
            throw new FrameworkException("Property '" + key + "' is not configured");
        }
        return value.trim();
    }

    public static String get(ConfigKey key, String defaultValue) {
        String value = System.getProperty(key.getKey(), PROPERTIES.getProperty(key.getKey()));
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public static int getInt(ConfigKey key) {
        return Integer.parseInt(get(key));
    }

    public static long getLong(ConfigKey key) {
        return Long.parseLong(get(key));
    }

    public static boolean getBoolean(ConfigKey key) {
        return Boolean.parseBoolean(get(key));
    }
}
