package cn.jiuyou.codec;


import cn.jiuyou.compression.CompressionManager;
import cn.jiuyou.constant.MessageType;
import cn.jiuyou.serializer.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.jiuyou.constant.Constants.MAGIC_NUMBER;


/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/26 00:11
 * {@code @Description: } 按照自定义的消息格式解码数据
 */
@Slf4j
public class MyDecoder extends ByteToMessageDecoder {
    SerializerManager serializerManager = SerializerManager.getInstance();
    CompressionManager compressionManager = CompressionManager.getInstance();

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) throws Exception {
        //获取压缩类型
        byte compressionTypeCode = byteBuf.readByte();
        //获取魔法数
        int magicNumber = byteBuf.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            //魔法数错误，不解析
            return;
        }
        //获取消息类型
        int messageTypeCode = byteBuf.readInt();
        if (messageTypeCode != MessageType.RPC_REQUEST.getCode() &&
                messageTypeCode != MessageType.RPC_RESPONSE.getCode()) {
            log.error("暂不支持");
            return;
        }
        //读取序列化方式
        int serializerTypeCode = byteBuf.readInt();
        //获取序列化器
        if (serializerManager.getTypeCode() != serializerTypeCode) {
            log.error("服务器序列化方式与客户端序列化方式不同");
        }
        //读取消息体长度
        int length = byteBuf.readInt();
        byte[] bytes = new byte[length];
        //解析消息体
        byteBuf.readBytes(bytes);
        //进行解压
        if (compressionManager.getTypeCode() != compressionTypeCode) {
            log.error("服务器解压方式与客户端解压方式不同");
        }
        bytes = compressionManager.decompress(bytes);
        Object deserialize = serializerManager.deserialize(bytes, messageTypeCode);
        list.add(deserialize);
        log.info("decode success. compressionTypeCode: {}, magicNumber: {},messageTypeCode: {}, serializerTypeCode: {}, length: {}",
                compressionTypeCode, magicNumber, messageTypeCode, serializerTypeCode, length);
    }
}
