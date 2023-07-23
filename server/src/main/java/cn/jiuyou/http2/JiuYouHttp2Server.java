package cn.jiuyou.http2;

import cn.jiuyou.Server;
import cn.jiuyou.ServiceProvider;
import cn.jiuyou.constant.Payload;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import cn.jiuyou.utils.Http2Util;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import org.apache.curator.x.discovery.ServiceInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import static cn.jiuyou.constant.Constants.PORT;

/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/7/23
 * {@code @Description: }
 */
public class JiuYouHttp2Server implements Server {
    private static final Logger logger = LoggerFactory.getLogger(JiuYouHttp2Server.class);

    @Override
    public void run() throws Exception {
        ZookeeperServiceDiscovery discovery = ZookeeperServiceDiscovery.getInstance();
        ArrayList<ServiceInstance<Payload>> serviceInstances = ServiceProvider.getServiceInstances();
        for (ServiceInstance<Payload> serviceInstance : serviceInstances) {
            discovery.registerService(serviceInstance);
            logger.info("注册服务成功: {} 权重为:{}", serviceInstance, serviceInstance.getPayload().getWeight());
        }


        // 创建SSL上下文，用于支持HTTPS连接
        SslContext sslCtx = Http2Util.createSSLContext(true);

        // 创建NIO事件循环组，用于处理I/O操作
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            // 创建服务器启动器
            ServerBootstrap b = new ServerBootstrap();
            // 设置服务器启动器参数，设置SO_BACKLOG选项为1024，表示服务器套接字的最大连接队列长度
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(group)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            // 如果SSL上下文不为空，表示需要使用HTTPS连接
                            if (sslCtx != null) {
                                // 在处理流水线（pipeline）中添加SSL处理器和HTTP/2服务端的应用层协议协商处理器
                                // SSL处理器用于处理安全套接字层（SSL/TLS）的加密和解密
                                // HTTP/2服务端的应用层协议协商处理器用于进行TLS协议的ALPN扩展协商，以支持HTTP/2协议
                                ch.pipeline()
                                        .addLast(sslCtx.newHandler(ch.alloc()), Http2Util.getServerAPNHandler());
                            }
                        }

                    });

            Channel ch = b.bind(PORT)
                    .sync()
                    .channel();

            logger.info("HTTP/2 Server is listening on https://127.0.0.1:" + PORT + '/');

            // 等待服务器通道关闭
            ch.closeFuture()
                    .sync();
        } finally {
            // 关闭事件循环组，释放资源
            group.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
