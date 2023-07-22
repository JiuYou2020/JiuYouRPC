package cn.jiuyou.http2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http2.*;
import io.netty.util.CharsetUtil;

@Sharable
public class Http2ServerResponseHandler extends ChannelDuplexHandler {

    static final ByteBuf RESPONSE_BYTES = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Hello World", CharsetUtil.UTF_8));

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
        ctx.close();
    }

@Override
public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    // 判断接收到的消息是否为HTTP/2头帧（Http2HeadersFrame）
    if (msg instanceof Http2HeadersFrame) {
        // 将接收到的消息转换为Http2HeadersFrame类型
        Http2HeadersFrame msgHeader = (Http2HeadersFrame) msg;
        // 检查头帧是否表示流的结束（即响应结束）
        if (msgHeader.isEndStream()) {
            // 创建一个ByteBuf用于存放响应内容
            ByteBuf content = ctx.alloc().buffer();
            // 将RESPONSE_BYTES的副本写入ByteBuf中（这里假设RESPONSE_BYTES是响应内容的字节数组）
            content.writeBytes(RESPONSE_BYTES.duplicate());

            // 创建HTTP/2头部（Headers）对象，设置响应状态为200 OK
            Http2Headers headers = new DefaultHttp2Headers().status(HttpResponseStatus.OK.codeAsText());

            // 将响应头部和内容分别封装成DefaultHttp2HeadersFrame和DefaultHttp2DataFrame
            // 然后通过ctx（ChannelHandlerContext）写入到HTTP/2连接中，完成响应的发送
            // 设置这两个帧的streamId与收到的头帧的streamId一致，确保它们属于同一个HTTP/2流
            ctx.write(new DefaultHttp2HeadersFrame(headers).stream(msgHeader.stream()));
            ctx.write(new DefaultHttp2DataFrame(content, true).stream(msgHeader.stream()));
        }

    } else {
        // 如果不是HTTP/2头帧，调用父类的channelRead方法，继续处理其他类型的消息
        super.channelRead(ctx, msg);
    }
}


    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

}
