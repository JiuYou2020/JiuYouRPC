package cn.jiuyou.netty;


import cn.jiuyou.Client;
import cn.jiuyou.constant.Payload;
import cn.jiuyou.entity.RpcRequest;
import cn.jiuyou.entity.RpcResponse;
import cn.jiuyou.serviceDiscovery.impl.ZookeeperServiceDiscovery;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.curator.x.discovery.ServiceInstance;


/**
 * {@code @Author: } JiuYou
 * {@code @Date: } 2023/06/25 13:12
 * {@code @Description: }
 */
public class NettyClient implements Client {
    private static final Bootstrap BOOTSTRAP;
    private static final EventLoopGroup EVENT_LOOP_GROUP;

    // netty客户端初始化，重复使用
    static {
        EVENT_LOOP_GROUP = new NioEventLoopGroup();
        BOOTSTRAP = new Bootstrap();
        BOOTSTRAP.group(EVENT_LOOP_GROUP).channel(NioSocketChannel.class)
                .handler(new NettyClientInitializer());
    }

    /**
     * 这里需要操作一下，因为netty的传输都是异步的，你发送request，会立刻返回， 而不是想要的相应的response
     */
    @Override
    public RpcResponse call(RpcRequest request) throws Exception {
        try {
            ZookeeperServiceDiscovery discovery = ZookeeperServiceDiscovery.getInstance();
            ServiceInstance<Payload> stringServiceInstance = discovery.getInstanceByStrategy(request.getInterfaceName());

            String address = stringServiceInstance.getAddress();
            int port = stringServiceInstance.getPort();
            ChannelFuture channelFuture = BOOTSTRAP.connect(address, port).sync();
            Channel channel = channelFuture.channel();
            // 发送数据
            channel.writeAndFlush(request);
            channel.closeFuture().sync();
            // 阻塞的获得结果，通过给channel设计别名，获取特定名字下的channel中的内容（这个在hanlder中设置）
            // AttributeKey是，线程隔离的，不会由线程安全问题。
            // 实际上不应通过阻塞，可通过回调函数
            AttributeKey<RpcResponse> key = AttributeKey.valueOf("RpcResponse");
            return channel.attr(key).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}