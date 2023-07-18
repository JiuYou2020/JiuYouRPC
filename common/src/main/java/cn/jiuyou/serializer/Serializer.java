package cn.jiuyou.serializer;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 20:05
 * {@code @Description: } 序列化器接口
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param obj 要序列化的对象
     * @return 序列化后的二进制数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     *
     * @param bytes           要反序列化的byte数组
     * @param messageTypeCode 消息体的类型码
     * @return 反序列化后的对象
     */
    Object deserialize(byte[] bytes, int messageTypeCode);

    /**
     * 获取实现类的TypeCode
     * @return 实现类的TypeCode
     */
    int getTypeCode();
}
