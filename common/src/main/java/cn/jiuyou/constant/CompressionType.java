package cn.jiuyou.constant;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/13
 * {@code @Description: } 对应全限定类名
 */
public enum CompressionType {
    /**
     * 不压缩
     */
    NONE((byte) 0x00),
    /**
     * Gzip压缩
     */
    GZIP((byte) 0x01),
    /**
     * Deflate压缩
     */
    DEFLATE((byte) 0x02);

    private final byte code;

    CompressionType(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
