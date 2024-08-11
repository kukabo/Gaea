package org.example.common.utils;

import org.example.common.manager.impl.config.ConfigManager;

import java.util.Properties;

public class ConfigUtil {

    public static Properties getProperties() {
        return ConfigManager.getProperties();
    }

    public static String getProperty(String key) {
        return ConfigManager.getProperty(key);
    }

}
