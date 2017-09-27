package com.wufan.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by 7cc on 2017/9/20
 */
public class ConfigurationManager {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigurationManager.class);

    private static final String CONFIG_FORMAT = "application-%s.properties";
    private static final String CONFIG_NAME = "application.properties";
    private static final String KEY_PROFILES_ACTIVE = "spring.profiles.active";
    private static final Properties prop = new Properties();


    static {
        try {
            InputStreamLoad(CONFIG_NAME);
        } catch (Throwable e) {
            throw new IllegalArgumentException(String.format("ConfigurationManager init failed - exception message==%s", e.getMessage()));
        }
        String value = getProperty(KEY_PROFILES_ACTIVE);
        if (StringUtils.isNotBlank(value)) {
            String activeConfigName = String.format(CONFIG_FORMAT, value);
            try {
                InputStreamLoad(activeConfigName);
            } catch (Throwable e) {
                LOG.error(activeConfigName + e.getMessage(), e);
            }
        }
        System.err.println(getProperty("7ccTest"));
    }

    private static void InputStreamLoad(String configName) throws Throwable {
        InputStream in = ConfigurationManager.class
                .getClassLoader().getResourceAsStream(configName);
        prop.load(in);
    }

    public static String getProperty(String key) {
        try {
            return prop.getProperty(key);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        System.out.print(getProperty("bucketName"));
        System.out.println("7cc");
    }

}
