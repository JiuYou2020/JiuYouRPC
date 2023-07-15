package cn.jiuyou.codec;


import cn.jiuyou.compression.CompressionManager;
import cn.jiuyou.constant.MessageType;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.serializer.SerializerManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

import static cn.jiuyou.constant.Constants.MAGIC_NUMBER;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 23:40
 * {@code @Description: } 依次按照自定义的消息格式写入，传入的数据为request或者response
 * 需要持有一个serialize器，负责将传入的对象序列化成字节数组
 */
@Slf4j
public class MyEncoder extends MessageToByteEncoder {
    CompressionManager compressionManager = CompressionManager.getInstance();
    SerializerManager serializerManager = SerializerManager.getInstance();

    @Override
    protected void encode(ChannelHandlerContext ctx, Object obj, ByteBuf byteBuf) throws Exception {
        //写入压缩类型，1字节，例如00x0
        byteBuf.writeByte(compressionManager.getTypeCode());
        //写入魔法数
        byteBuf.writeInt(MAGIC_NUMBER);
        //写入消息类型
        if (obj instanceof RpcRequest) {
            byteBuf.writeInt(MessageType.RPC_REQUEST.getCode());
        } else if (obj instanceof RpcResponse) {
            byteBuf.writeInt(MessageType.RPC_RESPONSE.getCode());
        } else {
            System.out.println("暂未实现");
        }
        //写入序列化方式
        byteBuf.writeInt(serializerManager.getTypeCode());
        //序列化消息体
        byte[] bytes = serializerManager.serialize(obj);
        //进行压缩
        bytes = compressionManager.compress(bytes);
        //写入消息体长度
        byteBuf.writeInt(bytes.length);
        //写入序列化后的消息
        byteBuf.writeBytes(bytes);
        log.info("encode success. compressionTypeCode: {}, magicNumber: {}, messageTypeCode: {}, serializerTypeCode: {}, length: {}",
                compressionManager.getTypeCode(), MAGIC_NUMBER, MessageType.RPC_REQUEST.getCode(), serializerManager.getTypeCode(), bytes.length);
    }
}
