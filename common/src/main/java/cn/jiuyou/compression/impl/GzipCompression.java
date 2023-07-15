package cn.jiuyou.compression.impl;


import cn.jiuyou.compression.Compression;
import cn.jiuyou.constant.CompressionType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import static cn.jiuyou.compression.impl.DeflateCompression.getBytes;


/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/13
 * {@code @Description: }使用Gzip算法压缩
 */
public class GzipCompression implements Compression {
    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {
            gzipOutputStream.write(bytes);
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (GZIPInputStream gzipInputStream = new GZIPInputStream(bis)) {
            return getBytes(gzipInputStream);
        }
    }

    @Override
    public byte getTypeCode() {
        return CompressionType.GZIP.getCode();
    }
}
