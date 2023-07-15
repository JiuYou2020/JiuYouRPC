package cn.jiuyou.compression.impl;


import cn.jiuyou.compression.Compression;
import cn.jiuyou.constant.CompressionType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/13
 * {@code @Description: }使用Deflate算法压缩
 */
public class DeflateCompression implements Compression {
    static byte[] getBytes(InflaterInputStream inflaterInputStream) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inflaterInputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, bytesRead);
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] compress(byte[] bytes) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflateOutputStream = new DeflaterOutputStream(bos, new Deflater())) {
            deflateOutputStream.write(bytes);
        }
        return bos.toByteArray();
    }

    @Override
    public byte[] decompress(byte[] bytes) throws IOException {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try (InflaterInputStream inflaterInputStream = new InflaterInputStream(bis, new Inflater())) {
            return getBytes(inflaterInputStream);
        }
    }

    @Override
    public byte getTypeCode() {
        return CompressionType.DEFLATE.getCode();
    }
}
