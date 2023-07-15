package cn.jiuyou.constant;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 23:54
 * {@code @Description: }
 */
public enum SerializerType {
    /**
     * jdk自带序列化
     */
    OBJECT_SERIALIZER(1),
    /**
     * kryo序列化
     */
    KRYO_SERIALIZER(2), NONE(3);
    /**
     * 序列化类型码
     */
    private final int code;

    SerializerType(int code) {
        this.code = code;
    }

    public static SerializerType getEnumByTypeCode(int code) {
        for (SerializerType value : SerializerType.values()) {
            if (value.getCode() == code) {
                return value;
            }
        }
        return null;
    }

    public int getCode() {
        return code;
    }
}
