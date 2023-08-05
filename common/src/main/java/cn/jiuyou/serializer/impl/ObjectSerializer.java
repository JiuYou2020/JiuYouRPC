package cn.jiuyou.serializer.impl;

import cn.jiuyou.serializer.Serializer;
import cn.jiuyou.serializer.SerializerManager;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

import static cn.jiuyou.constant.SerializerType.OBJECT_SERIALIZER;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 20:09
 * {@code @Description: }
 */
@Slf4j
public class ObjectSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ObjectOutputStream outputStream = new ObjectOutputStream(bos)) {
            outputStream.writeObject(obj);
            outputStream.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Object deserialize(byte[] bytes, int messageTypeCode) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInputStream inputStream = new ObjectInputStream(bis)) {
            return inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
