package cn.jiuyou.compression;

import java.io.IOException;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/13
 * {@code @Description: }
 */
public interface Compression {
    /**
     * 压缩
     *
     * @param bytes 待压缩的字节数组
     * @return 压缩后的字节数组
     * @throws IOException io异常
     */
    byte[] compress(byte[] bytes) throws IOException;

    /**
     * 解压
     *
     * @param bytes 待解压的字节数组
     * @return 解压后的字节数组
     * @throws IOException io异常
     */
    byte[] decompress(byte[] bytes) throws IOException;

    /**
     * 获取压缩算法的类型码
     *
     * @return 压缩算法的类型码
     */
    byte getTypeCode();
}
