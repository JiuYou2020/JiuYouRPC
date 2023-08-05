package cn.jiuyou.http2;

import cn.jiuyou.Client;
import cn.jiuyou.constant.Payload;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.serializer.SerializerManager;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import cn.jiuyou.utils.Http2Util;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.ssl.SslContext;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;
import java.util.concurrent.TimeUnit;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/23
 * {@code @Description: }
 */
public class JiuYouHttp2Client implements Client {
    private static final Logger logger = LoggerFactory.getLogger(JiuYouHttp2Client.class);
    private final SslContext sslCtx;

    public JiuYouHttp2Client() throws CertificateException, SSLException {
        sslCtx = Http2Util.createSSLContext(false);
    }

    @Override
    public RpcResponse call(RpcRequest rpcRequest) throws Exception {
        // 创建服务发现对象
        ZookeeperServiceDiscovery discovery = ZookeeperServiceDiscovery.getInstance();
        ServiceInstance<Payload> stringServiceInstance = discovery.getInstanceByStrategy(rpcRequest.getInterfaceName());
        String address = stringServiceInstance.getAddress();
        int port = stringServiceInstance.getPort();
        // 创建工作线程组
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 创建HTTP/2客户端的初始化器，指定SSL上下文、最大内容长度、服务器主机和端口
        Http2ClientInitializer initializer = new Http2ClientInitializer(sslCtx, Integer.MAX_VALUE, address, port);
        RpcResponse response;

        try {
            // 创建启动器
            Bootstrap b = new Bootstrap();
            // 配置启动器参数
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, true);
            b.remoteAddress(address, port);
            b.handler(initializer);
            // 连接到指定的HTTP/2服务器
            Channel channel = b.connect()
                    .syncUninterruptibly()
                    .channel();
            logger.info("Connected to [" + address + ':' + port + ']');
            // 获取HTTP/2的设置处理器，并等待设置完成
            Http2SettingsHandler http2SettingsHandler = initializer.getSettingsHandler();
            http2SettingsHandler.awaitSettings(60, TimeUnit.SECONDS);
            logger.info("Sending request(s)...");

            SerializerManager serializerManager = SerializerManager.getInstance();
            byte[] req = serializerManager.serialize(rpcRequest);

            // 构建一个HTTP/2 POST请求
            FullHttpRequest request = Http2Util.createPostRequest(address, port, req);
            // 设置请求头，指定请求内容类型为application/json
            request.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json");
            // 设置请求头，指定请求内容长度
            request.headers().set(HttpHeaderNames.CONTENT_LENGTH, request.content().readableBytes());
            // 获取HTTP/2的响应处理器
            Http2ClientResponseHandler responseHandler = initializer.getResponseHandler();
            int streamId = 3;
            // 将请求发送到服务器，并将响应结果保存到响应处理器中
            responseHandler.put(streamId, channel.write(request), channel.newPromise());
            channel.flush();
            // 等待HTTP/2响应并获取结果
            response = responseHandler.awaitResponses(60, TimeUnit.SECONDS);
            logger.info("Finished HTTP/2 request(s)");

        } finally {
            // 关闭工作线程组
            workerGroup.shutdownGracefully();
        }
        return response;
    }
}
