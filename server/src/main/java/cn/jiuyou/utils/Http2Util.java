package cn.jiuyou.utils;


import cn.jiuyou.http2.Http2ServerResponseHandler;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.codec.http2.Http2SecurityUtil;
import io.netty.handler.codec.http2.HttpConversionUtil;
import io.netty.handler.ssl.*;
import io.netty.handler.ssl.ApplicationProtocolConfig.Protocol;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectedListenerFailureBehavior;
import io.netty.handler.ssl.ApplicationProtocolConfig.SelectorFailureBehavior;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import io.netty.util.CharsetUtil;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

public class Http2Util {
    public static SslContext createSSLContext(boolean isServer) throws SSLException, CertificateException {
        SslContext sslCtx;

        // 创建一个自签名证书用于测试目的
        SelfSignedCertificate ssc = new SelfSignedCertificate();

        if (isServer) {
            // 如果是服务器端，创建用于服务器端的SSL上下文
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                    .sslProvider(SslProvider.JDK)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    // 指定应用层协议配置，使用HTTP/2和HTTP/1.1
                    .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                            SelectorFailureBehavior.NO_ADVERTISE,
                            SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2, ApplicationProtocolNames.HTTP_1_1))
                    .build();
        } else {
            // 如果是客户端，创建用于客户端的SSL上下文
            sslCtx = SslContextBuilder.forClient()
                    .sslProvider(SslProvider.JDK)
                    .ciphers(Http2SecurityUtil.CIPHERS, SupportedCipherSuiteFilter.INSTANCE)
                    // 指定信任所有证书的TrustManager，适用于测试环境，生产环境需要使用合适的TrustManager
                    .trustManager(InsecureTrustManagerFactory.INSTANCE)
                    // 指定应用层协议配置，仅使用HTTP/2
                    .applicationProtocolConfig(new ApplicationProtocolConfig(Protocol.ALPN,
                            SelectorFailureBehavior.NO_ADVERTISE,
                            SelectedListenerFailureBehavior.ACCEPT, ApplicationProtocolNames.HTTP_2))
                    .build();
        }
        return sslCtx;
    }


    public static ApplicationProtocolNegotiationHandler getServerAPNHandler() {
        // 创建一个应用层协议协商处理器，用于处理HTTP/2协议
        ApplicationProtocolNegotiationHandler serverAPNHandler = new ApplicationProtocolNegotiationHandler(ApplicationProtocolNames.HTTP_2) {

            @Override
            // 在协议协商成功后，根据协议类型进行相应的通道处理配置
            protected void configurePipeline(ChannelHandlerContext ctx, String protocol) {
                // 如果协议为HTTP/2
                if (ApplicationProtocolNames.HTTP_2.equals(protocol)) {
                    // 在通道的处理流水线中添加HTTP/2帧编解码器和HTTP/2服务器响应处理器
                    ctx.pipeline()
                            .addLast(Http2FrameCodecBuilder.forServer()
                                    .build(), new Http2ServerResponseHandler());
                    return;
                }
                // 如果协议不为HTTP/2，抛出异常，因为此处理器仅支持HTTP/2协议
                throw new IllegalStateException("Protocol: " + protocol + " not supported");
            }
        };
        return serverAPNHandler;
    }


    public static FullHttpRequest createGetRequest(String host, int port) {
        // 创建一个HTTP/2.0版本的GET请求，默认路径为"/"，空缓冲体
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf("HTTP/2.0"), HttpMethod.GET, "/", Unpooled.EMPTY_BUFFER);

        // 设置请求头信息
        return setRequestHeader(host, port, request);
    }

    public static FullHttpRequest createPostRequest(String host, int port, String requestJson) {
        // 创建一个HTTP/2.0版本的POST请求，默认路径为"/"，空缓冲体
        FullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.valueOf("HTTP/2.0"), HttpMethod.POST, "/", Unpooled.copiedBuffer(requestJson, CharsetUtil.UTF_8));

        // 设置请求头信息
        return setRequestHeader(host, port, request);
    }

    private static FullHttpRequest setRequestHeader(String host, int port, FullHttpRequest request) {
        request.headers()
                // 添加主机名和端口号到HOST请求头中
                .add(HttpHeaderNames.HOST, host + ":" + port);
        request.headers()
                // 指定请求使用HTTPS协议
                .add(HttpConversionUtil.ExtensionHeaderNames.SCHEME.text(), HttpScheme.HTTPS);
        request.headers()
                // 添加ACCEPT_ENCODING请求头，表示客户端接受的内容编码方式
                .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.GZIP);
        request.headers()
                // 添加ACCEPT_ENCODING请求头，支持GZIP和DEFLATE压缩方式
                .add(HttpHeaderNames.ACCEPT_ENCODING, HttpHeaderValues.DEFLATE);
        return request;
    }


}
