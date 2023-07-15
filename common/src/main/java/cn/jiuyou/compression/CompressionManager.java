package cn.jiuyou.compression;

import cn.jiuyou.compression.impl.DeflateCompression;
import cn.jiuyou.compression.impl.GzipCompression;
import cn.jiuyou.constant.CompressionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/14
 * {@code @Description: } 负责处理用户选择的实现，并提供相应的访问方法。先查看用户有没有set，再尝试读取配置文件，如果都选了，则默认为配置文件中的。
 */
public class CompressionManager {
    public static final Logger log = LoggerFactory.getLogger(CompressionManager.class);
    public static Properties properties;
    private static volatile CompressionManager instance;
    private static Compression compressionImpl;

    /*
      在类加载时读取配置文件
     */
    static {
        properties = new Properties();
        try (InputStream inputStream = CompressionManager.class.getClassLoader().getResourceAsStream("config.properties")) {
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


    public static CompressionManager getInstance() {
        if (instance == null) {
            synchronized (CompressionManager.class) {
                if (instance == null) {
                    instance = new CompressionManager();
                    instance.setFromConfigFile();
                }
            }
        }
        return instance;
    }

    /**
     * 用户可以通过set方法来设置自己想要的实现
     *
     * @param compression 用户想要的实现
     */
    public static void setCompressionImpl(Compression compression) {
        compressionImpl = compression;
        log.info("===== {} be used from set =====", compression.getClass().toString());
    }

    public byte[] compress(byte[] input) throws IOException {
        if (compressionImpl != null) {
            return compressionImpl.compress(input);
        }
        return input;
    }

    public byte[] decompress(byte[] input) throws IOException {
        if (compressionImpl != null) {
            return compressionImpl.decompress(input);
        }
        return input;
    }

    /**
     * 从配置文件中读取用户想要的实现，如果没有set的话
     */
    public void setFromConfigFile() {
        if (compressionImpl != null) {
            return;
        }
        String impl = properties.getProperty("compressionImpl");
        if (impl == null) {
            return;
        }
        if (CompressionType.DEFLATE.toString().equals(impl)) {
            compressionImpl = new DeflateCompression();
        } else if (CompressionType.GZIP.toString().equals(impl)) {
            compressionImpl = new GzipCompression();
        } else if (CompressionType.NONE.toString().equals(impl)) {
            compressionImpl = null;
        } else {
            log.error("解压方式填写错误");
        }
        log.info("===== {} be used from config.properties =====", impl);
    }

    public byte getTypeCode() {
        if (compressionImpl == null) {
            return CompressionType.NONE.getCode();
        }
        return compressionImpl.getTypeCode();
    }
}
