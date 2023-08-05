package cn.jiuyou.constant;

import cn.jiuyou.config.PropertiesLoader;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/24 21:34
 * {@code @Description: }
 */
public class Constants {
    public static final String HOST;
    public static final int PORT;
    public static final String SERIALIZER_IMPL;
    public static final String BASE_PATH;
    public static final int CONNECTION_TIMEOUT_MS;
    public static final int SESSION_TIMEOUT_MS;
    public static final String CONNECTION_STRING;

    static {
        try {
            HOST = PropertiesLoader.properties.getProperty("host");
            PORT = Integer.parseInt(PropertiesLoader.properties.getProperty("port"));
            SERIALIZER_IMPL = PropertiesLoader.properties.getProperty("serializeImpl");
            BASE_PATH = PropertiesLoader.properties.getProperty("basePath");
            CONNECTION_TIMEOUT_MS = Integer.parseInt(PropertiesLoader.properties.getProperty("connectionTimeoutMs"));
            SESSION_TIMEOUT_MS = Integer.parseInt(PropertiesLoader.properties.getProperty("sessionTimeoutMs"));
            CONNECTION_STRING = PropertiesLoader.properties.getProperty("connectionString");
        } catch (NumberFormatException e) {
            throw new NumberFormatException();
        }
    }

}
