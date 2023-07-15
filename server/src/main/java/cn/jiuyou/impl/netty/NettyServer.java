package cn.jiuyou.impl.netty;


import cn.jiuyou.Server;

import cn.jiuyou.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.AllArgsConstructor;

import static cn.jiuyou.constant.Constants.PORT;


/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:46
 * {@code @Description: } NettyServer
 */
@AllArgsConstructor
public class NettyServer implements Server {
    private ServiceProvider serviceProvider;
    private int port;

    public NettyServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
        this.port = PORT;
    }

    @Override
    public void run() {
        // netty 服务线程组boss负责建立连接， work负责具体的请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 启动netty服务器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 初始化
            serverBootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new NettyServerInitializer(serviceProvider));
            // 同步阻塞
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            // 死循环监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    @Override
    public void stop() {

    }
}
