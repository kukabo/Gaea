package org.example.common.manager.impl.config;

import lombok.Getter;
import org.example.common.manager.Manager;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager implements Manager {

    private static volatile ConfigManager configManager;
    @Getter
    private static Properties properties;

    static  {
        getInstance();
    }

    private ConfigManager() {
        loadProperties();
    }

    //单例模式
    public static void getInstance() {
        if (configManager == null) {
            synchronized (ConfigManager.class) {
                if (configManager == null) {
                    configManager = new ConfigManager();
                }
            }
        }
    }

    private void loadProperties() {
        properties = new ApplicationProperties();
        try {
            InputStream inputStream = ConfigManager.class.getClassLoader().getResourceAsStream("application.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("配置装载错误！", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
