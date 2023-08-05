package cn.jiuyou.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/18
 * {@code @Description: }
 */
@Slf4j
public class PropertiesLoader {
    public static Properties properties;

    /*
        在类加载时读取配置文件
    */
    static {
        properties = new Properties();
        try (InputStream inputStream = PropertiesLoader.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream != null) {
                properties.load(inputStream);
                log.info("===== config.properties loaded =====");
            } else {
                log.error("config.properties file not found.");
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }
}
