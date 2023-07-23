package cn.jiuyou.http2;

import cn.jiuyou.entity.RpcResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public class Http2ClientResponseHandler extends SimpleChannelInboundHandler<FullHttpResponse> {

    private final Logger logger = LoggerFactory.getLogger(Http2ClientResponseHandler.class);
    private final Map<Integer, MapValues> streamidMap;

    public Http2ClientResponseHandler() {
        streamidMap = new HashMap<>();
    }

    public MapValues put(int streamId, ChannelFuture writeFuture, ChannelPromise promise) {
        return streamidMap.put(streamId, new MapValues(writeFuture, promise));
    }

    // 等待所有HTTP/2请求的响应
    public RpcResponse awaitResponses(long timeout, TimeUnit unit) {

        // 获取HTTP/2请求和响应的映射迭代器
        Iterator<Entry<Integer, MapValues>> itr = streamidMap.entrySet().iterator();

        // 初始化响应字符串为null
        RpcResponse response = null;

        while (itr.hasNext()) {
            Entry<Integer, MapValues> entry = itr.next();
            ChannelFuture writeFuture = entry.getValue().getWriteFuture();

            // 等待写操作完成（请求发送完成）
            if (!writeFuture.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("Timed out waiting to write for stream id " + entry.getKey());
            }
            if (!writeFuture.isSuccess()) {
                throw new RuntimeException(writeFuture.cause());
            }

            // 获取与当前请求对应的响应Promise
            ChannelPromise promise = entry.getValue().getPromise();

            // 等待响应完成
            if (!promise.awaitUninterruptibly(timeout, unit)) {
                throw new IllegalStateException("Timed out waiting for response on stream id " + entry.getKey());
            }
            if (!promise.isSuccess()) {
                throw new RuntimeException(promise.cause());
            }
            logger.info("---Stream id: " + entry.getKey() + " received---");
            response = entry.getValue().getResponse();

            // 从映射中移除已经处理完的请求和响应
            itr.remove();
        }

        // 返回最后一个响应内容
        return response;
    }

    // 处理HTTP/2服务器端的响应
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpResponse msg) {
        // 获取响应的streamId
        Integer streamId = msg.headers().getInt(HttpConversionUtil.ExtensionHeaderNames.STREAM_ID.text());

        // 如果streamId为空，表示收到的消息不是HTTP/2的响应，记录错误并返回
        if (streamId == null) {
            logger.error("HttpResponseHandler unexpected message received: " + msg);
            return;
        }

        // 根据streamId获取对应的请求信息
        MapValues value = streamidMap.get(streamId);

        // 如果未找到对应的请求信息，表示收到了未知的streamId，记录错误并关闭连接
        if (value == null) {
            logger.error("Message received for unknown stream id " + streamId);
            ctx.close();
        } else {
            // 从响应消息中获取响应内容，并保存到value对象中
            ByteBuf content = msg.content();
            if (content.isReadable()) {
                try {
                    String requestBody = content.toString(CharsetUtil.UTF_8);
                    ObjectMapper objectMapper = new ObjectMapper();
                    RpcResponse response = objectMapper.readValue(requestBody, RpcResponse.class);
                    logger.info("Response from Server: " + (response));
                    value.setResponse(response);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }

            // 设置响应Promise为成功状态
            value.getPromise().setSuccess();
        }
    }


    public static class MapValues {
        ChannelFuture writeFuture;
        ChannelPromise promise;
        RpcResponse response;

        public RpcResponse getResponse() {
            return response;
        }

        public void setResponse(RpcResponse response) {
            this.response = response;
        }

        public MapValues(ChannelFuture writeFuture2, ChannelPromise promise2) {
            this.writeFuture = writeFuture2;
            this.promise = promise2;
        }

        public ChannelFuture getWriteFuture() {
            return writeFuture;
        }

        public ChannelPromise getPromise() {
            return promise;
        }

    }
}
