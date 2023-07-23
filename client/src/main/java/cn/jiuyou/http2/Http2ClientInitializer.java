package cn.jiuyou.http2;


import cn.jiuyou.utils.Http2Util;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

public class Http2ClientInitializer extends ChannelInitializer<SocketChannel> {

    private final SslContext sslCtx;
    private final int maxContentLength;
    private Http2SettingsHandler settingsHandler;
    private Http2ClientResponseHandler responseHandler;
    private final String host;
    private final int port;

    public Http2ClientInitializer(SslContext sslCtx, int maxContentLength, String host, int port) {
        this.sslCtx = sslCtx;
        this.maxContentLength = maxContentLength;
        this.host = host;
        this.port = port;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        // 创建Http2SettingsHandler，用于处理HTTP/2的设置帧，并使用一个Promise来跟踪设置是否完成
        settingsHandler = new Http2SettingsHandler(ch.newPromise());

        // 创建Http2ClientResponseHandler，用于处理HTTP/2客户端的响应
        responseHandler = new Http2ClientResponseHandler();

        // 如果SSL上下文（sslCtx）不为空，表示需要使用HTTPS连接
        if (sslCtx != null) {
            // 获取当前Channel的处理流水线（pipeline）
            ChannelPipeline pipeline = ch.pipeline();
            // 添加SSL处理器到处理流水线，用于处理安全套接字层（SSL/TLS）的加密和解密
            pipeline.addLast(sslCtx.newHandler(ch.alloc(), host, port));
            // 添加HTTP/2客户端的应用层协议协商处理器（ApplicationProtocolNegotiationHandler）到处理流水线
            // 该处理器用于进行TLS协议的ALPN扩展协商，以支持HTTP/2协议
            // 同时，将Http2SettingsHandler和Http2ClientResponseHandler传递给应用层协议协商处理器，用于处理HTTP/2的设置和响应
            pipeline.addLast(Http2Util.getClientAPNHandler(maxContentLength, settingsHandler, responseHandler));
        }
    }


    public Http2SettingsHandler getSettingsHandler() {
        return settingsHandler;
    }

    public Http2ClientResponseHandler getResponseHandler() {
        return responseHandler;
    }
}
