package cn.jiuyou.serializer;


import cn.jiuyou.constant.SerializerType;
import cn.jiuyou.serializer.impl.KryoSerializer;
import cn.jiuyou.serializer.impl.ObjectSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static cn.jiuyou.constant.Constants.SERIALIZER_IMPL;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/14
 * {@code @Description: } 负责处理用户选择的实现，并提供相应的访问方法。先查看用户有没有set，再尝试读取配置文件，如果都选了，则默认为配置文件中的。
 */
public class SerializerManager {
    public static final Logger log = LoggerFactory.getLogger(SerializerManager.class);
    private static volatile SerializerManager instance;
    private static Serializer serializerImpl;


    public static SerializerManager getInstance() {
        if (instance == null) {
            synchronized (SerializerManager.class) {
                if (instance == null) {
                    instance = new SerializerManager();
                    instance.setFromConfigFile();
                }
            }
        }
        return instance;
    }

    /**
     * 用户可以通过set方法来设置自己想要的实现
     *
     * @param serializer 用户想要的实现
     */
    public static void setCompressionImpl(Serializer serializer) {
        serializerImpl = serializer;
        log.info("===== {} be used from set =====", serializer.getClass().toString());
    }

    public byte[] serialize(Object input) {
        return serializerImpl.serialize(input);
    }

    public Object deserialize(byte[] bytes, int messageTypeCode) {
        return serializerImpl.deserialize(bytes, messageTypeCode);
    }

    /**
     * 用户可以通过配置文件设置自己想要的实现，如果没有set的话
     */
    public void setFromConfigFile() {
        if (serializerImpl != null) {
            return;
        }
        if (SERIALIZER_IMPL == null) {
            serializerImpl = new KryoSerializer();
            return;
        }
        log.info("===== {} be used from config.properties =====", SERIALIZER_IMPL);
        if (SerializerType.OBJECT_SERIALIZER.toString().equals(SERIALIZER_IMPL)) {
            serializerImpl = new ObjectSerializer();
        } else if (SerializerType.KRYO_SERIALIZER.toString().equals(SERIALIZER_IMPL)) {
            serializerImpl = new KryoSerializer();
        } else {
            log.error("序列化方式填写错误");
        }
    }

    public int getTypeCode() {
        if (serializerImpl == null) {
            return SerializerType.NONE.getCode();
        }
        return serializerImpl.getTypeCode();
    }
}
